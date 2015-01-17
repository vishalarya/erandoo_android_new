package erandoo.app.custom;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.adapters.SingleChoiceAdapter;
import erandoo.app.cmds.Cmd;
import erandoo.app.cmds.CmdFactory;
import erandoo.app.config.Config;
import erandoo.app.database.City;
import erandoo.app.database.Country;
import erandoo.app.database.Region;
import erandoo.app.database.SingleChoice;
import erandoo.app.database.State;
import erandoo.app.database.WorkLocation;
import erandoo.app.main.AppGlobals;
import erandoo.app.main.registration.SignUpActivity;
import erandoo.app.network.NetworkMgr;
import erandoo.app.network.NetworkResponse;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.Util;

public class SingleChoiceDialog extends CustomDialog implements OnClickListener {

	private EditText eTextSCSearch;
	private ImageButton imgBtnSCClearSearch;

	private static String id;
	private static String fromWhere = "";
	private View view;
	private ListView singleChoiceLV;
	private ArrayList<SingleChoice> singleChoiceList;
	private ArrayList<SingleChoice> filteredList;

	private SingleChoiceAdapter singleChoiceAdapter;
	static WorkLocation workLocationObj;
	private static String setName;
	private static TextView setText;
	private String key_name;
	private String key_id;

	private OnClickListener singleChoiceClick;
	private TextView txtHLeft;
	private TextView txtHTitle;
	private TextView txtHRight1;
	private TextView txtHRight2;
	
