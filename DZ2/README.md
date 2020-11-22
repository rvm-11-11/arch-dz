K8s

```
watch kubectl get all
kubectl delete all --all
```

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