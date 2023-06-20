package net.juankprada.sales;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.ejb.Local;
import jakarta.enterprise.context.ApplicationScoped;
import net.juankprada.inventory.model.Item;
import net.juankprada.sales.model.Sale;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class SaleRepository implements PanacheRepository<Sale> {


    public List<Sale> findInRangePaginated(LocalDate startDate, LocalDate endDate, int page, int pageSize) {
        return find("createdOn >= :startDate and createdOn <= :endDate",
                Parameters.with("startDate", startDate).and("endDate", endDate))
                .page(page, pageSize).list();
    }

    public long countEntriesInRange(LocalDate startDate, LocalDate endDate) {
        return find("createdOn >= :startDate and createdOn <= :endDate",
                Parameters.with("startDate", startDate).and("endDate", endDate)).count();
    }


    public Double findTotalSalesInRange(LocalDate startDate, LocalDate endDate) {
        return find("createdOn >= :startDate and createdOn <= :endDate",
                Parameters.with("startDate", startDate).and("endDate", endDate)).stream().mapToDouble(Sale::getValue).sum();
    }

}
