package ru.otus.kafka.hw03;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class Producer {
    public static void main(String[] args) throws Exception{

        Properties transactionProducerProps = new Properties();
        transactionProducerProps.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "hw3Producer");
        transactionProducerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:19092");
        transactionProducerProps.put(ProducerConfig.ACKS_CONFIG, "all");
        transactionProducerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        transactionProducerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        try (var producer = new KafkaProducer<String, String>(transactionProducerProps)) {
            producer.initTransactions();
            producer.beginTransaction();
            for (int i = 0; i < 5; ++i) {
                producer.send(new ProducerRecord<>("topic1", "tx-one-commited-" + i));
                producer.send(new ProducerRecord<>("topic2", "tx-two-commited-" + i));
            }
            producer.commitTransaction();

            producer.beginTransaction();
            for (int i = 0; i < 2; ++i) {
                producer.send(new ProducerRecord<>("topic1", "rb-one-aborted-" + i));
                producer.send(new ProducerRecord<>("topic2", "rb-two-aborted-" + i));
            }
            producer.abortTransaction();
        }
    }
}
