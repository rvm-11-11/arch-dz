### Scripts

```
./mvnw package
java -jar target/dz4-0.0.1-SNAPSHOT.jar
docker build --tag rvm1111/arch:dz4v1 .
docker run --publish 8080:8080 --detach rvm1111/arch:dz4v1
docker push rvm1111/arch:dz4v1
```