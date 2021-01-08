package rvm.dz.dz6users.controllers;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class CheckoutRequest {

    final Long itemId;

    final Long userId;

}
