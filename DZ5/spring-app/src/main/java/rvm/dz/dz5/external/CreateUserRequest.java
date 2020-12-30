package rvm.dz.dz5.external;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUserRequest {

    final String username;

    final String email;

    final String firstName;

    final String lastName;


}
