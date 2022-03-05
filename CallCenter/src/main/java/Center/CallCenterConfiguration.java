package Center;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CallCenterConfiguration {

    private String address;

    private Integer connectTimeOut;

    private Integer sessionTimeOut;

    private String serviceName;

    private String serverName;
}
