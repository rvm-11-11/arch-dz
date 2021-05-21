package rvm.dz.archfinalproject.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.Instant;
import java.util.Date;

@RequiredArgsConstructor
@Data
public class CreateUpdateTourRequest {

    @JsonProperty
    private String name;

    @JsonProperty
    private String description;

    @JsonProperty
    private Long price;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonProperty
    private Date fromDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonProperty
    private Date toDate;

    @JsonProperty
    private String fromDestination; //TODO enum?

    @JsonProperty
    private String toDestination;  //TODO enum?

    @JsonProperty
    private Long hotelId; //TODO enum?

}