	private ProgressBar pBarSingleChoice;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.ed_single_choice, container, false);
		initialize();

		initializeSettingHeaderView(getActivity(), txtHLeft, txtHTitle, txtHRight1, txtHRight2, fromWhere, "", null, Gravity.CENTER);
		singleChoiceList = new ArrayList<SingleChoice>();
		
		getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		
		singleChoiceClick = new OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickPerform(v);
			}
		};
		
		return view;
	}

	public void onClickPerform(View v) {
		SingleChoice singleChoice = (SingleChoice) v.getTag();
		singleChoice.isSelected = true;
		singleChoiceAdapter.notifyDataSetChanged();
		String name = singleChoice.name;
		String id_value = singleChoice.id;
		setText.setText(name);
		if (fromWhere.equals(Constants.setState)) {
			State state = new State();
			state.state_id = id_value;
			state.state_name = name;
			workLocationObj.state = state;

		} else if (fromWhere.equals(Constants.setRegion)) {
			Region region = new Region();
			region.region_id = id_value;
			region.region_name = name;
			workLocationObj.region = region;

		} else if (fromWhere.equals(Constants.setCity)) {
			City city = new City();
			city.city_id = id_value;
			city.city_name = name;
			workLocationObj.city = city;

		} else {
			Country country = new Country();
			country.country_code = id_value;
			country.country_name = name;
			workLocationObj.country = country;
		}
		dismiss();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		GetDataFromServerAsync getDataAsync = new GetDataFromServerAsync();
		getDataAsync.execute();
	}

	private void initialize() {
		singleChoiceLV = (ListView) view.findViewById(R.id.singleChoiceLV);
		eTextSCSearch = (EditText) view.findViewById(R.id.eTextSCSearch);
		imgBtnSCClearSearch = (ImageButton) view.findViewById(R.id.imgBtnSCClearSearch);
		txtHTitle = (TextView) view.findViewById(R.id.txtHTitle);
		pBarSingleChoice = (ProgressBar) view.findViewById(R.id.pBarSingleChoice);

		imgBtnSCClearSearch.setOnClickListener(this);

		txtHTitle.setText(fromWhere);
		String[] titleStringArr = fromWhere.split(" ");
		eTextSCSearch.setHint("Search " + titleStringArr[1]);

		eTextSCSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (eTextSCSearch.getText().toString().trim().length() != 0) {
					getFilteredList(eTextSCSearch.getText().toString());
				} else {
					setListData(singleChoiceList);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override
			public void afterTextChanged(Editable s) {}
		});

	}

	public static SingleChoiceDialog getInstance(String from, String _id, WorkLocation workLocation, String _setName, TextView _setText) {
		SingleChoiceDialog fragment;
		fragment = new SingleChoiceDialog();
		fromWhere = from;
		id = _id;
		workLocationObj = workLocation;
		setName = _setName;
		setText = _setText;
		return fragment;
	}

	// ----------------TASK TO GET MY Country/state/region/city LIST --------------------------------
	private class GetDataFromServerAsync extends AsyncTask<String, Void, NetworkResponse> {
		
		@Override
		protected void onPreExecute() {
			pBarSingleChoice.setVisibility(View.VISIBLE);			
		}

		@Override
		protected NetworkResponse doInBackground(String... params) {
			NetworkResponse response = null;
			if (Util.isDeviceOnline(getActivity())) {
				Cmd cmd = null;
				if (fromWhere.equals(Constants.setState)) {
					key_id = WorkLocation.FLD_STATE_ID;
					key_name = WorkLocation.FLD_STATE_NAME;
					cmd = CmdFactory.getStateCmd("0", id);
					response = NetworkMgr.httpPost(Config.API_URL, cmd.getJsonData());
					
				} else if (fromWhere.equals(Constants.setRegion)) {
					key_id = WorkLocation.FLD_REGION_ID;
					key_name = WorkLocation.FLD_REGION_NAME;
					cmd = CmdFactory.getRegionCmd("0", id);
					response = NetworkMgr.httpPost(Config.API_URL, cmd.getJsonData());

				} else if (fromWhere.equals(Constants.setCity)) {
					key_id = WorkLocation.FLD_CITY_ID;
					key_name = WorkLocation.FLD_CITY_NAME;
					cmd = CmdFactory.getCityCmd("0", id);
					response = NetworkMgr.httpPost(Config.API_URL, cmd.getJsonData());
					
				} else {
					if (getActivity() instanceof SignUpActivity) {
						cmd = CmdFactory.createGetCountryCmd("0");
						response = NetworkMgr.httpPost(Config.API_URL, cmd.getJsonData());
						if (response != null) {
							if (response.isSuccess()) {
								parseCountryList(response.getJsonObject());
							}
						}
						return response;
						
					} else {
						for (int i = 0; i < AppGlobals.countries.size(); i++) {
							SingleChoice singleChoice = new SingleChoice();
							singleChoice.name = AppGlobals.countries.get(i).country_name;
							singleChoice.id = AppGlobals.countries.get(i).country_code;
							singleChoice.isSelected = false;
							singleChoiceList.add(singleChoice);
						}
					}
				}

				if (response != null && response.isSuccess()) {
					JSONArray jarray;
					JSONObject jobje = response.getJsonObject();
					try {
						jarray = jobje.getJSONArray("data");
						for (int j = 0; j < jarray.length(); j++) {
							JSONObject job = jarray.getJSONObject(j);
							SingleChoice singleChoice = new SingleChoice();
							singleChoice.name = job.getString(key_name);
							singleChoice.id = job.getString(key_id);
							singleChoice.isSelected = false;
							singleChoiceList.add(singleChoice);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			return response;
		}

		@Override
		protected void onPostExecute(NetworkResponse networkResponse) {
			pBarSingleChoice.setVisibility(View.GONE);
			if (networkResponse != null) {
				if (!networkResponse.isSuccess()) {
					Util.showCenteredToast(getActivity(), networkResponse.getErrMsg());
				}
			}
			setListData(singleChoiceList);
		}
	}

	private void setListData(ArrayList<SingleChoice> data) {
		if (this.isVisible()) {
			if (data != null) {
				singleChoiceAdapter = new SingleChoiceAdapter(getActivity(), R.layout.ed_single_choice_row, data, singleChoiceClick, setName);
				singleChoiceLV.setAdapter(singleChoiceAdapter);
			}
		}
	}

	@Override
	public void onClick(View v) {
		int clickedId = v.getId();
		if (clickedId == R.id.imgBtnSCClearSearch) {
			eTextSCSearch.setText("");
			setListData(singleChoiceList);
		}
		if (clickedId == R.id.txtHLeft) {
			dismiss();
		}
	}

	private void getFilteredList(String s) {
		filteredList = new ArrayList<SingleChoice>();
		for (int k = 0; k < singleChoiceList.size(); k++) {
			if (singleChoiceList.get(k).name.toLowerCase().startsWith(s.toLowerCase())) {
				filteredList.add(singleChoiceList.get(k));
			}
		}
		setListData(filteredList);
	}

	private void initializeSettingHeaderView(Activity act, TextView txtHLeft, TextView txtHTitle, TextView txtHRight1, TextView txtHRight2, String headerTitle, String setTxt2, Drawable imgHLeft, int setTitleGravity) {
		txtHLeft = (TextView) view.findViewById(R.id.txtHLeft);
		txtHTitle = (TextView) view.findViewById(R.id.txtHTitle);
		txtHRight1 = (TextView) view.findViewById(R.id.txtHRight1);
		txtHRight2 = (TextView) view.findViewById(R.id.txtHRight2);
		txtHTitle.setText(headerTitle);
		txtHLeft.setOnClickListener((OnClickListener) act);
		txtHRight1.setVisibility(View.GONE);
		txtHRight2.setVisibility(View.GONE);

		if (setTxt2.trim().length() != 0) {
			txtHRight2.setText(setTxt2);
			txtHRight2.setOnClickListener((OnClickListener) act);
			txtHRight2.setVisibility(View.VISIBLE);
		}

		if (imgHLeft == null) {
			imgHLeft = act.getResources().getDrawable(R.drawable.ic_back);
		}

		txtHLeft.setCompoundDrawablesWithIntrinsicBounds(imgHLeft, null, null, null);
		txtHTitle.setGravity(setTitleGravity);
	}
	
	
	private void parseCountryList(JSONObject jsonObject) {
		try {
			if (jsonObject != null) {
				JSONArray countries = jsonObject.getJSONArray(Constants.FLD_DATA);
				if (countries.length() > 0) {
					for (int i = 0; i < countries.length(); i++) {
						SingleChoice singleChoice = new SingleChoice();
						singleChoice.name = countries.getJSONObject(i).getString(Country.FLD_COUNTRY_NAME);
						singleChoice.id = countries.getJSONObject(i).getString(Country.FLD_COUNTRY_CODE);
						singleChoice.isSelected = false;
						singleChoiceList.add(singleChoice);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
