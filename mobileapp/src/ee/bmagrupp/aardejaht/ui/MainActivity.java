package ee.bmagrupp.aardejaht.ui;

import ee.bmagrupp.aardejaht.R;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
	public static Toast toast;
	public static final int LOGIN_REQUEST = 1;
	public static final int REGISTRATION_REQUEST = 2;
	private MapFragment mapFragment;
	private ProfileFragment profileFragment;
	private HighScoreFragment highscoreFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		checkKeyAndSetView();

	}

	@Override
	public void onStop() {
		if ("google_sdk".equals(Build.PRODUCT)) // if emulator is used
			System.exit(0);
		super.onStop();
	}

	private void checkKeyAndSetView() {
		if (getLoginKey().equals(""))
			setContentView(R.layout.intro_layout);
		else {
			setContentView(R.layout.main_layout);
			if (mapFragment == null)
				createFragments();
			else
				getFragmentManager().beginTransaction().detach(mapFragment)
						.attach(mapFragment).commit();
		}
	}

	private String getLoginKey() {
		SharedPreferences sharedPref = getSharedPreferences("prefs",
				Context.MODE_PRIVATE);
		return sharedPref.getString("loginKey", "");
	}

	private void createFragments() {
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mapFragment = new MapFragment();
		Tab mapTab = actionBar.newTab().setText("Map")
				.setTabListener(new TabListener(mapFragment, "MapFragment"));
		actionBar.addTab(mapTab);

		profileFragment = new ProfileFragment();
		Tab profileTab = actionBar
				.newTab()
				.setText("Profile")
				.setTabListener(
						new TabListener(profileFragment, "ProfileFragment"));
		actionBar.addTab(profileTab);

		highscoreFragment = new HighScoreFragment();
		Tab highscoreTab = actionBar
				.newTab()
				.setText("Highscores")
				.setTabListener(
						new TabListener(highscoreFragment, "HighscoreFragment"));
		actionBar.addTab(highscoreTab);

		actionBar.setSelectedNavigationItem(0);
	}

	public static void showMessage(Context context, String message) {
		toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
		toast.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} else if (id == R.id.action_logout) {
			new AlertDialog.Builder(this)
					.setTitle("Log Out")
					.setMessage("Are you sure you want to log out?")
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									SharedPreferences sharedPref = getSharedPreferences(
											"prefs", Context.MODE_PRIVATE);
									SharedPreferences.Editor editor = sharedPref
											.edit();
									editor.putString("username", "");
									editor.putString("loginKey", "");
									editor.commit();
									setContentView(R.layout.intro_layout);
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// do nothing
								}
							}).setIcon(android.R.drawable.ic_dialog_alert)
					.show();

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void sortByUnits(View v) {
		highscoreFragment.sortEntries("averageUnits");
	}

	public void sortByTerritories(View v) {
		highscoreFragment.sortEntries("territoriesOwned");
	}

	public void login(View v) {
		Intent loginIntent = new Intent(this, LoginActivity.class);
		startActivityForResult(loginIntent, LOGIN_REQUEST);
	}

	public void register(View v) {
		Intent registerIntent = new Intent(this, RegisterActivity.class);
		startActivityForResult(registerIntent, REGISTRATION_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == LOGIN_REQUEST) {
			if (resultCode == RESULT_OK) {
				checkKeyAndSetView();
			}
		}
	}
}
