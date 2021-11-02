### Exercise 10 - Kafka Streaming Example - Converting/Filtering Weather Data

Create the following topics with 1 partition and replication factor 1

```
daily_temperature_celsius
daily_temperature_farenheit
hot_days
```

Generate random weather data to a topic daily_temperature_celsius
(Run StreamProducer.java in com.kafka.lab.stream.solution package)

Using Kafka Streams, convert the celsius value to farenheit and write results to a new topic daily_temperature_farenheit (value should be Double)

Also filter all temperatures greater than 31 degree celsius and write to new topic hot_days

Refer to the partial java classes and the full solution in com.kafka.lab.stream.partial and com.kafka.lab.stream.soluton packages respectively

Write Consumers for the topics and inspect the data or stream the output topics and print results

### Exercise 11 - Kafka Streaming Example - Word Count Example

Run the SentenceDataProducer to produce some sentence every few seconds. WordCountProcessor streams this and performs a word count and outputs the results

### Exercise 12 - Kafka Streaming Example using Spring Cloud Stream

Simple use case of Producing and consuming of messages using Spring Cloud Stream. Clone the project https://github.com/Zabi82/SpringCloudStreamKafka for this exercise

### Exercise 13 - Kafka Streaming .Net example

Please refer to the repository https://github.com/Zabi82/KafkaStreamingDotNet/tree/master 
