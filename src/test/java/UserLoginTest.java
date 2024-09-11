import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import settings.*;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserLoginTest {

    private Response response;
    private final RestClient restClient = new RestClient();
    private UserRegisterTest urt = new UserRegisterTest();
    private UserDeleteApi uda = new UserDeleteApi();
    private String token;
    UserRegisterBody regBody = new UserRegisterBody("new@user.com", "12345", "New User");
    private UserLoginBody loginBody;
    private UserResponseBody responseBody;
    private List<UserLoginBody> loginBodies = new ArrayList<>();
    public UserLoginTest() {
        loginBodies.add(new UserLoginBody(regBody.getEmail(), regBody.getPassword()));
        loginBodies.add(new UserLoginBody("invalid_email@user.com", regBody.getPassword()));
        loginBodies.add(new UserLoginBody(regBody.getEmail(), "invPass"));
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = restClient.getBaseUrl();

        urt.getUserRegister(regBody);
    }

    @Test
    @DisplayName("Тест авторизации существующего пользователя с валидными данными")
    public void userValidAuthTest() {
        loginBody = loginBodies.get(0);
        response = getUserAuth(loginBody);
        checkStatusCode(response, 200);
        checkValidResponseBody(response);
    }

    @Test
    @DisplayName("Тест авторизации существующего пользователя с неверным email")
    public void userInvalidEmailAuthTest() {
        loginBody = loginBodies.get(1);
        response = getUserAuth(loginBody);
        checkStatusCode(response, 401);
        checkInvalidResponseBody(response);
    }

    @Test
    @DisplayName("Тест авторизации существующего пользователя с неверным паролем")
    public void userInvalidPasswordAuthTest() {
        loginBody = loginBodies.get(2);
        response = getUserAuth(loginBody);
        checkStatusCode(response, 401);
        checkInvalidResponseBody(response);
    }

    @After
    public void cleanUp() {
        uda.cleanUp(token);
    }

    @Step("Вызван метод авторизации пользователя")
    public Response getUserAuth(UserLoginBody loginBody) {
        response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(loginBody)
                        .when()
                        .post(restClient.getUserAuth());
        return response;
    }

    @Step("В ответе получен ожидаемый статус-код")
    public void checkStatusCode(Response response, int statusCode) {
        response.then().statusCode(statusCode);
    }

    @Step("Получено ожидаемое тело ответа при успешной авторизации пользователя")
    public void checkValidResponseBody(Response response) {
        response.then().assertThat()
                .body("success", equalTo(true))
                .body("user.email", equalTo(loginBody.getEmail()))
                .body("user.name", equalTo(regBody.getName()))
                .body("accessToken", startsWith("Bearer"))
                .body("refreshToken", instanceOf(String.class));

        responseBody = response.body().as(UserResponseBody.class);
        token = responseBody.getAccessToken();
    }

    @Step("Получено ожидаемое тело ответа при попытке авторизации пользователя с некорректными данными")
    public void checkInvalidResponseBody(Response response) {
        response.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));

        token = null;
    }
}
