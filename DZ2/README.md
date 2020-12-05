

Background

https://spring.io/guides/tutorials/rest/
https://spring.io/guides/gs/accessing-data-rest/
https://github.com/habuma/spring-in-action-5-samples/tree/master/ch06
https://reflectoring.io/spring-boot-openapi/
https://editor.swagger.io/ -- Java 7 generation quite old
and maven plugin option
https://www.baeldung.com/spring-boot-rest-client-swagger-codegen

Swagger Codegen -- https://github.com/swagger-api/swagger-codegen

OpenAPI generator -- https://github.com/OpenAPITools/openapi-generator

http://api.openapi-generator.tech/index.html

```
curl -X POST "http://api.openapi-generator.tech/api/gen/servers/spring" -H "Accept: */*" -H "Content-Type: application/json" -d "{ \"openAPIUrl\": \"https://api.swaggerhub.com/apis/otus55/users/1.0.0\", \"options\": {}, \"spec\": {}}"
```

https://maven.apache.org/guides/introduction/introduction-to-plugins.html
https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/htmlsingle/

https://learning.postman.com/docs/writing-scripts/test-scripts/
https://learning.postman.com/docs/writing-scripts/script-references/test-examples/
https://stackoverflow.com/questions/43336057/postman-is-it-possible-to-customize-the-sequence-of-test-runs-in-the-collection
https://learning.postman.com/docs/running-collections/intro-to-collection-runs/

```
newman run DZ2-User-API.postman_collection.json -e localhost.postman_environment.json
```

K8s

ReplicaSet tracks containers by labels
Deployment is higher level concept which manages ReplicaSets internally therefore recommended to use -- possible to easily update images and rollback changes, upscale and downscale.
Service -- can do load balancing.
Service discovery can be done via environment variables or via service FQDN.
Skaffold convenient for assembling Docker images, changing and applying manifests in one command.
Blue Green -- two deployment running in parallel and switch just by updating selector label.
Ingress does the routing e.g. rewrites URLs
StatefulSet allows do work with storage e.g. for DB.
ConfigMap to store configuration, can be referenced from deployment.
Secrets are actually Base64, not really secrets, just cannot be spied.
 
###Did a lot of debugging, bottom line -- remove existing resources fromo all namespaces!

```
minikube start
kubectl config set-context --current --namespace=myapp
kubectl desctibe pod hello-demo
kubectl set image deployment/hello-deployment hello-py=hello-py:v2 --record
minikube service rvm-dz2-service --url -n myapp
skaffold init
skaffold run

helm create dz2-chart
helm dependency update ./dz2-chart

docker run --publish 3306:3306 --name some-mariadb -e MYSQL_ROOT_PASSWORD=secretpassword123 -d mariadb:10
docker exec -it some-mariadb /bin/bash
mysql -u root -p secretpassword123
CREATE DATABASE my_db;
CREATE TABLE `user_entity` (   
    `id` bigint(20) NOT NULL,   
    `email` varchar(255) DEFAULT NULL,   
    `first_name` varchar(255) DEFAULT NULL,   
    `last_name` varchar(255) DEFAULT NULL,   
    `phone` varchar(255) DEFAULT NULL,   
    `username` varchar(255) DEFAULT NULL,   
    PRIMARY KEY (`id`) ) 
    ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
INSERT INTO user_entity (id, email, first_name, last_name, phone, username)
    VALUES ('74', 'email74', 'firstName74', 'lastName74', 'phone74', 'username74');

USE my_db:
CREATE TABLE TestUsers (Id int, Name varchar(255));
INSERT INTO Users VALUES (1, 'Ivan');
SELECT * FROM Users;

helm list
helm repo add bitnami https://charts.bitnami.com/bitnami
helm install my-release bitnami/mariadb
helm install my-release \
  --set auth.rootPassword=secretpassword123,auth.database=app_database \
    bitnami/mariadb
helm uninstall my-release
kubectl get pv
kubectl get pvc

kubectl describe pv pvc-169c264a-b6d3-4d46-8daa-dbfdce7570a5 | grep Finalizers
kubectl patch pv pvc-169c264a-b6d3-4d46-8daa-dbfdce7570a5 -p '{"metadata":{"finalizers": []}}' --type=merge


watch kubectl get all
kubectl get ingress --all-namespaces
kubectl delete all --all -n myapp
```


https://stackoverflow.com/questions/64125048/get-error-unknown-field-servicename-in-io-k8s-api-networking-v1-ingressbacken
https://stackoverflow.com/questions/51358856/kubernetes-cant-delete-persistentvolumeclaim-pvc
https://kubernetes.io/docs/tasks/access-application-cluster/ingress-minikube/
https://medium.com/@ManagedKube/kubernetes-troubleshooting-ingress-and-services-traffic-flows-547ea867b120
https://artifacthub.io/packages/helm/bitnami/mariadb
https://hub.docker.com/_/mariadb