package com.kafka.lab.helloworld.solution;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.StickyAssignor;
import org.apache.kafka.common.serialization.StringDeserializer;

public class HelloConsumer {

	public static final String HELLO_TOPIC = "hello_world_topic";


	public void consumeMessages(int timeout) {

		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
		
		//props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, StickyAssignor.class.getName());
		
		

		try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
			consumer.subscribe(Arrays.asList(HELLO_TOPIC));

			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(timeout));
				for (ConsumerRecord<String, String> record : records)	{
					System.out.printf("partition = %d, offset = %d, key = %s, value = %s\n", record.partition(),
							record.offset(), record.key(), record.value());
				}

			}
		}
	}
	
	

	public static void main(String[] args) {
		HelloConsumer consumer = new HelloConsumer();
		consumer.consumeMessages(100);
	}

}
