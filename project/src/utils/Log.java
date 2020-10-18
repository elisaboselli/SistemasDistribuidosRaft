package utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Log {

    private int term;
    private int index;
    private int id;
    private int value;

    public Log(int term, int index, int id, int value) {
        this.term = term;
        this.index = index;
        this.id = id;
        this.value = value;
    }

    public int getTerm() { return this.term; }

    public int getIndex() { return this.index; }

    public int getId() { return this.id; }

    public int getValue() { return this.value; }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this, Log.class);
    }

    public static Host fromJSON(String jsonStr) {
        Gson gson = new Gson();
        return gson.fromJson(jsonStr, Host.class);
    }

    public static List<Log> fromJSONArray(String jsonStr) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Log>>() {}.getType();
        return gson.fromJson(jsonStr, type);
    }
}
