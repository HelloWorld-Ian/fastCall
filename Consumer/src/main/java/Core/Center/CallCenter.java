package Core.Center;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class CallCenter {

    @Autowired
    private CallCenterConfiguration configuration;

    private CuratorFramework connection;

    @PostConstruct
    public void init(){
        connection= CuratorFrameworkFactory.builder()
                .connectString(configuration.getCenterAddress())
                .sessionTimeoutMs(configuration.getSessionTimeout())
                .connectionTimeoutMs(configuration.getConnectTimeout())
                .namespace(configuration.getAppId())
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        connection.start();
    }

    public CuratorFramework connection(){
        return connection;
    }

}
