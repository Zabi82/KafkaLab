### Exercise 12 - Schema Registry / Avro

Ensure the following properties are present and uncommented in <confluent_kafka_installation_folder>/etc/schema-registry

```
listeners=http://0.0.0.0:8081
kafkastore.bootstrap.servers=PLAINTEXT://localhost:9092
kafkastore.topic=_schemas
```
Now start the schema registry using below command from <confluent_kafka_installation_folder>/bin directory

```
nohup ./schema-registry-start ../etc/schema-registry/schema-registry.properties &

```

Verify Schema Registry by checking the URL http://localhost:8081/subjects using curl or postman

Now refer to the AvroProducer and AvroConsumer java classes in com.kafka.lab.avro package and run the producer and consumer.
This is a simple producer and consumer but uses Avro as the format for the Value using appropriate Serializer & Deserializer
The Avro file is present in src/main/resources/avro/transaction.avsc. The java model for this will be generated using avro maven plugin while doing a mvn install
Upon running the AvroProducer the schema will be registered in Schema Registry as it's not already present.
The schema can be retrieved by performing a Http request using postman or curl with the URL http://localhost:8081/subjects/txn_topic_avro-value/versions/1

### Exercise 13 - KSQL DB exercises

Ensure the following properties are present and uncommented in <confluent_kafka_installation_folder>/etc/ksqldb

```
listeners=http://0.0.0.0:8088
bootstrap.servers=localhost:9092
ksql.schema.registry.url=http://localhost:8081
```
Now start the KSQL DB server using below command from <confluent_kafka_installation_folder>/bin directory

```

nohup ./ksql-server-start ../etc/ksqldb/ksql-server.properties &

```

Now start the KSQL DB CLI using below command from <confluent_kafka_installation_folder>/bin directory

```

./ksql http://localhost:8088

```
Execute the below command to display all the topics and to check if KSQL CLI is communicating with KSQL DB server

```

show topics;

```

Now create a stream from the topic used in the previous exercise txn_topic_avro

```

SET 'auto.offset.reset'='earliest';


CREATE STREAM txn_stream (accountNumber VARCHAR, id VARCHAR, amount DOUBLE, debitOrCredit VARCHAR, date BIGINT)
WITH (kafka_topic='txn_topic_avro', value_format='AVRO', partitions=1);

```

select from the stream and view the data.


```

select * from txn_stream emit changes;

```
Now run the AvroProducer from previous exercise once again and see the data updating in the select output.
Control-C to terminate the query

Now create a table from the above stream by performing a sum(amount) for each accountNumber

```
CREATE TABLE account_balance AS
SELECT accountNumber, sum(amount) as totalAmount
FROM txn_stream
GROUP BY accountNumber;

```
select from the table and view the data (push query).

```

select * from account_balance emit changes;

```

Run a pull query on the table to get negative balance accounts
```
SET 'ksql.query.pull.table.scan.enabled'='true';
SELECT * from account_balance where totalAmount < 0 ;

```
### Exercise 14 - Kafka Connect

Pre-requisites:

Install SQLite Database. For ubuntu follow the instructions below to install SQLite and verify the installation (unless it's already present in the lab setup)
```
sudo apt-get update
sudo apt-get install sqlite3
sqlite3 --version
```

Download Kafka JDBC connectors

Go to https://www.confluent.io/hub/confluentinc/kafka-connect-jdbc?_ga=2.245271109.1148558584.1635854760-884231590.1623215623 and click download

Unzip and copy the directory "confluentinc-kafka-connect-jdbc-10.2.5" under <confluent_kafka_installation_folder>/etc

Update the "plugin.path" property in <confluent_kafka_installation_folder>/etc/schema-registry/connect-avro-standalone.properties.
Substitute <confluent_kafka_installation_folder> with actual path of installation in your environment

```
plugin.path=<confluent_kafka_installation_folder>/etc/confluentinc-kafka-connect-jdbc-10.2.5

```

Open a new terminal window and Launch SQLite and Create a new table and insert few records in sqlite. Launch this from HOME directory (e.g. /home/ubuntu)
Create a table and insert rows

```

sqlite3 test.db

CREATE TABLE contacts (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	first_name TEXT NOT NULL,
	last_name TEXT NOT NULL,
	email TEXT NOT NULL ,
	phone TEXT NOT NULL 
);

insert into contacts (first_name, last_name, email, phone) values ('Mark' , 'Taylor', 'mark.taylor@nomail.com', '91098890991');
insert into contacts (first_name, last_name, email, phone) values ('Tom' , 'Wood', 'tom.wood@nomail.com', '917878787');

```

Now update the "connection.url" property in <confluent_kafka_installation_folder>/etc/confluentinc-kafka-connect-jdbc-10.2.5/etc/source-quickstart-sqlite.properties
Ensure the path to test.db is as per how it was created in previous step

```
connection.url=jdbc:sqlite:/home/ubuntu/test.db
```

Now launch the connector in standalone mode from <confluent_kafka_installation_folder>/bin directory

```
./connect-standalone ../etc/schema-registry/connect-avro-standalone.properties ../etc/confluentinc-kafka-connect-jdbc-10.2.5/etc/source-quickstart-sqlite.properties

```

Verify that the data from the sqlite table are pushed to a kafka topic. in this example the topic would be "test-sqlite-jdbc-contacts". Verify using a command line or a java consumer
Verify that a new avro schema is registered too using postman or curl "http://localhost:8081/subjects/test-sqlite-jdbc-contacts-value/versions/1"


