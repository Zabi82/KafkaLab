package com.kafka.lab.avro;

import com.kafka.lab.model.Transaction;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class AvroConsumer {

    public static final String TXN_TOPIC_AVRO = "txn_topic_avro";


    public void consumeMessages(int timeout) {

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, io.confluent.kafka.serializers.KafkaAvroDeserializer.class);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");




        try (KafkaConsumer<String, Transaction> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Arrays.asList(TXN_TOPIC_AVRO));

            while (true) {
                ConsumerRecords<String, Transaction> records = consumer.poll(Duration.ofMillis(timeout));
                for (ConsumerRecord<String, Transaction> record : records) {
                    System.out.printf("Consumed message from topic = %s, partition = %d, offset = %d, key = %s, value = %s\n", record.topic(), record.partition(),
                            record.offset(), record.key(), record.value());
                }

            }
        }
    }


    public static void main(String[] args) {
        AvroConsumer consumer = new AvroConsumer();
        consumer.consumeMessages(100);
    }

}
