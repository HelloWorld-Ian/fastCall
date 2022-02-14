package Core.Pojo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ServerMonitor implements Serializable,Comparable<ServerMonitor> {

    private static final long serialVersionUID = -8892569870391530906L;

    private Integer load;
    private String host;
    private Integer port;


    @Override
    public int compareTo(ServerMonitor o) {
        return this.getLoad().compareTo(o.getPort());
    }

    @Override
    public String toString(){
        return "Monitor [load=" + load + ", host=" + host + ", port="
                + port + "]";
    }

    public byte[] bytes(){
        return toString().getBytes();
    }
}
