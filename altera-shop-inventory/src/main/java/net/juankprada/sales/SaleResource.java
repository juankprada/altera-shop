package net.juankprada.sales;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;


@Path("api/v1/sales")
@Produces(MediaType.APPLICATION_JSON)
public class SaleResource {

    private Logger log = LoggerFactory.getLogger(SaleResource.class);

    @Inject
    private SalesService salesService;

    @GET
    @Counted(name = "countGetSales", description = "Counts how many times the getSales method has been invoked")
    @Timed(name = "timeGetSales", description = "Times how long it takes to invoke the getSales method", unit = MetricUnits.MILLISECONDS)
    public Response getSales(@QueryParam("start-date") String startDateStr, @QueryParam("end-date") String endDateStr, @QueryParam("page") Integer page, @QueryParam("page-size") Integer pageSize) throws Exception {
        log.info("Getting catalogue Items");

        // TODO: Validate these query params better. Provide a validation method or use @Valid annotation with proper
        // Validation implementations.
        if (page == null) {
            page = 0;
        }

        if (pageSize == null) {
            pageSize = 10;
        }

        LocalDate startDate;
        if (StringUtils.isNotBlank(startDateStr)) {
            startDate = LocalDate.parse(startDateStr);
        } else {
            startDate = LocalDate.now().minusMonths(1);
        }

        LocalDate endDate;
        if (StringUtils.isNotBlank(endDateStr)) {
            endDate = LocalDate.parse(endDateStr);
        } else {
            endDate = LocalDate.now();
        }

        log.info("Retrieving sales for period {} to {}.", startDate, endDate);
        return Response.ok(salesService.getSales(startDate, endDate, page, pageSize)).build();

    }
}
