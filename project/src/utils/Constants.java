package utils;

public final class Constants {

    // private void Constants(){}

    public static final String ALL_SERVERS_FILE = "textFiles/allServers.txt";
    public static final int SERVERS_QTY = 5;
    public static final int QUORUM = (SERVERS_QTY / 2) + 1;

    public static final int MIN_TIMEOUT = 60000; // 1 minute
    public static final int MAX_TIMEOUT = 120000; // 2 minutes
    public static final int HEART_BEAT = 10000; // 10 seconds

    // Message Types
    public static final String CLIENT_MESSAGE = "client_message";
    public static final String HEART_BEAT_MESSAGE = "heart_beat_message";
    public static final String SERVER_MESSAGE = "server_message";

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
}
