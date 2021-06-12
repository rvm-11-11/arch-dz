## Scripts

### Kafka

  cd /home/superuser/Documents/tmp/kafka_2.13-2.7.0

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
  helm upgrade my-release bitnami/kafka

  kubectl delete -f ./k8s/my-app/.
  kubectl apply -f ./k8s/my-app/.
  kubectl apply -f ./k8s/oauth2-proxy/.
  kubectl apply -f ./k8s/keycloak/.

  kubectl logs service/rvm-final-project-flights-service


### Use helm chart from DZ5

Admin tool does not work -- mixed content and CSP violation

https://stackoverflow.com/questions/18321032/how-to-get-chrome-to-allow-mixed-content -- does not really help here because it also violates CSP

but I can access it via weird service port, that is cool!
http://192.168.49.2:32281/auth/admin/master/console/#/realms/myrealm/users

helm uninstall dz5-chart ./dz5-chart

helm create final-project-chart
helm uninstall final-project-chart
helm dependency update ./final-project-chart
helm upgrade final-project-chart ./final-project-chart

helm install final-project-chart ./final-project-chart --dry-run  > debug-helm.txt

helm install dz55-chart ./dz5-chart --dry-run > debug-helm.txt

helm install final-project-chart ./final-project-chart



### Create new chart

  helm create final-project-chart
  helm uninstall final-project
  helm install final-project ./final-project-chart --dry-run  > debug-helm.txt

  helm install final-project ./final-project-chart

  helm dependency update ./final-project-chart
  kubectl get serviceaccount

  helm upgrade final-project ./final-project-chart

  kubectl logs ingress-nginx-controller-799c9469f7-zmflx  -n=kube-system

### Deploy IdP integrated version into k8s

  ./mvnw package
  docker build --tag rvm1111/arch:final-project-tours-v3 .
  docker push rvm1111/arch:final-project-tours-v3

  helm uninstall final-project
  helm install final-project ./final-project-chart

  kubectl logs service/final-project-final-project-chart
  kubectl logs pod/my-release-kafka-0  

  helm upgrade final-project ./final-project-chart


  https://arch.homework/otusapp/romanov/users/my-id

  ./mvnw package
  docker build --tag rvm1111/arch:final-project-orders-v2 .
  docker push rvm1111/arch:final-project-orders-v2

  ./mvnw package
  docker build --tag rvm1111/arch:final-project-payments-v1 .
  docker push rvm1111/arch:final-project-payments-v1

  ./mvnw package
  docker build --tag rvm1111/arch:final-project-hotels-v1 .
  docker push rvm1111/arch:final-project-hotels-v1

  ./mvnw package
  docker build --tag rvm1111/arch:final-project-flights-v1 .
  docker push rvm1111/arch:final-project-flights-v1


  - name: SPRING_KAFKA_CONSUMER_GROUP_ID
    value: foofoo
