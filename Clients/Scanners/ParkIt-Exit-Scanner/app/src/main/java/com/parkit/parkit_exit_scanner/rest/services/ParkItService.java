package com.parkit.parkit_exit_scanner.rest.services;

import com.parkit.parkit_exit_scanner.rest.models.Cost;
import com.parkit.parkit_exit_scanner.rest.models.ExitRequest;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by vikram on 15/11/15.
 */
public interface ParkItService {




    public static final String BASE_URL = "http://192.168.1.4:5000";




    @POST("/api/customer/exit/")
    public void requestExit(
            @Header("token") String authToken,
            @Body ExitRequest exitRequest,
            Callback<Cost> cb );
    // 400 , 401, 404, 409, 200

}
