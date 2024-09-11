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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;

public class GetOrderByUserTest {
    private RestClient restClient = new RestClient();
    private Response response;
    private UserRegisterBody urb = new UserRegisterBody("new@user.ru", "12345", "New User");
    private UserRegisterTest urt = new UserRegisterTest();
    private UserResponseBody responseBody;
    private UserDeleteApi uda = new UserDeleteApi();
    private String accessToken;
    private OrderCreationTest oct = new OrderCreationTest();
    private OrderCreateBody ocb;
    private List<OrderCreateBody> orderCreateBodies = new ArrayList<OrderCreateBody>();

    public GetOrderByUserTest() {
        orderCreateBodies.add(new OrderCreateBody(new String[] {"61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa70"}));
        orderCreateBodies.add(new OrderCreateBody(new String[] {"61c0c5a71d1f82001bdaaa6c", "61c0c5a71d1f82001bdaaa6e"}));
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = restClient.getBaseUrl();

        response = urt.getUserRegister(urb);
        responseBody = response.body().as(UserResponseBody.class);
        accessToken = responseBody.getAccessToken();

        oct.postCreateOrderWithAuth(orderCreateBodies.get(0));
        oct.postCreateOrderWithAuth(orderCreateBodies.get(1));
    }

    @Test
    @DisplayName("Тест получения списка заказов пользователя, если пользователь авторизован")
    public void getOrderListByAuthUserTest() {
        response = getOrderListByAuthUser();
        checkStatusCode(response, 200);
        checkValidResponseBody(response);
    }

    @Test
    @DisplayName("Тест получения списка заказов пользователя без авторизации")
    public void getOrderListByNoAuthTest() {
        response = getOrderListByNoAuth();
        checkStatusCode(response, 401);
        checkInvalidResponseBody(response);
    }

    @After
    public void cleanUp() {
        uda.cleanUp(accessToken);
    }

    @Step("Вызван метод получения списка заказов авторизованного пользователя")
    public Response getOrderListByAuthUser() {
        response =
                given()
                        .header("Authorization", accessToken)
                        .when()
                        .get(restClient.getOrdersEndpoint());
        return response;
    }

    @Step("Вызван метод получения списка заказов без авторизации")
    public Response getOrderListByNoAuth() {
        response =
                given()
                        .header("Authorization", null)
                        .when()
                        .get(restClient.getOrdersEndpoint());
        return response;
    }

    @Step("В ответе получен ожидаемый статус-код")
    public void checkStatusCode(Response response, int statusCode) {
        response.then().statusCode(statusCode);
    }

    @Step("В ответе получено ожидаемое тело ответа при успешном получении списка заказов авторизованного пользователя")
    public void checkValidResponseBody(Response response) {
        response.then().assertThat()
                .body("success", equalTo(true))
                .body("orders.ingredients", instanceOf(String[].class))
                .body("orders._id", instanceOf(String.class))
                .body("orders.status", instanceOf(String.class))
                .body("orders.createdAt", instanceOf(String.class))
                .body("orders.updatedAt", instanceOf(String.class))
                .body("orders.number", instanceOf(Integer.class))
                .body("total", instanceOf(Integer.class))
                .body("totalToday", instanceOf(Integer.class));
    }

    @Step("Получено ожидаемое тело ответа при попытке получения списка заказов без авторизации")
    public void checkInvalidResponseBody(Response response) {
        response.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

}
