package Core.Pojo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FastResponse {

    private String requestId;

    private Object res;

    private Throwable error;

}