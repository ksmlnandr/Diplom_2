import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import settings.RestClient;
import static org.hamcrest.Matchers.*;


import static io.restassured.RestAssured.given;

public class UserRegisterTest {
    private RestClient restClient = new RestClient();

    @Before
    public void setUp() {
        RestAssured.baseURI = restClient.getBaseUrl();
    }

    @Test
    @DisplayName("Регистрация нового пользователя")
    public void newUserRegisterTest() {
        UserRegisterBody body = setRequestBody("новый пользователь");
        Response response = getUserRegister(body);
        checkResponseBody(response);
    }



    @Step("Создано тело запроса")
    public UserRegisterBody setRequestBody(String bodyOption){
        UserRegisterBody newUser = null;
        if(bodyOption == "новый пользователь") {
            newUser = new UserRegisterBody("new@user.ru", "12345", "New User");
        } else if (bodyOption == "существующий пользователь") {
            newUser = new UserRegisterBody("existing@user.ru", "123456", "Existing User");
        } else if (bodyOption == "пользователь без email") {
            newUser = new UserRegisterBody(null, "123456", "NoEmail User");
        } else if (bodyOption == "пользователь без пароля") {
            newUser = new UserRegisterBody("nopassword@user.ru", null, "NoPassword User");
        } else if (bodyOption == "пользователь без имени") {
            newUser = new UserRegisterBody("noname@user.ru", "1234567", null);
        }
        return newUser;
    }

    @Step("Вызван метод регистрации нового пользователя")
    public Response getUserRegister(UserRegisterBody body) {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(body)
                        .when()
                        .post(restClient.getUserRegister());
        return response;
    }

    @Step("В ответе получен ожидаемый статус-код")
    public void checkStatusCode(Response response, int statusCode) {
        response.then().statusCode(statusCode);
    }

    @Step("Получено ожидаемое тело ответа при успешном создании нового пользователя")
    public void checkResponseBody(Response response) {
        UserRegisterBody body = setRequestBody("новый пользователь");

        response.then().assertThat()
                .body("success", equalTo(true))
                .body("user.email", equalTo(body.getEmail()))
                .body("user.name", equalTo(body.getName()))
                .body("accessToken", contains("Bearer"))
                .body("refreshToken", equalTo(String.class));
    }
}
