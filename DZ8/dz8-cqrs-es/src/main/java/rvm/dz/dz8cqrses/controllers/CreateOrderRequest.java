package rvm.dz.dz8cqrses.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rvm.dz.dz8cqrses.repositories.UserEntity;

@NoArgsConstructor
@Data
public class CreateOrderRequest {

    Long userId;

}
