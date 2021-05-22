package rvm.dz.archfinalproject.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import rvm.dz.archfinalproject.repositories.UserEntity;

import java.util.Date;

@RequiredArgsConstructor
@Data
public class CreateUpdateUserRequest {

    @JsonProperty
    private String name;

    @JsonProperty
    private UserEntity.Role role;

}
