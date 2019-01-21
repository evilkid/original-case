package com.afkl.cases.df.domain;

public class CompleteFare {
    private Double amount;
    private String currency;
    private Location origin;
    private Location destination;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Location getOrigin() {
        return origin;
    }

    public void setOrigin(Location origin) {
        this.origin = origin;
    }

    public Location getDestination() {
        return destination;
    }

    public void setDestination(Location destination) {
        this.destination = destination;
    }

    public boolean isValid(){
        return amount != null && currency != null && origin != null && destination != null;
    }

    @Override
    public String toString() {
        return "CompleteFare{" +
                "amount=" + amount +
                ", currency='" + currency + '\'' +
                ", origin=" + origin +
                ", destination=" + destination +
                '}';
    }
}
