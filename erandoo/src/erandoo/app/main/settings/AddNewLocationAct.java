package erandoo.app.main.settings;

import android.app.Dialog;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.cmds.Cmd;
import erandoo.app.cmds.CmdFactory;
import erandoo.app.config.Config;
import erandoo.app.custom.AppHeaderView;
import erandoo.app.custom.BaseFragActivity;
import erandoo.app.custom.SingleChoiceDialog;
import erandoo.app.database.DatabaseMgr;
import erandoo.app.database.TableType;
import erandoo.app.database.WorkLocation;
import erandoo.app.network.NetworkMgr;
import erandoo.app.network.NetworkResponse;
import erandoo.app.tempclasses.WorkLocationData;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.Util;

public class AddNewLocationAct extends BaseFragActivity implements
		OnClickListener {

	private EditText locAddNew_eTextAddress;
	private EditText locAddNew_eTextZipCode;
	private TextView txtSelCity;
	private TextView txtSelectReg;
	private TextView txtSelState;
	private TextView txtSelCountry;
	private SingleChoiceDialog singleChoiceDialog;
	private WorkLocation workLocation;
	private DatabaseMgr databaseMgr;
	private AppHeaderView appHeaderView;
	private String textForRight2;
	private String headerTitle;
	private CheckBox chkBxIsBilling;
	private CheckBox chkBxIsShipping;
	private CheckBox chkBxIsDefault;
	private String isBillingValue = "0";
	private String isShippingValue = "0";
	private String operationNameValue;
    private Dialog yesNoDialog;
	public static final String FLD_OPERATION_delete = "delete";
	public static final String FLD_OPERATION_is_shipping = "is_shipping";
	public static final String FLD_OPERATION_is_billing = "is_billing";
	public static final String FLD_OPERATION_is_default = "is_default";

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		setContentView(R.layout.ed_setting_add_new_location);
		inIt();

		chkBxIsDefault.setVisibility(View.GONE);
		 chkBxIsBilling.setVisibility(View.GONE);
		 chkBxIsShipping.setVisibility(View.GONE);
	}

	private void inIt() {
		Bundle b = getIntent().getExtras();
		appHeaderView = (AppHeaderView) findViewById(R.id.appHeaderView);
		databaseMgr = DatabaseMgr.getInstance(this);
		locAddNew_eTextAddress = (EditText) findViewById(R.id.locAddNew_eTextAddress);
		locAddNew_eTextZipCode = (EditText) findViewById(R.id.locAddNew_eTextZipCode);
		txtSelCity = (TextView) findViewById(R.id.txtSelCity);
		txtSelectReg = (TextView) findViewById(R.id.txtSelectReg);
		txtSelState = (TextView) findViewById(R.id.txtSelState);
		txtSelCountry = (TextView) findViewById(R.id.txtSelCountry);
		chkBxIsBilling = (CheckBox) findViewById(R.id.chkBxIsBilling);
		chkBxIsShipping = (CheckBox) findViewById(R.id.chkBxIsShipping);
		chkBxIsDefault = (CheckBox) findViewById(R.id.chkBxIsDefault);

		if (b != null && b.containsKey("location")) {
			workLocation = (WorkLocation) getIntent().getSerializableExtra(
					"location");
			setDataToFields();

			chkBxIsDefault
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							if (isChecked) {
								operationNameValue = FLD_OPERATION_is_default;
								updateStatus();

							} else {
								chkBxIsDefault.setChecked(true);
							}

						}
					});
			chkBxIsBilling
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							if (isChecked) {
								operationNameValue = FLD_OPERATION_is_billing;
								updateStatus();
							} else {
								chkBxIsBilling.setChecked(true);
							}
						}
					});
			chkBxIsShipping
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							if (isChecked) {
								operationNameValue = FLD_OPERATION_is_shipping;
								updateStatus();
							} else {
								chkBxIsShipping.setChecked(true);
							}
						}
					});
		} else {
			txtSelCity.setOnClickListener(this);
			txtSelectReg.setOnClickListener(this);
			txtSelState.setOnClickListener(this);
			txtSelCountry.setOnClickListener(this);
			workLocation = new WorkLocation();
			textForRight2 = getResources().getString(R.string.save);
			headerTitle = getResources().getString(R.string.Add_New_Location);
			setRightImage(txtSelCity);
			setRightImage(txtSelectReg);
			setRightImage(txtSelState);
			setRightImage(txtSelCountry);
		}

		appHeaderView.setHeaderContent(
				getResources().getDrawable(R.drawable.ic_back), headerTitle,
				null, textForRight2, Gravity.CENTER);

	}

	private void updateStatus() {
		DeleteLocationAsync deleteNewLocationAsync = new DeleteLocationAsync();
		if (deleteNewLocationAsync.getStatus() == AsyncTask.Status.PENDING) {
			deleteNewLocationAsync.execute();
		}
	}

	private void setDataToFields() {

		headerTitle = getResources().getString(R.string.Location);
		locAddNew_eTextAddress.setText(workLocation.address);
		locAddNew_eTextZipCode.setText(workLocation.zipcode);
		txtSelCity.setText(workLocation.city.city_name);
		txtSelectReg.setText(workLocation.region.region_name);
		txtSelState.setText(workLocation.state.state_name);
		txtSelCountry.setText(workLocation.country.country_name);

		if (workLocation.is_billing.equals("1")) {
			chkBxIsBilling.setChecked(true);
		}
		if (workLocation.is_shipping.equals("1")) {
			chkBxIsShipping.setChecked(true);

		}
		if (workLocation.is_default_location.equals("1")) {
			chkBxIsDefault.setChecked(true);
		} else {
			textForRight2 = getResources().getString(R.string.Delete);
		}

//		chkBxIsDefault.setVisibility(View.VISIBLE);
		
		locAddNew_eTextZipCode.setEnabled(false);
		locAddNew_eTextAddress.setEnabled(false);

	}

	private void setRightImage(TextView txtView) {
		txtView.setCompoundDrawablesWithIntrinsicBounds(0, 0,
				R.drawable.ic_arrow, 0);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int clickedItemId = v.getId();
		if (clickedItemId == appHeaderView.txtHLeft.getId()) {
			onBackPressed();
		}

		else if (clickedItemId == appHeaderView.txtHRight2.getId()) {

			if(appHeaderView.txtHRight2.getText().toString().equals(getResources().getString(R.string.Delete)))
			{
				yesNoDialog=Util.ShowYesNoDialog(AddNewLocationAct.this, getResources().getString(R.string.msg_Do_you_want_del),getResources().getString(R.string.yes),getResources().getString(R.string.no),getResources().getString(R.string.app_name));
				
			}
			else
			{
				onSaveClick();
			}
			
		} else if (clickedItemId == R.id.txtSelCountry) {
			clickOnItmeIs(Constants.setContry, "", txtSelCountry.getText()
					.toString(), txtSelCountry);
		}

		else if (clickedItemId == R.id.txtSelState) {
			if (workLocation.country != null) {

				clickOnItmeIs(Constants.setState,
						workLocation.country.country_code, txtSelState
								.getText().toString(), txtSelState);
			} else {
				Util.showCenteredToast(AddNewLocationAct.this, getResources()
						.getString(R.string.msg_country_field_is_emty));
			}

		}

		else if (clickedItemId == R.id.txtSelectReg) {
			if (workLocation.state != null) {
				clickOnItmeIs(Constants.setRegion, workLocation.state.state_id,
						txtSelectReg.getText().toString(), txtSelectReg);
			}

			else {
				Util.showCenteredToast(AddNewLocationAct.this, getResources()
						.getString(R.string.msg_state_field_is_emty));
			}
		}

		else if (clickedItemId == R.id.txtSelCity) {
			if (workLocation.region != null) {
				clickOnItmeIs(Constants.setCity, workLocation.region.region_id,
						txtSelCity.getText().toString(), txtSelCity);
			}

			else {
				Util.showCenteredToast(AddNewLocationAct.this, getResources()
						.getString(R.string.msg_region_field_is_emty));
			}
		}
		
		else if(clickedItemId==R.id.txtDialogYes)
		{
			yesNoDialog.dismiss();
			onSaveClick();
		}

	}

	private void onSaveClick() {

		if (appHeaderView.txtHRight2.getText().toString()
				.equals(getResources().getString(R.string.Delete))) {
			operationNameValue = FLD_OPERATION_delete;
			DeleteLocationAsync deletAsync = new DeleteLocationAsync();
			if (deletAsync.getStatus() == AsyncTask.Status.PENDING) {
				deletAsync.execute();
			}
		} else {

			if (chkBxIsBilling.isChecked()) {
				isBillingValue = "1";
			}
			if (chkBxIsShipping.isChecked()) {
				isShippingValue = "1";
			}
			workLocation.is_billing = isBillingValue;
			workLocation.is_shipping = isShippingValue;

			workLocation.address = locAddNew_eTextAddress.getText().toString();
			workLocation.zipcode = locAddNew_eTextZipCode.getText().toString();

			fieldInvalidation();
			if (!fieldInvalidation()) {
				AddNewLocationAsync addNewLocationAsync = new AddNewLocationAsync();
				if (addNewLocationAsync.getStatus() == AsyncTask.Status.PENDING) {
					addNewLocationAsync.execute();
				}
			}
		}
	}

	public void clickOnItmeIs(String from, String _id, String _setName,
			TextView setText) {
		singleChoiceDialog = SingleChoiceDialog.getInstance(from, _id,
				workLocation, _setName, setText);
		singleChoiceDialog.show(getSupportFragmentManager(),
				"SingleChoiceDialog");
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		setResult(11);
		finishActivity();
	}

	private void finishActivity() {
		AddNewLocationAct.this.finish();
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}

	public boolean checkDataField() {
		boolean isFilled = true;

		if (workLocation.country.country_code == null) {
			isFilled = false;
		} else if (workLocation.state.state_id == null) {
			isFilled = false;
		} else if (workLocation.region.region_id == null) {
			isFilled = false;
		}

		return isFilled;
	}

	// ----------------TASK TO Add new Location
	// --------------------------------
	private class AddNewLocationAsync extends
			AsyncTask<String, Void, NetworkResponse> {

		@Override
		protected void onPreExecute() {
			Util.showProDialog(AddNewLocationAct.this);
		}

		@Override
		protected NetworkResponse doInBackground(String... params) {
			NetworkResponse response = null;

			if (Util.isDeviceOnline(AddNewLocationAct.this)) {
				Cmd cmd = null;

				cmd = CmdFactory.addNewLocationCmd(workLocation);
				response = NetworkMgr.httpPost(Config.API_URL,
						cmd.getJsonData());
				if (response != null && response.isSuccess()) {
					saveWLocationData(response);
				}

			}

			return response;
		}

		@Override
		protected void onPostExecute(NetworkResponse networkResponse) {
			Util.dimissProDialog();
			if (networkResponse != null) {
				if (!networkResponse.isSuccess()) {
					Util.showCenteredToast(AddNewLocationAct.this,
							networkResponse.getErrMsg());
				}

				else {
					
					onBackPressed();
				}
			}

		}
	}

	// ----------------TASK TO Add new Location
	// --------------------------------
	private class DeleteLocationAsync extends
			AsyncTask<String, Void, NetworkResponse> {

		@Override
		protected void onPreExecute() {
			Util.showProDialog(AddNewLocationAct.this);
		}

		@Override
		protected NetworkResponse doInBackground(String... params) {
			NetworkResponse response = null;

			if (Util.isDeviceOnline(AddNewLocationAct.this)) {
				Cmd cmd = null;
				cmd = CmdFactory.DeleteLocationCmd(workLocation,
						operationNameValue);
				response = NetworkMgr.httpPost(Config.API_URL,
						cmd.getJsonData());
				if (response != null && response.isSuccess()) {
					if (operationNameValue.equals(FLD_OPERATION_delete)) {
						databaseMgr
								.deleteTableRow(
										TableType.WorkLocationTable,
										WorkLocation.FLD_WORK_LOCATION_ID
												+ " =?",
										new String[] { String
												.valueOf(workLocation.work_location_id) });
					} else {
						if (operationNameValue == FLD_OPERATION_is_default) {
							operationNameValue += "_location";
						}
						updateCheckStatus(operationNameValue, "0");
					}
				}

			}
			return response;
		}

		@Override
		protected void onPostExecute(NetworkResponse networkResponse) {
			Util.dimissProDialog();
			if (networkResponse != null) {
				if (!networkResponse.isSuccess()) {
					Util.showCenteredToast(AddNewLocationAct.this,
							networkResponse.getErrMsg());
				} else {
					if (operationNameValue.equals(FLD_OPERATION_delete)) {
						onBackPressed();
					}
					else
					{
						Util.showCenteredToast(AddNewLocationAct.this, getResources().getString(R.string.msg_status));
					}

				}
			}

		}

	}

	private void saveWLocationData(NetworkResponse response) {
		WorkLocationData wLocations = (WorkLocationData) Util
				.getJsonToClassObject(response.getJsonObject().toString(),
						WorkLocationData.class);
		databaseMgr.saveWorkLocationList(wLocations.data);
	}

	public boolean fieldInvalidation() {
		boolean flag = false;

		if (workLocation.address.trim().length() == 0) {
			flag = true;
			Util.showCenteredToast(AddNewLocationAct.this, getResources()
					.getString(R.string.msg_address_field_is_emty));
		} else if (workLocation.country == null) {
			flag = true;
			Util.showCenteredToast(AddNewLocationAct.this, getResources()
					.getString(R.string.msg_country_field_is_emty));
		} else if (workLocation.state == null) {
			flag = true;
			Util.showCenteredToast(AddNewLocationAct.this, getResources()
					.getString(R.string.msg_state_field_is_emty));
		} else if (workLocation.region == null) {
			flag = true;
			Util.showCenteredToast(AddNewLocationAct.this, getResources()
					.getString(R.string.msg_region_field_is_emty));
		} else if (workLocation.city == null) {
			flag = true;
			Util.showCenteredToast(AddNewLocationAct.this, getResources()
					.getString(R.string.msg_city_field_is_emty));
		} else if (workLocation.zipcode.trim().length() == 0) {
			flag = true;
			Util.showCenteredToast(AddNewLocationAct.this, getResources()
					.getString(R.string.msg_zip_field_is_emty));
		}

		return flag;

	}

	private void updateCheckStatus(String keyName,String value)
	{
		ContentValues cValues = new ContentValues();
		cValues.put(keyName, value);
		databaseMgr.updateTable(TableType.WorkLocationTable, cValues, null, null);
		cValues.put(keyName, "1");
		databaseMgr.updateTable(TableType.WorkLocationTable, cValues, WorkLocation.FLD_WORK_LOCATION_ID + "= ?", new String[]{workLocation.work_location_id});
		
	}
}