package ee.bmagrupp.aardejaht.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import ee.bmagrupp.aardejaht.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class MapFragment extends com.google.android.gms.maps.MapFragment
		implements ConnectionCallbacks, OnConnectionFailedListener,
		LocationListener {
	private GoogleMap map;
	private GoogleApiClient googleApiClient;
	private LocationRequest locationRequest;
	private LocationManager locationManager;
	private Activity activity;
	private LatLng lastLatLng;
	private float lastZoom;
	private ButtonClickListener buttonClickListener;
	private MapClickListener mapClickListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		setupMap();
		return v;
	}

	@Override
	public void onDestroyView() {
		lastLatLng = map.getCameraPosition().target;
		lastZoom = map.getCameraPosition().zoom;
		if (MainActivity.toast != null)
			MainActivity.toast.cancel();
		super.onDestroyView();
	}

	@Override
	public void onStart() {
		super.onStart();
		googleApiClient.connect();
	}

	@Override
	public void onStop() {
		googleApiClient.disconnect();
		super.onStop();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (MainActivity.toast != null)
			MainActivity.toast.cancel();
	}

	@Override
	public void onConnected(Bundle bundle) {
		locationRequest = LocationRequest.create();
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		locationRequest.setInterval(1000);
		locationRequest.setFastestInterval(500);
		LocationServices.FusedLocationApi.requestLocationUpdates(
				googleApiClient, locationRequest, this);
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
		if (MainActivity.toast != null)
			MainActivity.toast.cancel();
	}

	// currently not in use, but this will be used later
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setupMap() {
		if (map == null) {
			activity = getActivity();
			locationManager = (LocationManager) activity
					.getSystemService(Context.LOCATION_SERVICE);
			googleApiClient = new GoogleApiClient.Builder(activity)
					.addApi(LocationServices.API).addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this).build();
			buttonClickListener = new ButtonClickListener(activity,
					locationManager);
			mapClickListener = new MapClickListener((MainActivity) activity);
			map = this.getMap();

			if (map != null) {
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
						59.437046, 24.753742), 16.0f));
				setMapSettings();
			}
		} else {
			map = this.getMap();
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng,
					lastZoom));
			setMapSettings();
		}
	}

	private void setMapSettings() {
		map.setMyLocationEnabled(true);
		map.setOnMyLocationButtonClickListener(buttonClickListener);
		map.setOnMapClickListener(mapClickListener);
		map.setOnCameraChangeListener(new OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition position) {
				map.clear();
				if (map.getCameraPosition().zoom > 14)
					drawProvinces();
			}
		});
	}

	private void drawProvinces() {
		double lengthLatitude = 0.001;
		double lengthLongitude = 0.002;
		LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
		LatLng SW = bounds.southwest;
		LatLng NE = bounds.northeast;
		double SWlatitude = Math.floor(SW.latitude * 1000) / 1000;
		double SWlongitude = Math.floor(SW.longitude * 1000) / 1000;
		String string = String.valueOf(SWlongitude);
		if (Character.getNumericValue(string.charAt(string.length() - 1)) % 2 == 1)
			SWlongitude -= lengthLongitude / 2;
		double NElatitude = Math.ceil(NE.latitude * 1000) / 1000;
		double NElongitude = Math.ceil(NE.longitude * 1000) / 1000;
		string = String.valueOf(NElongitude);
		if (Character.getNumericValue(string.charAt(string.length() - 1)) % 2 == 1)
			NElongitude += lengthLongitude / 2;

		double currentLatitude = SWlatitude;
		double currentLongitude = SWlongitude;
		while (currentLatitude < NElatitude) {
			while (currentLongitude < NElongitude) {
				map.addPolygon(new PolygonOptions()
						.add(new LatLng(currentLatitude, currentLongitude),
								new LatLng(currentLatitude, currentLongitude
										+ lengthLongitude),
								new LatLng(currentLatitude + lengthLatitude,
										currentLongitude + lengthLongitude),
								new LatLng(currentLatitude + lengthLatitude,
										currentLongitude))
						.strokeColor(Color.BLACK).strokeWidth(1));
				currentLongitude += lengthLongitude;
			}
			currentLatitude = currentLatitude + lengthLatitude;
			currentLongitude = SWlongitude;

		}

	}

	public GoogleApiClient getGoogleApiClient() {
		return googleApiClient;
	}

	public LocationRequest getLocationRequest() {
		return locationRequest;
	}

	public LocationManager getLocationManager() {
		return locationManager;
	}

	public LatLng getLastLatLng() {
		return lastLatLng;
	}

	public float getLastZoom() {
		return lastZoom;
	}

	public ButtonClickListener getButtonClickListener() {
		return buttonClickListener;
	}
}