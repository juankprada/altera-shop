package net.juankprada.order;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import net.juankprada.config.WiremockInventoryService;
import net.juankprada.orders.dto.OrderDto;
import net.juankprada.orders.dto.OrderItemDto;
import org.junit.jupiter.api.*;

import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.fail;


@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@QuarkusTestResource(WiremockInventoryService.class)
public class RestAssuredOrderResourceTest {


    @Test
    @Order(1)
    @DisplayName("Test if Application is up by accessing health endpoint")
    public void test_applicationIsUp() {
        try {
            given().when()
                    .get("/q/health")
                    .then()
                    .statusCode(200)
                    .and()
                    .assertThat().body("status", equalTo("UP"));
        } catch (Exception e) {
            fail("Error occurred while tesing application health check", e);
        }
    }

    @Test
    @Order(3)
    @DisplayName("Test Place Order Success")
    public void test_placeOrderSuccess() {

        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setSku("SKU-001");
        orderItemDto.setQuantity(2);

        OrderDto testOrder = new OrderDto();
        testOrder.items = Collections.singletonList(orderItemDto);

        given().contentType("application/json")
                .body(testOrder)
                .post("/api/v1/orders")
                .then()
                .assertThat().statusCode(201);


    }


}
