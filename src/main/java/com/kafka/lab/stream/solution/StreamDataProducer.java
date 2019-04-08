package com.kafka.lab.stream.solution;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class StreamDataProducer {
	
	public static void main(String[] args) throws Exception {
		List<String> lines = Files.readAllLines(
			    Paths.get(StreamDataProducer.class.getResource("/weather-data.csv").toURI()), Charset.defaultCharset());
		
		
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		
		
		KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(props);
		
		for(int i=0; i < lines.size(); i++) {
			String[] tokens = lines.get(i).split(",");
			ProducerRecord<String,String> record = new ProducerRecord<>("daily_temperature_celsius", tokens[0], tokens[1]);
			
			kafkaProducer.send(record);
			
			System.out.printf("Produced message with key = %s and value = %s \n",tokens[0], tokens[1]);
		}
		
		kafkaProducer.close();
		
				
	}

}
