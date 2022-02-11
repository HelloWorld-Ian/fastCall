package Core.Client;

import Core.Pojo.FastRequest;
import Core.Pojo.FastResponse;
import Core.Pojo.ProviderInfo;
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
    public <T> T proxy(Class<T>interfaceClass,String providerName){
        return (T) java.lang.reflect.Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, (proxy, method, args) -> {
            FastRequest request=FastRequest.builder()
                    .requestId(UUID.randomUUID().toString())
                    .className(method.getDeclaringClass().getName())
                    .methodName(method.getName())
                    .params(args)
                    .paramsType(method.getParameterTypes())
                    .build();

            ProviderInfo providerInfo=probe.Provider(providerName);
            String address=providerInfo.getAddress();
            String[] split=address.split(":");
            String host=split[0];
            Integer port=Integer.parseInt(split[1]);

            Client client=new Client(host,port);
            FastResponse response=client.remoteExecute(request);

            Throwable error=response.getError();
            if (error!=null){
                throw error;
            }
            return response.getRes();
        });
    }
}
