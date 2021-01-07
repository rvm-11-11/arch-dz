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