import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.CommonMethods;
import model.Steps;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import settings.*;

import java.util.ArrayList;
import java.util.List;

public class UserLoginTest {
    private Steps step = new Steps();
    private CommonMethods commonMethods = new CommonMethods();
    private Response response;
    private Response regResponse;
    private String accessToken;
    UserRegisterBody regBody = new UserRegisterBody(String.format("%s@user.com", step.getRandomUser()), "12345", step.getRandomUser());
    private UserLoginBody loginBody;
    private List<UserLoginBody> loginBodies = new ArrayList<>();
    public UserLoginTest() {
        loginBodies.add(new UserLoginBody(regBody.getEmail(), regBody.getPassword()));
        loginBodies.add(new UserLoginBody("invalid_email@user.com", regBody.getPassword()));
        loginBodies.add(new UserLoginBody(regBody.getEmail(), "invPass"));
    }

    @Before
    public void setUp() {
        commonMethods.setBaseUrl();
        regResponse = step.getUserRegister(regBody);
    }

    @Test
    @DisplayName("Тест авторизации существующего пользователя с валидными данными")
    public void userValidAuthTest() {
        loginBody = loginBodies.get(0);
        response = step.getUserAuth(loginBody);
        step.checkStatusCode(response, 200);
        step.checkAuthValidResponseBody(response, loginBody, regBody);

        accessToken = step.getAccessToken(regResponse);
    }

    @Test
    @DisplayName("Тест авторизации существующего пользователя с неверным email")
    public void userInvalidEmailAuthTest() {
        loginBody = loginBodies.get(1);
        response = step.getUserAuth(loginBody);
        step.checkStatusCode(response, 401);
        step.checkAuthInvalidResponseBody(response);

        accessToken = step.getAccessToken(regResponse);
    }

    @Test
    @DisplayName("Тест авторизации существующего пользователя с неверным паролем")
    public void userInvalidPasswordAuthTest() {
        loginBody = loginBodies.get(2);
        response = step.getUserAuth(loginBody);
        step.checkStatusCode(response, 401);
        step.checkAuthInvalidResponseBody(response);

        accessToken = step.getAccessToken(regResponse);
    }

    @After
    public void cleanUp() {
        commonMethods.cleanUp(accessToken);
    }
}
