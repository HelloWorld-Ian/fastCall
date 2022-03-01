package Core.Client;

import Core.Pojo.FastRequest;
import Core.Pojo.FastResponse;
import Core.Pojo.ProviderInfo;
import Core.Pojo.ServerMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

@Component
public class Proxy {

    @Autowired
    private Probe probe;

    @SuppressWarnings("unchecked")
    public <T> T proxy(Class<T>interfaceClass){
        return (T) java.lang.reflect.Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, (proxy, method, args) -> {
            FastRequest request=FastRequest.builder()
                    .requestId(UUID.randomUUID().toString())
                    .className(method.getDeclaringClass().getName())
                    .methodName(method.getName())
                    .params(args)
                    .paramsType(method.getParameterTypes())
                    .build();

            ServerMonitor monitor=probe.Provider();
            String host= monitor.getHost();
            Integer port= monitor.getPort();

            Client client=new Client(host,port);

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
