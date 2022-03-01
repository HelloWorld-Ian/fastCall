import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

import java.util.ArrayList;
import java.util.List;

/**
 * 基于 zookeeper 的服务注册、治理中心
 */

@Slf4j
public class CallCenterApplication {

    private CuratorFramework connection;

    private final String address;

    private final Integer connectTimeOut;

    private final Integer sessionTimeOut;

    private final String serviceName;

    private final String host;

    private final Integer port;

    private final String serverName;

    private final String route;

    private List<ServerMonitor>providerInfoList;

    public CallCenterApplication(CallCenterConfiguration configuration){
        this.address=configuration.getAddress();
        this.connectTimeOut= configuration.getConnectTimeOut();
        this.sessionTimeOut= configuration.getSessionTimeOut();
        this.serviceName= configuration.getServiceName();
        this.host= configuration.getHost();
        this.port= configuration.getPort();
        this.serverName = host + ":" + port;
        this.route="/"+serviceName+"/"+ serviceName;
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
            List<String> providerNameList=connection.getChildren().forPath(serviceName);
            List<ServerMonitor>providerInfoList=new ArrayList<>();
            for(String name:providerNameList){
                byte[]info=connection.getData().forPath(assembleRoute(serverName,name));
                ServerMonitor monitor=ServerMonitor.toObj(info);
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

    public CuratorFramework connection(){
        return connection;
    }

    public void register() throws Exception {

        ServerMonitor monitor=serverMonitor(host,port,serviceName);

        // 创建节点
        if(connection.checkExists().forPath(route)==null){
            connection.create()
                    .creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(route,monitor.bytes());
        }

        log.info("producer {} register success",serverName);
    }

    public ServerMonitor serverMonitor(String host,Integer port,String groupName){
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
