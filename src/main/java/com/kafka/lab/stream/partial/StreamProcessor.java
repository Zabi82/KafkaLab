package com.kafka.lab.stream.partial;

import java.math.BigDecimal;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.ValueMapper;

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
		streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

		StreamsBuilder builder = new StreamsBuilder();

		// TODO Read the input Kafka Topic into a KStream instance.

		// TODO Create another stream by converting value into farenheit and store as
		// Double

		// TODO Write the converted temperatures to another topic, use different value
		// Serde for Double value

		// TODO Use filter() to get temperatures greater than 31 degree celsius and
		
		// TODO write the filtered stream to new topic hot_days
		

		
		//Create KafkaStreams object by calling builder.build()
		KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration);
		//start stream
		streams.start();
		// Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
		Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

	}

}
