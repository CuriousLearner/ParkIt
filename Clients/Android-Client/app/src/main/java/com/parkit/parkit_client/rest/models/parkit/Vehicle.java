package com.parkit.parkit_client.rest.models.parkit;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vikram on 8/11/15.
 */
public class Vehicle implements Parcelable {
    public String vehicle_type, vehicle_number, vehicle_rc_link;



    public Vehicle(String vehicleType, String vehicleNumber, String vehicleRCLink) {
        this.vehicle_type = vehicleType;
        this.vehicle_number = vehicleNumber;
        this.vehicle_rc_link = vehicleRCLink;
    }


    // parcelable code
    // - describeContents()
    // - writeToParcel()
    // - constructor with parcel param
    // - static final creator

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        String[] vehicleData = new String[] {
            this.vehicle_type,
            this.vehicle_number,
            this.vehicle_rc_link
        };
        dest.writeStringArray(vehicleData);
    }

    public Vehicle(Parcel in) {
        String[] vehicleData = new String[] {
                "",
                "",
                ""
        };
        in.readStringArray(vehicleData);
        this.vehicle_type = vehicleData[0];
        this.vehicle_number = vehicleData[1];
        this.vehicle_rc_link = vehicleData[2];
    }

    // vehicle creator
    public static final Parcelable.Creator<Vehicle> CREATOR
            = new Parcelable.Creator<Vehicle>() {
        public Vehicle createFromParcel(Parcel in) {
            return new Vehicle(in);
        }

        public Vehicle[] newArray(int size) {
            return new Vehicle[size];
        }
    };

}
