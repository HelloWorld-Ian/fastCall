package Core.Client;

import Annotation.FastConsumer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import java.lang.reflect.Field;

@Component
@ConditionalOnClass(FastConsumer.class)
public class ConsumerConfiguration {

    @Autowired
    private Proxy proxy;

    /**
     * 设置动态代理
     */
    @Bean
    public BeanPostProcessor beanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName)
                    throws BeansException {
               Class<?>beanClass=bean.getClass();
               for(Field field:beanClass.getDeclaredFields()){
                   FastConsumer consumer=field.getAnnotation(FastConsumer.class);
                   if(null != consumer){
                       Class<?> type = field.getType();
                       field.setAccessible(true);
                       try {
                           field.set(bean,proxy.proxy(type, consumer.providerName()));
                       } catch (IllegalAccessException e) {
                           e.printStackTrace();
                       }finally {
                           field.setAccessible(false);
                       }
                   }
               }
               return bean;
            }

            @Override
            public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
                return o;
            }
        };
    }
}
