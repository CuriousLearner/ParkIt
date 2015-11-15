package com.parkit.parkit_exit_scanner.rest.models;

/**
 * Created by vikram on 15/11/15.
 */
public class ExitRequest {

    public String QR_CODE_DATA;
    public String pid;
    public String vehicle_type;

    public ExitRequest(String hash, String parkingLotID, String vehicleType) {
        this.QR_CODE_DATA = hash;
        this.pid = parkingLotID;
        this.vehicle_type = vehicleType;
    }

    /*{
        "QR_CODE_DATA": "66bbf8f0329bc12cee00debae451c6323a875fec",
            "vehicle_type": "two_wheeler",
            "pid": 1
    }*/


    public String toString() {
        return "QR_CODE_DATA : " + this.QR_CODE_DATA +
               "\npid : " + this.pid +
               "\nvehicle_type : " + this.vehicle_type;
    }
}
