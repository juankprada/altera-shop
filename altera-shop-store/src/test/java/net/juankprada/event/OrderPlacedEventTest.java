package net.juankprada.event;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import net.juankprada.config.KafkaTestResource;
import net.juankprada.config.OrderMessageDeserializer;
import net.juankprada.orders.dto.OrderDto;
import net.juankprada.orders.dto.OrderItemDto;
import net.juankprada.orders.model.OrderMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.fail;

@QuarkusTestResource(KafkaTestResource.class)
@QuarkusTest
public class OrderPlacedEventTest {

    private Logger log = LoggerFactory.getLogger(OrderPlacedEventTest.class);

    public static KafkaConsumer<Integer, OrderMessage> createConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getProperty("kafka.bootstrap.servers"));
        props.put(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG, "true");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-placed");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, OrderMessageDeserializer.class.getName());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<Integer, OrderMessage> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("order-placed"));

        return consumer;
    }

    @Test
    public void test() throws Exception {

        KafkaConsumer<Integer, OrderMessage> consumer = createConsumer();

        try {
            String sku = "SKU-001";

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

            // Check for Event Messages on Kafka
            Unreliables.retryUntilTrue(45, TimeUnit.SECONDS, () -> {
                ConsumerRecords<Integer, OrderMessage> records = consumer.poll(Duration.ofMillis(100));

                if (records.isEmpty()) {
                    return false;
                }

                records.forEach(record -> {
                    log.info(String.format("==> Received %s ", record.value().getSku()));
                    if (record.value().getSku().equals(sku)) {
                        log.info(String.format("==> Order Placed Event received SKU::%s - Quantity::%s", record.value().getSku(), record.value().getQuantity()));
                        Assertions.assertEquals(record.value().getQuantity(), 2);
                    }
                });

                return true;
            });
            consumer.unsubscribe();

        } catch (Exception e) {
            fail("Error occurred while testing Order Placed event", e);
        } finally {
            consumer.close();
        }
    }
}
