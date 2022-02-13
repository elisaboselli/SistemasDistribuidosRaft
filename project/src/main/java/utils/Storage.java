package utils;

import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Storage {
    private List<Entry> entryList;

    public Storage() {
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

    public void addEntry(Entry entry){
        this.entryList.add(entry);
    }

    public Entry getEntryByIndex(int index){
        for(Entry entry : entryList) {
            if(entry.getIndex() == index){
                return entry;
            }
        }
        return null;
    }

    public Entry getNewestEntryById(int id){
        for(Entry entry : entryList) {
            if(entry.getId() == id) {
                return entry;
            }
        }
        return null;
    }

    public Entry getCommitedEntryById(int id){
        for(Entry entry : entryList) {
            if(entry.getId() == id && entry.isCommited()) {
                return entry;
            }
        }
        return null;
    }

    public Entry getOldestUncommitedEntry() {
        int size = entryList.size();
        for(int i = size-1; i>=0 ; i-- ) {
            Entry entry = entryList.get(i);
            if(!entry.isCommited()) {
                return entry;
            }
        }
        return null;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this, Storage.class);
    }

    public static Storage q(String jsonStr) {
        if(jsonStr.isEmpty()){
            return new Storage();
        }
        Gson gson = new Gson();
        return gson.fromJson(jsonStr, Storage.class);
    }

    public JSONArray toJsonArray() {
        return new JSONArray(entryList);
    }

    public static Storage fromJsonArray(String jsonStr){
        Storage storage = new Storage();

        if(jsonStr != null && !jsonStr.equals("")){
            JSONArray jsonArray = new JSONArray(jsonStr);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonEntry = jsonArray.getJSONObject(i);
                Entry entry = Entry.fromJSON(jsonEntry);
                storage.addEntry(entry);
            }
        }

        return storage;
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
