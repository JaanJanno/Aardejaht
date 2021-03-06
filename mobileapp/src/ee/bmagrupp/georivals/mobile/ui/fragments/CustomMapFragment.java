package ee.bmagrupp.georivals.mobile.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import ee.bmagrupp.georivals.mobile.R;
import ee.bmagrupp.georivals.mobile.core.communications.loaders.province.ProvinceUILoader;
import ee.bmagrupp.georivals.mobile.core.location.LocationChangeUIHandler;
import ee.bmagrupp.georivals.mobile.models.map.CameraFOV;
import ee.bmagrupp.georivals.mobile.models.province.ProvinceDTO;
import ee.bmagrupp.georivals.mobile.models.province.ProvinceType;
import ee.bmagrupp.georivals.mobile.ui.MainActivity;
import ee.bmagrupp.georivals.mobile.ui.listeners.MapClickListener;
import ee.bmagrupp.georivals.mobile.ui.widgets.TabItem;

@SuppressLint("InflateParams")
@SuppressWarnings("deprecation")
public class CustomMapFragment extends MapFragment implements TabItem,
		OnMyLocationButtonClickListener, OnCameraChangeListener {
	// non-static immutable variables (local constants)
	private MainActivity activity;
	private Resources resources;
	private final int tabNameId = R.string.map;
	private final int tabIconId = R.drawable.globe_icon;
	private final double provinceLatitudeRadius = 0.0005;
	private final double provinceLongitudeRadius = 0.001;

	// non-static mutable variables
	private GoogleMap map;
	private LatLng defaultCameraLatLng = new LatLng(59.437046, 24.753742);
	private float defaultCameraZoom = 17;
	private ArrayList<ProvinceDTO> drawnProvincesList = new ArrayList<ProvinceDTO>();
	private List<ProvinceDTO> provinceList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		activity = (MainActivity) getActivity();
		resources = activity.getResources();
		initializeMap();
		if (MainActivity.choosingHomeProvince) {
			addSetHomeViews();
		}
		return v;
	}

	/**
	 * Initializes the map; moves the camera to the right location, sets whether
	 * 'My Location' is enabled, sets 'My Location' click listener, map click
	 * listener and camera change listener.
	 */

	private void initializeMap() {
		map = this.getMap();
		if (map != null) {
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(
					defaultCameraLatLng, defaultCameraZoom));
			map.setMyLocationEnabled(true);
			map.setOnMyLocationButtonClickListener(this);
			map.setOnMapClickListener(new MapClickListener(activity));
			map.setOnCameraChangeListener(this);
		}
	}

	@Override
	public boolean onMyLocationButtonClick() {
		LocationManager locationManager = (LocationManager) activity
				.getSystemService(Context.LOCATION_SERVICE);
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			activity.showToastMessage("GPS is disabled!");
		} else {
			activity.showToastMessage("Waiting for location...");
		}
		return false;
	}

	@Override
	public void onCameraChange(CameraPosition position) {
		refreshMap();
	}

	/**
	 * Sets up all 'Set Home' views.
	 */

	private void addSetHomeViews() {
		TextView chooseHomeLabel = (TextView) activity
				.findViewById(R.id.choose_home_label);
		chooseHomeLabel.setVisibility(View.VISIBLE);
		chooseHomeLabel.setTypeface(MainActivity.GABRIOLA_FONT);

		Button setHomeButton = (Button) activity
				.findViewById(R.id.set_home_current);
		setHomeButton.setVisibility(View.VISIBLE);
		setHomeButton.setTypeface(MainActivity.GABRIOLA_FONT);
		setHomeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Location playerLocation = LocationChangeUIHandler
						.getLastPlayerLocation();
				long lastLocationUpdateTime = LocationChangeUIHandler
						.getLastLocationUpdateTime();
				int minTimeSinceLocationUpdate = 60 * 1000; // one minute
				if (playerLocation != null
						&& lastLocationUpdateTime + minTimeSinceLocationUpdate > System
								.currentTimeMillis()) {
					MainActivity.REGISTRATION_FRAGMENT
							.showPhase2ConfirmationDialog(new LatLng(
									playerLocation.getLatitude(),
									playerLocation.getLongitude()));
				} else {
					activity.showToastMessage(resources
							.getString(R.string.error_get_location));
				}
			}
		});
	}

	@Override
	public void onDestroyView() {
		defaultCameraLatLng = map.getCameraPosition().target;
		defaultCameraZoom = map.getCameraPosition().zoom;
		activity.cancelToastMessage();
		super.onDestroyView();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	/**
	 * Refreshes the map; checks whether new province tiles need to be drawn or
	 * old ones cleared.
	 */

	public void refreshMap() {
		if (map.getCameraPosition().zoom > 15)
			requestProvinceListData();
		else if (drawnProvincesList.size() > 0) {
			clearMap();
		}
	}

	public void clearMap() {
		map.clear();
		drawnProvincesList.clear();
	}

	/**
	 * Requests displayable provinces list data from the server. If successful,
	 * it draws the provinces on the map.
	 */

	private void requestProvinceListData() {
		LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
		ProvinceUILoader l = new ProvinceUILoader(MainActivity.sid,
				new CameraFOV(bounds), activity) {

			@Override
			public void handleResponseInUI(List<ProvinceDTO> responseList) {
				if (responseList != null) {
					provinceList = responseList;
					drawProvinces();
				} else {
					activity.showToastMessage(resources
							.getString(R.string.error_retrieval_fail));
				}
			}

			@Override
			public void handleResponseInBackground(
					List<ProvinceDTO> responseList) {

			}

		};
		l.retrieveResponse();
	}

	/**
	 * Draws all the new provinces, which were returned from the server, on the
	 * map.
	 */

	private void drawProvinces() {
		ArrayList<ProvinceDTO> newDrawnProvincesList = new ArrayList<ProvinceDTO>();
		for (int i = 0; i < provinceList.size(); i++) {
			ProvinceDTO province = provinceList.get(i);
			ProvinceDTO drawnProvince = findDrawnProvince(province);
			if (!provincesEqual(province, drawnProvince)) {
				LinearLayout provinceLayout;
				if (province.getType() != ProvinceType.PLAYER
						&& province.getType() != ProvinceType.HOME
						&& (province.isUnderAttack() || !province
								.isAttackable())) {
					provinceLayout = getSpecialProvinceLayout(province);
				} else {
					provinceLayout = getNormalProvinceLayout(province);
				}

				Bitmap provinceBitmap = createBitmap(provinceLayout);

				LatLngBounds provinceBounds = new LatLngBounds(new LatLng(
						province.getLatitude() - provinceLatitudeRadius,
						province.getLongitude() - provinceLongitudeRadius),
						new LatLng(province.getLatitude()
								+ provinceLatitudeRadius, province
								.getLongitude() + provinceLongitudeRadius));

				GroundOverlay groundOverlay = createGroundOverlay(
						provinceBitmap, provinceBounds);
				province.setGroundOverlay(groundOverlay);
				newDrawnProvincesList.add(province);
			} else {
				newDrawnProvincesList.add(drawnProvince);
			}
		}

		removeOldProvinces(newDrawnProvincesList);
	}

	/**
	 * @param province
	 * 
	 * @return The drawn province object which corresponds to the given
	 *         province.
	 */

	private ProvinceDTO findDrawnProvince(ProvinceDTO province) {
		double latitude = province.getLatitude();
		double longitude = province.getLongitude();
		for (ProvinceDTO drawnProvince : drawnProvincesList) {
			if (doublesEqual(drawnProvince.getLatitude(), latitude)
					&& doublesEqual(drawnProvince.getLongitude(), longitude))
				return drawnProvince;
		}
		return null;

	}

	/**
	 * @param double1
	 * @param double2
	 * 
	 * @return Whether the given doubles are equal or not.
	 */

	private boolean doublesEqual(double double1, double double2) {
		if (double1 > double2 - 0.0001 && double1 < double2 + 0.0001)
			return true;
		else
			return false;
	}

	/**
	 * @param province1
	 * @param province2
	 * 
	 * @return Whether the given provinces are equal or not.
	 */

	private boolean provincesEqual(ProvinceDTO province1, ProvinceDTO province2) {
		if (province1 != null
				&& province2 != null
				&& province1.getUnitSize() == province2.getUnitSize()
				&& province1.isAttackable() == province2.isAttackable()
				&& province1.getType() == province2.getType()
				&& province1.getProvinceName().equals(
						province2.getProvinceName()))
			return true;
		else
			return false;
	}

	/**
	 * Removes expired province tiles from the map.
	 * 
	 * @param newDrawnProvincesList
	 */

	private void removeOldProvinces(ArrayList<ProvinceDTO> newDrawnProvincesList) {
		for (ProvinceDTO province : drawnProvincesList) {
			boolean mustRemove = true;
			for (ProvinceDTO province2 : newDrawnProvincesList) {
				if (provincesEqual(province, province2)) {
					mustRemove = false;
					break;
				}
			}
			if (mustRemove)
				province.getGroundOverlay().remove();
		}
		drawnProvincesList = newDrawnProvincesList;
	}

	/**
	 * @param province
	 * 
	 * @return The layout of a 'locked' and 'under attack' province tile.
	 */

	private LinearLayout getSpecialProvinceLayout(ProvinceDTO province) {
		LinearLayout provinceLayout = (LinearLayout) LayoutInflater.from(
				activity).inflate(R.layout.province_tile_special, null);
		ImageView specialIcon = (ImageView) provinceLayout
				.findViewById(R.id.province_special_icon);
		int provinceColor;
		if (province.isUnderAttack()) {
			specialIcon.setImageResource(R.drawable.province_alert_icon);
			provinceColor = resources.getColor(R.color.green_transparent);
		} else {
			specialIcon.setImageResource(R.drawable.lock_icon);
			provinceColor = resources.getColor(R.color.dark_brown_transparent);
		}

		GradientDrawable provinceBackground = new GradientDrawable();
		provinceBackground.setColor(provinceColor);
		provinceBackground.setStroke(1,
				resources.getColor(R.color.black_transparent));
		provinceLayout.setBackgroundDrawable(provinceBackground);

		return provinceLayout;
	}

	/**
	 * @param province
	 * 
	 * @return The layout of a normal province tile.
	 */

	private LinearLayout getNormalProvinceLayout(ProvinceDTO province) {
		LinearLayout provinceLayout = (LinearLayout) LayoutInflater.from(
				activity).inflate(R.layout.province_tile_normal, null);
		TextView provinceName = (TextView) provinceLayout
				.findViewById(R.id.province_name_text);
		provinceName.setTypeface(MainActivity.GABRIOLA_FONT);
		int provinceColor;
		ProvinceType provinceType = province.getType();
		if (provinceType == ProvinceType.BOT) {
			RelativeLayout provinceNameLayout = (RelativeLayout) provinceLayout
					.findViewById(R.id.province_name);
			provinceNameLayout.setVisibility(View.INVISIBLE);
			provinceColor = resources.getColor(R.color.brown_transparent);
		} else {
			provinceName.setText(province.getProvinceName());
			if (provinceType == ProvinceType.HOME
					|| provinceType == ProvinceType.PLAYER) {
				if (provinceType == ProvinceType.PLAYER)
					hideHomeIcon(provinceLayout);
				provinceColor = resources.getColor(R.color.green_transparent);
			} else {
				provinceColor = resources.getColor(R.color.brown_transparent);
				hideHomeIcon(provinceLayout);
			}
		}

		GradientDrawable provinceBackground = new GradientDrawable();
		provinceBackground.setColor(provinceColor);
		provinceBackground.setStroke(1,
				resources.getColor(R.color.black_transparent));
		provinceLayout.setBackgroundDrawable(provinceBackground);

		setProvinceUnitCount(provinceLayout, province.getUnitSize());

		return provinceLayout;
	}

	private void hideHomeIcon(LinearLayout provinceLayout) {
		ImageView homeIcon = (ImageView) provinceLayout
				.findViewById(R.id.province_home_icon);
		homeIcon.setVisibility(View.GONE);
	}

	/**
	 * Sets the unit count view of the province.
	 * 
	 * @param provinceLayout
	 * @param unitCount
	 */

	private void setProvinceUnitCount(LinearLayout provinceLayout, int unitCount) {
		TextView unitCountTextView = (TextView) provinceLayout
				.findViewById(R.id.province_unit_count_text);
		unitCountTextView.setText(String.valueOf(unitCount));
		unitCountTextView.setTypeface(MainActivity.GABRIOLA_FONT);

		RelativeLayout unitCountLayout = (RelativeLayout) provinceLayout
				.findViewById(R.id.province_unit_count);
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) unitCountLayout
				.getLayoutParams();
		params.topMargin = (int) Math
				.abs(2.5 * map.getCameraPosition().target.latitude);
		unitCountLayout.setLayoutParams(params);
	}

	/**
	 * @param view
	 * 
	 * @return A bitmap of the given view.
	 */

	private Bitmap createBitmap(View view) {
		view.setDrawingCacheEnabled(true);
		view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		return view.getDrawingCache();
	}

	/**
	 * @param bitmap
	 * @param bounds
	 * 
	 * @return A ground overlay object of the given bitmap, which is placed in
	 *         the given bounds.
	 */

	private GroundOverlay createGroundOverlay(Bitmap bitmap, LatLngBounds bounds) {
		GroundOverlayOptions overlayOptions = new GroundOverlayOptions().image(
				BitmapDescriptorFactory.fromBitmap(bitmap)).positionFromBounds(
				bounds);
		return map.addGroundOverlay(overlayOptions);
	}

	/**
	 * @return The last saved camera location.
	 */

	public LatLng getLastCameraLatLng() {
		return defaultCameraLatLng;
	}

	/**
	 * @return The last saved camera zoom level.
	 */

	public float getLastCameraZoom() {
		return defaultCameraZoom;
	}

	@Override
	public int getTabIconId() {
		return tabIconId;
	}

	@Override
	public int getTabNameId() {
		return tabNameId;
	}

	public void setDefaultCameraLatLng(LatLng latLng) {
		this.defaultCameraLatLng = latLng;
	}

}