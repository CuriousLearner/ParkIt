package com.parkit.parkit_client.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.parkit.parkit_client.rest.services.ImgurService;
import com.parkit.parkit_client.rest.services.ParkItService;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by vikram on 8/11/15.
 */
public class RestClient {

    public static ParkItService parkItService;
    public static ImgurService imgurService;

    private static RestClient currentAdapter;


    public RestClient() {

        // form gson with JSON date format
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .create();


        // create gson converter
        GsonConverter gsonConverter = new GsonConverter(gson);


        RestAdapter parkItAdapter = new RestAdapter.Builder()
                .setConverter(gsonConverter)
                .setEndpoint(ParkItService.BASE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();


        // ParkIt API
        parkItService  = parkItAdapter.create(ParkItService.class);

        RestAdapter imgurAdapter = new RestAdapter.Builder()
                .setConverter(gsonConverter)
                .setEndpoint(ImgurService.BASE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();



        // Imgur API
        imgurService = imgurAdapter.create(ImgurService.class);


    }


}
