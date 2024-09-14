package model;

import io.restassured.RestAssured;
import settings.UserDeleteApi;

import static settings.RestClient.*;

public class CommonMethods {
    UserDeleteApi userDeleteApi = new UserDeleteApi();
    public void setBaseUrl() {
        RestAssured.baseURI = BASE_URL;
    }

    public void cleanUp(String bearerToken) {
        userDeleteApi.cleanUp(bearerToken);
    }
}
