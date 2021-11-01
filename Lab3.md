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

TBD
