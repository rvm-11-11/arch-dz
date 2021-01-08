```
cd spring-orders-service
./mvnw package
docker build --tag rvm1111/arch:dz7-orders-v1 .
docker push rvm1111/arch:dz7-orders-v1
cd ..

kubectl create namespace idempotence
kubectl config set-context --current --namespace=idempotence

kubectl apply -f ./k8s/.

newman run DZ7.postman_collection.json --environment=local-k8s-ingress.postman_environment.json
```