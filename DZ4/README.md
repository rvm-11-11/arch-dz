

## DZ4 Description

Task:
https://github.com/izhigalko/otus-homework-istio

0. Install Minikube
1. Install Istio with Ingress Gateway, Kiali
2. Deploy two versions of app 
3. Set up ballancing 50/50
4. Kiali map screenshot

Demo with VirtualBox/Vargant:
https://github.com/izhigalko/otus-demo-istio

Demo with Minikube:
https://github.com/izhigalko/otus-demo-istio/tree/minikube

Istio getting started:
https://istio.io/latest/docs/setup/getting-started/

istioctl full path:
/home/superuser/Documents/OTUS/bin/istio-1.8.1/bin/istioctl

/home/superuser/Documents/OTUS/bin/istio-1.8.1/bin/istioctl operator init --watchedNamespaces istio-system --operatorNamespace istio-operator

Check Istio status -- FAILS, no resources :( 
`kubectl get all -n istio-system -l istio.io/rev=default`

but this works:
`kubectl describe istiooperator.install.istio.io -n istio-system`

Kiali is not installed properly:

kubectl apply -f kiali/kiali.yaml
Returns:
```
flag provided but not defined: -v
Usage of /opt/kiali/kiali:
  -config string
        Path to the YAML configuration file. If not specified, environment variables will be used for configuration.
```
Trying default config of from Istio get started:

```
kubectl apply -f kiali/kiali-from-docs.yaml 
/home/superuser/Documents/OTUS/bin/istio-1.8.1/bin/istioctl dashboard kiali
```

Seems to work...

Stuck here, will try official Istio example
