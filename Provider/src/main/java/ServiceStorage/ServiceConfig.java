package ServiceStorage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceConfig<T> {
    T reference;
    Class<?>interfaceClass;

    public ServiceConfig(Class<?>interfaceClass,T reference){
        this.reference=reference;
        this.interfaceClass=interfaceClass;
    }
}
