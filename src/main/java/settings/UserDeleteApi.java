package settings;

import static io.restassured.RestAssured.given;

public class UserDeleteApi {
    private RestClient restClient = new RestClient();
    public void cleanUp(String bearerToken) {
        if (bearerToken != null) {
            given()
                    .header("Authorization", bearerToken)
                    .when()
                    .delete(restClient.getUserUpdate());
        } else {
            System.out.println("Удалять нечего");
        }
    }
}
