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

    public Entry getLastEntry(){
        if(entryList.isEmpty()) { return null; }
        return entryList.get(0);
    }

    public void appendEntry(Entry entry) {
        this.entryList.add(0, entry);
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this, Log.class);
    }

    public static Log fromJSON(String jsonStr) {
        if(jsonStr.isEmpty()){
            return new Log();
        }
        Gson gson = new Gson();
        return gson.fromJson(jsonStr, Log.class);
    }

    public int getLastIndex() {
        if(entryList.isEmpty()) { return 0; }
        return entryList.get(0).getIndex();
    }

    public void printLog(){
        System.out.println("---------------------------");
        for(Entry e : this.entryList){
            e.printEntry();
            System.out.println("---------------------------");
        }
    }
}
