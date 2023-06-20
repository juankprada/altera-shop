package net.juankprada.config;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.KafkaContainer;

import java.util.HashMap;
import java.util.Map;

public class KafkaTestResource implements QuarkusTestResourceLifecycleManager {

    private final KafkaContainer KAFKA = new KafkaContainer();

    @Override
    public Map<String, String> start() {
        KAFKA.start();

        System.setProperty("kafka.bootstrap.servers", KAFKA.getBootstrapServers());

        Map<String, String> map = new HashMap<>();
        map.put("mp.messaging.incoming.order-placed.bootstrap.servers", KAFKA.getBootstrapServers());

        return map;
    }

    @Override
    public void stop() {
        System.clearProperty("kafka.bootstrap.servers");
        KAFKA.close();
    }
}
