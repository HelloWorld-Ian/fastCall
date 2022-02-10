package Core.Server;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理bean对象,私有构造方法不可初始化
 */
@SuppressWarnings("unchecked")
public class BeanFactory {

    private BeanFactory(){

    }

    private static final Map<Class<?>,Object> beans=new HashMap<>();

    public static void addBeans(Class<?>type,Object obj){
        beans.put(type,obj);
    }

    public static  <T> T getBean(Class<T>type){
        if(!beans.containsKey(type)){
            return null;
        }
        return (T)beans.get(type);
    }

}
