package rvm.dz.archfinalproject.external;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Credential {

    boolean temporary;

    String value;

    String type;

}
