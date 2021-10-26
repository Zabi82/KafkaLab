package com.kafka.lab.stream.wordcount;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.Random;

import static com.kafka.lab.stream.solution.TopicConstants.DAILY_TEMP_CELSIUS_TOPIC;
import static com.kafka.lab.stream.wordcount.WordCountProcessor.INPUT_TEXT_TOPIC;

/**
 * Produces random stream of daily temperatures in celcius every few seconds
 * @author zabeer
 *
 */
public class SentenceDataProducer {

	public static String[] sentences = new String[]{"Kafka is a distributed middleware infrastructure", " Kafka has a producer and consumer API",
													"Confluent Kafka provides enterprise support and provides additional components",
													"Stream API internally uses Kafka client API for producer and consumer",
													"Kafka streaming support at least once and exactly once processing"};

	public static final Integer MIN_INDEX = 0;
	public static final Integer MAX_INDEX = sentences.length - 1;

	public static final int TWO_SECONDS = 2000;

	public static void main(String[] args) throws Exception {

		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

		try (KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(props)) {

			Long counter = 1l;
			while (true) {

				Integer index = getRandomIndex();
				ProducerRecord<String, String> record = new ProducerRecord<>(INPUT_TEXT_TOPIC, counter.toString(),
						sentences[getRandomIndex()]);

				kafkaProducer.send(record);

				System.out.printf("Produced message with key = %s and value = %s \n", counter, record.value());
				counter++;
				
				//sleep 2 sec
				Thread.sleep(TWO_SECONDS);
			}

		}

	}

	private static Integer getRandomIndex() {
		Random random = new Random();
		return random.ints(MIN_INDEX, (MAX_INDEX + 1)).findFirst().getAsInt();
	}



}
