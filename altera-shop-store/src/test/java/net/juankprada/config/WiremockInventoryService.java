package net.juankprada.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.util.Collections;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class WiremockInventoryService implements QuarkusTestResourceLifecycleManager {

    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();

        stubFor(get(urlEqualTo("/api/v1/inventory/SKU-001"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n" +
                                "\t\"sku\": \"SKU-001\",\n" +
                                "\t\"name\": \"Chocolate\",\n" +
                                "\t\"description\": \"The recommended one\",\n" +
                                "\t\"category\": \"Icecream\",\n" +
                                "\t\"price\": \"50\",\n" +
                                "\t\"inventory\": 20\n" +
                                "}")
                ));

        return Collections.singletonMap("net.juankprada.orders.proxy.InventoryProxy/mp-rest/url",
                wireMockServer.baseUrl());
    }

    @Override
    public void stop() {
        if (null != wireMockServer) {
            wireMockServer.stop();
        }
    }
}
