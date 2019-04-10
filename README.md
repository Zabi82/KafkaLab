# KafkaLab

## Lab exercises will be using Kafka Client for Java

## Prerequisites for this lab as follows

### Install JDK

Download & Install JDK 8 (Update 31 or later) - https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

Set JAVA_HOME environment variable pointing to the JDK folder (Path variable to JAVA_HOME/bin should be setup as part of installation)

Verify java version -> java -version

### Install Java IDE

Download & Install Eclipse IDE (https://www.eclipse.org/downloads/) or your other favourite Java IDE (IntelliJ)

### Install Maven

Download & Install Maven (https://www-eu.apache.org/dist/maven/maven-3/3.6.0/binaries/apache-maven-3.6.0-bin.zip)

Set M2_HOME environment variable pointing to the Maven installation folder. Add path variable for M2_HOME/bin as well

Verify maven version -> mvn -version

### Install 7Zip

Download and install 7zip if you don't have it in your PC

### Download & Install Apache Kafka

Download Kafka 2.2.0 tgz file from https://www-us.apache.org/dist/kafka/2.2.0/kafka_2.12-2.2.0.tgz

Unzip the tgz file using 7zip or using tar -xzf kafka_2.12-2.2.0.tgz

#### Start Zookeeper

cd <kafka_installation_folder>/bin/windows    (For Linux cd <kafka_installation_folder>/bin)	
   
zookeeper-server-start.bat ../../config/zookeeper.properties (For Linux, use the .sh file from bin directory with correct relative path to config file)

#### Start Kafka Broker

cd <kafka_installation_folder>/bin/windows    (For Linux cd <kafka_installation_folder>/bin)

kafka-server-start.bat ../../config/server.properties (For Linux, use the .sh file from bin directory with correct relative path to config file)

### Exercise 1 - Kafka Command Line Producer & Consumer (Simple messages)

#### Command line producer 

Open a command prompt / shell and cd to <KAFKA_HOME>/bin/windows (<KAFKA_HOME>/bin in case of Linux) and type the below command

```

 kafka-console-producer --broker-list localhost:9092 --topic My_Topic

```

Type the messages to be sent to the topic in the prompt 

Here the topics are automatically created since it's not present already

#### Command line consumer 

Open another command prompt / shell and cd to <KAFKA_HOME>/bin/windows (<KAFKA_HOME>/bin in case of Linux) and type the below command

```

 kafka-console-consumer --bootstrap-server localhost:9092 --from-beginning --topic My_Topic

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

 kafka-console-producer --broker-list localhost:9092 --topic My_Topic_KV 
 --property parse.key=true --property key.separator=,

```

Type messages with keys and values separated by commas

```
> 1,Java
> 2,.Net
> 3,Python
> 4,Go

```

Run another command line to consumer the key value pairs produced by the producer

```
kafka-console-consumer --bootstrap-server localhost:9092 --from-beginning 
--topic My_Topic_KV --property print.key=true

```

Observe the messages being displayed with key and value. Without the --property print.key=true , only the value will get printed

### Exercise 3 - Creating topics with Partitions & Replication factor and see ordering in consumer side

Create a topic manually using command line tool with 2 partitions

```

kafka-topics --zookeeper localhost:2181 --create --topic My_Topic_P2 --partitions 2 --replication-factor 1

```
Run a command line producer for the above topic and type messages from 1 to 10

```
kafka-console-producer --broker-list localhost:9092 --topic My_Topic_P2

>1
>2
>3
.
.
>10

```

Run a command line consumer to consume above messages

```

kafka-console-consumer --bootstrap-server localhost:9092 --from-beginning --topic My_Topic_P2
 
```

Observe that the messages are not consumed in the same order in which it is produced. This is because the consumer consumes set of messages 
from each of the 2 partitions of the topic and ordering is guaranteed only within a partition

Try to consume from 3 command line consumer windows with same consumer group id and observe that only two receives data as we have only two partitions

```

kafka-console-consumer --bootstrap-server localhost:9092 --from-beginning --topic My_Topic_P2 --group MyGroup
 
```

The 3rd consumer within the consumer group won't be receiving messages.
Try to stop the 1st or 2nd consumer window and observe that now the 3rd consumer is assigned the partition earlier read by the stopped consumer



### Exercise 4 - Describe details of a topics

Describe the details of the topic created in the previous exercises

```

kafka-topics --describe --bootstrap-server localhost:9092 --topic My_Topic_P2

```
Partition 0 & 1 represents the 2 partitions

Leader 0 indicates the leader for both the partitions are present in the broker with id 0( Refer server.properties , broker.id = 0) 

Replication 0 indicates the replica for the parition also resides in the broker with id 0

### Exercise 5 - Zookeeper Shell

Kafka’s data in ZooKeeper can be accessed using the zookeeper-shell command:

```
zookeeper-shell localhost:2181

```

From within the zookeeper-shell application, type ls / to view the directory structure in ZooKeeper. 

```
ls /

	[schema_registry, cluster, controller_epoch, controller, brokers, zookeeper, admin,
	isr_change_notification, consumers, config]

ls /brokers

    [ids, topics, seqid]
	
ls /brokers/ids
   
    [0]
	
ls /brokers/topics

    [My_Topic, __consumer_offsets, My_Topic_KV]	

```

__consumer_offsets is a special topic which keeps tracks of the consumer offsets for different topics/partitions

Press Ctrl + C to exit the shell

### Exercise 6 - Simple Producer & Consumer using Kafka Java Client

Download / clone this project 

Import the project into Eclipse or your favourite IDE

Create a new topic "hello_world_topic" with 2 partitions and replication factor 1 

Write a java producer and consumer programs to produce and consume from the above topic. Refer to the partial java classes and the full solution 
under com.kafka.lab.helloworld.partial and com.kafka.lab.helloworld.solution packages respectively

Try to run the consumer with different consumer group id and observe that the new consumer gets a complete copy of the messages

Stop the consumer and run the producer once again to inject new messages in the topic. Before starting the consumer again run below command 
to check the offsets for each partition and the lag for the consumer

```

kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group testgroup

```
 
Now run the consumer again and recheck the above command and notice that the lag is now 0 in both partitions 
as all messages are consumed


### Exercise 7 - Kafka Streaming Example

Load sample weather data from resources/weather-data.csv (key value pair of date and temperature in celsius) to a topic daily_temperature_celsius
(Run StreamProducer.java in com.kafka.lab.stream.solution package)

Using Kafka Streams, convert the celsius value to farenheit and write results to a new topic daily_temperature_farenheit (value should be Double)

Also filter all temperatures greater than 31 degree celsius and write to new topic hot_days 

Refer to the partial java classes and the full solution in com.kafka.lab.stream.partial and com.kafka.lab.stream.soluton packages respectively

Write Consumers for the topics and inspect the data

### DIY Exercises

1) Write a consumer which consumes all messages from a Topic whenever it is started

2) Setup a 3 node cluster within your local machine and verify your understanding of replication

3) Implement a CustomPartitioner in Producer side to implement a custom logic for allocating partitions for a produced message.

4) Implement a Producer & Consumer which uses Avro format and Schema Registry

5) Try out different connectors to transfer message to and fro from a Kafka topic to Database table, File, Queues etc

6) Try out the REST API (Proxy) to produce and consume messages

7) Perform aggregation and windowing functions using KTable & KStreams 

8) Try out KSQL streams and windowing