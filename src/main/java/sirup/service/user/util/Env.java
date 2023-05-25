package sirup.service.user.util;

public class Env {
    public static final String USER_DB_ADDRESS;
    public static final int USER_DB_PORT;
    public static final String USER_DB_NAME;
    public static final String USER_DB_USERNAME;
    public static final String USER_DB_PASSWORD;
    public static final int USER_PORT;
    public static final String AUTH_ADDRESS;
    public static final int AUTH_PORT;
    public static final String LOG_ADDRESS;
    public static final int LOG_PORT;
    public static final String NOTI_ADDRESS;
    public static final int NOTI_PORT;
    static {
        USER_DB_ADDRESS = System.getenv("USER_DB_ADDRESS");
        USER_DB_PORT = Integer.parseInt(System.getenv("USER_DB_PORT"));
        USER_DB_NAME = System.getenv("USER_DB_NAME");
        USER_DB_USERNAME = System.getenv("USER_DB_USERNAME");
        USER_DB_PASSWORD = System.getenv("USER_DB_PASSWORD");
        USER_PORT = Integer.parseInt(System.getenv("USER_PORT"));
        AUTH_ADDRESS = System.getenv("AUTH_ADDRESS");
        AUTH_PORT = Integer.parseInt(System.getenv("AUTH_PORT"));
        LOG_ADDRESS = System.getenv("LOG_ADDRESS");
        LOG_PORT = Integer.parseInt(System.getenv("LOG_PORT"));
        NOTI_ADDRESS = System.getenv("NOTI_ADDRESS");
        NOTI_PORT = Integer.parseInt(System.getenv("NOTI_PORT"));
    }
}
