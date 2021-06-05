package rvm.dz.archfinalproject.useCases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import rvm.dz.archfinalproject.gateways.CreateUserInput;
import rvm.dz.archfinalproject.gateways.IdpClient;
import rvm.dz.archfinalproject.repositories.UserEntity;
import rvm.dz.archfinalproject.repositories.UsersRepository;
import rvm.dz.archfinalproject.responses.OkResponse;
import rvm.dz.archfinalproject.responses.UseCaseResponse;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RegistrationUseCase {

    private final IdpClient idpClient;
    private final UsersRepository usersRepository;

    public UseCaseResponse register(RegistrationUseCaseInput input) {
        CreateUserInput createUserInput = CreateUserInput.builder()
                .username(input.username)
                .email(input.email)
                .firstName(input.firstName)
                .lastName(input.lastName)
                .password(input.password)
                .build();

        String userId = idpClient.registerUser(createUserInput);

        UserEntity createdUser = usersRepository.save(
                UserEntity.builder()
                        .userId(userId)
                        .role(input.getRole())
                        .build()
        );


        return new OkResponse(Map.of("userId", createdUser.getUserId()));
    }
}
