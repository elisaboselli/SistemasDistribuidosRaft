package messages;

import java.net.InetAddress;

import com.google.gson.Gson;

public class ClientMessage {

  private String type;
  private int id;
  private int value;
  private int port;
  private InetAddress address;

  public ClientMessage() {
  }

  public void setType(String _type) {
    this.type = _type;
  }

  public void setId(int _id) {
    this.id = _id;
  }

  public void setValue(int _value) {
    this.value = _value;
  }

  public void setPort(int _port) {
    this.port = _port;
  }

  public void setAddress(InetAddress _address) {
    this.address = _address;
  }

  public String toJSON() {
    Gson gson = new Gson();
    return gson.toJson(this, ClientMessage.class);
  }

}
