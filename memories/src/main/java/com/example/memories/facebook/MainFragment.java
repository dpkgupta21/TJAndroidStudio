/*package com.example.memories.facebook;

import java.util.Arrays;

import android.content.pm.PackageInstaller.Session;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.memories.R;
import com.facebook.SessionState;
import com.facebook.widget.LoginButton;

public class MainFragment extends Fragment {

	private static final String TAG = "MainFragment";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main, container, false);

		LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
		authButton.setFragment(this);
		authButton.setReadPermissions(Arrays.asList("user_likes", "user_status"));

		return view;
	}

	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (state.isOpened()) {
			Log.i(TAG, "Logged in...");
		} else if (state.isClosed()) {
			Log.i(TAG, "Logged out...");
		}
	}

}
*/