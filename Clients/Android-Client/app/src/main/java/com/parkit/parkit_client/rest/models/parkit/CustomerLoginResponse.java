package com.parkit.parkit_client.rest.models.parkit;

import java.util.ArrayList;

/**
 * Created by vikram on 29/11/15.
 */
public class CustomerLoginResponse {

    public String first_name,
                  last_name,
                  contact_no,
                  email,
                  address,
                  driving_licence_link,

                  QR_CODE_DATA;

    public ArrayList<Vehicle> vehicles;

    public CustomerLoginResponse(
        String firstName,
        String lastName,
        String contactNumber,
        String email,
        String address,
        String drivingLicenseLink,
        String hash
    ) {
        this.first_name = firstName;
        this.last_name = lastName;
        this.contact_no = contactNumber;
        this.email = email;
        this.address = address;
        this.driving_licence_link = drivingLicenseLink;

        this.QR_CODE_DATA = hash;
    }


    public String toString() {
        String representation =
                "First Name : " + this.first_name +
                "\nLast Name : " + this.last_name +
                "\nContact Number : "+this.contact_no +
                "\nEmail : "+this.email +
                "\nAddress : "+this.address +
                "\nDriving License Image Link : "+this.driving_licence_link +
                "\nHash : "+this.QR_CODE_DATA;
        return representation;
    }

}
