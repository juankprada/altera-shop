package net.juankprada.orders;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.juankprada.orders.dto.OrderDto;
import net.juankprada.orders.dto.PurchaseResultDto;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("api/v1/orders")
@Produces(MediaType.APPLICATION_JSON)
public class OrdersResource {

    private Logger log = LoggerFactory.getLogger(OrdersResource.class);

    @Inject
    private OrderService orderService;


    @POST
    @Counted(name = "countPlaceOrder", description = "Counts how many times the placeOrder method has been invoked")
    @Timed(name = "timePlaceOrder", description = "Times how long it takes to invoke the placeOrder method", unit = MetricUnits.MILLISECONDS)
    public Response placeOrder(@Valid OrderDto order) throws Exception {
        log.info("Placing order");
        PurchaseResultDto result = orderService.placeOrder(order);

        return Response.status(201).entity(result).build();
    }

}
