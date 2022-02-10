package Core.Center;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("fastCall.center.config")
@Getter
@Setter
public class CallCenterConfiguration {
    private String centerAddress;
    private String rootDir;
}
