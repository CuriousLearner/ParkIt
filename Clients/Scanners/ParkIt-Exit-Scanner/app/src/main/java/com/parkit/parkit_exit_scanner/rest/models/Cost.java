package com.parkit.parkit_exit_scanner.rest.models;

/**
 * Created by vikram on 15/11/15.
 */
public class Cost {

    private Double cost;

    public Cost(Double cost) {
        this.cost = cost;
    }

    public String toString() {
        return "Total Cost (in Rupees) : " + this.cost;
    }

    public Double getCost() { return cost; }
}
