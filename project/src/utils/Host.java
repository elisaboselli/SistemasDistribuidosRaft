package utils;

import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Host {

    private InetAddress address;
    private int port;
    private Boolean isLeader;

    public Host(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress() {
        return this.address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setIsLeader(Boolean isLeader){
        this.isLeader = isLeader;
    }

    public Boolean getIsLeader() {
        return this.isLeader;
    }

    @Override
    public String toString() {
        return "Host [address = " + this.address + ", port = " + this.port + "]";
    }

    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this, Host.class);
    }

    public static Host fromJSON(String jsonStr) {
        Gson gson = new Gson();
        return gson.fromJson(jsonStr, Host.class);
    }

    public static List<Host> fromJSONArray(String jsonStr) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Host>>() {}.getType();
        return gson.fromJson(jsonStr, type);
    }

}
