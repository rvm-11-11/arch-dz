package rvm.dz.archfinalproject.external;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UpdateUserRequest {

    final String username;

    final String email;

    final String firstName;

    final String lastName;
}
