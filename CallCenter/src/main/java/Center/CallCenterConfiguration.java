package Center;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Getter
@Setter
public class CallCenterConfiguration {

    private String address;

    private Integer connectTimeOut;

    private Integer sessionTimeOut;

    private String serviceName;

    private String host;

    private Integer port;

}
