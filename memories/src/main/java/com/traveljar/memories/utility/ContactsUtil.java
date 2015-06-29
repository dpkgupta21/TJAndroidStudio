package com.traveljar.memories.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.RequestFuture;
import com.traveljar.memories.SQLitedatabase.ContactDataSource;
import com.traveljar.memories.models.Contact;
import com.traveljar.memories.volley.AppController;
import com.traveljar.memories.volley.CustomJsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by ankit on 24/6/15.
 */
public class ContactsUtil {

    private static final String TAG = "ContactsUtil";

    public static boolean fetchContact(Context context, String contactId) {
        String url = Constants.URL_USER_SHOW_DETAILS + "/" + contactId + "?api_key=" + TJPreferences.getApiKey(context);

        RequestFuture<JSONObject> futureRequest = RequestFuture.newFuture();
        CustomJsonRequest jsonRequest = new CustomJsonRequest(Request.Method.GET, url, null, futureRequest, futureRequest);
        AppController.getInstance().getRequestQueue().add(jsonRequest);

        try {
            JSONObject response = futureRequest.get(30, TimeUnit.SECONDS);
            Log.d(TAG, "buddy fetched with response " + response);

            final String idOnServer = response.getJSONObject("user").getString("id");
            String userName = response.getJSONObject("user").getString("name");
            String email = response.getJSONObject("user").getString("email");
            String status = response.getJSONObject("user").getString("status");
            String interests = response.getJSONObject("user").getString("interests");
            String phone_no = response.getJSONObject("user").getString("phone");
            String phoneBookName = HelpMe.getContactNameFromNumber(context, phone_no);
            String picServerUrl = response.getJSONObject("user").getJSONObject("profile_picture").getJSONObject("thumb").getString("url");
            String allJourneyIds = response.getJSONObject("user").getString("journey_ids");

            //Fetching the profile image
            RequestFuture<Bitmap> requestFuture = RequestFuture.newFuture();
            ImageRequest imageRequest = new ImageRequest(picServerUrl, requestFuture, 0, 0, null, null, requestFuture);
            AppController.getInstance().getRequestQueue().add(imageRequest);
            FileOutputStream out = null;
            String fileName;
            try {
                Bitmap bitmap = requestFuture.get(30, TimeUnit.SECONDS);
                fileName = Constants.TRAVELJAR_FOLDER_BUDDY_PROFILES + idOnServer + ".jpeg";
                out = new FileOutputStream(fileName);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } catch (Exception e) {
                fileName = Constants.GUMNAAM_IMAGE_URL;
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Contact tempContact = new Contact(idOnServer, userName, phoneBookName, email, status, picServerUrl, fileName,
                    phone_no, allJourneyIds, true, interests);
            ContactDataSource.createContact(tempContact, context);
            return true;
        } catch (InterruptedException e) {
            Log.d(TAG, "note couldnot be uploaded InterruptedException");
            e.printStackTrace();
        } catch (ExecutionException e) {
            Log.d(TAG, "note couldnot be uploaded ExecutionException");
            e.printStackTrace();
        } catch (TimeoutException e) {
            Log.d(TAG, "note couldnot be uploaded TimeoutException");
            e.printStackTrace();
        } catch (JSONException e) {
            Log.d(TAG, "note couldnot be parsed although uploaded successfully");
            e.printStackTrace();
        }
        return false;
    }

        public static boolean fetchProfilePicture(Context context, String url, String fileName){
            Log.d(TAG, "fetching image from url" + url);

            final RequestFuture<Bitmap> futureRequest = RequestFuture.newFuture();
            ImageRequest imageRequest = new ImageRequest(url, futureRequest, 0, 0, null, null,futureRequest);
            AppController.getInstance().getRequestQueue().add(imageRequest);
            try {
                Bitmap bitmap = futureRequest.get(60, TimeUnit.SECONDS);
                Log.d(TAG, "successfully downloaded profile image");
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(fileName);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                }catch (Exception e){
                }finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
                return true;
            } catch (TimeoutException e) {
                e.printStackTrace();
            }catch (CancellationException e){
                e.printStackTrace();
            }
            return false;
        }

    }