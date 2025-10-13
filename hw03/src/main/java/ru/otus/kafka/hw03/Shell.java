package ru.otus.kafka.hw03;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Properties;

public class Shell {
    private static final Logger logger = LoggerFactory.getLogger(Shell.class);
    private static final String PRODUCER = "producer";
    private static final String CONSUMER = "consumer";

    public static void main(String[] args) {
        if (args.length != 1 ) {
            logger.info("args.length = "+args.length);
            logger.info("""
                     Use
                     java -cp ./libs/hw03.jar ru.otus.kafka.hw03.Shell producer
                     or
                     java -cp ./libs/hw03.jar ru.otus.kafka.hw03.Shell kafkaConsumer
                     """);
        }
        switch (args[0]) {
            case PRODUCER -> MyProducer.produce();
            case CONSUMER -> MyConsumer.consume();
            default -> logger.info("wrong command {}", args[1]);
        }
    }

    public static Properties configure(Properties properties, java.util.function.Consumer<Properties> propertiesAppender) {
        Properties currentProperties = (Properties) properties.clone();
        if (propertiesAppender != null)
            propertiesAppender.accept(currentProperties);
        return currentProperties;
    }
}