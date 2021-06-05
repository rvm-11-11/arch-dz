package rvm.dz.archfinalproject.useCases;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import rvm.dz.archfinalproject.repositories.UserEntity;

@RequiredArgsConstructor
@Data
@Builder
public class RegistrationUseCaseInput {

    final String username;

    final String email;

    final String firstName;

    final String lastName;

    final String password;

    final UserEntity.Role role;
}
