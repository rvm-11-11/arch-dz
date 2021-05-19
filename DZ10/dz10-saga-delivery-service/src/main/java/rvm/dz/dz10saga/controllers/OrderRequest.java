package rvm.dz.dz10saga.controllers;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.util.Date;

@RequiredArgsConstructor
@Data
public class OrderRequest {

    final Long userId;

    final Long itemId;

    final Long price;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    final Date deliveryDate;
}
