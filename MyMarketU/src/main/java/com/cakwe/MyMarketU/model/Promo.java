package com.cakwe.MyMarketU.model;

import jakarta.persistence.*;

@Entity
public class Promo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private double discountPercentage;

    @Column(nullable = false)
    private double minimumSpend;

    @Column(nullable = false)
    private double maximumDiscount;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public double getMinimumSpend() {
        return minimumSpend;
    }

    public void setMinimumSpend(double minimumSpend) {
        this.minimumSpend = minimumSpend;
    }

    public double getMaximumDiscount() {
        return maximumDiscount;
    }

    public void setMaximumDiscount(double maximumDiscount) {
        this.maximumDiscount = maximumDiscount;
    }
}
