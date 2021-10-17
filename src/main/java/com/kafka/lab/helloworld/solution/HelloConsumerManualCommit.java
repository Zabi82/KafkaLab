package com.kafka.lab.helloworld.solution;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class HelloConsumerManualCommit {

    public static final String HELLO_TOPIC = "hello_world_topic";


    public void consumeMessages(int timeout) {

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "testgroup");

        //props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, StickyAssignor.class.getName());


        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Arrays.asList(HELLO_TOPIC));

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(timeout));
                if(!records.isEmpty()) {
					for (ConsumerRecord<String, String> record : records) {
						System.out.printf("partition = %d, offset = %d, key = %s, value = %s\n", record.partition(),
								record.offset(), record.key(), record.value());
					}

					//synchronous commit which waits and performs retry in case of failure
					//consumer.commitSync();

					//async commit
					consumer.commitAsync((map, ex) -> {
						if (ex != null) {
							throw new RuntimeException("Commit Failed", ex);
						} else {
							map.entrySet().stream().forEach(e -> System.out.printf("Offset committed for topic %s and partition %s ==> %s \n", e.getKey().topic(), e.getKey().partition(), e.getValue().offset()));
						}
					});
				}


            }
        }
    }


    public static void main(String[] args) {
        HelloConsumerManualCommit consumer = new HelloConsumerManualCommit();
        consumer.consumeMessages(100);
    }

}
