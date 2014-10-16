package ee.bmagrupp.aardejaht.ui;

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;

import ee.bmagrupp.aardejaht.R;
import android.support.v4.app.FragmentActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

public class MapActivity extends FragmentActivity implements
		ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
	private GoogleMap map;
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;
	private LocationManager locationManager;
	private static Toast mToast;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_layout);
		context = getApplicationContext();
		setupMap();

	}

	@Override
	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	@Override
	protected void onStop() {
		mGoogleApiClient.disconnect();
		if ("google_sdk".equals(Build.PRODUCT)) // if emulator is used
			System.exit(0);
		super.onStop();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mToast != null)
			mToast.cancel();
	}

	@Override
	public void onConnected(Bundle bundle) {
		// set up location requester
		mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setInterval(1000);
		mLocationRequest.setFastestInterval(500);
		LocationServices.FusedLocationApi.requestLocationUpdates(
				mGoogleApiClient, mLocationRequest, this);
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.d("DEBUG", "Google Api Client connection has been suspended.");
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.d("DEBUG", "Google Api Client connection has failed.");
	}

	@Override
	public void onLocationChanged(Location location) {
		focusOnLocation(location);
		if (mToast != null)
			mToast.cancel();
	}

	// focuses the camera on the given location
	public void focusOnLocation(Location location) {
		float currentZoom = map.getCameraPosition().zoom;
		LatLng latLng = new LatLng(location.getLatitude(),
				location.getLongitude());
		map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(latLng).zoom(currentZoom).bearing(0).tilt(0).build();
		CameraUpdate cameraUpdate = CameraUpdateFactory
				.newCameraPosition(cameraPosition);
		map.animateCamera(cameraUpdate);
	}

	// show toast message
	public static void showMessage(Context context, String message) {
		mToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
		mToast.show();
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
		} else if (id == R.id.email) {
			Intent highscoreIntent = new Intent(this, HighScoreActivity.class);
			startActivity(highscoreIntent);
			return true;
		} else if (id == R.id.action_profile) {
			Intent profileIntent = new Intent(this, ProfileActivity.class);
			startActivity(profileIntent);
			return true;
		} 
		return super.onOptionsItemSelected(item);
	}

	private void setupMap() {
		locationManager = (LocationManager) getApplicationContext()
				.getSystemService(Context.LOCATION_SERVICE);
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addApi(LocationServices.API).addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).build();
		map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		map.setMyLocationEnabled(true);

		// set default camera location
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(59.437046,
				24.753742), 16.0f));

		// set 'my location' button listener
		map.setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener() {
			@Override
			public boolean onMyLocationButtonClick() {
				if (!locationManager
						.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					showMessage(context, "GPS is disabled!");
				} else {
					showMessage(context, "Waiting for location...");
					return false;
				}
				return false;
			}
		});
	}
}
