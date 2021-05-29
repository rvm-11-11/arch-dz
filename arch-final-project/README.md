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


### Docker

  docker login

  ./mvnw package
  docker build --tag rvm1111/arch:final-project-tours-v1 .
  docker push rvm1111/arch:final-project-tours-v1

  ./mvnw package
  docker build --tag rvm1111/arch:final-project-orders-v1 .
  docker push rvm1111/arch:final-project-orders-v1

  ./mvnw package
  docker build --tag rvm1111/arch:final-project-payments-v1 .
  docker push rvm1111/arch:final-project-payments-v1

  ./mvnw package
  docker build --tag rvm1111/arch:final-project-hotels-v1 .
  docker push rvm1111/arch:final-project-hotels-v1

  ./mvnw package
  docker build --tag rvm1111/arch:final-project-flights-v1 .
  docker push rvm1111/arch:final-project-flights-v1

### k8s

  minikube start
  kubectl get namespaces
  kubectl create namespace final-project
  kubectl config set-context --current --namespace=final-project
  watch kubectl get all

  helm uninstall my-release bitnami/kafka
  helm install my-release bitnami/kafka

  kubectl delete -f ./k8s/.
  kubectl apply -f ./k8s/.
