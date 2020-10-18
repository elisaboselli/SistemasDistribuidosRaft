package utils;

public final class Constants {

    // private void Constants(){}
    public static final String FILES_PATH = "project/textFiles/";
    public static final String ALL_SERVERS_FILE = "allServers.txt";
    public static final int SERVERS_QTY = 5;
    public static final int QUORUM = (SERVERS_QTY / 2) + 1;

    public static final int MIN_TIMEOUT = 60000; // 1 minute
    public static final int MAX_TIMEOUT = 120000; // 2 minutes
    // public static final int HEART_BEAT = 10000; // 10 seconds
    public static final int HEART_BEAT = 5000; // 5 seconds

    public static final int NO_TERM = 0;

    // Message Types
    public static final String EMPTY_MESSAGE = "Empty Message";
    public static final String CLIENT_MESSAGE = "Client Message";
    public static final String CLIENT_GET_MESSAGE = "Client Get Message";
    public static final String CLIENT_SET_MESSAGE = "Client Set Message";
    public static final String HEART_BEAT_MESSAGE = "Heartbeat Message";
    public static final String SERVER_MESSAGE = "Server Message";

    // Client Message Types
    public static final String CLIENT_GET = "get";
    public static final String CLIENT_SET = "set";

    // Server Message Types
    public static final String GET_RESPONSE = "get_response";
    public static final String SET_RESPONSE = "set_response";
    public static final String UPDATE = "update";
    public static final String UPDATE_OK = "update_ok";
    public static final String COMMIT = "commit";
    public static final String COMMIT_OK = "commit_ok";
    public static final String POSTULATION = "postulation";
    public static final String VOTE_OK = "vote_ok";
    public static final String VOTE_REJECT = "vote_reject";
    public static final String NOT_LEADER = "Not leader to process set command";
}
