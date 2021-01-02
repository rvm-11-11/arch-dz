package rvm.dz.dz5.responses;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

@Data
@RequiredArgsConstructor
public class OkResponse implements UseCaseResponse{

    public static final OkResponse EMPTY = new OkResponse(Optional.empty());

    private final Optional<String> body;

    @Override
    public ResponseEntity toResponseEntity() {
//        return ResponseEntity.ok().build();
        return ResponseEntity.ok(body);
    }
}
