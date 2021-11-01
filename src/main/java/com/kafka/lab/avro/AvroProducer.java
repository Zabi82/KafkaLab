package com.kafka.lab.avro;

import com.kafka.lab.model.Transaction;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

public class AvroProducer {

	public static final String TXN_TOPIC_AVRO = "txn_topic_avro";

	public static long MIN_AMOUNT = 5;
	public static long MAX_AMOUNT = 100000;

	public void produceMessage(int numMessages) {

		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, io.confluent.kafka.serializers.KafkaAvroSerializer.class);
		props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");

		KafkaProducer<String, Transaction> kafkaProducer = new KafkaProducer<>(props);

		for (int i = 0; i < numMessages; i++) {
			String accountNumber =  String.format("%1$" + 10 + "s", i).replace(' ', '0');
			Transaction txn = Transaction.newBuilder().setId("Txn" + i)
					.setAccountNumber(accountNumber)
					.setDebitOrCredit(i % 2 == 0 ? "Credit" : "Debit")
					.setAmount(getRandomAmount() * (i % 2 == 0 ? 1 : -1))
					.setDate(System.currentTimeMillis()).build();
			ProducerRecord<String, Transaction> record = new ProducerRecord<>(TXN_TOPIC_AVRO, accountNumber, txn);
			//call back handler lambda for to evaluate the result of sending a record
			kafkaProducer.send(record, (recordMetaData, exception) -> {
				if (recordMetaData != null) {
					System.out.printf("Produced message with key = %s and value = %s in topic = %s and partition = %s with offset = %s \n",
							accountNumber, txn, recordMetaData.topic(), recordMetaData.partition(), recordMetaData.offset());
				}
				else if(exception != null) {
					System.out.println("Exception occured " + exception);
					exception.printStackTrace();
				}
			});

		}

		kafkaProducer.close();

	}

	private double getRandomAmount() {
		return Math.round(new Random().doubles(MIN_AMOUNT, (MAX_AMOUNT + 1)).findFirst().getAsDouble());
	}

	public static void main(String[] args) {
		AvroProducer producer = new AvroProducer();
		producer.produceMessage(15);
	}
}