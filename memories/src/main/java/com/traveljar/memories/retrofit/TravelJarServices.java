package com.traveljar.memories.retrofit;

import org.json.JSONObject;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.Streaming;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

public interface TravelJarServices {

    @GET("/places")
    public void getPlaces(@Query("api_key") String apiKey, Callback<String> callback);

    @FormUrlEncoded
    @POST("/places")
    public void addPlace(@Field("api_key") String apiKey, @Field("place[name]") String place, Callback<String> callback);


    @Multipart
    @POST("/journeys/{journey_id}/pictures")
    public void uploadPicture(
            @Path("journey_id") String journeyId,
            @Part("api_key") TypedString apiKey,
            @Part("picture[user_id]") TypedString userId,
            @Part("picture[picture_file]") TypedFile photo,
            Callback<String> callback);

    @Multipart
    @POST("/journeys/{journey_id}/audios")
    public void uploadAudio(
            @Path("journey_id") String journeyId,
            @Part("api_key") TypedString apiKey,
            @Part("audio[longitude]") TypedString latitude,
            @Part("audio[latitude]") TypedString longitude,
            @Part("audio[user_id]") TypedString userId,
            @Part("audio[audio_file]") TypedFile audio,
            Callback<JSONObject> callback);

    @Multipart
    @POST("/journeys/{journey_id}/videos")
    public void uploadVideo(
            @Path("journey_id") String journeyId,
            @Part("api_key") TypedString apiKey,
            @Part("video[user_id]") TypedString userId,
            @Part("video[video_file]") TypedFile video,
            Callback<String> callback);

    @Multipart
    @PUT("/users/{id}")
    public void updateProfilePicture(@Path("id") String userId,
                                     @Part("api_key") TypedString apiKey,
                                     @Part("user[profile_picture]") TypedFile photo,
                                     Callback<String> callback);

    @Streaming
    @GET("{download_url}")
    public void downloadVideo(@Path("downloadurl") String url, Callback<Response> callback);

}
