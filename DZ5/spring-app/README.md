### Scripts

```
./mvnw package
java -jar target/dz5-0.0.4-SNAPSHOT.jar
docker build --tag rvm1111/arch:dz5v4 .
docker run --publish 8080:8080 --detach rvm1111/arch:dz5v4
docker push rvm1111/arch:dz5v4
```