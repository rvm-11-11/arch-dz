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

  kubectl delete -f ./k8s/my-app/.
  kubectl apply -f ./k8s/my-app/.

  kubectl delete all --all -n final-project
  kubectl delete all --all -n saga
  kubectl delete pv data-my-release-kafka-0 -n final-project


  kubectl delete pod --all / pod-name
  kubectl delete pvc --all / pvc-name
  kubectl delete pv --all / pv-name



### Keycloak

  kubectl apply -f ./k8s/keycloak/keycloak-quickstart.yaml

  wget -q -O - https://raw.githubusercontent.com/keycloak/keycloak-quickstarts/latest/kubernetes-examples/keycloak-ingress.yaml | \
  sed "s/KEYCLOAK_HOST/keycloak.$(minikube ip).nip.io/" | \
  kubectl apply -f -

  (kubectl apply -f ./k8s/keycloak-ingress.yaml)

  wget -qO- http://rvm-final-project-flights-service:8080/flights/

  kubectl run -it --rm alpine-curl-jq --image=dwdraju/alpine-curl-jq

  export ACCESS_TOKEN=$(\
      curl --location --request POST 'http://keycloak:8080/auth/realms/master/protocol/openid-connect/token' \
      --header 'Content-Type: application/x-www-form-urlencoded' \
      --data-urlencode 'grant_type=password' \
      --data-urlencode 'client_id=admin-cli' \
      --data-urlencode 'username=admin' \
      --data-urlencode 'password=admin' \
      --insecure \
      | jq -r '.access_token'\
  )

  export REALM_JSON=$(curl https://raw.githubusercontent.com/rvm-11-11/arch-dz/DZ5/DZ5/realm-export.json)

  curl --location --request POST 'http://keycloak:8080/auth/admin/realms/' \
    --header 'Authorization: Bearer '"$ACCESS_TOKEN" \
    --header 'Content-Type: application/json' \
    --data-raw "$REALM_JSON"


  kubectl logs ingress-nginx-controller-799c9469f7-zmflx  -n=kube-system


  https://keycloak.192.168.49.2.nip.io/auth/realms/myrealm/protocol/openid-connect/auth?approval_prompt=force&client_id=account&redirect_uri=http%3A%2F%2Farch.homework%2Foauth2%2Fcallback&response_type=code&scope=openid+email+profile&state=5f332605c6ee0440a64db344dfcaefaa%3A%2Fotusapp%2Fromanov%2Ftours

  http://arch.homework/auth/realms/myrealm/protocol/openid-connect/auth?approval_prompt=force&client_id=account&redirect_uri=http%3A%2F%2Farch.homework%2Foauth2%2Fcallback&response_type=code&scope=openid+email+profile&state=5f332605c6ee0440a64db344dfcaefaa%3A%2Fotusapp%2Fromanov%2Ftours


  http://arch.homework/otusapp/romanov/tours

  vasya
  Abc123!

  kubectl logs service/oauth2-proxy --since=5m
  kubectl logs service/keycloak  --tail=100

  kubectl apply -f ./k8s/oauth2-proxy/.
  kubectl apply -f ./k8s/my-app/.
  kubectl apply -f ./k8s/keycloak/.


### Keycloak try again

  helm uninstall my-release bitnami/kafka
  helm install my-release bitnami/kafka

  kubectl delete -f ./k8s/my-app/.
  kubectl apply -f ./k8s/my-app/.
  kubectl apply -f ./k8s/oauth2-proxy/.
  kubectl apply -f ./k8s/keycloak/.

  kubectl logs service/rvm-final-project-flights-service
