package Center;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.List;

/**
 * 基于 zookeeper 的服务注册、治理中心
 */

@Slf4j
@Getter
@Setter
public class CallCenterApplication {

    private CuratorFramework connection;

    private final String address;

    private final Integer connectTimeOut;

    private final Integer sessionTimeOut;

    private final String serviceName;

    private String serverName;

    private List<ServerMonitor>providerInfoList;

    public CallCenterApplication(CallCenterConfiguration configuration){
        this.address=configuration.getAddress();
        this.connectTimeOut= configuration.getConnectTimeOut();
        this.sessionTimeOut= configuration.getSessionTimeOut();
        this.serviceName= configuration.getServiceName();
        this.serverName=configuration.getServerName();
        this.init();
    }

    public void  init(){
        connection= CuratorFrameworkFactory.builder()
                .connectString(address)
                .sessionTimeoutMs(sessionTimeOut)
                .connectionTimeoutMs(connectTimeOut)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        connection.start();
    }

    public void probeProvider(){
        try {
            List<String> providerNameList=connection.getChildren().forPath("/"+serviceName);
            List<ServerMonitor>providerInfoList=new ArrayList<>();
            for(String name:providerNameList){
                byte[]info=connection.getData().forPath(assembleRoute("/"+serviceName,name));
                ServerMonitor monitor= ServerMonitor.toObj(info);
                if(monitor!=null){
                    providerInfoList.add(monitor);
                }
            }
            this.providerInfoList=providerInfoList;
            log.info("get providers success : {}",this.providerInfoList);
        } catch (KeeperException | InterruptedException e) {
            log.info("get provider error");
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


    public void register(String host,Integer port) throws Exception {

        ServerMonitor monitor=serverMonitor(host,port,serviceName);
        String serverName = host + ":" + port;
        String route="/"+serviceName+"/"+ serverName;

        // 创建节点
        if(connection.checkExists().forPath(route)==null){
            connection.create()
                    .creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(route,monitor.bytes());
        }

        log.info("producer {} register success",serverName);
    }

    /**
     * 增加负载或者减少负载
     */
    public void updateLoad(Integer count){
        String route="/"+serviceName+"/"+ serverName;
        Stat stat=new Stat();
        ServerMonitor monitor;
        while (true){
            try {
                monitor=ServerMonitor.toObj(connection.getData().storingStatIn(stat).forPath(route));
                if(null == monitor){
                    return;
                }
                monitor.setLoad(monitor.getLoad()+count);
                connection.setData().withVersion(stat.getVersion()).forPath(route, monitor.bytes());
                return;
            } catch (Exception e) {
                if(e instanceof KeeperException.BadVersionException){
                    log.info("bad version",e);
                }else{
                    log.info("edit load fail",e);
                }
            }
        }
    }

    public ServerMonitor serverMonitor(String host, Integer port, String groupName){
        ServerMonitor monitor=new ServerMonitor();
        monitor.setHost(host);
        monitor.setPort(port);
        monitor.setGroupName(groupName);
        monitor.setLoad(0);
        return monitor;
    }

    private String assembleRoute(String root,String providerName){
        return root+"/"+providerName;
    }
}
