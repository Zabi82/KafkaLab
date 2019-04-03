package com.kafka.lab.helloworld.partial;

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
		
		//TODO: Create a KafkaProducer

		
		String key = "Key";
		String value = "Value";
		for(int i=0; i < numMessages; i++) {
			// TODO: Create the ProducerRecord

            // TODO: Write the record

            // TODO: Print the key and value
		}
		
		//TODO: Close the producer
		
	}
	
	public static void main(String[] args) {
		HelloProducer producer = new HelloProducer();
		producer.produceMessage(10);
	}
}