package rvm.dz.dz6users.controllers;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class RegistrationRequest {

    final String email;

    final String name;

}
