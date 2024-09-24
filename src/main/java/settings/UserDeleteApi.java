package settings;

import static io.restassured.RestAssured.given;
import static settings.RestClient.*;

public class UserDeleteApi {
    public void cleanUp(String accessToken) {
        if (accessToken != null) {
            given()
                    .header("Authorization", accessToken)
                    .when()
                    .delete(USER_UPDATE);
        } else {
            System.out.println("Удалять нечего");
        }
    }
}
