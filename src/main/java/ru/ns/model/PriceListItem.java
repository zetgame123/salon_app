package ru.ns.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PriceListItem {

    private int idPrice;
    private String haircutName;
    private BigDecimal price;
    private LocalDate startDate;

    public int getIdPrice() {
        return idPrice;
    }

    public void setIdPrice(int idPrice) {
        this.idPrice = idPrice;
    }

    public String getHaircutName() {
        return haircutName;
    }

    public void setHaircutName(String haircutName) {
        this.haircutName = haircutName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    @Override
    public String toString() {
        return haircutName +
                " — " +
                price +
                " руб. (с " +
                startDate +
                ")";
    }
}
