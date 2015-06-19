package com.traveljar.memories.volley;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class CustomMultiPartRequest extends Request<JSONObject> {

    private static final String FILE_PART_NAME = "file";
    private static final String STRING_PART_NAME = "text";
    private MultipartEntity entity = new MultipartEntity();
    private Response.Listener<JSONObject> mListener;
    private File mFilePart;
    private String mStringPart;

    public CustomMultiPartRequest(String url, Response.ErrorListener errorListener,
                                  Response.Listener<JSONObject> listener, File file, String stringPart) {
        super(Method.POST, url, errorListener);

        mListener = listener;
        mFilePart = file;
        mStringPart = stringPart;
        buildMultiPartEntity();
    }

    private void buildMultiPartEntity() {
        entity.addPart(FILE_PART_NAME, new FileBody(mFilePart));
        try {
            entity.addPart(STRING_PART_NAME, new StringBody(mStringPart));
        } catch (UnsupportedEncodingException e) {
            Log.d("User", "UnsupportedEncodingException");
        }
    }

    @Override
    public String getBodyContentType() {
        return entity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            entity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        Log.d("User", "Response coming");
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        mListener.onResponse(response);
    }
}