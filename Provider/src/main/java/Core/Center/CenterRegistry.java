package Core.Center;

import Core.Pojo.ServerMonitor;
import lombok.Getter;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Getter
public class CenterRegistry {

    private final Logger logger= LoggerFactory.getLogger(CenterRegistry.class);

    @Autowired
    private CallCenterConfiguration configuration;

    @Autowired
    private CallCenter center;

    private String uri;

    private String serverAddress;

    @PostConstruct
    public void init(){
        String groupName=configuration.getGroupName();
        String host= configuration.getHost();
        Integer port= configuration.getPort();
        serverAddress =host+":"+port;
        uri="/"+groupName+"/"+ serverAddress;
    }

    public void register() throws Exception {

        String groupName=configuration.getGroupName();
        String serverName=configuration.getServerName();
        String host= configuration.getHost();
        Integer port= configuration.getPort();

        ServerMonitor monitor=serverMonitor(host,port,groupName);

        // 创建节点
        if(center.connection().checkExists().forPath(uri)==null){
            center.connection().create()
                    .creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(uri,monitor.bytes());
        }

        logger.info("producer {} register success",serverName);
    }

    /**
     * 增加负载或者减少负载
     */
    public boolean editLoad(Integer count){
        Stat stat=new Stat();
        ServerMonitor monitor;
        while (true){
            try {
                monitor=ServerMonitor.toObj(center.connection().getData().storingStatIn(stat).forPath(uri));
                if(null == monitor){
                    return false;
                }
                monitor.setLoad(monitor.getLoad()+count);
                center.connection().setData().withVersion(stat.getVersion()).forPath(uri, monitor.bytes());
                return true;
            } catch (Exception e) {
                if(e instanceof KeeperException.BadVersionException){
                    logger.info("bad version",e);
                }else{
                    logger.info("edit load fail",e);
                    return false;
                }
            }
        }
    }

    public ServerMonitor serverMonitor(String host,Integer port,String groupName){
        ServerMonitor monitor=new ServerMonitor();
        monitor.setHost(host);
        monitor.setPort(port);
        monitor.setGroupName(groupName);
        monitor.setLoad(0);
        return monitor;
    }

}
