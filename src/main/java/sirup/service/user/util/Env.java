package sirup.service.user.util;

import io.github.cdimascio.dotenv.Dotenv;

public class Env {
    public static final String POSTGRES_CONNECTION_STRING;
    public static final String POSTGRES_USER;
    public static final String POSTGRES_PASSWORD;
    public static final String API_BASE_URL;
    public static final int API_PORT;
    public static final String AUTH_URL;
    public static final int AUTH_PORT;
    public static final String LOG_URL;
    public static final int LOG_PORT;
    static {
        Dotenv dotenv = Dotenv.load();
        POSTGRES_CONNECTION_STRING = dotenv.get("POSTGRES_CONNECTION_STRING");
        POSTGRES_USER = dotenv.get("POSTGRES_USER");
        POSTGRES_PASSWORD = dotenv.get("POSTGRES_PASSWORD");
        API_BASE_URL = dotenv.get("API_BASE_URL");
        API_PORT = Integer.parseInt(dotenv.get("API_PORT"));
        AUTH_URL = dotenv.get("AUTH_URL");
        AUTH_PORT = Integer.parseInt(dotenv.get("AUTH_PORT"));
        LOG_URL = dotenv.get("LOG_URL");
        LOG_PORT = Integer.parseInt(dotenv.get("LOG_PORT"));
    }
}
