package utils;

public final class Constants {

    // Paths
    public static final String FILES_PATH = "textFiles/";
    public static final String STORAGE_PATH = "storage/";
    public static final String LOGS_PATH = "logs/";
    public static final String ALL_SERVERS_FILE = "allServers.txt";

    // Configurations
    public static final int CLIENT_PORT = 6786;
    public static final int SERVERS_QTY = 3;
    public static final int QUORUM = (SERVERS_QTY / 2) + 1;
    public static final int HEART_BEAT = 10000; // 10 seconds

    public static final String SENT = "Sent";
    public static final int NO_TERM = 0;

    // Apps
    public static final String SERVER = "server";
    public static final String CLIENT = "client";

    // States
    public static final String FOLLOWER = "Follower";
    public static final String CANDIDATE = "Candidate";
    public static final String LEADER = "Leader";

    // Message Types
    public static final String EMPTY_MESSAGE = "Empty Message";
    public static final String CLIENT_MESSAGE = "Client Message";
    public static final String HEART_BEAT_MESSAGE = "Heartbeat Message";
    public static final String SERVER_MESSAGE = "Server Message";


    // Server Message Types
    public static final String GET = "get";
    public static final String SET = "set";

    public static final String APPEND = "append";
    public static final String APPEND_SUCCESS = "append_success";
    public static final String APPEND_FAIL = "append_fail";
    public static final String UPDATE_SUCCESS = "update_success";
    public static final String INCONSISTENT_LOG = "inconsistent_log";

    public static final String UPDATE = "update";
    public static final String COMMIT = "commit";
    public static final String COMMIT_SUCCESS = "commit_success";

    public static final String POSTULATION = "postulation";
    public static final String VOTE_OK = "vote_ok";
    public static final String VOTE_REJECT = "vote_reject";

    // Client Message Types
    public static final String NOT_LEADER = "Not leader to process set command";
    public static final String SET_ACCEPTED = "Set: request accepted";
    public static final String GET_FOUND = "Get: id found";
    public static final String GET_NOT_FOUND = "Get: id not found";
}
