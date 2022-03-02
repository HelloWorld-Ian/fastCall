package Core;

import Center.CallCenterApplication;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"unchecked"})
public class FastClient {

    private FastChannel channel;
    private final Map<Class<?>,Object> cache;


    private FastClient(){
        cache=new HashMap<>();
    }

    public void initChannel(CallCenterApplication application){
        channel=new FastChannel(application);
    }

    // protected : 自身、子类、同一个package
    protected static FastClient newInstance(){
        return new FastClient();
    }

    /**
     * 更新缓存
     */
    public void put(Class<?>interfaceProxy,Class<?>interfaceImpl){
        synchronized (FastClient.class){
            cache.put(interfaceImpl,channel.proxy(interfaceProxy));
        }
    }

    /**
     * 返回一个代理对象
     *
     * @param interfaceImpl 被代理的对象类
     * @param interfaceProxy 代理接口
     * @param <T> 代理对象
     */
    public <T> T get(Class<T>interfaceProxy,Class<?>interfaceImpl){
        synchronized (FastClient.class){
            if(cache.containsKey(interfaceImpl)){
                return (T) cache.get(interfaceImpl);
            }else {
                T instance = channel.proxy(interfaceProxy);
                cache.put(interfaceImpl,instance);
                return instance;
            }
        }
    }
}
