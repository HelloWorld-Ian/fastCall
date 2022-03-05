package Service;

import Center.CallCenterApplication;
import Center.CallCenterConfiguration;
import Core.FastServer;
import Core.FastServerBootstrap;
import ServiceImpl.HelloWorld;
import ServiceImpl.HelloWorldImpl;
import ServiceStorage.ServiceConfig;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Server {
    public static void main(String[] args) throws UnknownHostException {
        FastServerBootstrap serverBootstrap=new FastServerBootstrap();

        CallCenterConfiguration configuration=new CallCenterConfiguration();
        configuration.setAddress("127.0.0.1:2181");
        configuration.setServerName(InetAddress.getLocalHost().getHostAddress()+":81");
        configuration.setSessionTimeOut(20000);
        configuration.setConnectTimeOut(20000);
        configuration.setServiceName("FastService");

        CallCenterApplication application=new CallCenterApplication(configuration);

        ServiceConfig<HelloWorldImpl>serviceConfig=new ServiceConfig<>(HelloWorld.class,new HelloWorldImpl());

        FastServer server=serverBootstrap.setCallCenter(application)
                .serviceRegister(serviceConfig)
                .build();

        server.start(81);
    }
}
