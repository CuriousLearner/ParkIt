package com.parkit.parkit_entry_scanner.rest.models;

import java.util.ArrayList;

/**
 * Created by vikram on 14/11/15.
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


    public String toString() {

        String stringRep =
                "\nFirst Name : " + this.first_name +
                        "\nLast Name : " + this.last_name +
                        "\nContact Number : " + this.contact_no +
                        "\nAddress : " + this.address +
                        "\nDriver License Image Link : " + this.driving_licence_link +
                        "\nVehicles : \n";

        if (this.vehicles != null) {
            for (Vehicle vehicle : this.vehicles) {
                stringRep += "\n\tVehicle Number : " + vehicle.vehicle_number +
                        "\n\tVehicle Type : " + vehicle.vehicle_type +
                        "\n\t---------------------------------------------------";
            }
        }

        return stringRep;
    }
}
