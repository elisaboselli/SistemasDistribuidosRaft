package messages;

import com.google.gson.Gson;

import utils.Constants;

public class ServerRPC {

	private int term;
    private String type;
    private int from;
    private int to;
    
    public ServerRPC(int from, int to, int term, String type){
        this.type = type;
        this.from = from;
        this.to = to;
        this.term = term;
    }

    public int getTerm() {
		return term;
	}

	public void setTerm(int term) {
		this.term = term;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getTo() {
		return to;
	}

	public void setTo(int to) {
		this.to = to;
	}

	public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this, ClientMessage.class);
    }
}
