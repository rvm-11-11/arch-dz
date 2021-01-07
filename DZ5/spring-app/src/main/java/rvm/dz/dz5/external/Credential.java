package rvm.dz.dz5.external;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Credential {

    boolean temporary;

    String value;

    String type;

}
