package com.github.mckornfield.tutorial1;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemo {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerDemo.class);

    public static void main(String[] args) {
        Properties properties = new Properties();
        String bootstrapServers = "127.0.0.1:9092";

        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        for (int i = 0; i < 10; i++) {
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>("first_topic", "Hello World #" + i);

            // Sends data asynchronously
            producer.send(producerRecord, (metadata, exception) -> {
                if (exception == null) {
                    LOGGER.info("Received new metadata. Topic: {}, Partition: {}, Offset: {}, Timestamp: {}",
                        metadata.topic(), metadata.partition(), metadata.offset(), metadata.timestamp());

                } else {
                    LOGGER.info("Error while producing", exception);
                }
            });
        }

        // Flush asynchronous data
        producer.flush();
        producer.close();
    }
}
