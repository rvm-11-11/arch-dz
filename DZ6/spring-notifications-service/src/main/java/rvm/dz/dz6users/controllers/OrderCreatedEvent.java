package rvm.dz.dz6users.controllers;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class OrderCreatedEvent {

    Long userId;

    Long sum;

    Long orderId;
}
