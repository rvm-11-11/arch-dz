package rvm.dz.archfinalproject.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@RequiredArgsConstructor
@Data
public class CreateUpdateOrderRequest {

    @JsonProperty
    private Long tourId;

}
