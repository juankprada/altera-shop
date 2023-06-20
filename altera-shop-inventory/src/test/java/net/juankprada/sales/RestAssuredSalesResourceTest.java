package net.juankprada.sales;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import net.juankprada.sales.model.Sale;
import org.junit.BeforeClass;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;


@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RestAssuredSalesResourceTest {

    @Inject
    private SaleRepository saleRepository;

    @BeforeClass
    public void setup() {

        Sale sale = new Sale();
        sale.setSku("SKU-001");
        sale.setQuantity(2);
        sale.setValue(100.0);
        sale.setCreatedOn(LocalDate.now());


        saleRepository.persist(sale);

    }


    public void test_getSales() {

        given().get("/api/v1/sales")
                .then()
                .statusCode(200)
                .assertThat().body("sales", hasSize(greaterThanOrEqualTo(1)));
    }


}
