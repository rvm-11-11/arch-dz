package rvm.dz.archfinalproject.responses;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

@Data
@RequiredArgsConstructor
public class OkResponse implements UseCaseResponse{

    public static final OkResponse EMPTY = new OkResponse(null);

    private final Object body;

    @Override
    public ResponseEntity toResponseEntity() {
        return ResponseEntity.ok(body);
    }
}
