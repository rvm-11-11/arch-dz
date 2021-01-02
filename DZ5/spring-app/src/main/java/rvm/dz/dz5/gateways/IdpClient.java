package rvm.dz.dz5.gateways;

import rvm.dz.dz5.external.GetUserInfoResponse;
import rvm.dz.dz5.external.UpdateUserInfoResponse;
import rvm.dz.dz5.external.UpdateUserRequest;

public interface IdpClient {

    String registerUser(CreateUserInput createUserInput);

    GetUserInfoResponse getUserInfo(String userId);

    void updateUserInfo(String userId, UpdateUserRequest updateUserRequest);
}
