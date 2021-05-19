## Scripts

Kafka startup

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

## Package app into docker image

    chmod a+x ./mvnw
    ./mvnw package
    java -jar target/dz10-saga-orders-service-0.0.1-SNAPSHOT.jar
    docker build --tag rvm1111/arch:dz10-orders-v1 .
    docker run --publish 8081:8081 --detach rvm1111/arch:dz10-orders-v1
    docker stop <container-id>
    docker rm <container-id>
    docker login
    docker push rvm1111/arch:dz10-orders-v1

    ./mvnw package
    java -jar target/dz10-saga-payment-service-0.0.1-SNAPSHOT.jar
    docker build --tag rvm1111/arch:dz10-payments-v1 .
    docker run --publish 8082:8082 --detach rvm1111/arch:dz10-payments-v1
    docker stop <container-id>
    docker rm <container-id>
    docker login
    docker push rvm1111/arch:dz10-payments-v1

    ./mvnw package
    java -jar target/dz10-saga-delivery-service-0.0.1-SNAPSHOT.jar
    docker build --tag rvm1111/arch:dz10-delivery-v1 .
    docker run --publish 8083:8083 --detach rvm1111/arch:dz10-delivery-v1
    docker stop <container-id>
    docker rm <container-id>
    docker login
    docker push rvm1111/arch:dz10-delivery-v1

    ./mvnw package
    java -jar target/dz10-saga-store-service-0.0.1-SNAPSHOT.jar
    docker build --tag rvm1111/arch:dz10-store-v1 .
    docker run --publish 8084:8084 --detach rvm1111/arch:dz10-store-v1
    docker stop <container-id>
    docker rm <container-id>
    docker login
    docker push rvm1111/arch:dz10-store-v1

## k8s

    minikube start
    kubectl get namespaces
    kubectl create namespace saga
    kubectl config set-context --current --namespace=saga
    watch kubectl get all

    helm install my-release bitnami/kafka

    kubectl apply -f ./k8s/.


## Tests

  newman run DZ10.postman_collection.json --environment=local-k8s-ingress.postman_environment.json  --folder "local ingress"
