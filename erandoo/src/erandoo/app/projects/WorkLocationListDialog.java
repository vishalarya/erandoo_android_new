package erandoo.app.projects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.adapters.WLocationListAdapter;
import erandoo.app.config.Config;
import erandoo.app.custom.CustomDialog;
import erandoo.app.custom.IListItemClickListener;
import erandoo.app.database.DatabaseMgr;
import erandoo.app.database.WorkLocation;
import erandoo.app.main.AppGlobals;
import erandoo.app.network.JSONParser;
import erandoo.app.utilities.Util;

public class WorkLocationListDialog extends CustomDialog implements OnClickListener , TextWatcher{
	private View view;
	private ImageButton imgBtnSimpleListBack;
	private ImageButton imgBtnSListClearSearch;
	private ListView listVSimpleList;
	private TextView txtSimpleListTitle;
	private EditText eTextSimpleListSearch;
	private TextView txtSListDone;
	private WLocationListAdapter adapter;
	private static final String TAG_RESULT = "predictions";
	private PaserDataTask paserDataTask;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.ed_simple_list, container, false);
		initialize();
		return view;
	}

	private void initialize() {
		getDialog().getWindow().getAttributes().windowAnimations = R.style.AppDialogFadeAnimation;
		imgBtnSimpleListBack = (ImageButton)view.findViewById(R.id.imgBtnSimpleListBack);
		imgBtnSListClearSearch = (ImageButton)view.findViewById(R.id.imgBtnSListClearSearch);
		listVSimpleList = (ListView)view.findViewById(R.id.listVSimpleList);
		txtSimpleListTitle = (TextView)view.findViewById(R.id.txtSimpleListTitle);
		eTextSimpleListSearch = (EditText)view.findViewById(R.id.eTextSimpleListSearch);
		txtSListDone = (TextView)view.findViewById(R.id.txtSListDone);
		eTextSimpleListSearch.setHint(R.string.Hint_Search_location); 
		txtSListDone.setVisibility(View.GONE); 
		txtSimpleListTitle.setText(view.getContext().getResources().getString(R.string.Saved_Location));  
		imgBtnSimpleListBack.setOnClickListener(this);
		imgBtnSListClearSearch.setOnClickListener(this);

		listVSimpleList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				target.onItemSelected(AppGlobals.workLocations.get(position), position); 
				WorkLocationListDialog.this.dismiss();
			}
		});
		
		eTextSimpleListSearch.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				Util.hideKeypad(view.getContext(), eTextSimpleListSearch);
				if(actionId == EditorInfo.IME_ACTION_SEARCH){
					goToSearchLocations();
				}
				return true;
			}
		});
		
		eTextSimpleListSearch.addTextChangedListener(this); 
		setWLocationListData();
	}
	
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.imgBtnSimpleListBack) {
			WorkLocationListDialog.this.dismiss();
		}else if(v.getId() == R.id.imgBtnSListClearSearch){
			eTextSimpleListSearch.setText("");
		}
	}
	
	@Override
	public void onTextChanged(CharSequence s, int start, int before,int count) {
		if(eTextSimpleListSearch.getText().toString().trim().replaceAll(" ", ",").length() == 0){
			loadLocationData("saved_location");
		}
	}
	
	private void goToSearchLocations(){
		AppGlobals.workLocations.clear();
		refreshListData();
		String str  = eTextSimpleListSearch.getText().toString().trim().replaceAll(" ", ",");
		if(str.length() != 0){
			String[] searchArr =str.split(","); 
			String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="
					+ searchArr[0] + "&sensor=true&key=" + Config.GPLACE_API_TOKEN;
			if(searchArr[0] != ""){
				if (searchArr.length >= 1) {
					loadLocationData(url);
				}
			}
		}else{
			loadLocationData("saved_location");
		}
	}
	
	private void loadLocationData(String param){
		Util.clearAsync(paserDataTask); 
		if (Util.isDeviceOnline(getActivity())) { 
			paserDataTask = new PaserDataTask();
			paserDataTask.execute(new String[]{param});
		}else{
			Util.showCenteredToast(getActivity(), getActivity().getResources().getString(R.string.msg_Connect_internet));
		}
	}
	
	public class PaserDataTask extends AsyncTask<String, Integer, Void> {
		@Override
		protected void onPreExecute() {
			Util.showProDialog(view.getContext()); 
		}
		@Override
		protected Void doInBackground(String... params) {
			if(params[0].equals("saved_location")){ 
				DatabaseMgr dbMgr = DatabaseMgr.getInstance(getActivity());
				dbMgr.getWorkLocationList();
			}else{
				try {
					JSONParser jParser = new JSONParser();
					JSONObject json = jParser.getJSONFromUrl(params[0]);
					if (json != null) {
						
							JSONArray contacts = json.getJSONArray(TAG_RESULT);
							for (int i = 0; i < contacts.length(); i++) {
								JSONObject c = contacts.getJSONObject(i);
								WorkLocation location = getLatLngFromAddress(c.getString("description"));
								AppGlobals.workLocations.add(location);
							}
						
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result); 
			Util.dimissProDialog();
			refreshListData();
		}
	}
	
	private WorkLocation getLatLngFromAddress(String address){
		WorkLocation location = new WorkLocation();
		location.address = address;
		location.location_name = address;
		location.location_geo_area = address;
		location.is_default_location = "0";
		JSONParser jParser = new JSONParser();
		try {
			JSONArray jsonObject = jParser.getLatLongFromGivenAddress(address).getJSONArray("results");  
			if(jsonObject.length()>0){
				location.longitude = jsonObject.getJSONObject(0)
						.getJSONObject("geometry").getJSONObject("location").getString("lng");
				location.latitude = jsonObject.getJSONObject(0).
						getJSONObject("geometry").getJSONObject("location").getString("lat");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return location;
	}
	
	private void refreshListData(){
		adapter.notifyDataSetChanged();
		listVSimpleList.setAdapter(adapter);
	}

	
	private void setWLocationListData(){
		adapter = new WLocationListAdapter(getActivity(), R.layout.ed_simple_list_row, AppGlobals.workLocations);
		adapter.setNotifyOnChange(true); 
		listVSimpleList.setAdapter(adapter);
	}
	
	// --------------------------CUSTOM EVENT
		private IListItemClickListener target;

		public static WorkLocationListDialog getInstance(final IListItemClickListener target) {
			WorkLocationListDialog fragment;
			fragment = new WorkLocationListDialog();
			fragment.setTarget(target);
			return fragment;
		}

		public void setTarget(IListItemClickListener target) {
			this.target = target;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,int after) {}

		@Override
		public void afterTextChanged(Editable s) {}	
}
