package com.parkit.parkit_exit_scanner.rest.models;

import com.parkit.parkit_exit_scanner.rest.services.ParkItService;

/**
 * Created by vikram on 15/11/15.
 */
public class ParkItError {


    private String Message;

    public ParkItError(String message) {
        this.Message = message;
    }

    public String toString() {
        return "Error Description : "+this.Message;
    }

    public String getMessage() {
        return this.Message;
    }
}
