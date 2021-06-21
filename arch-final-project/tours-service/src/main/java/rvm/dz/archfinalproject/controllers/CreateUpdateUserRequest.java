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

    private String name;

    private UserEntity.Role role;

    final String username;

    final String email;

    final String firstName;

    final String lastName;

    final String password;

}
