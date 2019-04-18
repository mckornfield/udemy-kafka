package com.github.mckornfield.tutorial1;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class ConsumerDemoWithThreads {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerDemoWithThreads.class);

    public static void main(String[] args) {
        String bootstrapServers = "127.0.0.1:9092";
        String first_topic = "first_topic";
        String groupId = "my-fifth-application";

        CountDownLatch latch = new CountDownLatch(1);

        Runnable myConsumerRunnable = new ConsumerThread(
            bootstrapServers,
            groupId,
            first_topic,
            latch
        );

        // poll for new data
        Thread myThread = new Thread(myConsumerRunnable);
        myThread.start();

        // Add a shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            LOGGER.info("Caught shutdown hook");
            ((ConsumerThread) myConsumerRunnable).shutdown();
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
        try {
            latch.await();
        } catch (InterruptedException e) {
            LOGGER.error("Application interrupted");
        }
    }

    public static class ConsumerThread implements Runnable {

        private final CountDownLatch latch;
        private final KafkaConsumer<String, String> consumer;

        public ConsumerThread(String bootstrapServers, String groupId, String topic, CountDownLatch latch) {
            this.latch = latch;
            Properties properties = new Properties();

            properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

            // create consumer
            this.consumer = new KafkaConsumer<>(properties);
            this.consumer.subscribe(Collections.singleton(topic));
        }

        @Override
        public void run() {
            try {
                while (true) {
                    ConsumerRecords<String, String> records =
                        consumer.poll(Duration.ofMillis(100)); // Need to use duration in new version of kafka
                    for (ConsumerRecord<String, String> record : records) {
                        LOGGER.info("Key: {}, Value: {}", record.key(), record.value());
                        LOGGER.info("Partition: {}, Offset: {}", record.partition(), record.offset());
                    }

                }
            } catch (WakeupException e) {
                LOGGER.info("Received shutdown signal");
            } finally {
                consumer.close();
                // tell main code we're done with the consumer
                latch.countDown();
            }
        }

        public void shutdown() {
            // The wakeup() method is a special method to interrupt poll
            // It will throw the exception WakeUpException
            consumer.wakeup();
        }
    }
}
