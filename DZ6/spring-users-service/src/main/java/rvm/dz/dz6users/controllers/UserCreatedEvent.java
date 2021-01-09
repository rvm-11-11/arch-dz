package rvm.dz.dz6users.controllers;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class UserCreatedEvent {

    final Long id;

}
