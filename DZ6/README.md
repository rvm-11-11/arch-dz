### Task

Design and implement Orders, Billing and Notifications services

### Ideas

Let's try Kafka
https://bitnami.com/stack/kafka/helm

```
kubectl create namespace streams
kubectl config set-context --current --namespace=streams

helm install my-release bitnami/kafka
helm install -f k8s/myvalues.yaml  my-release bitnami/kafka
helm uninstall my-release
```

There is no default Web UI, let's try command line stuff as proposed by Helm chart output:

    kubectl run my-release-kafka-client --restart='Never' --image docker.io/bitnami/kafka:2.7.0-debian-10-r1 --namespace streams --command -- sleep infinity
    kubectl exec --tty -i my-release-kafka-client --namespace streams -- bash

    PRODUCER:
        kafka-console-producer.sh \
            --broker-list my-release-kafka-0.my-release-kafka-headless.streams.svc.cluster.local:9092 \
            --topic test

    CONSUMER:
        kafka-console-consumer.sh \
            --bootstrap-server my-release-kafka.streams.svc.cluster.local:9092 \
            --topic test \
            --from-beginning

https://docs.spring.io/spring-kafka/reference/html/#even-quicker-with-spring-boot

Could not expose Kafka from k8s, so had to install it locally like this:
https://kafka.apache.org/quickstart

    bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic orderProcessed

Seems to work!

### Implementation (outdated a bit)

#### Users service

/register

    POST request: email, name 
    Response: userId
    Publishes: USER_CREATED -- userId
    
/getUserInfo

    GET: userId
    Response: userName
    Can be used to send email

#### Billing service

/createAccount

    Subscribed to USER_CREATED event
    Creates account with userId and balance zero

/myBalance

    GET
    Request: userId
    Returns: how much money is here

/deposit

    POST request: userId, sum

/payment

    Request: userId, sum
    Subscribed to ORDER_CREATED event
    Publishes: ORDER_PROCESSED -- result: paid/not enough money

#### Orders service

/createOrder

    POST itemId, userId
    Publishes: ORDER_CREATED -- itemId, userId, sum

#### Notifications service

/getAllSentMessages

    GET
    Response: all sent messages

/sendEmail

    Subscribed to ORDER_PROCESSED
    Action: save to DB email with result

### Scripts

```
cd spring-users-service
./mvnw package
docker build --tag rvm1111/arch:dz6-users-v2 .
docker push rvm1111/arch:dz6-users-v2
cd ..

cd spring-billing-service
./mvnw package
docker build --tag rvm1111/arch:dz6-billing-v1 .
docker push rvm1111/arch:dz6-billing-v1
cd ..

cd spring-orders-service
./mvnw package
docker build --tag rvm1111/arch:dz6-orders-v1 .
docker push rvm1111/arch:dz6-orders-v1
cd ..

cd spring-notifications-service
./mvnw package
docker build --tag rvm1111/arch:dz6-notifications-v1 .
docker push rvm1111/arch:dz6-notifications-v1
cd ..
```

```
kubectl exec --stdin --tty pod/rvm-dz6-notifications-deployment-8454c87b79-zwb6c  -- /bin/bash
```

```
newman run DZ6.postman_collection.json --environment=local-k8s-ingress.postman_environment.json  --folder "k8s ingress"
```