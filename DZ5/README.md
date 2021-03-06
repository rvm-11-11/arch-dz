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

```
kubectl exec pod/ingress-nginx-controller-799c9469f7-zmflx -n kube-system -- cat /etc/nginx/nginx.conf > nginx.conf

```

https://stackoverflow.com/questions/62438259/kubernetes-nginx-ingress-disable-external-auth-for-specific-path

https://learning.postman.com/docs/writing-scripts/script-references/variables-list/

http://arch.homework/otusapp/romanov/getUserInfo

https://www.keycloak.org/docs/latest/server_admin/#_authentication-flows

Enabled direct grant -- does not really help
https://developer.okta.com/blog/2018/06/29/what-is-the-oauth2-password-grant


    nginx.ingress.kubernetes.io/auth-url: "http://oauth2-proxy.auth-test.svc.cluster.local:4180/oauth2/auth"
    nginx.ingress.kubernetes.io/auth-signin: "https://$host/oauth2/start?rd=$escaped_request_uri"

http://arch.homework/otusapp/romanov/oauth2/auth
    
    kubectl logs service/oauth2-proxy 
    kubectl logs pod/ingress-nginx-controller-799c9469f7-zmflx -n=kube-system

`--cookie-secure=false` -- helped

Postman test suit created

### Wrap everything into Helm chart:

https://artifacthub.io/packages/helm/bitnami/keycloak

helm dependency update ./dz5-chart

https://www.keycloak.org/docs-api/5.0/rest-api/index.html#_realms_admin_resource

https://stackoverflow.com/questions/1955505/parsing-json-with-unix-tools


    until $(curl --output /dev/null --silent --head --fail http://dz5-chart-keycloak/auth/realms/master); do
        printf '.'
        sleep 5
    done
    
    export ACCESS_TOKEN=$(\
        curl --location --request POST 'http://arch.homework/auth/realms/master/protocol/openid-connect/token' \
        --header 'Content-Type: application/x-www-form-urlencoded' \
        --data-urlencode 'grant_type=password' \
        --data-urlencode 'client_id=admin-cli' \
        --data-urlencode 'username=admin' \
        --data-urlencode 'password=admin' \
        --insecure \
        | jq -r '.access_token'\
    )
    
    export REALM_JSON=$(curl https://raw.githubusercontent.com/rvm-11-11/arch-dz/DZ5/DZ5/realm-export.json)
    
    curl --location --request POST 'http://192.168.49.2:30325/auth/admin/realms/' \
    --header 'Authorization: Bearer '"$ACCESS_TOKEN" \
    --header 'Content-Type: application/json' \
    --data-raw ''"$REALM_JSON"''

    curl --location --request POST 'http://example.com/' \
    --header 'Authorization: Bearer $(ACCESS_TOKEN)' \
    --header 'Content-Type: application/json' \
    --data-raw '($REALM_JSON)'


    kubectl exec --stdin --tty dz5-keycloak-init-r8j8t  -- /bin/bash
    kubectl exec --stdin --tty oauth2-proxy-59d88657f6-r4mlr    -- /bin/bash

    dz5-chart-keycloak.192.168.49.2.nip.io 
    
    newman run DZ5.postman_collection.json --environment=local-k8s-ingress.postman_environment.json  --folder "My app" --insecure
    
### Cleanup:

```
helm uninstall dz5-chart
kubectl delete all --all -n=auth-test
```