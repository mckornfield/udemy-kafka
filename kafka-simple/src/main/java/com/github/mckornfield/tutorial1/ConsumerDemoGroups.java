package com.github.mckornfield.tutorial1;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class ConsumerDemoGroups {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerDemoGroups.class);

    public static void main(String[] args) {
        String bootstrapServers = "127.0.0.1:9092";
        String groupId = "my-fifth-application";
        String first_topic = "first_topic";

        // Create consumer configs
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // create consumer

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

        // subscribe consumer
        consumer.subscribe(Collections.singleton(first_topic));

        // poll for new data
        while (true) {
            ConsumerRecords<String, String> records =
                consumer.poll(Duration.ofMillis(100)); // Need to use duration in new version of kafka
            for (ConsumerRecord<String, String> record : records) {
                LOGGER.info("Key: {}, Value: {}", record.key(), record.value());
                LOGGER.info("Partition: {}, Offset: {}", record.partition(), record.offset());

            }

        }

    }
}
