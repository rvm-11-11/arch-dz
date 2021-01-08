package rvm.dz.dz6users.controllers;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

//@RequiredArgsConstructor
@Data
@Builder
public class OrderCreatedEvent {

    Long userId;

    Long sum;

    Long orderId;
}
