package settings;

public class RestClient {
    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site";
    private static final String API_VERSION = "/api";
    private static final String AUTH = "/auth";
    private static final String USER_REGISTER = API_VERSION + AUTH + "/register";
    private static final String USER_AUTH = API_VERSION + AUTH + "/login";
    private static final String USER_UPDATE = API_VERSION + AUTH + "/user";
    private static final String ORDERS_ENDPOINT = API_VERSION + "/orders";

    public String getBaseUrl() {return BASE_URL;}
    public String getUserRegister() {return USER_REGISTER;}
    public String getUserAuth() {return USER_AUTH;}
    public String getUserUpdate() {return USER_UPDATE;}
    public String getOrdersEndpoint() {return ORDERS_ENDPOINT;}
}
