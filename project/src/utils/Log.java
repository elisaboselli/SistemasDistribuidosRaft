package utils;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

public class Log {
    private List<Entry> entryList;

    public Log() {
        this.entryList = new ArrayList<>();
    }

    public List<Entry> getEntryList() {
        return this.entryList;
    }

    public void appendEntry(Entry entry) {
        this.entryList.add(entry);
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this, Log.class);
    }

    public static Log fromJSON(String jsonStr) {
        Gson gson = new Gson();
        return gson.fromJson(jsonStr, Log.class);
    }

    public void printLog(){
        System.out.println("---------------------------");
        for(Entry e : this.entryList){
            e.printEntry();
            System.out.println("---------------------------");
        }
    }
}
