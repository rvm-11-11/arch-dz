package rvm.dz.dz5.external;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ListOfUsersResponse {

    List<GetUserInfoResponse> users;
}
