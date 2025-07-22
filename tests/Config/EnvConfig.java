package tests.Config;

public class EnvConfig {

    // Base URLs
    public static final String BASE_URL = "http://localhost";
    public static final String LOGIN_URL = BASE_URL + "/user/login";
    public static final String NAMESPACES_URL = BASE_URL + "/rep/manage/namespaces";
    public static final String FILES_URL = BASE_URL + "/rep/select/mt/";
    public static final String UPLOAD_URL = BASE_URL + "/rep/manage/addmt/";

    // Credentials
    public static final String USERNAME = "admin";
    public static final String PASSWORD = "admin";

    // Timeout settings
    public static final int DEFAULT_WAIT_SECONDS = 15;
    public static final int MAX_ATTEMPTS = 30;
    public static final int WAIT_BETWEEN_ATTEMPTS_MS = 10000;


    private EnvConfig() {
        // Private constructor to prevent instantiation
    }
}
