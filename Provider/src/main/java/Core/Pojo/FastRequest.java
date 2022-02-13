package Core.Pojo;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

@Setter
@Getter
@Builder
public class FastRequest {

    private String requestId;

    private String className;

    private String methodName;

    private Class<?>[] paramsType;

    private Object[] params;

    @Tolerate
    FastRequest(){

    }

}
