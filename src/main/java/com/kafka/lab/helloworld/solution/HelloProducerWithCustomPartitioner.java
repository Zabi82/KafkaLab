package com.kafka.lab.helloworld.solution;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class HelloProducerWithCustomPartitioner {

    public static final String HELLO_TOPIC = "hello_world_topic";

    public void produceMessage(int numMessages) {

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, "com.kafka.lab.helloworld.solution.MyCustomPartitioner");

        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(props);

        for (int i = 0; i < numMessages; i++) {
            String key =  "Key" + i;
            String value = "Value" + i;
            ProducerRecord<String, String> record = new ProducerRecord<>(HELLO_TOPIC, key, value);
            //call back handler lambda for to evaluate the result of sending a record
            kafkaProducer.send(record, (recordMetaData, exception) -> {
                if (recordMetaData != null) {
                    System.out.printf("Produced message with key = %s and value = %s in topic = %s and partition = %s with offset = %s \n",
                            key, value, recordMetaData.topic(), recordMetaData.partition(), recordMetaData.offset());
                }
                else if(exception != null) {
                    System.out.println("Exception occured " + exception);
                    exception.printStackTrace();
                }
            });

        }

        kafkaProducer.close();

    }

    public static void main(String[] args) {
        HelloProducerWithCustomPartitioner producer = new HelloProducerWithCustomPartitioner();
        producer.produceMessage(10);
    }
}



