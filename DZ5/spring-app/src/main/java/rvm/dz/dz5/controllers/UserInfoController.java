package rvm.dz.dz5.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rvm.dz.dz5.controllers.requests.RegistrationRequest;
import rvm.dz.dz5.external.GetUserInfoResponse;
import rvm.dz.dz5.external.UpdateUserInfoResponse;
import rvm.dz.dz5.external.UpdateUserRequest;
import rvm.dz.dz5.gateways.IdpClient;
import rvm.dz.dz5.useCases.RegistrationUseCase;
import rvm.dz.dz5.useCases.RegistrationUseCaseInput;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserInfoController {

    private final IdpClient idpClient;

    @GetMapping("/getUserInfo")
    public ResponseEntity getUserInfo(@RequestHeader("X-Auth-Request-User") String userId) {
        log.info("/getUserInfo: {}", userId);

        if (userId == null || userId.isEmpty()) {
            return new ResponseEntity<>("UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
        } else {
            GetUserInfoResponse response = idpClient.getUserInfo(userId);
            return ResponseEntity.ok(response);
        }
    }

    @PutMapping("/updateUserInfo")
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
