package com.parkit.parkit_client.rest.services;


import com.parkit.parkit_client.rest.models.parkit.Customer;
import com.parkit.parkit_client.rest.models.parkit.QRCodeResponse;


import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by vikram on 8/11/15.
 */
public interface ParkItService {


    final static String BASE_URL = "http://192.168.1.41:5000";


    @POST("/api/customer/register/")
    public void registerCustomer(

        @Body Customer customer,
        Callback<QRCodeResponse> cb
    );


}