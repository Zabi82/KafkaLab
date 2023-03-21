package com.kafka.lab.helloworld.solution;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.*;

public class HelloConsumerCommitOnRebalance {

    public static final String HELLO_TOPIC = "hello_world_topic";

    private Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    KafkaConsumer<String, String> consumer;

    private class RebalanceHandler implements ConsumerRebalanceListener {
        public void onPartitionsAssigned(Collection<TopicPartition>
                                                 partitions) {
			System.out.println("following topic partitions assigned as part of rebalance");
			partitions.forEach( tp -> System.out.printf("Topic %s , partition %s \n", tp.topic(), tp.partition()));
        }

        public void onPartitionsRevoked(Collection<TopicPartition>
                                                partitions) {
            System.out.println("Lost partitions in rebalance, so committing current  offsets:" + currentOffsets);
            consumer.commitSync(currentOffsets);
        }
    }

    public void initConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group-rebalance");



        consumer = new KafkaConsumer<>(props);


    }

    public void consumeMessages(int timeout) {


        try {
            consumer.subscribe(Arrays.asList(HELLO_TOPIC), new RebalanceHandler());

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(timeout));
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("Consumed message from topic = %s, partition = %d, offset = %d, key = %s, value = %s\n", record.topic(), record.partition(),
                            record.offset(), record.key(), record.value());
                    currentOffsets.put(new TopicPartition(record.topic(),
                            record.partition()), new
                            OffsetAndMetadata(record.offset() + 1, "no metadata"));
                }
                consumer.commitAsync(currentOffsets, null);

            }
        } finally {
            try {
                consumer.commitSync(currentOffsets);
            } finally {
                consumer.close();
                System.out.println("Closed consumer and we are done");
            }
        }

    }


    public static void main(String[] args) {
        HelloConsumerCommitOnRebalance consumer = new HelloConsumerCommitOnRebalance();
        //initialize consumer
        consumer.initConsumer();
        consumer.consumeMessages(100);
    }

}
