package net.juankprada.inventory;

import io.quarkus.test.junit.QuarkusTest;
import net.juankprada.inventory.model.Category;
import net.juankprada.inventory.model.Item;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.fail;


@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RestAssuredInventoryResourceTest {


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
    @DisplayName("Test Add Inventory Item Success")
    public void test_addInventoryItemSuccess() {
        Item testItem = new Item(
                "SKU-001",
                "Catalog Item - 1",
                "Catalog Desc - 1",
                Category.ICECREAM.getValue(),
                10.00,
                10
        );

        given().contentType("application/json")
                .body(testItem)
                .post("/api/v1/inventory")
                .then()
                .assertThat().statusCode(201);


    }

    @Test
    @Order(4)
    @DisplayName("Test Get Inventory Item Success")
    public void test_getInventoryItemSuccess() {
        given().get("/api/v1/inventory/SKU-001")
                .then()
                .statusCode(200)
                .assertThat().body("sku", equalTo("SKU-001"))
                .and()
                .assertThat().body("name", equalTo("Catalog Item - 1"))
                .assertThat().body("description", equalTo("Catalog Desc - 1"))
                .assertThat().body("category", equalTo(Category.ICECREAM.getValue()))
                .assertThat().body("price", equalTo(10.00F))
                .assertThat().body("inventory", equalTo(10));
    }


    @Test
    @Order(5)
    @DisplayName("Test Update Inventory Item Success")
    public void test_updateInventoryItemSuccess() {
        Item testItem = new Item(
                "SKU-001",
                "Catalog Item - 2",
                "Catalog Desc - 2",
                Category.ICECREAM.getValue(),
                20.00,
                30
        );

        given().contentType("application/json")
                .body(testItem)
                .put("/api/v1/inventory/SKU-001")
                .then()
                .assertThat().statusCode(200);

        given().get("/api/v1/inventory/SKU-001")
                .then()
                .statusCode(200)
                .assertThat().body("sku", equalTo("SKU-001"))
                .and()
                .assertThat().body("name", equalTo("Catalog Item - 2"))
                .assertThat().body("description", equalTo("Catalog Desc - 2"))
                .assertThat().body("category", equalTo(Category.ICECREAM.getValue()))
                .assertThat().body("price", equalTo(20.00F))
                .assertThat().body("inventory", equalTo(30));
    }


    @Test
    @Order(6)
    @DisplayName("Test Get Inventory Items By Category")
    public void test_addInventoryItemByCategorySuccess() {
        Item testItem1 = new Item(
                "SKU-002",
                "Catalog Item - 3",
                "Catalog Desc - 3",
                Category.ICECREAM.getValue(),
                10.00,
                10
        );
        given().contentType("application/json")
                .body(testItem1)
                .post("/api/v1/inventory")
                .then()
                .assertThat().statusCode(201);

        Item testItem2 = new Item(
                "SKU-003",
                "Catalog Item - 4",
                "Catalog Desc - 4",
                Category.SNACK_BAR.getValue(),
                120.00,
                40
        );

        given().contentType("application/json")
                .body(testItem2)
                .post("/api/v1/inventory")
                .then()
                .assertThat().statusCode(201);

        given().get("/api/v1/inventory/category/SNACK_BAR")
                .then()
                .statusCode(200)
                .assertThat().body("$", hasSize(1))
                .and().assertThat().body("[0].sku", equalTo("SKU-003"));
    }

    @Test
    @Order(7)
    @DisplayName("Test Get Inventory Items - Non Empty Response")
    public void test_getInventoryItemsNonEmptyResponse() {
        given().get("/api/v1/inventory/")
                .then()
                .statusCode(200)
                .assertThat().body("$", hasSize(greaterThanOrEqualTo(3)))
                .and().assertThat().body("$", hasSize(greaterThanOrEqualTo(3)));

    }

    @Test
    @Order(8)
    @DisplayName("Test Delete Inventory Items")
    public void test_deleteInventoryItems() {
        given().delete("/api/v1/inventory/SKU-003")
                .then()
                .statusCode(204);

        given().get("/api/v1/inventory/SKU-003")
                .then()
                .statusCode(404);

    }


}
