package rvm.dz.dz5.controllers.requests;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class RegistrationRequest {

    final String username;

    final String email;

    final String firstName;

    final String lastName;
}
