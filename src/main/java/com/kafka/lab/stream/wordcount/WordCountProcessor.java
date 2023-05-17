package com.kafka.lab.stream.wordcount;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;

import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;

public class WordCountProcessor {

    public static final String INPUT_TEXT_TOPIC = "input_text_topic";
    public static final String WORDCOUNT_OUTPUT_TOPIC = "word_count_output_topic";

    public static void main(String[] args) {

        Properties streamsConfiguration = new Properties();

        // Give the Streams application a unique name. The name must be unique in the
        // Kafka cluster
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount");

        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        streamsConfiguration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // Specify default (de)serializers for record keys and for record values.
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        StreamsBuilder builder = new StreamsBuilder();

        final Serde<String> stringSerde = Serdes.String();
        final Serde<Long> longSerde = Serdes.Long();

        final Pattern pattern = Pattern.compile("\\W+");
        KStream<String, String> textLines = builder.stream(INPUT_TEXT_TOPIC, Consumed.with(stringSerde, stringSerde));

        KTable<String, Long> wordCounts = textLines
                .flatMapValues(value -> Arrays.asList(pattern.split(value.toLowerCase())))
                .map((key, value) -> new KeyValue<>(value, value))
                .filter((key, value) -> (!value.equals("the")))
                .groupBy((key, value) -> value)
                // Count the occurrences of each word (message key).
                .count();

        wordCounts.toStream().to(WORDCOUNT_OUTPUT_TOPIC, Produced.with(stringSerde, longSerde));

        KStream<String, Long> wordCountStream = builder.stream(WORDCOUNT_OUTPUT_TOPIC, Consumed.with(stringSerde, longSerde));

        wordCountStream.foreach(new ForeachAction<String, Long>() {
            public void apply(String key, Long value) {
                System.out.println(key + ": " + value);
            }
        });



//        wordCountStream.foreach((k,v) -> {
//            System.out.println("Word -> " + k + " count : " + v);
//        });

        KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration);

        streams.start();
        // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
