package model;


import io.qameta.allure.Step;
import io.restassured.response.Response;
import settings.UserRegisterBody;
import settings.UserResponseBody;

import static settings.RestClient.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

public class Steps {
    private Response response;
    private String accessToken;

    //общий шаг для всех тестов
    @Step("В ответе получен ожидаемый статус-код")
    public void checkStatusCode(Response response, int statusCode) {
        response.then().statusCode(statusCode);
    }

    //метод получения токена пользователя
    public String getAccessToken(Response response) {
        boolean success = (response.body().as(UserResponseBody.class)).isSuccess();

        if (success) {
            accessToken = (response.body().as(UserResponseBody.class)).getAccessToken();
        } else {
            accessToken = null;
        }
        return accessToken;
    }

    //шаги для тестов на регистрацию нового пользователя
    @Step("Вызван метод регистрации нового пользователя")
    public Response getUserRegister(UserRegisterBody regBody) {
        response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(regBody)
                        .when()
                        .post(USER_REGISTER);
        return response;
    }

    @Step("Получено ожидаемое тело ответа при успешном создании нового пользователя")
    public void checkRegisterValidResponseBody(Response response, UserRegisterBody userRegisterBody) {
        response.then().assertThat()
                .body("success", equalTo(true))
                .body("user.email", equalTo(userRegisterBody.getEmail()))
                .body("user.name", equalTo(userRegisterBody.getName()))
                .body("accessToken", startsWith("Bearer"))
                .body("refreshToken", instanceOf(String.class));
    }

    @Step("Получено ожидаемое тело ответа при попытке регистрации пользователя с некорректными данными")
    public void checkRegisterInvalidResponseBody(Response response, UserRegisterBody userRegisterBody) {
        if (!(userRegisterBody.getEmail() == null)
                && !(userRegisterBody.getPassword() == null)
                && !(userRegisterBody.getName() == null)) {
            response.then().assertThat()
                    .body("success", equalTo(false))
                    .body("message", equalTo("User already exists"));
        } else
        if (userRegisterBody.getEmail() == null
                || userRegisterBody.getPassword() == null
                || userRegisterBody.getName() == null) {
            response.then().assertThat()
                    .body("success", equalTo(false))
                    .body("message", equalTo("Email, password and name are required fields"));
        }
    }
}