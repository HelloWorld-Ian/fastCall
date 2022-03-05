package FastFramer.Message;

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

    private String interfaceClass;

    private String referenceClass;

    private String methodName;

    private Class<?>[] paramsType;

    private Object[] params;

    @Tolerate
    FastRequest(){

    }

    @Override
    public String toString(){
        return "FastRequest { requestId="+requestId+", interfaceClass="+ interfaceClass +", referenceClass="+referenceClass+", methodName="+methodName
                +", paramsType="+ Arrays.toString(paramsType)+", params="+Arrays.toString(paramsType)+" }";
    }

}
