package net.juankprada.event;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import net.juankprada.config.KafkaTestResource;
import net.juankprada.config.OrderMessageSerializer;
import net.juankprada.inventory.model.Category;
import net.juankprada.inventory.model.Item;
import net.juankprada.inventory.model.OrderMessage;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.fail;

@QuarkusTestResource(KafkaTestResource.class)
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderPlacedReceivedEventTest {

    private Logger log = LoggerFactory.getLogger(OrderPlacedReceivedEventTest.class);

    public static Producer<String, OrderMessage> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getProperty("kafka.bootstrap.servers"));
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "order-placed");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, OrderMessageSerializer.class.getName());

        return new KafkaProducer<String, OrderMessage>(props);
    }

    @Test
    @Order(99)
    public void testOrderPlacedIncomingEvent() throws Exception {

        String sku = "SKU-010";

        // Create Catalogue Item
        Item testItem = new Item(
                sku,
                "Catalog Item - 10",
                "Catalog Desc - 10",
                Category.SHAVED_ICE.getValue(),
                10.00,
                10
        );
        Producer<String, OrderMessage> producer = createProducer();

        try {

            given().contentType("application/json")
                    .body(testItem)
                    .post("/api/v1/inventory")
                    .then()
                    .assertThat().statusCode(201);

            // Wait for 10 seconds before publishing the message for the cluster to startup properly
            Thread.sleep(10000);

            log.info(String.format("===> Producing order placed event for %s", sku));
            producer.send(new ProducerRecord<>("order-placed", "testcontainers", new OrderMessage(sku, 1)));

            // Wait for 10 seconds for the message to be handled by the application
            Thread.sleep(10000);

            log.info(String.format("===> Invocking get request for ", sku));

            // Get item and compare the response fields
            Response response = given().get("/api/v1/inventory/SKU-010");

            Item inventoryItem = response.getBody().as(Item.class);
            log.info(String.format("===> Received response for %s with inventory-%s", sku, inventoryItem.getInventory()));

            Assertions.assertEquals(inventoryItem.getInventory(), testItem.getInventory() - 1);

            given().delete("/api/v1/inventory/SKU-010")
                    .then()
                    .statusCode(204);


        } catch (Exception e) {
            fail("Error occurred while testing Order Placed Received event", e);
        } finally {
            producer.close();
        }

    }
}
