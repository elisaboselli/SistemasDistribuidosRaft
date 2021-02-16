import org.junit.jupiter.api.*;
import utils.Entry;
import utils.Log;

import java.util.List;

import static org.junit.Assert.*;

public class LogTest {

    private static Entry entry1, entry2, entry3, entry4;
    Log log;

    @BeforeAll
    static void setEntry() {
        entry1 = new Entry(2, 5, 10, 30);
        entry2 = new Entry(3, 6, 11, 40);
        entry3 = new Entry(3, 7, 12, 50);
        entry4 = new Entry(4, 8, 13, 60);

    }

    @BeforeEach
    void setLog() {
        log = new Log();
    }

    @Test
    void test_getEntryList_Empty(){
        List<Entry> entryList = log.getEntryList();
        assertTrue(entryList.isEmpty());
    }

    @Test
    void test_getEntryList(){
        log.appendEntry(entry1);
        List<Entry> entryList = log.getEntryList();

        assertTrue(entryList.size()==1);
    }

    @Test
    void test_appendEntry(){

        List<Entry> entryList = log.getEntryList();
        assertTrue(entryList.isEmpty());

        log.appendEntry(entry1);

        entryList = log.getEntryList();
        assertTrue(entryList.size()==1);
        assertTrue(entryList.get(0).equals(entry1));
    }

    @Test
    void test_getLastEntry(){
        log.appendEntry(entry1);
        log.appendEntry(entry2);
        log.appendEntry(entry3);
        log.appendEntry(entry4);

        Entry entry = log.getLastEntry();
        assertTrue(entry.equals(entry4));
    }

    @Test
    void test_getEntryByIndex_found(){
        log.appendEntry(entry1);
        log.appendEntry(entry2);
        log.appendEntry(entry3);
        log.appendEntry(entry4);

        Entry entry = log.getEntryByIndex(7);
        assertTrue(entry.equals(entry3));
    }

    @Test
    void test_getEntryByIndex_notFound(){
        log.appendEntry(entry1);
        log.appendEntry(entry2);
        log.appendEntry(entry3);
        log.appendEntry(entry4);

        Entry entry = log.getEntryByIndex(10);
        assertNull(entry);
    }

    @Test
    void test_toJson(){
        String expected = "{\"entryList\":[{\"term\":3,\"index\":6,\"id\":11,\"value\":40,\"commited\":false,\"quorum\":1},{\"term\":2,\"index\":5,\"id\":10,\"value\":30,\"commited\":false,\"quorum\":1}]}";

        log.appendEntry(entry1);
        log.appendEntry(entry2);

        String logStr = log.toJson();
        assertEquals(logStr, expected);
    }

    @Test
    void test_fromJson_Empty(){
        String logStr = "";

        Log log = Log.fromJSON(logStr);
        List<Entry> entryList = log.getEntryList();

        assertTrue(entryList.isEmpty());

    }

    @Test
    void test_fromJson(){
        String logStr = "{\"entryList\":[{\"term\":3,\"index\":6,\"id\":11,\"value\":40,\"commited\":false," +
                "\"quorum\":1}," +
                "{\"term\":2,\"index\":5,\"id\":10,\"value\":30,\"commited\":false,\"quorum\":1}]}";

        Log log = Log.fromJSON(logStr);
        List<Entry> entryList = log.getEntryList();

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
        log.appendEntry(entry1);
        log.appendEntry(entry2);
        log.appendEntry(entry3);
        log.appendEntry(entry4);

        int lastIndex = log.getLastIndex();
        assertEquals(lastIndex, 8);
    }

    @Test
    void test_getLastIndex_empty(){

        int lastIndex = log.getLastIndex();
        assertEquals(lastIndex, 0);
    }
}
