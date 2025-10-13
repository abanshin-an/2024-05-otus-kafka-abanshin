package ru.otus.kafka.hw03;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class MyConsumer {

    private static final Logger logger = LoggerFactory.getLogger(MyConsumer.class);

    public static final Properties KAFKA_CONSUMER_CONFIG = new Properties() {{
        put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092,localhost:39092,localhost:49092");
        put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        put(ConsumerConfig.GROUP_ID_CONFIG, "consumer");
    }};

    public static void consume() {
        logger.info("Start consumer...");

        try (KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(KAFKA_CONSUMER_CONFIG)) {
            kafkaConsumer.subscribe(Arrays.asList("topic1", "topic2"));
            while (!Thread.interrupted()) {
                var read = kafkaConsumer.poll(Duration.ofSeconds(1));
                for (var record : read) {
                    logger.info("Receive {}:{} at {}", record.key(), record.value(), record.offset());
                }
            }
        }


    }
}
