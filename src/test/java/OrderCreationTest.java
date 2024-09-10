import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import settings.*;

import java.util.ArrayList;
import java.util.List;

public class OrderCreationTest {
    private RestClient restClient = new RestClient();
    private Response response;
    private UserRegisterBody urb = new UserRegisterBody("new@user.ru", "12345", "New User");
    private UserRegisterTest urt = new UserRegisterTest();
    private UserResponseBody responseBody;
    private UserDeleteApi uda = new UserDeleteApi();
    private String accessToken;
    private OrderCreateBody ocb = new OrderCreateBody();
    private List<OrderCreateBody> orderCreateBodies = new ArrayList<OrderCreateBody>();
    private OrderCreationTest() {
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

    }

    @Test
    @DisplayName("Тест создания заказа без авторизации")
    public void createOrderWithoutAuthTest() {

    }

    @Test
    @DisplayName("Тест создания заказа с ингредиентами")
    public void createOrderWithIngredientsTest() {

    }

    @Test
    @DisplayName("Тест создания заказа без ингредиентов")
    public void createOrderWithoutIngredientsTest() {

    }

    @Test
    @DisplayName("Тест создания заказа с неверным хэшем ингредиентов")
    public void createOrderWithInvalidIngredientsTest() {

    }

    @After
    public void cleanUp() {
        uda.cleanUp(accessToken);
    }
}