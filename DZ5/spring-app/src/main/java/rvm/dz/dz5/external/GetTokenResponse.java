package rvm.dz.dz5.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetTokenResponse {

    private String access_token;

    private String refresh_token;

}
