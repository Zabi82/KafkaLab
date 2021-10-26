package com.kafka.lab.stream.solution;

import java.math.BigDecimal;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;

import static com.kafka.lab.stream.solution.TopicConstants.DAILY_TEMP_CELSIUS_TOPIC;
import static com.kafka.lab.stream.solution.TopicConstants.HOT_DAYS_TOPIC;

/**
 * Streams data from daily_temperature_celsius, converts temperature to
 * farenheit and writes to daily_temperature_farenheit topic and then filters
 * temperatures greater than 90 farenheit to very_hot_days topic
 * 
 * @author User
 *
 */
public class StreamProcessor {

	public static final BigDecimal HOT_TEMP = new BigDecimal(31);


	public static void main(String[] args) throws Exception {

		Properties streamsConfiguration = new Properties();

		// Give the Streams application a unique name. The name must be unique in the
		// Kafka cluster
		streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-exercise");

		streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		streamsConfiguration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		// Specify default (de)serializers for record keys and for record values.
		streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Long().getClass().getName());
		streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Integer().getClass().getName());

		StreamsBuilder builder = new StreamsBuilder();

		// Read the input Kafka Topic into a KStream instance.
		KStream<Long, Integer> tempCelsius = builder.stream(DAILY_TEMP_CELSIUS_TOPIC);

		// Convert the values to farenheit
		KStream<Long, Double> tempFarenheit = tempCelsius.mapValues((k,v) -> Double.valueOf(v * (9/5) + 32));
		// Write the converted temperatures to another topic, use different value Serde
		tempFarenheit.to("daily_temperature_farenheit", Produced.valueSerde(Serdes.Double()));

		tempFarenheit.foreach((k, v) -> {
			System.out.println(String.format("Temperature in farenheit topic key %s and value %s",k.toString() , v.toString() ));
		});

		// Use filter() to get temperatures greater than 31 degree celsius and write to hot_days topic
		tempCelsius.filter((k, v) -> new BigDecimal(v).compareTo(HOT_TEMP) > 0).to(HOT_DAYS_TOPIC);

		KStream<Long, Integer> hotDaysStream = builder.stream(HOT_DAYS_TOPIC);

		//Another way to print the stream contents
		//hotDaysStream.print(Printed.toSysOut());
		hotDaysStream.foreach((k, v) -> {
			System.out.println(String.format("Hot temperature days topic key %s and value %s",k.toString() , v.toString() ));
		});
		
		
		
		KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration);

		streams.start();
		// Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
		Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

	}

}
