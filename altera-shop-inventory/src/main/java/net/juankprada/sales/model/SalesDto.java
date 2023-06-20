package net.juankprada.sales.model;

import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class SalesDto {


    public static class SaleEntry {
        public String sku;

        public Integer quantity;

        public Double value;

        public LocalDate saleDate;

    }

    public LocalDate startDate;

    public LocalDate endDate;

    public List<SaleEntry> sales;


    public long totalSales;
    public Double totalSalesValue;

    public long pageSize;
    public long page;
    public long totalPages;


}
