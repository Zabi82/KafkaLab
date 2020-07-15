package com.kafka.lab.stream.solution;

import java.math.BigDecimal;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Serialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;

/**
 * Streams data from daily_temperature_celsius, converts temperature to
 * farenheit and writes to daily_temperature_farenheit topic and then filters
 * temperatures greater than 90 farenheit to very_hot_days topic
 * 
 * @author User
 *
 */
public class StreamProcessor {

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
		KStream<Long, Integer> tempCelsius = builder.stream("daily_temperature_celsius");

		// Convert the values to farenheit
		KStream<Long, Double> tempFarenheit = tempCelsius.mapValues((k,v) -> new Double(v * (9/5) + 32));
		// Write the converted temperatures to another topic, use different value Serde
		tempFarenheit.to("daily_temperature_farenheit", Produced.valueSerde(Serdes.Double()));

		// Use filter() to get temperatures greater than 31 degree celsius
		KStream<Long, Integer> hot_days = tempCelsius
				.filter((k, v) -> new BigDecimal(v).compareTo(new BigDecimal(31)) > 0);

		// Write the hot temperatures to topic hot_days and print results
		hot_days.through("hot_days").foreach((k, v) -> {
			System.out.println(k.toString() + " - " + v.toString());
		});
		
		
		
		KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration);

		streams.start();
		// Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
		Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

	}

}
