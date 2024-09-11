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

public class OrderCreationTest {
    private RestClient restClient = new RestClient();
    private Response response;
    private UserRegisterBody urb = new UserRegisterBody("new@user.ru", "12345", "New User");
    private UserRegisterTest urt = new UserRegisterTest();
    private UserResponseBody responseBody;
    private UserDeleteApi uda = new UserDeleteApi();
    private String accessToken;
    private OrderCreateBody ocb;
    private List<OrderCreateBody> orderCreateBodies = new ArrayList<OrderCreateBody>();
    public OrderCreationTest() {
        orderCreateBodies.add(new OrderCreateBody(new String[] {"61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa70"}));
        orderCreateBodies.add(new OrderCreateBody(new String[] {}));
        orderCreateBodies.add(new OrderCreateBody(new String[] {"incorrectIngredient1", "incorrectIngredient2"}));
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = restClient.getBaseUrl();

        response = urt.getUserRegister(urb);
        responseBody = response.body().as(UserResponseBody.class);
        accessToken = responseBody.getAccessToken();
    }

    @Test
    @DisplayName("Тест создания заказа с авторизацией")
    public void createOrderWithAuthTest() {
        ocb = orderCreateBodies.get(0);
        response = postCreateOrderWithAuth(ocb);
        checkStatusCode(response, 200);
        checkValidResponseBodyWithAuth(response);
    }

    @Test
    @DisplayName("Тест создания заказа без авторизации")
    public void createOrderWithoutAuthTest() {
        ocb = orderCreateBodies.get(0);
        response = postCreateOrderWithoutAuth(ocb);
        checkStatusCode(response, 401);
    }

    @Test
    @DisplayName("Тест создания заказа с ингредиентами")
    public void createOrderWithIngredientsTest() {
        ocb = orderCreateBodies.get(0);
        response = postCreateOrderWithAuth(ocb);
        checkStatusCode(response, 200);
        checkValidResponseBodyWithAuth(response);
    }

    @Test
    @DisplayName("Тест создания заказа без ингредиентов")
    public void createOrderWithoutIngredientsTest() {
        ocb = orderCreateBodies.get(1);
        response = postCreateOrderWithAuth(ocb);
        checkStatusCode(response, 400);
        checkInvalidResponseBodyWithoutIngredients(response);
    }

    @Test
    @DisplayName("Тест создания заказа с неверным хэшем ингредиентов")
    public void createOrderWithInvalidIngredientsTest() {
        ocb = orderCreateBodies.get(2);
        response = postCreateOrderWithAuth(ocb);
        checkStatusCode(response, 500);
    }

    @After
    public void cleanUp() {
        uda.cleanUp(accessToken);
    }

    @Step("Вызван метод создания заказа с авторизацией")
    public Response postCreateOrderWithAuth(OrderCreateBody ocb) {
        response =
                given()
                        .header("Content-type", "application/json")
                        .header("Authorization", accessToken)
                        .and()
                        .body(ocb)
                        .when()
                        .post(restClient.getOrdersEndpoint());
        return response;
    }

    @Step("Вызван метод создания заказа с авторизацией")
    public Response postCreateOrderWithoutAuth(OrderCreateBody ocb) {
        response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(ocb)
                        .when()
                        .post(restClient.getOrdersEndpoint());
        return response;
    }

    @Step("В ответе получен ожидаемый статус-код")
    public void checkStatusCode(Response response, int statusCode) {
        response.then().statusCode(statusCode);
    }


    @Step("Получено ожидаемое тело ответа при успешном создании заказа без авторизации")
    public void checkValidResponseBodyWithAuth(Response response) {
        response.then().assertThat()
                .body("success", equalTo(true))
                .body("name", instanceOf(String.class))
                .body("order.ingredients._id", instanceOf(String.class))
                .body("order.ingredients.name", instanceOf(String.class))
                .body("order.ingredients.type", instanceOf(String.class))
                .body("order.ingredients.proteins", instanceOf(Integer.class))
                .body("order.ingredients.fat", instanceOf(Integer.class))
                .body("order.ingredients.carbohydrates", instanceOf(Integer.class))
                .body("order.ingredients.calories", instanceOf(Integer.class))
                .body("order.ingredients.price", instanceOf(Integer.class))
                .body("order.ingredients.image", instanceOf(String.class))
                .body("order.ingredients.image_mobile", instanceOf(String.class))
                .body("order.ingredients.image_large", instanceOf(String.class))
                .body("order.ingredients.__v", instanceOf(Integer.class))
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
    public void checkInvalidResponseBodyWithoutIngredients(Response response) {
        response.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }
}