package ru.otus.kafka.hw04;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.SessionWindows;
import org.apache.kafka.streams.kstream.WindowedSerdes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    public static final String TOPIC = "events";
    private static final String APPLICATION_ID = "hw04";
    private static final String APPLICATION_HOST = "localhost:29092";

    public static final Properties properties = new Properties() {{
        put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, APPLICATION_HOST);
        put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_ID);
        put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    }};

    public static Properties createStreamConfig(Consumer<Properties> consumer) {
        Properties currentProperties = (Properties) properties.clone();
        if (consumer != null)
            consumer.accept(currentProperties);
        return currentProperties;
    }
    public static void main(String[] args) {
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> sourceStream = builder.stream(TOPIC);
        Duration inactivityGap = Duration.ofMinutes(5);
        sourceStream
                .peek((key, value) -> logger.info("input key: {}, value: {}", key, value))
                .groupByKey()
                .windowedBy(SessionWindows.ofInactivityGapWithNoGrace(inactivityGap))
                .count(Materialized.as("session-counts-store")) // Optional: Materialize to a state store
                .toStream()
                .peek((key, value) -> logger.info("count output key: {}, value: {}", key, value))
                .to("events-count-by-key", Produced.with(WindowedSerdes.sessionWindowedSerdeFrom(String.class), Serdes.Long()));

        Topology topology = builder.build();
        logger.info(topology.describe().toString());

        try (KafkaStreams kafkaStreams = new KafkaStreams(topology, createStreamConfig(null))) {
            kafkaStreams.start();
            Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
            while (true) {
                TimeUnit.SECONDS.sleep(5);
            }
        } catch (InterruptedException e) {
            logger.info("Main thread interrupted.");
        } finally {
            logger.info("Application shutting down gracefully.");
        }
    }

}
