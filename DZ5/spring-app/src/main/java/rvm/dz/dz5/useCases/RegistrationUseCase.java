package rvm.dz.dz5.useCases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import rvm.dz.dz5.gateways.CreateUserInput;
import rvm.dz.dz5.gateways.IdpClient;
import rvm.dz.dz5.responses.OkResponse;
import rvm.dz.dz5.responses.UseCaseResponse;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RegistrationUseCase {

    private final IdpClient idpClient;

    public UseCaseResponse register(RegistrationUseCaseInput input) {
        CreateUserInput createUserInput = CreateUserInput.builder()
                .username(input.username)
                .email(input.email)
                .firstName(input.firstName)
                .lastName(input.lastName)
                .password(input.password)
                .build();

        idpClient.registerUser(createUserInput);

        return OkResponse.EMPTY;
    }
}
