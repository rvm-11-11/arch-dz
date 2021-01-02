package rvm.dz.dz5.controllers;

import lombok.RequiredArgsConstructor;
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
public class UserInfoController {

    private final IdpClient idpClient;

    @GetMapping("/getUserInfo")
    public ResponseEntity getUserInfo(@RequestHeader("X-UserId") String userId) {

        if (userId == null || userId.isEmpty()) {
            return new ResponseEntity<>("UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
        } else {
            GetUserInfoResponse response = idpClient.getUserInfo(userId);
            return ResponseEntity.ok(response);
        }
    }

    @PutMapping("/updateUserInfo")
    public ResponseEntity updateUserInfo(@RequestHeader("X-UserId") String userId,
                                         @RequestBody UpdateUserRequest updateUserRequest) {

        if (userId == null || userId.isEmpty()) {
            return new ResponseEntity<>("UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
        } else {
            idpClient.updateUserInfo(userId, updateUserRequest);
            return ResponseEntity.ok().build();
        }
    }

}
