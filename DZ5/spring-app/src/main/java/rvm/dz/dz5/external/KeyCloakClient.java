package rvm.dz.dz5.external;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import rvm.dz.dz5.gateways.CreateUserInput;
import rvm.dz.dz5.gateways.IdpClient;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KeyCloakClient implements IdpClient {

    @Value("${keycloak.host}")
    private String host;

    private final String createUserEndpoint = "/myrealm/users";
    private final String getTokenEndpoint = "/auth/realms/master/protocol/openid-connect/token";

    private final RestTemplate restTemplate;

    @Override
    public void registerUser(CreateUserInput createUserInput) {
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .email(createUserInput.getEmail())
                .firstName(createUserInput.getFirstName())
                .lastName(createUserInput.getLastName())
                .username(createUserInput.getUsername())
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAccessToken());
        HttpEntity<CreateUserRequest> createUserRequestHttpEntity = new HttpEntity<>(createUserRequest, headers);
        restTemplate.exchange(host + createUserEndpoint, HttpMethod.POST,
                createUserRequestHttpEntity, String.class, Map.of());
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

        ResponseEntity<String> response =
                restTemplate.exchange(host + getTokenEndpoint,
                        HttpMethod.POST,
                        entity,
                        String.class);
        return response.getBody();
    }
}
