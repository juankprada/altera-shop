package net.juankprada.orders.proxy;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import net.juankprada.orders.dto.ItemDto;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("api/v1/inventory")
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "inventory-service")
public interface InventoryProxy {
    @GET
    @Path("/{sku}")
    ItemDto getInventoryItem(@PathParam("sku") String sku);

}
