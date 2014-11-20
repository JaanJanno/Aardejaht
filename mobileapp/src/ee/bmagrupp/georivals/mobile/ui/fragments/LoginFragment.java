package ee.bmagrupp.georivals.mobile.ui.fragments;

import ee.bmagrupp.georivals.mobile.R;
import ee.bmagrupp.georivals.mobile.ui.MainActivity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

@SuppressWarnings("deprecation")
public class LoginFragment extends Fragment {
	private RelativeLayout loginLayout;
	private MainActivity activity;
	private Resources resources;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		loginLayout = (RelativeLayout) inflater.inflate(R.layout.login_layout,
				container, false);
		activity = (MainActivity) getActivity();
		resources = activity.getResources();
		MainActivity.changeFonts(loginLayout);
		setButtonListeners();
		return loginLayout;
	}

	@Override
	public void onDestroyView() {
		if (MainActivity.toast != null)
			MainActivity.toast.cancel();
		super.onDestroyView();
	}

	private void setButtonListeners() {
		Button startButton = (Button) loginLayout
				.findViewById(R.id.login_start);
		Button sendKeyButton = (Button) loginLayout
				.findViewById(R.id.login_send_key);

		startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText keyEditText = (EditText) loginLayout
						.findViewById(R.id.login_key_textbox);
				String loginKey = keyEditText.getText().toString();
				if (loginKey.length() == 16 || loginKey.equals("test")
						|| loginKey.equals("johnny")) {
					loginRequest(loginKey);
				} else {
					activity.showMessage(resources
							.getString(R.string.error_invalid_key));
				}
			}
		});

		sendKeyButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText emailEditText = (EditText) loginLayout
						.findViewById(R.id.login_email_textbox);
				String email = emailEditText.getText().toString();
				if (isValidEmail(email)) {
					sendKeyRequest(email);
				} else {
					activity.showMessage(resources
							.getString(R.string.error_invalid_email));
				}
			}
		});
	}

	private void loginRequest(String loginKey) {
		// test users
		if (loginKey.equals("test")) {
			SharedPreferences sharedPref = activity
					.getPreferences(Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString("sid", "BPUYYOU62flwiWJe");
			editor.putInt("userId", 1);
			editor.commit();
			activity.updatePlayerInfo();
			activity.getActionBar().setSelectedNavigationItem(0);
		} else if (loginKey.equals("johnny")) {
			SharedPreferences sharedPref = activity
					.getPreferences(Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString("sid", "UJ86IpW5xK8ZZH7t");
			editor.putInt("userId", 5);
			editor.commit();
			activity.updatePlayerInfo();
			activity.getActionBar().setSelectedNavigationItem(0);
		}
	}

	private void sendKeyRequest(String email) {

	}

	private boolean isValidEmail(CharSequence email) {
		if (email == null)
			return false;
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}

}