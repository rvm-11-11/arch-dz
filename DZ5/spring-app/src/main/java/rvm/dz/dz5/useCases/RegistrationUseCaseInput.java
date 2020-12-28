package rvm.dz.dz5.useCases;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
@Builder
public class RegistrationUseCaseInput {

    final String username;

    final String email;

    final String firstName;

    final String lastName;

}
