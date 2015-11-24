package com.parkit.parkit_client.rest.services;


import com.parkit.parkit_client.rest.models.parkit.Balance;
import com.parkit.parkit_client.rest.models.parkit.Customer;
import com.parkit.parkit_client.rest.models.parkit.CustomerModificationResponse;
import com.parkit.parkit_client.rest.models.parkit.QRCodeResponse;


import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by vikram on 8/11/15.
 */
public interface ParkItService {


    public static String BASE_URL = "http://192.168.1.4:5000";


    @POST("/api/customer/register/")
    public void registerCustomer(
        @Header("token") String token,
        @Body Customer customer,
        Callback<QRCodeResponse> cb
    );

    @GET("/api/customer/balance/")
    public void getWalletBalance(
            @Header("token") String token,
            @Query("QR_CODE_DATA") String hash,
            Callback<Balance> cb
    );

    @GET("/api/customer/{hash}/")
    public void getCustomer(
            @Header("token") String token,
            @Path("hash") String hash,
            Callback<Customer> cb
    );

    @PUT("/api/customer/modify/")
    public void putCustomer(
            @Header("token") String token,
            @Header("QR_CODE_DATA") String hash,
            @Body Customer customer,
            Callback<CustomerModificationResponse> cb
    );

}
