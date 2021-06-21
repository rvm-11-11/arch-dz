package rvm.dz.archfinalproject.gateways;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUserInput {

    final String username;

    final String email;

    final String firstName;

    final String lastName;

    final String password;

}
