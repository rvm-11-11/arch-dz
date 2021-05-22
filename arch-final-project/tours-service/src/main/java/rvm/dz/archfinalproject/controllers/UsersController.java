package rvm.dz.archfinalproject.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.BeanDefinitionDsl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rvm.dz.archfinalproject.repositories.TourEntity;
import rvm.dz.archfinalproject.repositories.ToursRepository;
import rvm.dz.archfinalproject.repositories.UserEntity;
import rvm.dz.archfinalproject.repositories.UsersRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UsersController {

    private final UsersRepository usersRepository;

    @GetMapping("/users")
    public ResponseEntity getAllUsers() {
        return ResponseEntity.ok(usersRepository.findAll());
    }

    @PostMapping("/users")
    public ResponseEntity createUser(@RequestBody CreateUpdateUserRequest request) {
        UserEntity createdUser = usersRepository.save(
                UserEntity.builder()
                        .name(request.getName())
                        .role(request.getRole())
                        .build()
        );

        return ResponseEntity.ok( Map.of("userId", createdUser.getUserId()) );
    }
}
