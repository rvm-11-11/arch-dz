package rvm.dz.dz5.external;

import org.springframework.beans.factory.annotation.Value;
import rvm.dz.dz5.gateways.CreateUserInput;
import rvm.dz.dz5.gateways.IdpClient;

public class KeyCloakClient implements IdpClient {

    @Value("${keycloak.host}")
    private String host;

    private final String createUserEndpoint = "/myrealm/users";

    @Override
    public void registerUser(CreateUserInput createUserInput) {

    }
}
