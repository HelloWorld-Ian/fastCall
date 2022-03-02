package ServiceStorage;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"unchecked"})
public class Service{
    Class<?>interfaceClass;
    Map<String,Object>serviceImpls;

    public Service(Class<?>interfaceClass){
        this.interfaceClass=interfaceClass;
        serviceImpls=new HashMap<>();
    }

    public void putImpl(String implName,Object instance){
        serviceImpls.put(implName,instance);
    }

    public <T> T getImpl(String serviceName){
        return (T) getImpl(serviceName,interfaceClass);
    }

    private  <T> T getImpl(String serviceName,Class<T>interfaceClass){
        if(!serviceImpls.containsKey(serviceName)){
            return null;
        }
        return (T) serviceImpls.get(serviceName);
    }
}
