package com.alm.model;

import java.time.LocalDateTime;

public class Liability {
    private int id;
    private String name;
    private String liabilityType;
    private double amount;
    private double interestRate;
    private LocalDateTime maturityDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Liability() {
    }

    public Liability(int id, String name, String liabilityType, double amount, double interestRate,
                     LocalDateTime maturityDate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.liabilityType = liabilityType;
        this.amount = amount;
        this.interestRate = interestRate;
        this.maturityDate = maturityDate;
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

    public String getLiabilityType() {
        return liabilityType;
    }

    public void setLiabilityType(String liabilityType) {
        this.liabilityType = liabilityType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public LocalDateTime getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(LocalDateTime maturityDate) {
        this.maturityDate = maturityDate;
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
        return "Liability{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", liabilityType='" + liabilityType + '\'' +
                ", amount=" + amount +
                ", interestRate=" + interestRate +
                ", maturityDate=" + maturityDate +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
