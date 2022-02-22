package Core.Pojo;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

import java.util.Arrays;

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

    @Override
    public String toString(){
        return "FastRequest { requestId="+requestId+", className="+className+", methodName="+methodName
                +", paramsType="+ Arrays.toString(paramsType)+", params="+Arrays.toString(paramsType)+" }";
    }

}
