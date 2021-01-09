package rvm.dz.dz6users.repositories;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {

  public enum Status {
    WAITING_CONFIRMATION, CONFIRMED
  }

  @Id
  @JsonProperty("id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @JsonProperty("itemId")
  private Long itemId;

  @JsonProperty("userId")
  private Long userId;

  @JsonProperty("status")
  private Status status;

  @JsonProperty("sum")
  private Long sum;

}

