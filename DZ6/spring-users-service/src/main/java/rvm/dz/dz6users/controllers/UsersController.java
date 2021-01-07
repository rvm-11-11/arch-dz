package rvm.dz.dz6users.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import rvm.dz.dz6users.repositories.UserEntity;
import rvm.dz.dz6users.repositories.UserRepository;

import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UsersController {

    private final UserRepository repository;

    @Autowired
    private KafkaTemplate<String, String> template;

    ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegistrationRequest request) throws JsonProcessingException {
        UserEntity createdUser = repository.save(
                UserEntity.builder()
                        .email(request.getEmail())
                        .name(request.getName()).build()
        );
        UserCreatedEvent event = new UserCreatedEvent(createdUser.getId());

        this.template.send( "userCreated", objectMapper.writeValueAsString(event));

        return ResponseEntity.ok( Map.of("id", createdUser.getId()) );
    }

    @GetMapping("/users")
    public ResponseEntity getUser(@RequestParam Long id) {
        UserEntity user = repository.findById(id).get();

        return ResponseEntity.ok(user);
    }

}
