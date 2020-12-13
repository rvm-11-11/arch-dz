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
```


https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html
https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html#production-ready-metrics-export-prometheus
https://stackabuse.com/monitoring-spring-boot-apps-with-micrometer-prometheus-and-grafana/
