package com.parkit.parkit_entry_scanner.rest.services;

import com.parkit.parkit_entry_scanner.rest.models.Customer;
import com.parkit.parkit_entry_scanner.rest.models.EntryRequest;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by vikram on 14/11/15.
 */
public interface ParkItService {



    public static final String BASE_URL = "http://192.168.1.2:5000";


    @POST("/api/customer/entry/")
    public void requestEntry(
            @Header("token") String authToken,
            @Body EntryRequest entryRequest,
            Callback<Customer> cb);


}
