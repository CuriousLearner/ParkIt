package com.parkit.parkit_client.rest.models.parkit;

import com.parkit.parkit_client.ParkItNavigationDrawer;

/**
 * Created by vikram on 9/11/15.
 */
public class ParkItError {
    public String Message = null;

    public ParkItError(String message) {
        this.Message = message;
    }

    public String toString() {
        return "Message : " + ((this.Message == null) ? "null" : this.Message);
    }

}
