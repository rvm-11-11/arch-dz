package rvm.dz.archfinalproject.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.BeanDefinitionDsl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rvm.dz.archfinalproject.external.GetUserInfoResponse;
import rvm.dz.archfinalproject.external.UpdateUserRequest;
import rvm.dz.archfinalproject.gateways.IdpClient;
import rvm.dz.archfinalproject.repositories.TourEntity;
import rvm.dz.archfinalproject.repositories.ToursRepository;
import rvm.dz.archfinalproject.repositories.UserEntity;
import rvm.dz.archfinalproject.repositories.UsersRepository;
import rvm.dz.archfinalproject.useCases.RegistrationUseCase;
import rvm.dz.archfinalproject.useCases.RegistrationUseCaseInput;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UsersController {

    private final UsersRepository usersRepository;
    private final RegistrationUseCase useCase;
    private final String USER_ID_HEADER = "X-Auth-Request-User";

    @PostMapping("/users/register")
    public ResponseEntity register(@RequestBody CreateUpdateUserRequest request) {
        log.info("/register: {}", request);
        final RegistrationUseCaseInput input = RegistrationUseCaseInput.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(request.getPassword())
                .role(request.getRole())
                .build();

        return useCase.register(input).toResponseEntity();
    }

    @GetMapping("/users/my-id")
    public ResponseEntity register(@RequestHeader(USER_ID_HEADER) String userId) {
        log.info("/users/my-id: {}", userId);
        return ResponseEntity.ok( Map.of("userId", userId));
    }

    @GetMapping("/users")
    public ResponseEntity getAllUsers() {
        return ResponseEntity.ok(usersRepository.findAll());
    }

    @PostMapping("/users")
    public ResponseEntity createUser(@RequestBody CreateUpdateUserRequest request) {
        UserEntity createdUser = usersRepository.save(
                UserEntity.builder()
//                        .name(request.getName())
                        .role(request.getRole())
                        .build()
        );

        return ResponseEntity.ok( Map.of("userId", createdUser.getUserId()) );
    }

    @PostMapping("/users/reset")
    public String resetUsers() {
        log.info("Calling resetUsers()");
        usersRepository.deleteAll();
        return "All users removed";
    }

    private final IdpClient idpClient;

    @GetMapping("/users/getUserInfo")
    public ResponseEntity getUserInfo(@RequestHeader("X-Auth-Request-User") String userId) {
        log.info("/getUserInfo: {}", userId);

        if (userId == null || userId.isEmpty()) {
            return new ResponseEntity<>("UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
        } else {
            GetUserInfoResponse response = idpClient.getUserInfo(userId);
            return ResponseEntity.ok(response);
        }
    }

    @PutMapping("/users/updateUserInfo")
    public ResponseEntity updateUserInfo(@RequestHeader("X-Auth-Request-User") String userId,
                                         @RequestBody UpdateUserRequest updateUserRequest) {
        log.info("/updateUserInfo: {}, {}", userId, updateUserRequest);

        if (userId == null || userId.isEmpty()) {
            return new ResponseEntity<>("UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
        } else {
            idpClient.updateUserInfo(userId, updateUserRequest);
            return ResponseEntity.ok().build();
        }
    }



}
