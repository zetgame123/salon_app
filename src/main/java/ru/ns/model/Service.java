package ru.ns.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Service {

    private int idService;
    private int idClient;
    private int idBranch;
    private int idPrice;
    private LocalDateTime serviceDate;
    private BigDecimal totalCost;
    private boolean discountApplied;
    private String clientWishes;

    public Service() {
    }

    public Service(int idService,
                   int idClient,
                   int idBranch,
                   int idPrice,
                   LocalDateTime serviceDate,
                   BigDecimal totalCost,
                   boolean discountApplied,
                   String clientWishes) {

        this.idService = idService;
        this.idClient = idClient;
        this.idBranch = idBranch;
        this.idPrice = idPrice;
        this.serviceDate = serviceDate;
        this.totalCost = totalCost;
        this.discountApplied = discountApplied;
        this.clientWishes = clientWishes;
    }

    public int getIdService() {
        return idService;
    }

    public void setIdService(int idService) {
        this.idService = idService;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public int getIdBranch() {
        return idBranch;
    }

    public void setIdBranch(int idBranch) {
        this.idBranch = idBranch;
    }

    public int getIdPrice() {
        return idPrice;
    }

    public void setIdPrice(int idPrice) {
        this.idPrice = idPrice;
    }

    public LocalDateTime getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(LocalDateTime serviceDate) {
        this.serviceDate = serviceDate;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public boolean isDiscountApplied() {
        return discountApplied;
    }

    public void setDiscountApplied(boolean discountApplied) {
        this.discountApplied = discountApplied;
    }

    public String getClientWishes() {
        return clientWishes;
    }

    public void setClientWishes(String clientWishes) {
        this.clientWishes = clientWishes;
    }
}