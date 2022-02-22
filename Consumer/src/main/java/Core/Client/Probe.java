package Core.Client;

import Core.Center.CallCenter;
import Core.Center.CallCenterConfiguration;
import Core.Pojo.ProviderInfo;
import Core.Pojo.ServerMonitor;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Component
public class Probe {

    @Autowired
    CallCenterConfiguration configuration;

    private final Logger logger= LoggerFactory.getLogger(Probe.class);

    @Autowired
    private CallCenter center;

    private volatile List<ServerMonitor>providerInfoList=new ArrayList<>();

    @PostConstruct
    public void init() throws IOException {
        probeProvider();
    }


    public void probeProvider(){
        String groupName="/"+configuration.getGroupName();
        try {
            List<String>providerNameList=center.connection().getChildren().forPath(groupName);

            List<ServerMonitor>providerInfoList=new ArrayList<>();
            for(String name:providerNameList){
                byte[]info=center.connection().getData().forPath(uri(groupName,name));
                ServerMonitor monitor=ServerMonitor.toObj(info);
                if(monitor!=null){
                    providerInfoList.add(monitor);
                }
            }
            this.providerInfoList=providerInfoList;
            logger.info("get providers success : {}",this.providerInfoList);
        } catch (KeeperException | InterruptedException e) {
            logger.info("get provider error");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ServerMonitor Provider(){
        Integer load=Integer.MAX_VALUE;
        ServerMonitor ret=null;
        for(ServerMonitor monitor:providerInfoList){
            Integer curLoad= monitor.getLoad();
            if(monitor.getLoad()<load){
                ret=monitor;
                load=curLoad;
            }
        }
        return ret;
    }

    private String uri(String root,String providerName){
        return root+"/"+providerName;
    }
}

