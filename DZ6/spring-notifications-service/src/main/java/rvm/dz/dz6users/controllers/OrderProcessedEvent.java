package rvm.dz.dz6users.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderProcessedEvent {

    enum Status {
        PAYMENT_REJECTED, PAYMENT_ACCEPTED
    }

    Status status;

    Long orderId;

    Long userId;
}
