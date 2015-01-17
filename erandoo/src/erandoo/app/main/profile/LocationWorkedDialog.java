package erandoo.app.main.profile;

import java.util.ArrayList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import erandoo.app.R;
import erandoo.app.custom.CustomDialog;
import erandoo.app.database.WorkLocation;
import erandoo.app.utilities.Constants;

public class LocationWorkedDialog extends CustomDialog implements OnClickListener {
	private ImageView imgDBack;
	private TextView txtDHTitle;
	private TextView txtDHRight;
	
	private View view;
	
	private MapView mapViewLocationWorked;
	private Bundle savedInstanceState;
	private GoogleMap googleMap;
	
	private ArrayList<WorkLocation> locationList;
	
	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.ed_profile_location_worked, container, false);
		locationList = (ArrayList<WorkLocation>) getArguments().getSerializable(Constants.SERIALIZABLE_DATA);
		this.savedInstanceState = savedInstanceState;
		initialize();
		return view;
	}

	private void initialize() {
		imgDBack = (ImageView) view.findViewById(R.id.imgDBack);
		txtDHTitle = (TextView) view.findViewById(R.id.txtDHTitle);
		txtDHRight = (TextView) view.findViewById(R.id.txtDHRight);
		
		txtDHTitle.setText(getResources().getString(R.string.location_worked));
		txtDHRight.setVisibility(View.GONE);
		
		
		mapViewLocationWorked = (MapView) view.findViewById(R.id.mapViewLocationWorked);
		
		imgDBack.setOnClickListener(this);
		
		loadGoogleMap();
	}
	
	private void loadGoogleMap(){
		mapViewLocationWorked.onCreate(savedInstanceState);
		try {
			MapsInitializer.initialize(getActivity());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (googleMap == null) {
			googleMap = mapViewLocationWorked.getMap();
			googleMap.setMyLocationEnabled(true);
			googleMap.getUiSettings().setZoomControlsEnabled(false);
			googleMap.getUiSettings().setMyLocationButtonEnabled(false); 
		}
		mapViewLocationWorked.onResume();
	}

	private void setMarkersOnGoogleMap(){
		if(locationList.size()>0){
			googleMap.clear();
			for (WorkLocation location : locationList) {
				if (location.latitude != null && location.longitude != null) {
					LatLng latLng = new LatLng(Double.valueOf(location.latitude), Double.valueOf(location.longitude));
					MarkerOptions marker = new MarkerOptions();
					marker.position(latLng);
					marker.title(location.address);				
					googleMap.addMarker(marker);
					googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
					googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom((latLng), 12));
				}
			}
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(locationList.size() > 0){
			setMarkersOnGoogleMap();
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mapViewLocationWorked.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mapViewLocationWorked.onLowMemory();
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.imgDBack) {
			dismiss();
		}
	}
}
