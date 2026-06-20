package ru.ns.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Price {

    private int idPrice;
    private int idHaircutType;
    private LocalDate startDate;
    private BigDecimal price;

    public Price() {
    }

    public Price(int idPrice,
                 int idHaircutType,
                 LocalDate startDate,
                 BigDecimal price) {
        this.idPrice = idPrice;
        this.idHaircutType = idHaircutType;
        this.startDate = startDate;
        this.price = price;
    }

    public int getIdPrice() {
        return idPrice;
    }

    public void setIdPrice(int idPrice) {
        this.idPrice = idPrice;
    }

    public int getIdHaircutType() {
        return idHaircutType;
    }

    public void setIdHaircutType(int idHaircutType) {
        this.idHaircutType = idHaircutType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}