## Scripts

### Kafka

  bin/zookeeper-server-start.sh config/zookeeper.properties

  bin/kafka-server-start.sh config/server.properties

  bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic travel-agency-events

  bin/kafka-topics.sh --create --topic travel-agency-events --bootstrap-server localhost:9092

Check server running and topic created:

  bin/kafka-topics.sh --describe --topic travel-agency-events --bootstrap-server localhost:9092

Producer:

  bin/kafka-console-producer.sh --topic travel-agency-events --bootstrap-server localhost:9092

Consumer:

  bin/kafka-console-consumer.sh --topic travel-agency-events --from-beginning --bootstrap-server localhost:9092
