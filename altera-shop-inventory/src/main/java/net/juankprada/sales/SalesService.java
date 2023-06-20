package net.juankprada.sales;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.juankprada.inventory.InventoryService;
import net.juankprada.inventory.model.Item;
import net.juankprada.sales.model.Sale;
import net.juankprada.sales.model.SalesDto;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class SalesService {


    @Inject
    private SaleRepository saleRepository;

    @Inject
    private InventoryService inventoryService;


    public void processPurchase(String sku, int quantity) {
        Item item = inventoryService.getItem(sku);

        Sale sale = new Sale();
        sale.setCreatedOn(LocalDate.now());
        sale.setQuantity(quantity);
        sale.setSku(sku);
        sale.setValue(quantity * item.getPrice());
        saleRepository.persist(sale);
    }


    public SalesDto getSales(LocalDate startDate, LocalDate endDate, int page, int pageSize) {

        SalesDto result = new SalesDto();

        List<Sale> sales = saleRepository.findInRangePaginated(startDate, endDate, page, pageSize);
        result.pageSize = pageSize;
        result.page = page;
        result.totalSales = saleRepository.countEntriesInRange(startDate, endDate);
        result.totalPages = (long) Math.ceil((double) result.totalSales / result.pageSize) + 1 ;
        result.sales = sales.stream().map( s -> {
            SalesDto.SaleEntry dto = new SalesDto.SaleEntry();
            dto.sku = s.getSku();
            dto.saleDate = s.getCreatedOn();
            dto.value = s.getValue();
            dto.quantity = s.getQuantity();
            return dto;
        }).toList();
        result.totalSalesValue = saleRepository.findTotalSalesInRange(startDate, endDate);

        return result;
    }
}
