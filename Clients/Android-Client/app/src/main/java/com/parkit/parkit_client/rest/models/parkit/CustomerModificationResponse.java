package com.parkit.parkit_client.rest.models.parkit;

/**
 * Created by vikram on 22/11/15.
 */
public class CustomerModificationResponse {

    public String QR_CODE_DATA, Message;

    public CustomerModificationResponse(String QR_CODE_DATA, String Message) {
        this.QR_CODE_DATA = QR_CODE_DATA;
        this.Message = Message;
    }

}
