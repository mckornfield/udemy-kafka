package com.github.mckornfield.tutorial1;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class ProducerDemo {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerDemo.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Properties properties = new Properties();
        String bootstrapServers = "127.0.0.1:9092";

        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        for (int i = 0; i < 10; i++) {
            String firstTopic = "first_topic";
            String key = "id_" + i;
            String value = "Hello World #" + i;
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(firstTopic, key, value);

            LOGGER.info("Key is {}", key);
            // id0 is partition 1
            // id1 is partition 0
            // id2 is partition 2
            // etc.

            // Sends data asynchronously
            producer.send(producerRecord, (metadata, exception) -> {
                if (exception == null) {
                    LOGGER.info("Received new metadata. Topic: {}, Partition: {}, Offset: {}, Timestamp: {}",
                        metadata.topic(), metadata.partition(), metadata.offset(), metadata.timestamp());

                } else {
                    LOGGER.info("Error while producing", exception);
                }
            }).get(); // Make it synchronous, don't do in production
        }

        // Flush asynchronous data
        producer.flush();
        producer.close();
    }
}
