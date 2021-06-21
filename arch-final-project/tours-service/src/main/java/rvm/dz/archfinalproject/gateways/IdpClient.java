package rvm.dz.archfinalproject.gateways;

import rvm.dz.archfinalproject.external.GetUserInfoResponse;
import rvm.dz.archfinalproject.external.UpdateUserInfoResponse;
import rvm.dz.archfinalproject.external.UpdateUserRequest;

public interface IdpClient {

    String registerUser(CreateUserInput createUserInput);

    GetUserInfoResponse getUserInfo(String userId);

    void updateUserInfo(String userId, UpdateUserRequest updateUserRequest);
}
