package utils;

import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Host {

    private InetAddress address;
    private int port;

    public Host(InetAddress _address, int _port) {
        this.address = _address;
        this.port = _port;
    }

    public InetAddress getAddress() {
        return this.address;
    }

    public void setAddress(InetAddress _address) {
        this.address = _address;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int _port) {
        this.port = _port;
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
