package utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import context.Context;

import java.lang.reflect.Type;
import java.util.List;

public class Entry {

    private int term;
    private int index;
    private int id;
    private int value;
    private boolean commited;
    private int quorum;

    public Entry(int term, int index, int id, int value) {
        this.term = term;
        this.index = index;
        this.id = id;
        this.value = value;
        this.commited = false;
        this.quorum = 1;
    }

    public Entry(Context context, int id, int value){
        this.term = context.getTerm();
        this.index = context.getLogIndex() + 1;
        this.id = id;
        this.value = value;
        this.commited = false;
        this.quorum = 1;
    }

    public int getTerm() { return this.term; }

    public int getIndex() { return this.index; }

    public int getId() { return this.id; }

    public int getValue() { return this.value; }

    public String getIndexStr() { return String.valueOf(this.index); }

    public String getTermStr() { return String.valueOf(this.term); }

    public String getIdStr() { return String.valueOf(this.id); }

    public String getValueStr() { return String.valueOf(this.value); }

    public void commit() { this.commited = true; }

    public boolean isCommited() { return this.commited; }

    public int getQuorum() { return this.quorum; }

    public void updateQuorum() { this.quorum++; }


    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this, Entry.class);
    }

    public static Host fromJSON(String jsonStr) {
        Gson gson = new Gson();
        return gson.fromJson(jsonStr, Host.class);
    }

    public static List<Entry> fromJSONArray(String jsonStr) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Entry>>() {}.getType();
        return gson.fromJson(jsonStr, type);
    }

    public void printEntry(){
        System.out.println("Index: " + this.index);
        System.out.println("Term: " + this.term);
        System.out.println("Id: " + this.id);
        System.out.println("Value: " + this.value);
    }
}
