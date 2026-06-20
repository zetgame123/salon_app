package ru.ns.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PriceListItem {

    private int idPrice;
    private int idHaircutType;
    private String haircutName;
    private String gender;
    private BigDecimal price;
    private LocalDate startDate;

    public int getIdHaircutType() {
        return idHaircutType;
    }

    public void setIdHaircutType(int idHaircutType) {
        this.idHaircutType = idHaircutType;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

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
