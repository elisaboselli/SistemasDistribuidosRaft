import org.junit.jupiter.api.*;
import utils.Entry;
import utils.Storage;

import java.util.List;

import static org.junit.Assert.*;

public class StorageTest {

    private static Entry entry1, entry2, entry3, entry4;
    Storage storage;

    @BeforeAll
    static void setEntry() {
        entry1 = new Entry(2, 5, 10, 30);
        entry2 = new Entry(3, 6, 11, 40);
        entry3 = new Entry(3, 7, 12, 50);
        entry4 = new Entry(4, 8, 13, 60);

    }

    @BeforeEach
    void setLog() {
        storage = new Storage();
    }

    @Test
    void test_getEntryList_Empty(){
        List<Entry> entryList = storage.getEntryList();
        assertTrue(entryList.isEmpty());
    }

    @Test
    void test_getEntryList(){
        storage.appendEntry(entry1);
        List<Entry> entryList = storage.getEntryList();

        assertTrue(entryList.size()==1);
    }

    @Test
    void test_appendEntry(){

        List<Entry> entryList = storage.getEntryList();
        assertTrue(entryList.isEmpty());

        storage.appendEntry(entry1);

        entryList = storage.getEntryList();
        assertTrue(entryList.size()==1);
        assertTrue(entryList.get(0).equals(entry1));
    }

    @Test
    void test_getLastEntry(){
        storage.appendEntry(entry1);
        storage.appendEntry(entry2);
        storage.appendEntry(entry3);
        storage.appendEntry(entry4);

        Entry entry = storage.getLastEntry();
        assertTrue(entry.equals(entry4));
    }

    @Test
    void test_getEntryByIndex_found(){
        storage.appendEntry(entry1);
        storage.appendEntry(entry2);
        storage.appendEntry(entry3);
        storage.appendEntry(entry4);

        Entry entry = storage.getEntryByIndex(7);
        assertTrue(entry.equals(entry3));
    }

    @Test
    void test_getEntryByIndex_notFound(){
        storage.appendEntry(entry1);
        storage.appendEntry(entry2);
        storage.appendEntry(entry3);
        storage.appendEntry(entry4);

        Entry entry = storage.getEntryByIndex(10);
        assertNull(entry);
    }

    @Test
    void test_toJson(){
        String expected = "{\"entryList\":[{\"term\":3,\"index\":6,\"id\":11,\"value\":40,\"commited\":false,\"quorum\":1},{\"term\":2,\"index\":5,\"id\":10,\"value\":30,\"commited\":false,\"quorum\":1}]}";

        storage.appendEntry(entry1);
        storage.appendEntry(entry2);

        String storageStr = storage.toJson();
        assertEquals(storageStr, expected);
    }

    @Test
    void test_fromJson_Empty(){
        String storageStr = "";

        Storage storage = Storage.fromJSON(storageStr);
        List<Entry> entryList = storage.getEntryList();

        assertTrue(entryList.isEmpty());

    }

    @Test
    void test_fromJson(){
        String storageStr = "{\"entryList\":[{\"term\":3,\"index\":6,\"id\":11,\"value\":40,\"commited\":false," +
                "\"quorum\":1}," +
                "{\"term\":2,\"index\":5,\"id\":10,\"value\":30,\"commited\":false,\"quorum\":1}]}";

        Storage storage = Storage.fromJSON(storageStr);
        List<Entry> entryList = storage.getEntryList();

        assertTrue(entryList.size() == 2);

        Entry first = entryList.get(0);
        assertEquals(first.getTerm(), 3);
        assertEquals(first.getIndex(), 6);

        Entry second = entryList.get(1);
        assertEquals(second.getTerm(), 2);
        assertEquals(second.getIndex(), 5);
    }

    @Test
    void test_getLastIndex(){
        storage.appendEntry(entry1);
        storage.appendEntry(entry2);
        storage.appendEntry(entry3);
        storage.appendEntry(entry4);

        int lastIndex = storage.getLastIndex();
        assertEquals(lastIndex, 8);
    }

    @Test
    void test_getLastIndex_empty(){

        int lastIndex = storage.getLastIndex();
        assertEquals(lastIndex, 0);
    }

    @Test
    void test_getCommitedEntryById(){
        Entry e1 = new Entry(2, 5, 10, 30);
        Entry e2 = new Entry(2, 5, 10, 30);
        e2.commit();
        Entry e3 = new Entry(3, 6, 12, 50);
        Entry e4 = new Entry(3, 6, 12, 50);
        e4.commit();
        Entry e5 = new Entry(4, 7, 10, 20);
        Entry e6 = new Entry(5, 8, 11, 80);

        storage.appendEntry(e1);
        storage.appendEntry(e2);
        storage.appendEntry(e3);
        storage.appendEntry(e4);
        storage.appendEntry(e5);

        // Not exist
        Entry entry = storage.getCommitedEntryById(8);
        assertNull(entry);

        // Not commited
        entry = storage.getCommitedEntryById(11);
        assertNull(entry);

        // Committed is last
        entry = storage.getCommitedEntryById(12);
        assertNotNull(entry);
        assertEquals(entry.getValue(),50);

        // Commited is not last
        entry = storage.getCommitedEntryById(10);
        assertNotNull(entry);
        assertEquals(entry.getValue(),30);

    }
}
