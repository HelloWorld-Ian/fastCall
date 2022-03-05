package Client;

import Center.CallCenterApplication;
import Center.CallCenterConfiguration;
import Core.FastClient;
import Core.FastClientBootstrap;
import ServiceImpl.HelloWorld;
import ServiceImpl.HelloWorldImpl;

public class Client {
    public static void main(String[] args) {
        FastClientBootstrap bootstrap=new FastClientBootstrap();
        CallCenterConfiguration configuration=new CallCenterConfiguration();
        configuration.setAddress("127.0.0.1:2181");
        configuration.setSessionTimeOut(20000);
        configuration.setConnectTimeOut(20000);
        configuration.setServiceName("FastService");

        CallCenterApplication application=new CallCenterApplication(configuration);
        FastClient client=bootstrap.setCallCenter(application)
                .build();
        HelloWorld helloWorld=client.get(HelloWorld.class, HelloWorldImpl.class);
        String res=helloWorld.sayHello();
        System.out.println(res);
    }
}
