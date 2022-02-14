package Core.Center;

import Core.Pojo.ServerMonitor;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CenterRegistry {

    private final Logger logger= LoggerFactory.getLogger(CenterRegistry.class);

    @Autowired
    private CallCenterConfiguration configuration;

    @Autowired
    private CallCenter center;

    public void register() throws Exception {

        String groupName= "/"+configuration.getGroupName();
        String serverName= configuration.getServerName();
        String uri=groupName+"/"+serverName;
        String host= configuration.getHost();
        Integer port= configuration.getPort();

        ServerMonitor monitor=serverMonitor(host,port);

        // 创建节点
        center.connection().create()
                .creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(uri,monitor.bytes());

        logger.info("producer {} register success",serverName);
    }

    public ServerMonitor serverMonitor(String host,Integer port){
        ServerMonitor monitor=new ServerMonitor();
        monitor.setHost(host);
        monitor.setPort(port);
        monitor.setLoad(0);
        return monitor;
    }

}
