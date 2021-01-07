package rvm.dz.dz5.external;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
//@NoArgsConstructor
public class GetUserInfoResponse {

    String id;

    String username;

    String email;

    String firstName;

    String lastName;

}
