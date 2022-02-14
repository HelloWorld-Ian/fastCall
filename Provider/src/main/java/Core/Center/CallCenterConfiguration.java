package Core.Center;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "fastCall.center.config")
public class CallCenterConfiguration {

    private String centerAddress;

    private Integer port;

    private String serverName;

    private String  host;

    private Integer connectTimeout;

    private Integer sessionTimeout;

    private String groupName;

    private String appId;


}
