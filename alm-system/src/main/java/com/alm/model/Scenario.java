package com.alm.model;

import java.time.LocalDateTime;

public class Scenario {
    private int id;
    private String name;
    private String description;
    private double interestRateShock;
    private double liquidityShock;
    private double creditShock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Scenario() {
    }

    public Scenario(int id, String name, String description, double interestRateShock,
                    double liquidityShock, double creditShock, LocalDateTime createdAt,
                    LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.interestRateShock = interestRateShock;
        this.liquidityShock = liquidityShock;
        this.creditShock = creditShock;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getInterestRateShock() {
        return interestRateShock;
    }

    public void setInterestRateShock(double interestRateShock) {
        this.interestRateShock = interestRateShock;
    }

    public double getLiquidityShock() {
        return liquidityShock;
    }

    public void setLiquidityShock(double liquidityShock) {
        this.liquidityShock = liquidityShock;
    }

    public double getCreditShock() {
        return creditShock;
    }

    public void setCreditShock(double creditShock) {
        this.creditShock = creditShock;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Scenario{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", interestRateShock=" + interestRateShock +
                ", liquidityShock=" + liquidityShock +
                ", creditShock=" + creditShock +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
