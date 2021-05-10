package rvm.dz.dz8cqrses.controllers;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AddItemToOrderRequest {

    Long itemId;

    Long quantity;
}
