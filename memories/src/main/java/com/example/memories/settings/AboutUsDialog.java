package com.example.memories.settings;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.example.memories.R;

/**
 * Created by ankit on 11/6/15.
 */
public class AboutUsDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View aboutUsDialogView = layoutInflater.inflate(R.layout.about_us, null);
        builder.setView(aboutUsDialogView);
        builder.setTitle(null);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AboutUsDialog.this.dismiss();
            }
        });
        return builder.create();
    }
}
