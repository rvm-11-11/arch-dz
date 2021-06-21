package rvm.dz.archfinalproject.external;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ListOfUsersResponse {

    List<GetUserInfoResponse> users;
}
