package Core.Client;

import Core.Center.CallCenter;
import Core.Center.CallCenterConfiguration;
import Core.Pojo.ProviderInfo;
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

    private volatile List<ProviderInfo>providerInfoList=new ArrayList<>();

    @PostConstruct
    public void init() throws IOException {
        probeProvider();
    }


    public void probeProvider(){
        String root= configuration.getRootDir();
        try {
            center.connection().getData()
            List<String>providerNameList=zooKeeper.getChildren(root, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    logger.info("probing the service provider");
                }
            });

            List<ProviderInfo>providerInfoList=new ArrayList<>();
            for(String name:providerNameList){
                byte[]info=zooKeeper.getData(uri(root,name),false,null);
                String[] infoStr=new String(info).split(",");
                if(infoStr.length==2){
                    String providerName=infoStr[0];
                    String providerAddress=infoStr[1];
                    providerInfoList.add(new ProviderInfo(providerName,providerAddress));
                }
            }
            this.providerInfoList=providerInfoList;
            logger.info("get providers success : {}",this.providerInfoList);
        } catch (KeeperException | InterruptedException e) {
            logger.info("get provider error");
            e.printStackTrace();
        }
    }

    public ProviderInfo Provider(String providerName){
        List<ProviderInfo>providerInfos=providerInfoList.stream()
                .filter(providerInfo -> providerName.equals(providerInfo.getName()))
                .collect(Collectors.toList());
        if(providerInfos.isEmpty()){
            return null;
        }
        int size=providerInfos.size();
        return providerInfos.get(ThreadLocalRandom.current().nextInt(size));
    }

    private String uri(String root,String providerName){
        return root+"/"+providerName;
    }
}

