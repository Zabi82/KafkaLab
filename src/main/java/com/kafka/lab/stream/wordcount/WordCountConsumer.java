package com.kafka.lab.stream.wordcount;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import static com.kafka.lab.stream.solution.TopicConstants.HOT_DAYS_TOPIC;
import static com.kafka.lab.stream.wordcount.WordCountProcessor.WORDCOUNT_OUTPUT_TOPIC;

public class WordCountConsumer {

	public void consumeMessages(int timeout) {

		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.LongDeserializer.class.getName());
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "word_test");

		// props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
		// StickyAssignor.class.getName());

		try (KafkaConsumer<String, Long> consumer = new KafkaConsumer<>(props)) {
			consumer.subscribe(Arrays.asList(WORDCOUNT_OUTPUT_TOPIC));

			while (true) {
				ConsumerRecords<String, Long> records = consumer.poll(Duration.ofMillis(timeout));
				for (ConsumerRecord<String, Long> record : records) {
					System.out.printf("partition = %d, offset = %d, key = %s, value = %s\n", record.partition(),
							record.offset(), record.key(), record.value());
				}
				// consumer.commitSync();
			}
		}
	}

	public static void main(String[] args) {
		WordCountConsumer consumer = new WordCountConsumer();
		consumer.consumeMessages(100);
	}

}
