package Core;

import ServiceStorage.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 管理bean对象,私有构造方法不可初始化
 */
public class ServiceFactory {

    private final static ReadWriteLock lock=new ReentrantReadWriteLock();

    private ServiceFactory(){}

    private static final Map<String, Service> interfaceClasses=new HashMap<>();

    /**
     * 注册服务
     *
     * @param interfaceClass 统一接口名
     * @param referenceName 服务对象名
     */
    public static void serviceRegister(Class<?>interfaceClass, Object referenceName){
        String interfaceClassName=interfaceClass.getName();
        try {
            lock.writeLock().lock();
            if (!interfaceClasses.containsKey(interfaceClassName)){
                interfaceClasses.put(interfaceClassName,new Service(interfaceClass));
            }
            interfaceClasses.get(interfaceClassName).putImpl(referenceName.getClass().getName(),referenceName);
        }finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 服务发现
     *
     * @param interfaceClassName 统一接口名
     * @param referenceName 服务对象名
     * @return 服务对象
     */
    public static  <T> T getService(String interfaceClassName,String referenceName){
        try {
            lock.readLock().lock();
            if(!interfaceClasses.containsKey(interfaceClassName)){
                return null;
            }
            return interfaceClasses.get(interfaceClassName).getImpl(referenceName);
        }finally {
            lock.readLock().unlock();
        }
    }


}
