package com.parkit.parkit_client.rest.models.parkit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vikram on 8/11/15.
 */
public class Customer {


    public String first_name, last_name, contact_no, address, driving_licence_link;

    public ArrayList<Vehicle> vehicles;


    public Customer(
            String fistName,
            String lastName,
            String contactNumber,
            String address,
            String driverLicenseLink,
            ArrayList<Vehicle> vehicles) {
        this.first_name = fistName;
        this.last_name = lastName;
        this.contact_no = contactNumber;
        this.address = address;
        this.driving_licence_link = driverLicenseLink;
        this.vehicles = vehicles;
    }



}
