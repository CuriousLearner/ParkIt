package com.parkit.parkit_client.rest.services;

import com.parkit.parkit_client.rest.models.imgur.ImgurImageResponse;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * Created by vikram on 8/11/15.
 */
public interface ImgurService {

    public final String BASE_URL = "https://api.imgur.com";
    public final String CLIENT_ID = "Client-ID fd4c11b8bca40bf";



    @POST("/3/image")
    void postImage(
            @Header("Authorization") String auth,
            @Query("title") String title,
            @Query("description") String description,
            @Query("album") String albumId,
            @Query("account_url") String username,
            @Body TypedFile file,
            Callback<ImgurImageResponse> cb
    );
}
