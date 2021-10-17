package com.kafka.lab.helloworld.solution;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

public class HelloConsumerSeekOffset {

	public static final String HELLO_TOPIC = "hello_world_topic";


	public void updateOffset() {

		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "testgroup");
		

		

		try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
			consumer.subscribe(Arrays.asList(HELLO_TOPIC));

			boolean seekCompleted = false;
			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
				//partitions are assigned to a consumer only when some messages are returned in the poll
				//so a seek can be performed only on partitions assigned to this consumer
				if(!consumer.assignment().isEmpty() && !seekCompleted) {
					//perform seekToBeginning or seekToEnd or seek to an arbitary offset as per requirement
					//consumer.seekToBeginning(consumer.assignment());
					//consumer.seekToEnd(consumer.assignment());
					consumer.assignment().forEach(tp -> consumer.seek(consumer.assignment().iterator().next(), new OffsetAndMetadata(10, "no metadata")));
					seekCompleted = true;
					continue;
				}
				for (ConsumerRecord<String, String> record : records)	{
					System.out.printf("partition = %d, offset = %d, key = %s, value = %s\n", record.partition(),
							record.offset(), record.key(), record.value());
				}
			}



		}
	}




	public static void main(String[] args) {
		HelloConsumerSeekOffset consumer = new HelloConsumerSeekOffset();
		consumer.updateOffset();
	}

}


