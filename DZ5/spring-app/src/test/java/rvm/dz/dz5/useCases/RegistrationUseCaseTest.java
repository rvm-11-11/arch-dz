package rvm.dz.dz5.useCases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import rvm.dz.dz5.gateways.CreateUserInput;
import rvm.dz.dz5.gateways.IdpClient;
import rvm.dz.dz5.responses.OkResponse;
import rvm.dz.dz5.responses.UseCaseResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class RegistrationUseCaseTest {

    private RegistrationUseCase sut;
    private IdpClient idpClient;

    @BeforeEach
    public void init() {
        idpClient = Mockito.mock(IdpClient.class);
        sut = new RegistrationUseCase(idpClient);
    }

    @Test
    public void shallRegisterNewUser() {
        final String username = "username";
        final String email = "email";
        final String firstName = "firstName";
        final String lastName = "lastName";

        final RegistrationUseCaseInput input = RegistrationUseCaseInput.builder()
                .username(username)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .build();

        UseCaseResponse response = sut.register(input);
        assertEquals(OkResponse.EMPTY, response);

        final CreateUserInput createUserInput = CreateUserInput.builder()
                .username(username)
                .email(email)
                .firstName(firstName)
                .lastName(lastName).build();
        verify(idpClient).registerUser(createUserInput);
    }

}