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
	
	
### Exercises/Lab

Download / clone this project 

Import the project into Eclipse or your favourite IDE

Perform a maven install from Eclipse or from Command prompt (mvn clean install) to download the maven dependencies (enables to work offline)








 
