package net.juankprada.inventory;

import io.fabric8.kubernetes.client.ResourceNotFoundException;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import net.juankprada.inventory.model.Category;
import net.juankprada.inventory.model.Item;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("api/v1/inventory")
@Produces(MediaType.APPLICATION_JSON)
public class InventoryResource {

    private Logger log = LoggerFactory.getLogger(InventoryResource.class);

    @Inject
    InventoryService itemService;

    @GET
    @Counted(name = "countGetCatalogueItems", description = "Counts how many times the getCatalogueItems method has been invoked")
    @Timed(name = "timeGetCatalogueItems", description = "Times how long it takes to invoke the getCatalogueItems method", unit = MetricUnits.MILLISECONDS)
    public Response getInventoryItems() throws Exception {
        log.info("Getting catalogue Items");
        return Response.ok(itemService.getCatalogueItems()).build();

    }

    @GET
    @Path("/{sku}")
    @Counted(name = "countGetCatalogueItems", description = "Counts how many times the getCatalogueItems method has been invoked")
    @Timed(name = "timeGetCatalogueItems", description = "Times how long it takes to invoke the getCatalogueItems method", unit = MetricUnits.MILLISECONDS)
    public Response getInventoryItem(@PathParam("sku") String sku) throws ResourceNotFoundException, Exception {
        // return itemRepo.findBySku(sku);
        log.info("Getting item {}.", sku);
        return Response.ok(itemService.getItem(sku)).build();
    }


    @GET
    @Path("/category/{category}")
    public Response getItemsByCategory(@Valid @PathParam("category") Category category) throws Exception {
        log.info("Retrieving items by category {}", category.name());

        return Response.ok(itemService.getByCategory(category)).build();
    }


    @POST
    public Response addInventoryItem(@Valid Item item) throws Exception {

        log.info("Adding Item with sku : {}", item.getSku());

        long result = itemService.addItem(item);
        //itemRepo.persist(item);

        return Response.status(201).entity(result).build();
    }


    @PUT
    @Path("/{sku}")
    public Response updateInventoryItem(
            @PathParam(value = "sku") String skuNumber,
            @Valid Item catalogueItem) throws ResourceNotFoundException, Exception {
        log.info("Updating Item with sku : {}", catalogueItem.getSku());

        itemService.updateItem(catalogueItem);

        return Response.ok().build();
    }


    @DELETE
    @Path("/{sku}")
    public Response deleteInventoryItem(@PathParam(value = "sku") String skuNumber) throws ResourceNotFoundException, Exception {
        log.info("Deleting Item with sku: {}", skuNumber);

        itemService.deleteItem(skuNumber);

        return Response.status(Response.Status.NO_CONTENT).build();

    }


    @Provider
    public static class ErrorMapper implements ExceptionMapper<Exception> {

        @Override
        public Response toResponse(Exception exception) {
            int code = 500;

            if (exception instanceof WebApplicationException) {
                code = ((WebApplicationException) exception).getResponse().getStatus();
            } else if (exception instanceof ResourceNotFoundException) {
                code = 404;
            }

            JsonObjectBuilder entityBuilder = Json.createObjectBuilder()
                    .add("exceptionType", exception.getClass().getName())
                    .add("code", code);

            if (exception.getMessage() != null) {
                entityBuilder.add("error", exception.getMessage());
            }

            return Response.status(code)
                    .entity(entityBuilder.build())
                    .build();
        }

    }

}
