package com.kafka.lab.helloworld.solution;

import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.requests.ProduceRequest;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.log4j.chainsaw.Main;

public class HelloProducer {
	
	public static final String HELLO_TOPIC = "hello_world_topic";
	
	public void produceMessage(int numMessages) {
		
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		
		
		KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(props);
		String key = "Key";
		String value = "Value";
		for(int i=0; i < numMessages; i++) {
			ProducerRecord<String,String> record = new ProducerRecord<>(HELLO_TOPIC, key + i, value+i);
			
			kafkaProducer.send(record);
			
			System.out.printf("Produced message with key = %s and value = %s \n",key + i, value+i);
		}
		
		kafkaProducer.close();
		
	}
	
	public static void main(String[] args) {
		HelloProducer producer = new HelloProducer();
		producer.produceMessage(10);
	}
}