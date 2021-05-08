# Task

Design and implement shopping using CQRS and Event Sourcing

# Ideas

## Kafka

Let's set up Kafka

Could not expose Kafka from k8s, so had to install it locally like this: https://kafka.apache.org/quickstart

Server:
    
    bin/zookeeper-server-start.sh config/zookeeper.properties
    
    bin/kafka-server-start.sh config/server.properties
    
    bin/kafka-topics.sh --create --topic shopping-events --bootstrap-server localhost:9092
 
 OR
 
    bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic shopping-events
 
 Check server running and topic created:
 
    bin/kafka-topics.sh --describe --topic shopping-events --bootstrap-server localhost:9092
 
Producer:

    bin/kafka-console-producer.sh --topic shopping-events --bootstrap-server localhost:9092
    
Consumer:

    bin/kafka-console-consumer.sh --topic shopping-events --from-beginning --bootstrap-server localhost:9092

## Next steps

https://www.baeldung.com/jpa-many-to-many

sudo ss -lptn 'sport = :8080'