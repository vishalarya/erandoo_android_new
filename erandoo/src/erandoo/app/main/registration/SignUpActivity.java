package erandoo.app.main.registration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.cmds.Cmd;
import erandoo.app.cmds.CmdFactory;
import erandoo.app.config.Config;
import erandoo.app.custom.AppHeaderView;
import erandoo.app.custom.BaseFragActivity;
import erandoo.app.custom.SingleChoiceDialog;
import erandoo.app.database.WorkLocation;
import erandoo.app.network.NetworkMgr;
import erandoo.app.network.NetworkResponse;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.Util;

public class SignUpActivity extends BaseFragActivity implements OnClickListener {
	
	private EditText eTextJoinEmail;
	private EditText eTextJoinPass;
	private EditText eTextJoinCPassword;
	private EditText eTextJoinFName;
	private EditText eTextJoinLName;
	private EditText eTextStreetAddress;
	private EditText eTextJoinZipCode;
	private ImageView imvJoinSignUpFB;
	private ImageView imvJoinSignUpLI;
	
	private TextView txtJoinConfirm;
	private TextView txtJoinCountry;
	private TextView txtJoinState;
	private TextView txtJoinRegion;
	private TextView txtJoinCity;
	
	private WorkLocation workLocation;
	private SingleChoiceDialog singleChoiceDialog;
	private AppHeaderView appHeaderView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ed_reg_join);
		
		initialize();
	}
	
	private void initialize() {
		appHeaderView = new AppHeaderView(this);
		workLocation = new WorkLocation();
		
		eTextJoinEmail = (EditText) findViewById(R.id.eTextJoinEmail);
		eTextJoinPass = (EditText) findViewById(R.id.eTextJoinPass);
		eTextJoinCPassword = (EditText) findViewById(R.id.eTextJoinCPassword);
		eTextJoinFName = (EditText) findViewById(R.id.eTextJoinFName);
		eTextJoinLName = (EditText) findViewById(R.id.eTextJoinLName);
		txtJoinConfirm = (TextView) findViewById(R.id.txtJoinConfirm);
		imvJoinSignUpFB = (ImageView) findViewById(R.id.imvJoinSignUpFB);
		imvJoinSignUpLI = (ImageView) findViewById(R.id.imvJoinSignUpLI);
		eTextStreetAddress = (EditText) findViewById(R.id.eTextStreetAddress);
		eTextJoinZipCode = (EditText) findViewById(R.id.eTextJoinZipCode);
		txtJoinCountry = (TextView) findViewById(R.id.txtJoinCountry);
		txtJoinState = (TextView) findViewById(R.id.txtJoinState);
		txtJoinRegion = (TextView) findViewById(R.id.txtJoinRegion);
		txtJoinCity = (TextView) findViewById(R.id.txtJoinCity);
		
		txtJoinConfirm.setOnClickListener(this);
		imvJoinSignUpFB.setOnClickListener(this);
		imvJoinSignUpLI.setOnClickListener(this);
		txtJoinCountry.setOnClickListener(this);
		txtJoinState.setOnClickListener(this);
		txtJoinRegion.setOnClickListener(this);
		txtJoinCity.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.txtJoinConfirm) {
			txtJoinConfirm();
		} else if (v.getId() == R.id.txtJoinCountry) {
			txtJoinCountry();
		} else if (v.getId() == R.id.txtJoinState) {
			txtJoinState();
		} else if (v.getId() == R.id.txtJoinRegion) {
			txtJoinRegion();
		} else if (v.getId() == R.id.txtJoinCity) {
			txtJoinCity();
		} else if (v.getId() == R.id.imvJoinSignUpFB) {
			//TOOD..
		} else if (v.getId() == R.id.imvJoinSignUpLI) {
			//TOOD..
		} else if (v.getId() == appHeaderView.txtHLeft.getId()) {
			singleChoiceDialog.dismiss();
		}
	}
	
	private void txtJoinConfirm(){
		if (isValidate()){
			getUserSignUp();
		}
	}
	
	private void txtJoinCountry(){
		if (Util.isDeviceOnline(SignUpActivity.this)) {
			clickOnItmeIs(Constants.setContry, "", txtJoinCountry.getText().toString(), txtJoinCountry);
		}else{
			Util.showCenteredToast(SignUpActivity.this, getResources().getString(R.string.msg_Connect_internet));
		}
	}
	private void txtJoinState(){
		if (workLocation.country != null) {
			if (Util.isDeviceOnline(SignUpActivity.this)) {
				clickOnItmeIs(Constants.setState, workLocation.country.country_code, txtJoinState.getText().toString(), txtJoinState);
			}else{
				Util.showCenteredToast(SignUpActivity.this, getResources().getString(R.string.msg_Connect_internet));
			}
		} else {
			Util.showCenteredToast(SignUpActivity.this, getResources().getString(R.string.msg_country_field_is_emty));
		}
	}

	private void txtJoinRegion(){
		if (workLocation.state != null) {
			if (Util.isDeviceOnline(SignUpActivity.this)) {
				clickOnItmeIs(Constants.setRegion, workLocation.state.state_id, txtJoinRegion.getText().toString(), txtJoinRegion);
			}else{
				Util.showCenteredToast(SignUpActivity.this, getResources().getString(R.string.msg_Connect_internet));
			}
		} else {
			Util.showCenteredToast(SignUpActivity.this, getResources().getString(R.string.msg_state_field_is_emty));
		}
	}
	
	private void txtJoinCity(){
		if (workLocation.region != null) {
			if (Util.isDeviceOnline(SignUpActivity.this)) {
				clickOnItmeIs(Constants.setCity, workLocation.region.region_id, txtJoinCity.getText().toString(), txtJoinCity);
			}else{
				Util.showCenteredToast(SignUpActivity.this, getResources().getString(R.string.msg_Connect_internet));
			}
		} else {
			Util.showCenteredToast(SignUpActivity.this, getResources().getString(R.string.msg_region_field_is_emty));
		}
	}
	
	private void getUserSignUp() {
		if (Util.isDeviceOnline(SignUpActivity.this)) {
			new SignUpTask().execute();
		} else {
			Util.showCenteredToast(this, getResources().getString(R.string.msg_Connect_internet));
		}
	}
	
	
	private class SignUpTask extends AsyncTask<Void, Void, NetworkResponse> {
		
		@Override
		protected void onPreExecute() {
			Util.showProDialog(SignUpActivity.this);
		}

		@Override
		protected NetworkResponse doInBackground(Void... params) {
			Cmd cmd = CmdFactory.createSignUpCmd();
			cmd.addData(Constants.FLD_CONTACT_ID, eTextJoinEmail.getText().toString().trim());
			cmd.addData(Constants.FLD_PASSWORD, eTextJoinPass.getText().toString().trim());
			cmd.addData(Constants.FLD_CONF_PASS, eTextJoinCPassword.getText().toString().trim());
			cmd.addData(Constants.FLD_FIRST_NAME, eTextJoinFName.getText().toString().trim());
			cmd.addData(Constants.FLD_LAST_NAME, eTextJoinLName.getText().toString().trim());
			cmd.addData(Constants.FLD_ADDRESS, eTextStreetAddress.getText().toString().trim());
			cmd.addData(Constants.FLD_COUNTRY, workLocation.country.country_code);
			cmd.addData(Constants.FLD_STATE, workLocation.state.state_id);
			cmd.addData(Constants.FLD_REGION, workLocation.region.region_id);
			cmd.addData(Constants.FLD_CITY, workLocation.city.city_id);
			cmd.addData(Constants.FLD_ZIPCODE, eTextJoinZipCode.getText().toString().trim());
			cmd.addData(Constants.FLD_CLOUD_KEY, "0");
			NetworkResponse response = NetworkMgr.httpPost(Config.API_URL,cmd.getJsonData());
			if (response != null) {
				if (response.isSuccess()) {
					getData(response);
				}
			}
			return response;
		}

		@Override
		protected void onPostExecute(NetworkResponse networkResponse) {
			Util.dimissProDialog();
			if (networkResponse != null) {
				if (networkResponse.isSuccess()) {
					startActvitiy(networkResponse);
				} else {
					Util.showCenteredToast(SignUpActivity.this,networkResponse.getErrMsg());
				}
			}
		}
	}

	private void getData(NetworkResponse response) {
		try {
			JSONObject jsonObject = response.getJsonObject().getJSONObject(Constants.FLD_DATA);
			Constants.V_CODE = response.getJsonObject().getString("tag");
			Constants.REG_ID = jsonObject.getString(Constants.FLD_REG_ID);
			Constants.CONTACT_ID = jsonObject.getString(Constants.FLD_CONTACT_ID);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void startActvitiy(NetworkResponse response){
		finish();
		Intent intent = new Intent(SignUpActivity.this, RegVerificationActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.right_in, R.anim.left_out);
	}
	
	public void clickOnItmeIs(String fromWhere, String _id, String _setName, TextView setText) {
		singleChoiceDialog = SingleChoiceDialog.getInstance(fromWhere, _id, workLocation, _setName, setText);
		singleChoiceDialog.show(getSupportFragmentManager(), "SingleChoiceDialog");
	}

	public boolean isValidate() {
		String password = eTextJoinPass.getText().toString().trim();
		String specialChars = "[^A-Za-z0-9]";
		Pattern pattern = Pattern.compile(specialChars);
		Matcher matcher = pattern.matcher(password);
		if (eTextJoinEmail.getText().toString().trim().length() == 0) {
			Util.showCenteredToast(SignUpActivity.this, getResources().getString(R.string.msg_email_address_cannot_blank));
		} else if (!Util.isEmailValid(eTextJoinEmail.getText().toString().trim())) {
			Util.showCenteredToast(SignUpActivity.this, getResources().getString(R.string.msg_email_address_invalid));
		} else if(password.length() == 0){
			Util.showCenteredToast(SignUpActivity.this, getResources().getString(R.string.msg_password_cannot_blank));
		} else if(password.length() < 6){
			Util.showCenteredToast(SignUpActivity.this, getResources().getString(R.string.msg_password_must_six_character));
		} else if(!password.matches(".*\\d.*") || !matcher.find()){
			Util.showCenteredToast(SignUpActivity.this, getResources().getString(R.string.msg_password_must_contain_numeric_special_character));
		} else if(eTextJoinCPassword.getText().toString().trim().length() == 0){
			Util.showCenteredToast(SignUpActivity.this, getResources().getString(R.string.msg_confirm_password_cannot_blank));
		} else if (!eTextJoinPass.getText().toString().trim().equals(eTextJoinCPassword.getText().toString().trim())) {
			Util.showCenteredToast(SignUpActivity.this, getResources().getString(R.string.msg_password_does_not_match));
		} else if (eTextJoinFName.getText().toString().trim().length() == 0) {
			Util.showCenteredToast(SignUpActivity.this, getResources().getString(R.string.msg_firstname_cannot_blank));
		} else if (eTextStreetAddress.getText().toString().trim().length() == 0){
			Util.showCenteredToast(SignUpActivity.this, getResources().getString(R.string.msg_address_cannot_blank));
		} else if (workLocation.country == null) {
			Util.showCenteredToast(SignUpActivity.this, getResources().getString(R.string.msg_country_field_is_emty));
		} else if (workLocation.state == null) {
			Util.showCenteredToast(SignUpActivity.this, getResources().getString(R.string.msg_state_field_is_emty));
		} else if (workLocation.region == null) {
			Util.showCenteredToast(SignUpActivity.this, getResources().getString(R.string.msg_region_field_is_emty));
		} else if (workLocation.city == null) {
			Util.showCenteredToast(SignUpActivity.this, getResources().getString(R.string.msg_city_field_is_emty));
		} else if (eTextJoinZipCode.getText().toString().trim().length() == 0) {
			Util.showCenteredToast(SignUpActivity.this, getResources().getString(R.string.msg_zipcode_cannot_blank));
		} else{
			return true;
		}
		return false;
	}
	
	@Override
	public void onBackPressed() {
		finishActivity();
	}

	private void finishActivity() {
		SignUpActivity.this.finish();
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}
}
