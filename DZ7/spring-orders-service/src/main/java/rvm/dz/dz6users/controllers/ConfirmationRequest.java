package rvm.dz.dz6users.controllers;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class ConfirmationRequest {

    final Long orderId;

    final Long userId;

}
