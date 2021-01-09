package rvm.dz.dz6users.controllers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class OrderProcessedEvent {

    enum Status {
        PAYMENT_REJECTED, PAYMENT_ACCEPTED
    }

    Long userId;

    Status status;

    Long orderId;

}
