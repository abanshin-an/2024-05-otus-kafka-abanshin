package ru.otus.kafka.hw03;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyProducer {
    private static final Logger logger = LoggerFactory.getLogger(MyProducer.class);

    public static final Properties KAFKA_PRODUCER_CONFIG = new Properties() {{
        put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092,localhost:39092,localhost:49092");
    }};

    public static void produce() {
        logger.info("produce messages");
        try (KafkaProducer<String,String> producer = new KafkaProducer<>(
                configure(KAFKA_PRODUCER_CONFIG,
                p -> p.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "hw03")))) {
            producer.initTransactions();
            producer.beginTransaction();
            logger.info("begin transaction");
            for (int i = 0; i < 5; ++i) {
                producer.send(new ProducerRecord<>("topic1", "tx-one-commited-" + i));
                producer.send(new ProducerRecord<>("topic2", "tx-two-commited-" + i));
                logger.info("sent pair of messages number {} in transaction", i);
            }
            producer.commitTransaction();
            logger.info("commit transaction");

            producer.beginTransaction();
            logger.info("begin transaction");
            for (int i = 0; i < 2; ++i) {
                producer.send(new ProducerRecord<>("topic1", "rb-one-aborted-" + i));
                producer.send(new ProducerRecord<>("topic2", "rb-two-aborted-" + i));
                logger.info("sent pair of messages number {}", i);
            }
            logger.info("abort transaction");
            producer.abortTransaction();
        }
    }

    public static Properties configure(Properties properties, java.util.function.Consumer<Properties> propertiesAppender) {
        Properties currentProperties = (Properties) properties.clone();
        if (propertiesAppender != null)
            propertiesAppender.accept(currentProperties);
        return currentProperties;
    }
}
