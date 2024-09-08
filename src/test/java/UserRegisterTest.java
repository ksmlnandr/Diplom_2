import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import settings.RestClient;
import settings.UserRegisterBody;

import static org.hamcrest.Matchers.*;


import static io.restassured.RestAssured.given;

public class UserRegisterTest {
    private final RestClient restClient = new RestClient();
    private UserRegisterBody body;
    private Response response;

    @Before
    public void setUp() {
        RestAssured.baseURI = restClient.getBaseUrl();
    }

    @Test
    @DisplayName("Тест регистрации нового пользователя")
    public void newUserRegisterTest() {
        body = setRequestBody("новый пользователь");
        response = getUserRegister(body);
        checkValidResponseBody(response);
        checkStatusCode(response, 200);
    }

    @Test
    @DisplayName("Тест регистрации существующего пользователя")
    public void existingUserRegisterTest() {
        body = setRequestBody("существующий пользователь");
        response = getUserRegister(body);
        checkInvalidResponseBody(response, body);
        checkStatusCode(response, 403);
    }

    @Test
    @DisplayName("Тест регистрации нового пользователя без email")
    public void noEmailRegisterTest() {
        body = setRequestBody("пользователь без email");
        response = getUserRegister(body);
        checkInvalidResponseBody(response, body);
        checkStatusCode(response, 403);
    }

    @Test
    @DisplayName("Тест регистрации нового пользователя без пароля")
    public void noPasswordRegisterTest() {
        body = setRequestBody("пользователь без пароля");
        response = getUserRegister(body);
        checkInvalidResponseBody(response, body);
        checkStatusCode(response, 403);
    }

    @Test
    @DisplayName("Тест регистрации нового пользователя без без имени")
    public void noNameRegisterTest() {
        body = setRequestBody("пользователь без имени");
        response = getUserRegister(body);
        checkInvalidResponseBody(response, body);
        checkStatusCode(response, 403);
    }


    @After
    public void cleanUp() {
        //дописать метод удаления данных пользователя
    }


    @Step("Создано тело запроса")
    public UserRegisterBody setRequestBody(String bodyOption){
        body = null;
        if(bodyOption == "новый пользователь") {
            body = new UserRegisterBody("new@user.ru", "12345", "New User");
        } else if (bodyOption == "существующий пользователь") {
            body = new UserRegisterBody("existing@user.ru", "123456", "Existing User");
        } else if (bodyOption == "пользователь без email") {
            body = new UserRegisterBody(null, "123456", "NoEmail User");
        } else if (bodyOption == "пользователь без пароля") {
            body = new UserRegisterBody("nopassword@user.ru", null, "NoPassword User");
        } else if (bodyOption == "пользователь без имени") {
            body = new UserRegisterBody("noname@user.ru", "123456", null);
        }
        return body;
    }

    @Step("Вызван метод регистрации нового пользователя")
    public Response getUserRegister(UserRegisterBody body) {
        response =
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
    public void checkValidResponseBody(Response response) {
        response.then().assertThat()
                .body("success", equalTo(true))
                .body("user.email", equalTo(body.getEmail()))
                .body("user.name", equalTo(body.getName()))
                .body("accessToken", contains("Bearer"))
                .body("refreshToken", equalTo(String.class));
    }

    @Step("Получено ожидаемое тело ответа при попытке регистрации пользователя с некорректными данными")
    public void checkInvalidResponseBody(Response response, UserRegisterBody body) {
        if(body.getEmail().equals(notNullValue())
                && body.getPassword().equals(notNullValue())
                && body.getName().equals(notNullValue())) {
            response.then().assertThat()
                    .body("success", equalTo(false))
                    .body("message", equalTo("User already exists"));
        } else
            if (body.getEmail().equals(null)
                || body.getPassword().equals(null)
                || body.getName().equals(null)) {
            response.then().assertThat()
                    .body("success", equalTo(false))
                    .body("message", equalTo("Email, password and name are required fields"));
        }
    }
}
