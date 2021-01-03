### TASK

Register, view and change own user profile

### Investigation

https://github.com/schetinnikov-otus/arch-labs/tree/master/nginx-forward-auth
Nice example with nginx forward-auth user management is hand-coded though

https://github.com/schetinnikov-otus/arch-labs/tree/master/nginx-oauth2
Also nice, oauth2 provided by using this image: 
quay.io/pusher/oauth2_proxy:latest]
Using gihub provider (seems there is no own user management)

Let's try Keycloak
https://www.keycloak.org/getting-started/getting-started-kube

maybe it can handle auth nicely

### Experiments

#### Keycloak

https://www.keycloak.org/getting-started/getting-started-kube

```
kubectl create namespace auth-test
kubectl config set-context --current --namespace=auth-test

kubectl apply -f keycloak-quickstart.yaml
```
In quickstart it is tricky because it creates deployment in different namespace (default)


```
Keycloak:                 https://keycloak.192.168.49.2.nip.io/auth
Keycloak Admin Console:   https://keycloak.192.168.49.2.nip.io/auth/admin
Keycloak Account Console: https://keycloak.192.168.49.2.nip.io/auth/realms/myrealm/account
```

https://stackoverflow.com/questions/45352880/keycloak-invalid-parameter-redirect-uri

**CONCLUSION: Keycloak looks promising as IdP!**

#### Trying to bring it all together

##### Example forward auth:

https://github.com/schetinnikov-otus/arch-labs/tree/master/nginx-forward-auth
    
Ingress to trigger Auth:
    
https://github.com/schetinnikov-otus/arch-labs/blob/master/nginx-forward-auth/app-ingress.yaml
    
Code for auth logic:    
https://github.com/schetinnikov-otus/arch-labs/blob/master/nginx-forward-auth/auth/src/app.py
https://github.com/schetinnikov-otus/arch-labs/blob/master/nginx-forward-auth/app/src/app.py

Basic Auth in example by Keycloak is not looking flexible enough:

https://stackoverflow.com/questions/57808046/does-keycloak-support-basic-authentication

Example OAuth2:
https://github.com/schetinnikov-otus/arch-labs/tree/master/nginx-oauth2
Docs OAuth2:
https://kubernetes.github.io/ingress-nginx/examples/auth/oauth-external-auth/
Docs BasicAuth:
https://kubernetes.github.io/ingress-nginx/examples/auth/external-auth/
Example from docs:
https://kubernetes.github.io/ingress-nginx/examples/customization/external-auth-headers/
Docs annotations:
https://kubernetes.github.io/ingress-nginx/user-guide/nginx-configuration/annotations/#external-authentication

### Key points:


https://github.com/schetinnikov-otus/arch-labs/tree/master/nginx-oauth2

https://oauth2-proxy.github.io/oauth2-proxy/

    nginx.ingress.kubernetes.io/auth-url: "http://oauth2-proxy.auth.svc.cluster.local:4180/oauth2/auth"
    nginx.ingress.kubernetes.io/auth-signin: "https://$host/oauth2/start?rd=$escaped_request_uri"

    kubectl exec --stdin --tty pod/app-57bf654666-rddsq   -- /bin/bash
    kubectl exec -n=kube-system --stdin --tty pod/ingress-nginx-controller-799c9469f7-zmflx  -- /bin/bash 

    http://arch.homework/something
    
### Running OAuth2 proxy example with own github repo
    
    Code in this repo + example in oauth2-proxy + created new github OAuth2 application

### Running OAuth2 proxy against local Keycloak
    
    https://keycloak.192.168.49.2.nip.io/auth/realms/myrealm/.well-known/openid-configuration

- how to do registration?
  - without first/last name?
  - or proxy call 
- how to verify email automatically?
    
    https://www.keycloak.org/docs-api/5.0/rest-api/index.html#_users_resource
    https://www.appsdeveloperblog.com/keycloak-rest-api-create-a-new-user/
    
    https://stackoverflow.com/questions/4072585/disabling-ssl-certificate-validation-in-spring-resttemplate
    
    https://stackoverflow.com/questions/9210514/unable-to-find-valid-certification578/-path-to-requested-target-error-even-after-c
    TODO
    
### Running OAuth2 proxy against local Keycloak and own app

```
kubectl config set-context --current --namespace=auth-test

helm create dz5-chart
helm install dz5-chart ./dz5-chart
```
http://192.168.49.2:31670/health

What should be keycloak host then?

```
kubectl exec --stdin --tty pod/dz5-chart-6f848c676b-cmshj  -- /bin/bash
```

Not clear if this URL is reachable:
http://keycloak/auth/realms/myrealm/.well-known/openid-configuration

http://arch.homework/otusapp/romanov/health
^already goes through auth proxy

Valid Redirect URIs -- changed to `*`

Copied over client secret for account

### Cleanup:

```
helm uninstall dz5-chart
```