package model;


import io.qameta.allure.Step;
import io.restassured.response.Response;
import settings.OrderCreateBody;
import settings.UserLoginBody;
import settings.UserRegisterBody;
import settings.UserResponseBody;

import java.util.ArrayList;

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

    //шаги для тестов на авторизацию пользователя
    @Step("Вызван метод авторизации пользователя")
    public Response getUserAuth(UserLoginBody loginBody) {
        response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(loginBody)
                        .when()
                        .post(USER_AUTH);
        return response;
    }

    @Step("Получено ожидаемое тело ответа при успешной авторизации пользователя")
    public void checkAuthValidResponseBody(Response response, UserLoginBody loginBody, UserRegisterBody regBody) {
        response.then().assertThat()
                .body("success", equalTo(true))
                .body("user.email", equalTo(loginBody.getEmail()))
                .body("user.name", equalTo(regBody.getName()))
                .body("accessToken", startsWith("Bearer"))
                .body("refreshToken", instanceOf(String.class));
    }

    @Step("Получено ожидаемое тело ответа при попытке авторизации пользователя с некорректными данными")
    public void checkAuthInvalidResponseBody(Response response) {
        response.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    //шаги для тестов на оформление нового заказа
    @Step("Вызван метод создания заказа с авторизацией")
    public Response postCreateOrderWithAuth(OrderCreateBody orderCreateBody, String accessToken) {
        response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .header("Authorization", accessToken)
                        .and()
                        .body(orderCreateBody)
                        .when()
                        .post(ORDERS_ENDPOINT);
        return response;
    }

    @Step("Вызван метод создания заказа без авторизации")
    public Response postCreateOrderWithoutAuth(OrderCreateBody ocb) {
        response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(ocb)
                        .when()
                        .post(ORDERS_ENDPOINT);
        return response;
    }

    @Step("Получено ожидаемое тело ответа при успешном создании заказа без авторизации")
    public void checkOrderValidResponseBodyWithAuth(Response response) {
        response.then().assertThat()
                .body("success", equalTo(true))
                .body("name", instanceOf(String.class))
                .body("order.ingredients._id", instanceOf(ArrayList.class))
                .body("order.ingredients.name", instanceOf(ArrayList.class))
                .body("order.ingredients.type", instanceOf(ArrayList.class))
                .body("order.ingredients.proteins", instanceOf(ArrayList.class))
                .body("order.ingredients.fat", instanceOf(ArrayList.class))
                .body("order.ingredients.carbohydrates", instanceOf(ArrayList.class))
                .body("order.ingredients.calories", instanceOf(ArrayList.class))
                .body("order.ingredients.price", instanceOf(ArrayList.class))
                .body("order.ingredients.image", instanceOf(ArrayList.class))
                .body("order.ingredients.image_mobile", instanceOf(ArrayList.class))
                .body("order.ingredients.image_large", instanceOf(ArrayList.class))
                .body("order.ingredients.__v", instanceOf(ArrayList.class))
                .body("order._id", instanceOf(String.class))
                .body("order.owner.name", instanceOf(String.class))
                .body("order.owner.email", instanceOf(String.class))
                .body("order.owner.createdAt", instanceOf(String.class))
                .body("order.owner.updatedAt", instanceOf(String.class))
                .body("order.status", instanceOf(String.class))
                .body("order.name", instanceOf(String.class))
                .body("order.createdAt", instanceOf(String.class))
                .body("order.updatedAt", instanceOf(String.class))
                .body("order.number", instanceOf(Integer.class))
                .body("order.price", instanceOf(Integer.class));
    }

    @Step("Получено ожидаемое тело ответа при попытке создания заказа без ингредиентов")
    public void checkOrderInvalidResponseBodyWithoutIngredients(Response response) {
        response.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }
}