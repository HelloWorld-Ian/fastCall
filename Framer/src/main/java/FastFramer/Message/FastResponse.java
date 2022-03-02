package FastFramer.Message;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Getter
@Setter
@Builder
public class FastResponse {

    private String requestId;

    private Object res;

    private Throwable error;

    @Tolerate
    FastResponse(){

    }

}
