# Kafka

The following assumes that you have Kafka installed on your local machine under ~/code/kafka_VERSION_STRING.

## Startup zookeeper:

```bash
cd ~/code/kafka_2.11-0.10.0.1/
bin/zookeeper-server-start.sh config/zookeeper.properties
```

## Update Kafka server properties to support log compaction:

```bash
vi ~/code/kafka_2.11-0.10.0.1/config/server.properties

log.retention.check.interval.ms=100
log.cleaner.delete.retention.ms=100
log.cleaner.enable=true
log.cleaner.min.cleanable.ratio=0.01
delete.topic.enable=true

# log.cleaner.enable=true
# log.roll.ms=30000
```

## Startup kafka server:

```bash
cd ~/code/kafka_2.11-0.10.0.1/
bin/kafka-server-start.sh config/server.properties
```

## Create topic:

```bash
cd ~/code/kafka_2.11-0.10.0.1/
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic <topic_name>
```

Create topic with log compaction:

```bash
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic <topic_name> --config cleanup.policy=compact --config min.cleanable.dirty.ratio=0.01 --config segment.ms=100 --config delete.retention.ms=100
```

List of current topics:

```bash
bin/kafka-topics.sh --list --zookeeper localhost:2181
```

Describe topic:

```bash
bin/kafka-topics.sh --describe --zookeeper localhost:2181 --topic <topic_name>

Topic:test5	PartitionCount:1	ReplicationFactor:1	Configs:cleanup.policy=compact
Topic: test5	Partition: 0	Leader: 0	Replicas: 0	Isr: 0
```

Delete topic:

```bash
bin/kafka-topics.sh —delete --zookeeper localhost:2181 --topic <topic_name>
```

Recompile code:

```bash
cd ~/code/kafkaAPIClient
mvn clean compile assembly:single
```

Start simple consumer:

```bash
cd ~/code/kafkaAPIClient
java -cp target/KafkaAPIClient-1.0-SNAPSHOT-jar-with-dependencies.jar com.spnotes.kafka.simple.Consumer <topic_name> group1
```

Start producer:

```bash
java -cp target/KafkaAPIClient-1.0-SNAPSHOT-jar-with-dependencies.jar com.spnotes.kafka.simple.Producer <topic_name> <noOfDistinctKeys> <noOfMessages> <messageDelayInMillis>
```

Start offset consumer (with added filter):

```bash
java -cp target/KafkaAPIClient-1.0-SNAPSHOT-jar-with-dependencies.jar com.spnotes.kafka.offset.Consumer <topicName> group1 <startingOffset> <keyFilter>
```

View cleaner logs:

```bash
cat ~/code/kafka_2.11-0.10.0.1/logs/log-cleaner.log
```

Kafka raw log files:

```bash
/tmp/kafka-logs
```

## TEST SCENARIO

```bash

bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic rolling

bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic lastKey --config cleanup.policy=compact --config min.cleanable.dirty.ratio=0.01 --config segment.ms=100 --config delete.retention.ms=100

java -cp target/KafkaAPIClient-1.0-SNAPSHOT-jar-with-dependencies.jar com.spnotes.kafka.offset.Consumer rolling group1 0 key
java -cp target/KafkaAPIClient-1.0-SNAPSHOT-jar-with-dependencies.jar com.spnotes.kafka.offset.Consumer lastKey group2 0 key

java -cp target/KafkaAPIClient-1.0-SNAPSHOT-jar-with-dependencies.jar com.spnotes.kafka.simple.Producer 5 200 10 rolling lastKey

java -cp target/KafkaAPIClient-1.0-SNAPSHOT-jar-with-dependencies.jar com.spnotes.kafka.offset.Consumer lastKey group3 0 key
```
