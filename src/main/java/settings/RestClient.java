package settings;

public class RestClient {
    public static final String BASE_URL = "https://stellarburgers.nomoreparties.site";
    private static final String API_VERSION = "/api";
    private static final String AUTH = "/auth";
    public static final String USER_REGISTER = API_VERSION + AUTH + "/register";
    public static final String USER_AUTH = API_VERSION + AUTH + "/login";
    public static final String USER_UPDATE = API_VERSION + AUTH + "/user";
    public static final String ORDERS_ENDPOINT = API_VERSION + "/orders";
    public static final String INGREDIENTS_ENDPOINT = API_VERSION + "/ingredients";
}
