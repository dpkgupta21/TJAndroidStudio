package com.traveljar.memories.profile;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.traveljar.memories.R;
import com.traveljar.memories.utility.TJPreferences;

public class ChangePassword extends Activity {

    EditText mCurrentPwd;
    EditText mNewPwd;
    EditText mVerifyPwd;
    Button mSubmitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_change_password);
        mCurrentPwd = (EditText) findViewById(R.id.currentPwd);
        mNewPwd = (EditText) findViewById(R.id.newPwd);
        mVerifyPwd = (EditText) findViewById(R.id.verifyPwd);
        mSubmitBtn = (Button) findViewById(R.id.submitBtn);
        mSubmitBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPwd.getText().toString() == null) {
                    Toast.makeText(ChangePassword.this,
                            "Current password field should not be blank",
                            Toast.LENGTH_SHORT).show();
                } else if (mNewPwd.getText().toString() == null) {
                    Toast.makeText(ChangePassword.this,
                            "New password field should not be blank",
                            Toast.LENGTH_SHORT).show();
                } else if (mVerifyPwd.getText().toString() == null) {
                    Toast.makeText(ChangePassword.this,
                            "Retype password field should not be blank",
                            Toast.LENGTH_SHORT).show();
                } else if (!mCurrentPwd
                        .getText()
                        .toString()
                        .equals(TJPreferences
                                .getUserPassword(ChangePassword.this))) {
                    Toast.makeText(ChangePassword.this,
                            "You typed a wrong password", Toast.LENGTH_SHORT)
                            .show();
                } else if (!mNewPwd.getText().toString()
                        .equals(mVerifyPwd.getText().toString())) {
                    Toast.makeText(ChangePassword.this,
                            "Your passwords do not match", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    TJPreferences.setUserPassword(ChangePassword.this, mNewPwd
                            .getText().toString());
                    Toast.makeText(ChangePassword.this,
                            "Your password is updated successfully",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

}
