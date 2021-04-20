package example;

import org.json.JSONArray;
import utils.Entry;
import utils.JSONUtils;
import utils.Storage;

public class StorageExample {

    public static void main(String args[]) {
        String fileName = JSONUtils.getFileName("prueba", true, true);
        JSONUtils.createStorageFile(fileName);

        Storage storage = new Storage();

        Entry entry1 = new Entry(1,1,1,1);
        storage.appendEntry(entry1);

        JSONArray storageStr = storage.toJsonArray();
        JSONUtils.writeStorageFile(fileName, storageStr);

        Storage readedStorage = JSONUtils.readStorageFile(fileName);

        if(readedStorage.getEntryList().size() == 1) {
            System.out.println("First Read");
            readedStorage.printLog();
        }

        Entry entry2 = new Entry(1, 2, 2,2);
        storage.appendEntry(entry2);

        storageStr = storage.toJsonArray();
        JSONUtils.writeStorageFile(fileName, storageStr);

        readedStorage = JSONUtils.readStorageFile(fileName);
        if(readedStorage.getEntryList().size() == 2) {
            System.out.println("\n\nSecond Read");
            readedStorage.printLog();
        }
    }
}
