package com.parkit.parkit_client.rest.models.parkit;

/**
 * Created by vikram on 19/11/15.
 */
public class Balance {

    private double balance;


    public Balance(double balance) {
        this.balance = balance;
    }


    public double getBalance() { return this.balance; }

    public String toString() {
        return Double.toString(this.balance);
    }
}
