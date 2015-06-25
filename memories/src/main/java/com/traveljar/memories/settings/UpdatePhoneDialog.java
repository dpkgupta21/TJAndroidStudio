package com.traveljar.memories.settings;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.traveljar.memories.R;
import com.traveljar.memories.utility.Constants;
import com.traveljar.memories.utility.HelpMe;
import com.traveljar.memories.utility.TJPreferences;
import com.traveljar.memories.volley.AppController;
import com.traveljar.memories.volley.CustomJsonRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ankit on 16/6/15.
 */
public class UpdatePhoneDialog extends DialogFragment {

    private static final String TAG = "UpdatePhoneDialog";
    OnPhoneUpdateListener mListener;

    private Context context;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.update_phone_number, null);
        final EditText phoneNumber = (EditText)view.findViewById(R.id.phone_number);
        if(TJPreferences.getPhone(getActivity()) == null || TJPreferences.getPhone(getActivity()).equals("null")){
            phoneNumber.setHint("phone number");
        }else {
            phoneNumber.setText(TJPreferences.getPhone(getActivity()));
        }
        builder.setView(view);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if (phoneNumber.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "phone number cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    if (!HelpMe.isNetworkAvailable(getActivity())) {
                        Toast.makeText(getActivity(), "Network unavailable please try after some time", Toast.LENGTH_SHORT).show();
                    } else {
                        String url = Constants.URL_UPDATE_USER_DETAILS + TJPreferences.getUserId(getActivity());
                        Map<String, String> params = new HashMap<>();
                        params.put("api_key", TJPreferences.getApiKey(getActivity()));
                        params.put("user[phone]", phoneNumber.getText().toString());
                        CustomJsonRequest uploadRequest = new CustomJsonRequest(Request.Method.PUT, url, params,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        TJPreferences.setPhone(context, phoneNumber.getText().toString());
                                        Log.d(TAG, "response on updating phone" + response);
                                        mListener.onPhoneUpdate();
                                        UpdatePhoneDialog.this.dismiss();
                                        Toast.makeText(context, "phone number updated successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "error in updating user phone number" + error);
                                Toast.makeText(context, "Could not update phone number please try after some time", Toast.LENGTH_SHORT).show();
                                error.printStackTrace();
                            }
                        });
                        AppController.getInstance().addToRequestQueue(uploadRequest);
                    }
                }
            }
        });
        return builder.create();
    }

    public interface OnPhoneUpdateListener{
        void onPhoneUpdate();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            mListener = (OnPhoneUpdateListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnPhoneUpdateListener");
        }
    }

}
