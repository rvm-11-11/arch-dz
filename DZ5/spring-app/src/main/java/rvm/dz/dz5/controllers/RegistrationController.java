package rvm.dz.dz5.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rvm.dz.dz5.controllers.requests.RegistrationRequest;
import rvm.dz.dz5.useCases.RegistrationUseCase;
import rvm.dz.dz5.useCases.RegistrationUseCaseInput;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RegistrationController {

    final RegistrationUseCase useCase;

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegistrationRequest request) {
        log.info("/register: {}", request);
        final RegistrationUseCaseInput input = RegistrationUseCaseInput.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(request.getPassword())
                .build();

        return useCase.register(input).toResponseEntity();
    }
}
