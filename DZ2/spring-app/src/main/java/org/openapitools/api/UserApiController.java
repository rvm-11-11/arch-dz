package org.openapitools.api;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.openapitools.model.Error;
import org.openapitools.model.UserEntity;
import org.openapitools.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-11-22T08:00:48.814Z[Etc/UTC]")
@Controller
@RequestMapping("${openapi.userService.base-path:/api/v1}")
@RequiredArgsConstructor
public class UserApiController implements UserApi {

    private final NativeWebRequest request;

    private final UserRepository repository;

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @Override
    public ResponseEntity<UserEntity> createUser(@Valid CreateUpdateUserRequest user) {
        UserEntity u = repository.save(user.toUserEntity());
        return ResponseEntity.of(Optional.of(u));
    }

    @Override
    public ResponseEntity<Void> deleteUser(Long userId) {
        repository.deleteById(userId);
        return null;
    }

    @ApiOperation(value = "", nickname = "findAll", notes = "Returns all users", response = UserEntity.class, tags={ "user", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "user response", response = UserEntity.class),
            @ApiResponse(code = 200, message = "unexpected error", response = Error.class) })
    @GetMapping(
            value = "/users",
            produces = { "application/json" }
    )
    @Override
    public ResponseEntity<List<UserEntity>> findAll() {
        List<UserEntity> users = new ArrayList<>();
        repository.findAll().forEach(users::add);
        return ResponseEntity.ok(users);
    }

    @Override
    public ResponseEntity<UserEntity> findUserById(Long userId) {
        Optional<UserEntity> u = repository.findById(userId);
        return ResponseEntity.of(u);
    }

    @Override
    public ResponseEntity<Void> updateUser(Long userId, @Valid CreateUpdateUserRequest user) {
        UserEntity userEntity = user.toUserEntity().id(userId);
        repository.saveAndFlush(userEntity);
        return null;
    }
}
