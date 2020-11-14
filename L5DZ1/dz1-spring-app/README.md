Build the app with `mvnw`:

`./mvnw package`

Run the app as plain .jar:

`java -jar target/dz1-0.0.1-SNAPSHOT.jar`

URL to test the app: http://localhost:8000/health/

Build the Docker image:

`docker build --tag rvm1111/arch:l5dz1 .`

https://spring.io/guides/gs/spring-boot-docker/
https://docs.docker.com/get-started/part2/
https://kubernetes.io/docs/tasks/access-application-cluster/ingress-minikube/t

Run the image:

`docker run --publish 8000:8000 --detach rvm1111/arch:l5dz1`

Should be accessible under the same URL as before

Stop the image and remove:

```
docker stop rvm1111/arch:l5dz1
docker rm rvm1111/arch:l5dz1
```

Push the image:

```
docker login
docker push rvm1111/arch:l5dz1
```

Kubernetes

```
kubectl apply -f deployment.yaml
minikube service rvm-dz1-service --url -n myapp

kubectl delete all --all
```

http://arch.homework/otusapp/romanov/health