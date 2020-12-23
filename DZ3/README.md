## Background

```
minikube start
watch kubectl get all --all-namespaces
kubectl create namespace monitoring
kubectl config set-context --current --namespace=monitoring

helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo add stable https://charts.helm.sh/stable
helm repo update
helm install prom prometheus-community/kube-prometheus-stack -f prometheus.yaml --atomic

minikube addons disable ingress
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update
helm install nginx ingress-nginx/ingress-nginx -f nginx-ingress.yaml --atomic

kubectl port-forward service/prom-grafana 9000:80
http://localhost:9000/login -- admin:prom-operator
kubectl port-forward service/prom-kube-prometheus-stack-prometheus 9090
http://localhost:9090/

./mvnw package
java -jar target/dz3-spring-app-1.0.0.jar
docker build --tag rvm1111/arch:dz3v3 .
docker run --publish 8080:8080 --detach rvm1111/arch:dz3v3
docker push rvm1111/arch:dz3v3
docker stop rvm1111/arch:dz3v3
docker rm rvm1111/arch:dz3v3
docker login

helm install my-helm-app ./my-helm-app
helm upgrade my-helm-app ./my-helm-app --atomic
kubectl get servicemonitors.monitoring.coreos.com
kubectl describe servicemonitors.monitoring.coreos.com my-helm-app-myapp  

helm upgrade nginx stable/nginx-ingress -f nginx-ingress.yaml

```
http://arch.homework/otusapp/romanov/version
http://arch.homework/otusapp/romanov/actuator/prometheus

```
while 1; do ab -n 5 -c 5 http://192.168.176.128:32033/db ; sleep 3;

while true; do ab -n 5 -c 5 http://localhost:8080/health ; sleep 3; done
ab -n 50 -c 5 http://localhost:8080/health ;

sum(rate(http_server_requests_seconds_count[1m]))
```

## App charts
1. Latency (response time) с квантилями по 0.5, 0.95, 0.99, max

histogram_quantile(0.5, (sum by (le)(rate(app_request_latency_seconds_bucket{exported_endpoint="/db"}[1m]))))

histogram_quantile(0.5, (sum by (le, uri)(rate(http_server_requests_seconds_bucket[1m]))))

2. RPS
sum by (uri) (rate(app_request_count_total[1m]))

sum by (uri) (rate(http_server_requests_seconds_count[1m]))

3. Error Rate

sum by (uri) (rate(http_server_requests_seconds_count{status=~"5.+"}[1m]))

## Same for ingress:

4. Latency (response time) quantiles 0.5, 0.95, 0.99, max

```
histogram_quantile(0.5, (sum by (le) (rate(nginx_ingress_controller_request_duration_seconds_bucket[5m]))))
histogram_quantile(0.95, (sum by (le) (rate(nginx_ingress_controller_request_duration_seconds_bucket[5m]))))
histogram_quantile(0.99, (sum by (le) (rate(nginx_ingress_controller_request_duration_seconds_bucket[5m]))))
histogram_quantile(1, (sum by (le) (rate(nginx_ingress_controller_request_duration_seconds_bucket[5m]))))
```

5. RPS

```
sum(rate(nginx_ingress_controller_request_duration_seconds_count[5m]))
```

6. Error Rate

```
sum(increase(nginx_ingress_controller_request_duration_seconds_count{status=~"5.+"}[5m]))
```

```
    kubectl scale deployment prom-kube-prometheus-stack-operator  --replicas=0
    kubectl scale deployment prom-kube-prometheus-stack-operator   --replicas=1

pod/nginx-ingress-nginx-controller-b88gg 
```


Cleanup:

```
helm uninstall my-helm-app
```

Background:

https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html
https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html#production-ready-metrics-export-prometheus
https://stackabuse.com/monitoring-spring-boot-apps-with-micrometer-prometheus-and-grafana/
WTF is this project? -- https://docs.spring.io/spring-metrics/docs/current/public/prometheus
https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready-metrics
https://github.com/prometheus/client_java
Dashboard for Micrometer instrumented applications  -- https://grafana.com/grafana/dashboards/4701

https://prometheus.io/docs/prometheus/latest/getting_started/
https://blog.autsoft.hu/defining-custom-metrics-in-a-spring-boot-application-using-micrometer/
https://prometheus.io/docs/practices/histograms/

Bash scripts tutorial: 
https://likegeeks.com/bash-script-easy-guide/
https://likegeeks.com/bash-scripting-step-step-part2/

https://httpd.apache.org/docs/2.4/programs/ab.html
https://stackoverflow.com/questions/60634832/postman-launches-with-a-blank-screen

MySQL Dashboard: https://grafana.com/grafana/dashboards/7362

https://medium.com/bitnami-perspectives/monitoring-a-mariadb-server-using-prometheus-and-grafana-5ee3d1c5360e