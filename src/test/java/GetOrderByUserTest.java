import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.CommonMethods;
import model.Steps;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import settings.*;


public class GetOrderByUserTest {

    private CommonMethods commonMethods = new CommonMethods();
    private Steps step = new Steps();
    private Response response;
    private UserRegisterBody regBody = new UserRegisterBody(String.format("%s@user.ru", step.getRandomUser()), "12345", step.getRandomUser());
    private String accessToken;
    private OrderCreateBody orderCreateBody;

    @Before
    public void setUp() {
        commonMethods.setBaseUrl();
        response = step.getUserRegister(regBody);
        accessToken = step.getAccessToken(response);

        orderCreateBody = new OrderCreateBody(new String[] {step.getIngredientId(), step.getIngredientId(), step.getIngredientId()});

        step.postCreateOrderWithAuth(orderCreateBody, accessToken);
        step.postCreateOrderWithAuth(orderCreateBody, accessToken);
    }

    @Test
    @DisplayName("Тест получения списка заказов пользователя, если пользователь авторизован")
    public void getOrderListByAuthUserTest() {
        response = step.getOrderListByAuthUser();
        step.checkStatusCode(response, 200);
        step.checkOrderListValidResponseBody(response);
    }

    @Test
    @DisplayName("Тест получения списка заказов пользователя без авторизации")
    public void getOrderListByNoAuthTest() {
        response = step.getOrderListByNoAuth();
        step.checkStatusCode(response, 401);
        step.checkOrderListInvalidResponseBody(response);
    }

    @After
    public void cleanUp() {
        commonMethods.cleanUp(accessToken);
    }
}
