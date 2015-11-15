package com.parkit.parkit_exit_scanner.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.parkit.parkit_exit_scanner.rest.services.ParkItService;

import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * Created by vikram on 15/11/15.
 */
public class RestClient {


    public static ParkItService parkItService;

    public RestClient() {
        // form gson with JSON date format
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .create();

        // create gson converter
        GsonConverter gsonConverter = new GsonConverter(gson);


        // build rest client

        RestAdapter parkItAdapter = new RestAdapter.Builder()
                .setConverter(gsonConverter)
                .setEndpoint(ParkItService.BASE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        // ParkIt API
        parkItService = parkItAdapter.create(ParkItService.class);



    }



}
