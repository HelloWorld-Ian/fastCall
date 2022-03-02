package Core;


import Center.CallCenterApplication;
import Center.ServerMonitor;
import FastFramer.Message.FastRequest;
import FastFramer.Message.FastResponse;

import java.util.UUID;

public class FastChannel {

    private final CallCenterApplication application;

    public FastChannel(CallCenterApplication application){
        this.application=application;
    }

    @SuppressWarnings("unchecked")
    public <T> T proxy(Class<T>interfaceClass){
        return (T) java.lang.reflect.Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass},
                (proxy, method, args) -> {
            FastRequest request=FastRequest.builder()
                    .requestId(UUID.randomUUID().toString())
                    .interfaceClass(method.getDeclaringClass().getName())
                    .methodName(method.getName())
                    .params(args)
                    .paramsType(method.getParameterTypes())
                    .build();

            ServerMonitor monitor = application.Provider();

            String host= monitor.getHost();
            Integer port= monitor.getPort();

            FastStream client=new FastStream(host,port);

            // 执行回调函数
            FastResponse response=client.remoteExecute(request);

            Throwable error=response.getError();
            if (error!=null){
                throw error;
            }
            return response.getRes();
        });
    }
}
