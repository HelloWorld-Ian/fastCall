package Core;

import Center.CallCenterApplication;

/**
 * client 引导类
 */
public class FastClientBootstrap {

    private final FastClient client;

    public FastClientBootstrap(){
        this.client=FastClient.newInstance();
    }

    public FastClientBootstrap setCallCenter(CallCenterApplication application){
        client.initChannel(application);
        application.probeProvider();
        return this;
    }

    public FastClient build(){
        return client;
    }
}
