package com.parkit.parkit_entry_scanner.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.parkit.parkit_entry_scanner.rest.services.ParkItService;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.GsonConverter;

/**
 * Created by vikram on 13/11/15.
 */
public class RestClient {

    public static ParkItService parkItService;

    public static RestClient currentClient;

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
