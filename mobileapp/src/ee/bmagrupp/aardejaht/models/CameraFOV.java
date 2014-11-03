package ee.bmagrupp.aardejaht.models;

import com.google.android.gms.maps.model.LatLngBounds;

@SuppressWarnings("unused")
public class CameraFOV {
	private double SWlatitude;
	private double SWlongitude;
	private double NElatitude;
	private double NElongitude;
	
	public CameraFOV (LatLngBounds latLngBounds){
		this.SWlatitude = latLngBounds.southwest.latitude;
		this.SWlongitude = latLngBounds.southwest.longitude;
		this.NElatitude = latLngBounds.northeast.latitude;
		this.NElongitude = latLngBounds.northeast.longitude;
	}

}