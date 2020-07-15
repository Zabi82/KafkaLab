package com.kafka.lab.stream.solution;

import java.util.Properties;
import java.util.Random;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.LongSerializer;
/**
 * Produces random stream of daily temperatures in celcius every few seconds
 * @author zabeer
 *
 */
public class StreamDataProducer {

	public static final Integer MIN_TEMP = 25;
	public static final Integer MAX_TEMP = 35;

	public static void main(String[] args) throws Exception {

		Random random = new Random();
		System.out.println(random.ints(MIN_TEMP, (MAX_TEMP + 1)).findFirst().getAsInt());
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);

		try (KafkaProducer<Long, Integer> kafkaProducer = new KafkaProducer<>(props)) {

			Long counter = 1l;
			while (true) {

				Integer temp = getRandomTemp();
				ProducerRecord<Long, Integer> record = new ProducerRecord<>("daily_temperature_celsius", counter,
						getRandomTemp());

				kafkaProducer.send(record);

				System.out.printf("Produced message with key = %s and value = %s \n", counter, temp);
				counter++;
				
				//sleep 2 sec
				Thread.sleep(2000);
			}

		}

	}

	private static Integer getRandomTemp() {
		Random random = new Random();
		return random.ints(MIN_TEMP, (MAX_TEMP + 1)).findFirst().getAsInt();
	}

}
