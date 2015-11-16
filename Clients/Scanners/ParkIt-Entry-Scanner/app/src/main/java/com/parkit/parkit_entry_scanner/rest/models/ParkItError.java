package com.parkit.parkit_entry_scanner.rest.models;

/**
 * Created by vikram on 14/11/15.
 */
public class ParkItError {

    private String Message;

    public ParkItError(String message) {
        this.Message = message;
    }

    public String getMessage() { return this.Message; }

    public String toString() { return "Error Description : " + this.Message; }

}
