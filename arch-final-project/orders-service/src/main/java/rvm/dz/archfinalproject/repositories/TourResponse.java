package rvm.dz.archfinalproject.repositories;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourResponse {

  @JsonProperty
  private Long tourId;

  @JsonProperty
  private String name;

  @JsonProperty
  private String description;

  @JsonProperty
  private Long price;

  @JsonProperty
  private Instant fromDate;

  @JsonProperty
  private Instant toDate;

  @JsonProperty
  private String fromDestination; //TODO enum?

  @JsonProperty
  private String toDestination;  //TODO enum?

  @JsonProperty
  private Long hotelId; //TODO enum?

//  @JsonProperty
//  private enum meals; //TODO enum?

}

