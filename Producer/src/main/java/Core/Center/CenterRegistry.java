package Core.Center;

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

    public void register() throws InterruptedException, KeeperException {
        Integer timeout=configuration.getTimeout();
        String address=configuration.getCenterAddress();
        ZooKeeper center= null;

        try {
            center = new ZooKeeper(address, timeout, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    logger.info("center connection established,starting registering......");
                }
            });
        } catch (IOException e) {
            logger.error("connect to call center fail",e);
        }

        String rootDir= configuration.getRootDir();
        String serverName= configuration.getServerName();
        String uri=rootDir+"/"+serverName;
        String host= configuration.getHost();
        Integer port= configuration.getPort();

        // 创建根节点
        assert center != null;
        if(center.exists(rootDir,false)==null){
            center.create(rootDir,rootDir.getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }

        // 在注册中心创建节点
        center.create(uri,(serverName + ","+ host + ":" + port).getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

        logger.info("producer {} register success",serverName);
    }
}
