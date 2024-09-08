import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import settings.RestClient;
import settings.UserRegisterBody;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;


import static io.restassured.RestAssured.given;

public class UserRegisterTest {
    private final RestClient restClient = new RestClient();
    private Response response;
    private UserRegisterBody body;
    private List<UserRegisterBody> regBodies = new ArrayList<>();
    public UserRegisterTest() {
        regBodies.add(new UserRegisterBody("new@user.ru", "12345", "New User"));
        regBodies.add(new UserRegisterBody("existing@user.ru", "123456", "Existing User"));
        regBodies.add(new UserRegisterBody(null, "123456", "NoEmail User"));
        regBodies.add(new UserRegisterBody("nopassword@user.ru", null, "NoPassword User"));
        regBodies.add(new UserRegisterBody("noname@user.ru", "123456", null));

    }

    @Before
    public void setUp() {
        RestAssured.baseURI = restClient.getBaseUrl();
    }

    @Test
    @DisplayName("Тест регистрации нового пользователя")
    public void newUserRegisterTest() {
        body = regBodies.get(0);
        response = getUserRegister(body);
        checkValidResponseBody(response);
        checkStatusCode(response, 200);
    }

    @Test
    @DisplayName("Тест регистрации существующего пользователя")
    public void existingUserRegisterTest() {
        body = regBodies.get(1);
        response = getUserRegister(body);
        checkInvalidResponseBody(response, body);
        checkStatusCode(response, 403);
    }

    @Test
    @DisplayName("Тест регистрации нового пользователя без email")
    public void noEmailRegisterTest() {
        body = regBodies.get(2);
        response = getUserRegister(body);
        checkInvalidResponseBody(response, body);
        checkStatusCode(response, 403);
    }

    @Test
    @DisplayName("Тест регистрации нового пользователя без пароля")
    public void noPasswordRegisterTest() {
        body = regBodies.get(3);
        response = getUserRegister(body);
        checkInvalidResponseBody(response, body);
        checkStatusCode(response, 403);
    }

    @Test
    @DisplayName("Тест регистрации нового пользователя без без имени")
    public void noNameRegisterTest() {
        body = regBodies.get(4);
        response = getUserRegister(body);
        checkInvalidResponseBody(response, body);
        checkStatusCode(response, 403);
    }


    @After
    public void cleanUp() {
        //дописать метод удаления данных пользователя
    }


    @Step("Вызван метод регистрации нового пользователя")
    public Response getUserRegister(UserRegisterBody regBody) {
        response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(regBody)
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
                .body("refreshToken", instanceOf(String.class));
    }

    @Step("Получено ожидаемое тело ответа при попытке регистрации пользователя с некорректными данными")
    public void checkInvalidResponseBody(Response response, UserRegisterBody body) {
        if (!(body.getEmail() == null)
                && !(body.getPassword() == null)
                && !(body.getName() == null)) {
            response.then().assertThat()
                    .body("success", equalTo(false))
                    .body("message", equalTo("User already exists"));
        } else
            if (body.getEmail() == null
                || body.getPassword() == null
                || body.getName() == null) {
            response.then().assertThat()
                    .body("success", equalTo(false))
                    .body("message", equalTo("Email, password and name are required fields"));
        }
    }
}
