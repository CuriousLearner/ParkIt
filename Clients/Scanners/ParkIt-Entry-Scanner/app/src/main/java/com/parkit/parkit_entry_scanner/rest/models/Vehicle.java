package com.parkit.parkit_entry_scanner.rest.models;

/**
 * Created by vikram on 14/11/15.
 */
public class Vehicle {

    public String vehicle_type, vehicle_number, vehicle_rc_link;



    public Vehicle(String vehicleType, String vehicleNumber, String vehicleRCLink) {
        this.vehicle_type = vehicleType;
        this.vehicle_number = vehicleNumber;
        this.vehicle_rc_link = vehicleRCLink;
    }

}
