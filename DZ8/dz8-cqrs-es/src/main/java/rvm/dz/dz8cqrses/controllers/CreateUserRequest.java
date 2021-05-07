package rvm.dz.dz8cqrses.controllers;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import rvm.dz.dz8cqrses.repositories.UserEntity;

@Data
public class CreateUserRequest {

    final String name;

    final UserEntity.Role role;
}
