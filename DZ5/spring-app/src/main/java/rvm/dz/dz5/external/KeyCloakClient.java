package rvm.dz.dz5.external;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.apache.http.conn.ssl.TrustStrategy;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import rvm.dz.dz5.gateways.CreateUserInput;
import rvm.dz.dz5.gateways.IdpClient;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

@Component
public class KeyCloakClient implements IdpClient {

    @Value("${keycloak.host}")
    private String host;

    private final String usersEndpoint = "/auth/admin/realms/myrealm/users";
    private final String getTokenEndpoint = "/auth/realms/master/protocol/openid-connect/token";

    private RestTemplate restTemplate = new RestTemplate();

    KeyCloakClient() {
        try {
            restTemplate = restTemplate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String registerUser(CreateUserInput createUserInput) {
        Credential password = Credential.builder()
                .temporary(false)
                .type("password")
                .value(createUserInput.getPassword())
                .build();
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .email(createUserInput.getEmail())
                .firstName(createUserInput.getFirstName())
                .lastName(createUserInput.getLastName())
                .username(createUserInput.getUsername())
                .credentials(List.of(password))
                .emailVerified(true)
                .enabled(true)
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAccessToken());
        HttpEntity<CreateUserRequest> createUserRequestHttpEntity = new HttpEntity<>(createUserRequest, headers);
        restTemplate.exchange(host + usersEndpoint, HttpMethod.POST,
                createUserRequestHttpEntity, String.class, Map.of());
        return getUserIdByUsername(createUserInput.getUsername());
    }

    @Override
    public GetUserInfoResponse getUserInfo(String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAccessToken());
        HttpEntity<CreateUserRequest> createUserRequestHttpEntity = new HttpEntity<>(null, headers);
        GetUserInfoResponse user = restTemplate.exchange(host + usersEndpoint + "/" + userId,
                HttpMethod.GET,
                createUserRequestHttpEntity,
                GetUserInfoResponse.class).getBody();
        return user;
    }

    @Override
    public void updateUserInfo(String userId, UpdateUserRequest updateUserRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAccessToken());
        HttpEntity<UpdateUserRequest> updateUserRequestHttpEntity = new HttpEntity<>(updateUserRequest, headers);
        restTemplate.exchange(host + usersEndpoint + "/" + userId, HttpMethod.PUT,
                updateUserRequestHttpEntity, String.class, Map.of());
//        return getUserIdByUsername(createUserInput.getUsername());
    }

    private String getAccessToken() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers.add("PRIVATE-TOKEN", "xyz");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type","password");
        map.add("client_id","admin-cli");
        map.add("username","admin");
        map.add("password","admin");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<GetTokenResponse> response =
                restTemplate.exchange(host + getTokenEndpoint,
                        HttpMethod.POST,
                        entity,
                        GetTokenResponse.class);
        return response.getBody().getAccess_token();
    }

    public RestTemplate restTemplate()
            throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                return true;
            }
        };
        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return restTemplate;
    }

    private String getUserIdByUsername(String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAccessToken());
        HttpEntity<CreateUserRequest> createUserRequestHttpEntity = new HttpEntity<>(null, headers);
        GetUserInfoResponse[] users = restTemplate.exchange(host + usersEndpoint, HttpMethod.GET,
                createUserRequestHttpEntity, GetUserInfoResponse[].class, Map.of("username", username)).getBody();
        return users[0].getId();
    }

}
