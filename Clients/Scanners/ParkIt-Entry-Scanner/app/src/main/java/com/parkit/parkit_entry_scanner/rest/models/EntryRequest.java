package com.parkit.parkit_entry_scanner.rest.models;

/**
 * Created by vikram on 13/11/15.
 */
public class EntryRequest {
    public String QR_CODE_DATA;

    public int pid;

    public String vehicle_type;




    public EntryRequest(String hash, int parkingLotId, String vehicleType) {
        this.QR_CODE_DATA = hash;
        this.pid = parkingLotId;
        this.vehicle_type = vehicleType;
    }

    public String toString() {
        return "QR_CODE_DATA : " + this.QR_CODE_DATA +
                "\nParking Lot ID : " + this.pid +
                "\nVehicle Type : " + this.vehicle_type;
    }
}
