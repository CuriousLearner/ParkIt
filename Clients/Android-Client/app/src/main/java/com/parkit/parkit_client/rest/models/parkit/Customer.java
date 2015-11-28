package com.parkit.parkit_client.rest.models.parkit;

import android.os.Parcel;
import android.os.Parcelable;

import com.parkit.parkit_client.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vikram on 8/11/15.
 */
public class Customer implements Parcelable{


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
        String customerRep =
                "\nFirst Name : " + this.first_name +
                "\nLast Name : " + this.last_name +
                "\nContact Number : " + this.contact_no +
                "\nAddress : " + this.address +
                "\nDriving License Link : " + this.driving_licence_link +
                "\nVehicles : ";
        if (this.vehicles != null) {
            for (Vehicle v : this.vehicles) {
                customerRep += "\n\tVehicle Number : " + v.vehicle_number +
                        "\n\tVehicle Type : " + v.vehicle_type +
                        "\n\tVehicle's RC Link : " + v.vehicle_rc_link;
            }
        }
        return customerRep;
    }

    // parcelable code
    // - describeContents()
    // - writeToParcel()
    // - constructor with parcel param
    // - static final creator

    public Customer(Parcel in) {
        String[] personalDetailsArray = new String[] {
                "",
                "",
                "",
                "",
                ""
        };
        in.readStringArray(personalDetailsArray);
        ArrayList<Vehicle> readVehiclesTo = new ArrayList<>();
        in.readTypedList(readVehiclesTo, Vehicle.CREATOR);
        this.vehicles = readVehiclesTo;
        this.first_name = personalDetailsArray[0];
        this.last_name = personalDetailsArray[1];
        this.contact_no = personalDetailsArray[2];
        this.address = personalDetailsArray[3];
        this.driving_licence_link = personalDetailsArray[4];
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        String[] personalDetailsArray = new String[] {
            this.first_name,
            this.last_name,
            this.contact_no,
            this.address,
            this.driving_licence_link
        };

        dest.writeStringArray(personalDetailsArray);
        dest.writeTypedList(this.vehicles);
    }

    // customer creator
    public static final Parcelable.Creator<Customer> CREATOR
            = new Parcelable.Creator<Customer>() {
        public Customer createFromParcel(Parcel in) {
            return new Customer(in);
        }

        public Customer[] newArray(int size) {
            return new Customer[size];
        }
    };


}
