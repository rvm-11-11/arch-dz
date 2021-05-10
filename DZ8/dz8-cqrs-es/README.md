# Task

Design and implement shopping using CQRS and Event Sourcing

# Ideas

## Kafka

Let's set up Kafka

Could not expose Kafka from k8s, so had to install it locally like this: https://kafka.apache.org/quickstart

Server:
    
    bin/zookeeper-server-start.sh config/zookeeper.properties
    
    bin/kafka-server-start.sh config/server.properties

    bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic shopping-events
    
    bin/kafka-topics.sh --create --topic shopping-events --bootstrap-server localhost:9092
 
 
 Check server running and topic created:
 
    bin/kafka-topics.sh --describe --topic shopping-events --bootstrap-server localhost:9092
 
Producer:

    bin/kafka-console-producer.sh --topic shopping-events --bootstrap-server localhost:9092
    
Consumer:

    bin/kafka-console-consumer.sh --topic shopping-events --from-beginning --bootstrap-server localhost:9092

## Next steps

https://www.baeldung.com/jpa-many-to-many

sudo ss -lptn 'sport = :8080'

https://stackoverflow.com/questions/17730905/is-there-a-way-to-delete-all-the-data-from-a-topic-or-delete-the-topic-before-ev

## Package app into docker image

    chmod a+x ./mvnw
    ./mvnw package
    java -jar target/dz8-cqrs-es-0.0.1-SNAPSHOT.jar
    docker build --tag rvm1111/arch:dz8-shopping-v1 .
    docker run --publish 8080:8080 --detach rvm1111/arch:dz8-shopping-v1
    docker stop <container-id>
    docker rm <container-id>
    docker login
    docker push rvm1111/arch:dz8-shopping-v1

## K8S

    minikube start
    kubectl get namespaces
    kubectl create namespace cqrs-es
    kubectl config set-context --current --namespace=cqrs-es
    watch kubectl get all
    
    helm install my-release bitnami/kafka
    
    kubectl apply -f ./k8s/.
    
## Side notes

1) команда установки (в неймспейс streams) из этой директории:
https://github.com/rvm-11-11/arch-dz/tree/DZ6/DZ6

helm install my-release bitnami/kafka

kubectl apply -f ./k8s/.

2) Тесты постмана:
newman run DZ6.postman_collection.json --environment=local-k8s-ingress.postman_environment.json  --folder "k8s ingress"

newman run DZ8.postman_collection.json --environment=local-k8s-ingress.postman_environment.json

