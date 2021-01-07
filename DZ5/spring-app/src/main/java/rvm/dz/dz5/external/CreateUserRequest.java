package rvm.dz.dz5.external;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CreateUserRequest {

    final String username;

    final String email;

    final String firstName;

    final String lastName;

    final List<Credential> credentials;

    final boolean emailVerified;

    final boolean enabled;
}
