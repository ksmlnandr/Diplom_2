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

public class OrderCreationTest {
    private CommonMethods commonMethods = new CommonMethods();
    private Steps step = new Steps();
    private Response response;
    private UserRegisterBody regBody = new UserRegisterBody(String.format("%s@user.com", step.getRandomUser()), "12345", step.getRandomUser());
    private String accessToken;
    private OrderCreateBody orderCreateBody;
    private List<OrderCreateBody> orderCreateBodies = new ArrayList<OrderCreateBody>();
    public OrderCreationTest() {
        orderCreateBodies.add(new OrderCreateBody(new String[] {step.getIngredientId(), step.getIngredientId(), step.getIngredientId()}));
        orderCreateBodies.add(new OrderCreateBody(new String[] {}));
        orderCreateBodies.add(new OrderCreateBody(new String[] {"incorrectIngredient1", "incorrectIngredient2"}));
    }

    @Before
    public void setUp() {
        commonMethods.setBaseUrl();
        response = step.getUserRegister(regBody);
        accessToken = step.getAccessToken(response);
    }

    @Test
    @DisplayName("Тест создания заказа с авторизацией")
    public void createOrderWithAuthTest() {
        orderCreateBody = orderCreateBodies.get(0);
        response = step.postCreateOrderWithAuth(orderCreateBody, accessToken);
        step.checkStatusCode(response, 200);
        step.checkOrderValidResponseBodyWithAuth(response);
    }

    @Test
    @DisplayName("Тест создания заказа без авторизации")
    public void createOrderWithoutAuthTest() {
        orderCreateBody = orderCreateBodies.get(0);
        response = step.postCreateOrderWithoutAuth(orderCreateBody);
        step.checkStatusCode(response, 401);
    }

    @Test
    @DisplayName("Тест создания заказа с ингредиентами")
    public void createOrderWithIngredientsTest() {
        orderCreateBody = orderCreateBodies.get(0);
        response = step.postCreateOrderWithAuth(orderCreateBody, accessToken);
        step.checkStatusCode(response, 200);
        step.checkOrderValidResponseBodyWithAuth(response);
    }

    @Test
    @DisplayName("Тест создания заказа без ингредиентов")
    public void createOrderWithoutIngredientsTest() {
        orderCreateBody = orderCreateBodies.get(1);
        response = step.postCreateOrderWithAuth(orderCreateBody, accessToken);
        step.checkStatusCode(response, 400);
        step.checkOrderInvalidResponseBodyWithoutIngredients(response);
    }

    @Test
    @DisplayName("Тест создания заказа с неверным хэшем ингредиентов")
    public void createOrderWithInvalidIngredientsTest() {
        orderCreateBody = orderCreateBodies.get(2);
        response = step.postCreateOrderWithAuth(orderCreateBody, accessToken);
        step.checkStatusCode(response, 500);
    }

    @After
    public void cleanUp() {
        commonMethods.cleanUp(accessToken);
    }
}