package Core.Pojo;

import lombok.Getter;
import lombok.Setter;

import java.io.*;

@Getter
@Setter
public class ServerMonitor implements Serializable,Comparable<ServerMonitor> {

    private static final long serialVersionUID = -8892569870391530906L;

    private Integer load;
    private String host;
    private Integer port;
    private String groupName;


    @Override
    public int compareTo(ServerMonitor o) {
        return this.getLoad().compareTo(o.getPort());
    }

    @Override
    public String toString(){
        return "Monitor [load=" + load + ", host=" + host + ", port="
                + port +", group="+groupName +"]";
    }

    public byte[] bytes(){
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        try {
            ObjectOutputStream objOut=new ObjectOutputStream(outputStream);
            objOut.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    public static ServerMonitor toObj(byte[]bytes){
        try {
            ObjectInputStream in=new ObjectInputStream(new ByteArrayInputStream(bytes));
            return (ServerMonitor) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
