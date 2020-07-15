package com.kafka.lab.stream.solution;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.fasterxml.jackson.databind.deser.std.NumberDeserializers.IntegerDeserializer;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers.LongDeserializer;

public class HotDaysConsumer {

	public void consumeMessages(int timeout) {

		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.LongDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.IntegerDeserializer.class.getName());
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "hot_test");

		// props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
		// StickyAssignor.class.getName());

		try (KafkaConsumer<Long, Integer> consumer = new KafkaConsumer<>(props)) {
			consumer.subscribe(Arrays.asList("hot_days"));

			while (true) {
				ConsumerRecords<Long, Integer> records = consumer.poll(Duration.ofMillis(timeout));
				for (ConsumerRecord<Long, Integer> record : records) {
					System.out.printf("partition = %d, offset = %d, key = %s, value = %s\n", record.partition(),
							record.offset(), record.key(), record.value());
				}
				// consumer.commitSync();
			}
		}
	}

	public static void main(String[] args) {
		HotDaysConsumer consumer = new HotDaysConsumer();
		consumer.consumeMessages(100);
	}

}
