package erandoo.app.main;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import erandoo.app.R;
import erandoo.app.cmds.Cmd;
import erandoo.app.cmds.CmdFactory;
import erandoo.app.config.Config;
import erandoo.app.custom.AppHeaderView;
import erandoo.app.custom.BaseFragActivity;
import erandoo.app.custom.IListItemClickListener;
import erandoo.app.database.TaskDoer;
import erandoo.app.database.WorkLocation;
import erandoo.app.network.NetworkMgr;
import erandoo.app.network.NetworkResponse;
import erandoo.app.projects.WorkLocationListDialog;
import erandoo.app.tempclasses.DoerData;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.Util;

public class InviteIDoerActivity extends BaseFragActivity implements OnClickListener,OnSeekBarChangeListener {
	private final String DATA_LOAD_FROM_SERVER = "from_server";
	private final String DATA_LOAD_FROM_LIST = "list_server";
	
	private TextView txtIIDSeekValue;
	
	private final String FLD_DOER_RANGE = "range";
	private TextView txtInviteIDoerSLocation;
	private CheckBox chkAutoSelectDoer;
	private GoogleMap googleMap;
	private Bundle savedInstanceState;
	private MapView mapViewIDoers;
	private LatLng latLng;
	private Cmd searchCmd;
	private InviteDoerTask inviteDoerTask;
	private SeekBar skbDoerRange;
	private AppHeaderView appHeaderView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ed_cp_invite_idoers); 
		
		this.savedInstanceState = savedInstanceState;
		initialize();
	}

	private void initialize() {

		txtIIDSeekValue = (TextView)findViewById(R.id.txtIIDSeekValue);
		txtInviteIDoerSLocation = (TextView)findViewById(R.id.txtInviteIDoerSLocation);
		mapViewIDoers = (MapView) findViewById(R.id.mapViewIDoers);
		skbDoerRange = (SeekBar)findViewById(R.id.skbDoerRange);
		chkAutoSelectDoer = (CheckBox)findViewById(R.id.chkAutoSelectDoer);
		
		txtIIDSeekValue.setText(String.valueOf(CreateProjectActivity.invitedDoerInRange)+" Miles"); 
		chkAutoSelectDoer.setChecked(CreateProjectActivity.invitedDoerSelectionType); 
		skbDoerRange.setProgress(CreateProjectActivity.invitedDoerInRange); 
		setHeaderView();
		loadGoogleMap();
		
		googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				if(!chkAutoSelectDoer.isChecked()){
					setADoerSelected(marker);
				}else{
					Util.showCenteredToast(InviteIDoerActivity.this, getResources().getString(R.string.msg_Auto_doer_selection)); 
				}
				return true;
			}
		});
		
		chkAutoSelectDoer.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				CreateProjectActivity.clearSelectedDoers();
				if(!isChecked){
					clearAllDoerSelection();
				}
				loadInviteDoerListData(DATA_LOAD_FROM_LIST); 
			}
		});
		
		skbDoerRange.setOnSeekBarChangeListener(this);
		txtInviteIDoerSLocation.setOnClickListener(this); 
		searchCmd = CmdFactory.createSearchDoerCmd();
		mapViewIDoers.setVisibility(View.VISIBLE); 
		
		if(AppGlobals.invitedDoers.size() == 0){
			loadInviteDoerListData(DATA_LOAD_FROM_SERVER);
		}
	}
	
	private void setHeaderView() {
		appHeaderView = (AppHeaderView) findViewById(R.id.appHeaderView);
		appHeaderView.setHeaderContent(getResources().getDrawable(R.drawable.ic_back), getResources()
						.getString(R.string.Invite_Doers),null, null, Gravity.LEFT);
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.txtInviteIDoerSLocation){
			showWLocationListDialog();
		}else if(v.getId() == appHeaderView.txtHLeft.getId()){
			CreateProjectActivity.updateInstantDoerSelection(chkAutoSelectDoer.isChecked(), skbDoerRange.getProgress());
			finishActivity(false);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(AppGlobals.invitedDoers.size() > 0){
			loadInviteDoerListData(DATA_LOAD_FROM_LIST);
		}
		setDefaultWLocation();
		mapViewIDoers.onResume();
	}
	
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		loadInviteDoerListData(DATA_LOAD_FROM_SERVER);
	}
	
	private void loadInviteDoerListData(String loadFrom){
		Util.clearAsync(inviteDoerTask); 
		String[] params = new String[]{loadFrom}; 
		if(loadFrom.equals(DATA_LOAD_FROM_SERVER)){ 
			if(Util.isDeviceOnline(InviteIDoerActivity.this)){
				inviteDoerTask  = new InviteDoerTask();
				inviteDoerTask.execute(params);
			}else{
				Util.showCenteredToast(InviteIDoerActivity.this, getResources().getString(R.string.msg_Connect_internet)); 
			}
		}else{
			inviteDoerTask  = new InviteDoerTask();
			inviteDoerTask.execute(params);
		}
	}
	
	private void setADoerSelected(Marker marker){
		Long userId = Long.valueOf(marker.getTitle().trim());
		for (TaskDoer doer: AppGlobals.invitedDoers) {
			if(doer.user_id.equals(userId)){
				if(!doer.isSelected){
					doer.isSelected = true;
					CreateProjectActivity.updateInvitedDoerList(doer);
					marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ic_idoer_selected)));
				}else{
					doer.isSelected = false;
					CreateProjectActivity.updateInvitedDoerList(doer);
					marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ic_idoer)));
				}
			}
		}
	}
	
	private void clearAllDoerSelection() {
		for (TaskDoer doer : AppGlobals.invitedDoers) {
			int index = AppGlobals.invitedDoers.indexOf(doer);
			AppGlobals.invitedDoers.get(index).isSelected = false;
		}
	}
	
	private void setDefaultWLocation(){
		if(AppGlobals.defaultWLocation != null){
			if(AppGlobals.defaultWLocation.location_name != null){
				txtInviteIDoerSLocation.setText(AppGlobals.defaultWLocation.location_name);
			}else{
				txtInviteIDoerSLocation.setHint(getResources().getString(R.string.Hint_Saved_locations)); 
			}
		}
	}
	
	private void showWLocationListDialog(){
		WorkLocationListDialog dialog = WorkLocationListDialog.getInstance(new IListItemClickListener() {
			@Override
			public void onItemSelected(Object item, int position) {
				resetDefaultWLocation();
				AppGlobals.workLocations.get(position).is_default_location = "1";
				WorkLocation location = AppGlobals.workLocations.get(position);
				txtInviteIDoerSLocation.setText(location.location_name);
				AppGlobals.defaultWLocation = location;
				loadInviteDoerListData(DATA_LOAD_FROM_SERVER);                                                                                                                                                                                                                                                                                                                                                                 
			}
		});
		dialog.show(getSupportFragmentManager(), "w list "); 
	}
	
	private void resetDefaultWLocation(){
		for (int i = 0; i < AppGlobals.workLocations.size(); i++) {
			AppGlobals.workLocations.get(i).is_default_location = "0";
		}
	}
	
	private class InviteDoerTask extends AsyncTask<String, MarkerOptions,MarkerOptions[]>{
		NetworkResponse response = null;
		@Override
		protected void onPreExecute() {
			Util.showProDialog(InviteIDoerActivity.this);
		};
       		
		@Override
		protected MarkerOptions[] doInBackground(String... params) {
			if(params[0].equals(DATA_LOAD_FROM_SERVER)){
				CreateProjectActivity.clearSelectedDoers();
				AppGlobals.invitedDoers.clear();
				searchCmd.addData(TaskDoer.FLD_PROJECT_TYPE, Constants.INSTANT); 
				searchCmd.addData(TaskDoer.FLD_LONGITUDE, AppGlobals.defaultWLocation.longitude);
				searchCmd.addData(TaskDoer.FLD_LATITUDE, AppGlobals.defaultWLocation.latitude);
				searchCmd.addData(FLD_DOER_RANGE,skbDoerRange.getProgress());
				response = NetworkMgr.httpPost(Config.API_URL, searchCmd.getJsonData());
				if(response != null){
					if(response.isSuccess()){
						getInvitedDoerListData(response);
						return getMarkersData();
					}
				}
			}else{
				if(chkAutoSelectDoer.isChecked()){
					CreateProjectActivity.clearSelectedDoers();
				}
				return getMarkersData();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(MarkerOptions[] values) {
			super.onPostExecute(values);
			Util.dimissProDialog();
			if (response != null) {
				if (!response.isSuccess()) {
					Util.showCenteredToast(InviteIDoerActivity.this,response.getErrMsg());
				}
			}
			setMarkersOnGoogleMap(values);
		}
	}
	
	private void setMarkersOnGoogleMap(MarkerOptions[] values){
		googleMap.clear();
		if(values != null){
			for (MarkerOptions marker: values) {
				googleMap.addMarker(marker); 
				googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
				googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom((latLng), 12));
			}
		}
	}
	
	private MarkerOptions[] getMarkersData(){
		MarkerOptions[] markerOptions = new MarkerOptions[AppGlobals.invitedDoers.size()];
		if(AppGlobals.invitedDoers.size()>0){
			int index = 0;
			for (TaskDoer doerDetail:AppGlobals.invitedDoers) {
				latLng = new LatLng(Double.valueOf(doerDetail.latitude), Double.valueOf(doerDetail.longitude));
				MarkerOptions marker = new MarkerOptions();
				marker.position(latLng);
				marker.title(String.valueOf(doerDetail.user_id));
				if(chkAutoSelectDoer.isChecked()){
					doerDetail.isSelected = true;
					CreateProjectActivity.updateInvitedDoerList(doerDetail);
				}else{
					doerDetail.isSelected = false;
					for (TaskDoer doer: CreateProjectActivity.updateInvitedDoerList(null)) {
						if(doer.user_id.equals(doerDetail.user_id)){
							doerDetail.isSelected = true;
						}
					}
				}
				if(doerDetail.isSelected){
					marker.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ic_idoer_selected)));
				}else{
					marker.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ic_idoer)));
				}
				markerOptions[index] = marker;
				index ++;
			}
		}
		return markerOptions;
	}
	
	private void getInvitedDoerListData(NetworkResponse response){ 
		try{
			DoerData doerData = (DoerData)Util.getJsonToClassObject(response.getJsonObject().toString(), DoerData.class);
			AppGlobals.invitedDoers = doerData.data;
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void loadGoogleMap(){
		mapViewIDoers.onCreate(savedInstanceState);
		try {
			MapsInitializer.initialize(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (googleMap == null) {
			googleMap = mapViewIDoers.getMap();
			googleMap.setMyLocationEnabled(true);
			googleMap.getUiSettings().setZoomControlsEnabled(false);
			googleMap.getUiSettings().setMyLocationButtonEnabled(false); 
		}
	}
	
	@Override
	public void onBackPressed() {
		finishActivity(true);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mapViewIDoers.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mapViewIDoers.onLowMemory();
	}
	
	private void finishActivity(boolean isClearSelection) {
		if(isClearSelection){
			if(CreateProjectActivity.updateInvitedDoerList(null) != null){
				clearAllDoerSelection();
			}
		}
		InviteIDoerActivity.this.finish();
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
		txtIIDSeekValue.setText(String.valueOf(progress)+" Miles"); 
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {}
}
