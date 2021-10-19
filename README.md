# KafkaLab

## Lab exercises will be using Kafka Client for Java

## Prerequisites for this lab as follows (unless a pre-defined lab setup is already provided)

### OS for the lab

Linux distribution (e.g. Ubuntu) preferred. Can manage with Windows as well

### Install JDK 11

Download & Install JDK 11 preferably AdoptOpenJDK  - https://adoptopenjdk.net/installation.html

Set JAVA_HOME environment variable pointing to the JDK folder (Path variable to JAVA_HOME/bin should be setup as part of installation)

Verify java version -> java -version

### Install Java IDE

Download & Install Intellij (https://www.jetbrains.com/idea/download) or Eclipse IDE (https://www.eclipse.org/downloads/packages/) or your other favourite Java IDE

### Install Maven

Download & Install Maven (https://maven.apache.org/install.html)

Set M2_HOME environment variable pointing to the Maven installation folder. Add path variable for M2_HOME/bin as well

Verify maven version -> mvn -version

### Install 7Zip if using windows

Download and install 7zip if you don't have it in your PC

### Download & Install Confluent Kafka

Download Confluent Kafka (community edition) version 6.2.1 (http://packages.confluent.io/archive/6.2/confluent-community-6.2.0.tar.gz)

Unzip/ the tgz file using 7zip or using tar -xvf confluent-community-6.2.0.tar.gz

#### Start Zookeeper (Training Lab still uses zookeeper instead of Kafka's inbuilt cluster coordinator Raft as it's still production ready)

cd <confluent_kafka_installation_folder>/bin

nohup ./zookeeper-server-start ../etc/kafka/zookeeper.properties &

For windows , cd <confluent_kafka_installation_folder>/bin/windows

zookeeper-server-start.bat ..\..\etc\zookeeper.properties)

#### Start Kafka Broker

cd <confluent_kafka_installation_folder>/bin

nohup ./kafka-server-start ../etc/kafka/server.properties &

For windows, cd <kafka_installation_folder>/bin/windows    

kafka-server-start.bat ..\..\etc\kafka\server.properties 

### Exercise 1 - Kafka Command Line Producer & Consumer (Simple messages)

#### Command line producer 

Open a command prompt / shell and cd to <KAFKA_HOME>/bin (<KAFKA_HOME>\bin\windows in case of Windows) and type the below command

```

 ./kafka-console-producer --bootstrap-server localhost:9092 --topic My_Topic

```

Type the messages to be sent to the topic in the prompt 

Here the topics are automatically created since it's not present already

#### Command line consumer 

Open another command prompt / shell and cd to <KAFKA_HOME>/bin/windows (<KAFKA_HOME>/bin in case of Linux) and type the below command

```

 ./kafka-console-consumer --bootstrap-server localhost:9092 --from-beginning --topic My_Topic

```

Observe that the messages typed in producer console are being consumed by the consumer

Type more messages in the producer console and observe that being consumed by the consumer

Stop and re-run the consumer and observe that it consumes messages from beginning

Also try to stop the consumer again and now re-run the consumer without the --from-beginning option 
and observe that it doesn't consume anything unless you type new messages in the producer prompt

### Exercise 2 - Kafka Command Line Producer & Consumer (Messages with key and values)

By default, kafka-console-producer and kafka-console-consumer assumes null keys for the messages. 
It is possible to write and read with keys as well as values. Re-run the Producer with additional arguments to write (key,value) pairs to the Topic

Run a command line producer to inject messages with key and values as given below

```

 ./kafka-console-producer --bootstrap-server localhost:9092 --topic My_Topic_KV \
 --property parse.key=true --property key.separator=,

```

Type messages with keys and values separated by commas

```
> 1,Java
> 2,.Net
> 3,Python
> 4,Go

```

Run another command line consumer to consume the key value pairs produced by the producer

```
./kafka-console-consumer --bootstrap-server localhost:9092 --from-beginning \
--topic My_Topic_KV --property print.key=true

```

Observe the messages being displayed with key and value. Without the --property print.key=true , only the value will get printed

### Exercise 3 - Creating topics with Partitions & Replication factor and see ordering in consumer side

Create a topic manually using command line tool with 2 partitions

```

./kafka-topics --bootstrap-server localhost:9092 --create --topic My_Topic_P2 --partitions 2 --replication-factor 1

```
Run a command line producer for the above topic and type messages from 1 to 10

```
./kafka-console-producer --bootstrap-server localhost:9092 --topic My_Topic_P2

>1
>2
>3
.
.
>10

```

Run a command line consumer to consume above messages

```

./kafka-console-consumer --bootstrap-server localhost:9092 --from-beginning --topic My_Topic_P2 --property print.partition=true --property print.offset=true
 
```

Observe that the messages are not consumed in the same order in which it is produced. This is because the consumer consumes set of messages 
from each of the 2 partitions of the topic and ordering is guaranteed only within a partition

Also try to produce new messages from producer console one by one and since there is no keys provided it will mostly get assigned to random \
in a round-robin fashion. if you produce messages faster messages might be batched together and an entire batch might land in the same partition

Try to consume from 3 command line consumer windows with same consumer group id and observe that only two receives data as we have only two partitions

```

./kafka-console-consumer --bootstrap-server localhost:9092 --from-beginning --topic My_Topic_P2 --group MyGroup --property print.partition=true --property print.offset=true
 
```

One of the consumer within the consumer group won't be receiving messages and is found idle.
Try to stop the one of the active consumer and observe that now that the earlier idle consumer is assigned the partition earlier read by the stopped consumer

### Exercise 4 - List Consumer Groups, Describe Consumer group to check lag,  Resetting offset

List the different consumer groups in the kafka cluster

```

./kafka-consumer-groups --bootstrap-server localhost:9092 --list

```

Describe consumer group to see the different topics used with that group, partitions and consumer members and their current offset , lag etc

```

./kafka-consumer-groups --bootstrap-server localhost:9092 --group MyGroup --describe

```

Reset offset for a particular topic (or topic partition) on a consumer group

-Dry Run option
```

./kafka-consumer-groups --bootstrap-server localhost:9092 --group MyGroup --topic My_Topic_P2 --reset-offsets --to-earliest --dry-run

```

-Execute option
```

./kafka-consumer-groups --bootstrap-server localhost:9092 --group MyGroup --topic My_Topic_P2 --reset-offsets --to-earliest --execute

```
After the reset, check the consumer is able to consume again the already consumed messages

There are other options available based on reset requirement

    --to-earliest: Reset to the beginning of the data.

    --to-lastest: Reset to the end of the topic.

    --to-offset: Reset to a known, fixed offset.

    --shift-by: Perform relative changes using the given integer. Use positive values to skip forward or negative values to move backwards.

    --to-datetime <YYYY-MM-DDTHH:mm:SS.sss>: Reset to the given timestamp.

    --topic <topicname>:<partition>: Apply the change to a specific partition, for example --topic test-topic:0. By default, the --topic argument applies to all partitions.

### Exercise 5 - Describe details of a topics

Describe the details of the topic created in the previous exercises

```

./kafka-topics --describe --bootstrap-server localhost:9092 --topic My_Topic_P2

```
Partition 0 & 1 represents the 2 partitions

Leader 0 indicates the leader for both the partitions are present in the broker with id 0( Refer server.properties , broker.id = 0). 
After all we have only 1 broker 

Replication 0 indicates the replica for the partition also resides in the broker with id 0 which is in fact the leader partition itself

Replication factor cannot be greater than the number of brokers. Replication factor indicates the number of copies of data in a partition including leader & followers

### Exercise 6 - Simple Producer & Consumer using Kafka Java Client

Download / clone this project 

Import the project into Intellij or Eclipse or any of your other favourite IDE

Create a new topic "hello_world_topic" with 2 partitions and replication factor 1 

```

./kafka-topics --bootstrap-server localhost:9092 --create --topic hello_world_topic --partitions 2 --replication-factor 1

```


Write a java producer and consumer programs to produce and consume from the above topic. Refer to java classes namely HelloProducer 
and HelloConsumer which has both a partial and  the full solution under com.kafka.lab.helloworld.partial and com.kafka.lab.helloworld.solution packages
respectively

Try to run the consumer with different consumer group id and observe that the new consumer gets a complete copy of the messages

Stop the consumer and run the producer once again to inject new messages in the topic. Before starting the consumer again run below command 
to check the offsets for each partition and the lag for the consumer

```

./kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group test-group

```
 
Now run the consumer again and recheck the above command and notice that the lag is now 0 in both partitions 
as all messages are consumed


### Exercise 7 - Understanding different producer and consumer configuration options

1) seeking to beginning offset, end offset or specific offsets - Refer HelloConsumerSeekOffset
2) committing offsets manually using commitySync and commitAsync methods - Refer HelloConsumerManualCommit
3) Custom Partitioner in producer - Refer MyCustomPartitioner and HelloProducerWithCustomPartitioner
4) Re-balancing Listener - Refer HelloConsumerCommitOnRebalance


### Exercise 8 - Kafka .Net client example

Please refer to the github repository https://github.com/Zabi82/KafkaDotNet for a .Net example of producer and consumer

### Exercise 9 - Producing & Consuming from Kafka using Spring Boot

Simple use case of Producing and consuming of messages using Spring Boot. Clone the project https://github.com/Zabi82/KafkaLabBoot for this exercise

### Exercise 10 - Kafka Streaming Example

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

Write Consumers for the topics and inspect the data


### Exercise 11 - Kafka Streaming Example using Spring Cloud Stream
TBD

### Exercise 12 - Kafka Connect 

TBD

### Exercise 13 - KSQL DB exercises

TBD


### DIY Exercises

1) Write a consumer which consumes all messages from a Topic whenever it is started

2) Setup a 3 node cluster within your local machine and verify your understanding of replication

3) Implement a CustomPartitioner in Producer side to implement a custom logic for allocating partitions for a produced message.

4) Implement a Producer & Consumer which uses Avro format and Schema Registry

5) Try out different connectors to transfer message to and fro from a Kafka topic to Database table, File, Queues etc

6) Try out the REST API (Proxy) to produce and consume messages

7) Perform aggregation and windowing functions using KTable & KStreams 

8) Try out KSQL streams and windowing
