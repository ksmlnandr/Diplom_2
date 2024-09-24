import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.CommonMethods;
import model.Steps;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import settings.UserRegisterBody;

import java.util.ArrayList;
import java.util.List;

public class UserRegisterTest {
    CommonMethods commonMethods = new CommonMethods();
    Steps step = new Steps();
    private Response response;
    private Response regResponse;
    private UserRegisterBody body;
    private String accessToken;
    private List<UserRegisterBody> regBodies = new ArrayList<>();

    public UserRegisterTest() {
        regBodies.add(new UserRegisterBody(String.format("%s@user.ru", step.getRandomUser()), "12345", step.getRandomUser()));
        regBodies.add(new UserRegisterBody(String.format("%s@user.ru", step.getRandomUser()), "12345", step.getRandomUser()));
        regBodies.add(new UserRegisterBody(null, "123456", step.getRandomUser()));
        regBodies.add(new UserRegisterBody(String.format("%s@user.ru", step.getRandomUser()), null, step.getRandomUser()));
        regBodies.add(new UserRegisterBody(String.format("%s@user.ru", step.getRandomUser()), "123456", null));
    }


    @Before
    public void setUrl() {
        commonMethods.setBaseUrl();
    }

    @Test
    @DisplayName("Тест регистрации нового пользователя")
    public void newUserRegisterTest() {
        body = regBodies.get(0);
        response = step.getUserRegister(body);
        step.checkStatusCode(response, 200);
        step.checkRegisterValidResponseBody(response, body);

        accessToken = step.getAccessToken(response);
    }

    @Test
    @DisplayName("Тест регистрации существующего пользователя")
    public void existingUserRegisterTest() {
        body = regBodies.get(1);
        regResponse = step.getUserRegister(body);
        response = step.getUserRegister(body);
        step.checkStatusCode(response, 403);

        accessToken = step.getAccessToken(regResponse);
    }

    @Test
    @DisplayName("Тест регистрации нового пользователя без email")
    public void noEmailRegisterTest() {
        body = regBodies.get(2);
        response = step.getUserRegister(body);
        step.checkStatusCode(response, 403);
        step.checkRegisterInvalidResponseBody(response, body);

        accessToken = step.getAccessToken(response);
    }

    @Test
    @DisplayName("Тест регистрации нового пользователя без пароля")
    public void noPasswordRegisterTest() {
        body = regBodies.get(3);
        response = step.getUserRegister(body);
        step.checkStatusCode(response, 403);
        step.checkRegisterInvalidResponseBody(response, body);

        accessToken = step.getAccessToken(response);
    }

    @Test
    @DisplayName("Тест регистрации нового пользователя без без имени")
    public void noNameRegisterTest() {
        body = regBodies.get(4);
        response = step.getUserRegister(body);
        step.checkStatusCode(response, 403);
        step.checkRegisterInvalidResponseBody(response, body);

        accessToken = step.getAccessToken(response);
    }


    @After
    public void cleanUp() {
        commonMethods.cleanUp(accessToken);
    }
}
