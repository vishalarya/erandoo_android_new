package erandoo.app.main.registration;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.cmds.Cmd;
import erandoo.app.cmds.CmdFactory;
import erandoo.app.config.Config;
import erandoo.app.custom.BaseFragActivity;
import erandoo.app.network.NetworkMgr;
import erandoo.app.network.NetworkResponse;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.Util;

public class RegVerificationActivity extends BaseFragActivity implements OnClickListener {
	
	private EditText eTextVCode;
	private TextView txtResendVCode;
	private TextView txtVerify;
	
	private String VERIFY = "verfiy";
	private String RESENT_VERIFICATION_CODE = "resend_verification";
	
	private String url;
	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ed_reg_verification);
		
		initialize();
	}
	
	private void initialize() {
		eTextVCode = (EditText) findViewById(R.id.eTextVCode);
		txtResendVCode = (TextView) findViewById(R.id.txtResendVCode);
		txtVerify = (TextView) findViewById(R.id.txtVerify);
		
		txtResendVCode.setOnClickListener(this);
		txtVerify.setOnClickListener(this);
		
		Util.showCenteredToast(getApplicationContext(), "The code is " + Constants.V_CODE);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.txtResendVCode) {
			callAsynTask(RESENT_VERIFICATION_CODE);
		} else if(v.getId() == R.id.txtVerify) {
			verifyUser();
		} else if(v.getId() == R.id.txtDialogYes) {
			dialog.dismiss();
			finish();
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
		}
	}
	
	private void verifyUser(){
		if(eTextVCode.getText().toString().trim().length() > 0) {
			callAsynTask(VERIFY);
		} else {
			Util.showCenteredToast(RegVerificationActivity.this, getResources().getString(R.string.blank_verification_code));
		}
	}
	
	private void callAsynTask(String operation) {
		if (Util.isDeviceOnline(RegVerificationActivity.this)) {
			new VerificationTask().execute(new String[]{operation});
		} else {
			Util.showCenteredToast(this, getResources().getString(R.string.msg_Connect_internet));
		}
	}

	private class VerificationTask extends AsyncTask<String, Void, String> {
		
		NetworkResponse response;
		
		@Override
		protected void onPreExecute() {
			Util.showProDialog(RegVerificationActivity.this);
		}

		@Override
		protected String doInBackground(String... params) {
			Cmd cmd = null;
			String operation = params[0];
			if(operation.equals(RESENT_VERIFICATION_CODE)) {
				cmd = CmdFactory.createResendVerifCodeCmd();
				cmd.addData(Constants.FLD_REG_ID, Constants.REG_ID);
				cmd.addData(Constants.FLD_CONTACT_ID, Constants.CONTACT_ID); 
			} else if (operation.equals(VERIFY)) {
				cmd = CmdFactory.createRegVerificationCmd();
				cmd.addData(Constants.FLD_REG_VERIF_CODE, eTextVCode.getText().toString().trim());
				cmd.addData(Constants.FLD_REG_ID, Constants.REG_ID);
				cmd.addData(Constants.FLD_CONTACT_ID, Constants.CONTACT_ID);
			}
			response = NetworkMgr.httpPost(Config.API_URL, cmd.getJsonData());
			if (response != null) {
				if (response.isSuccess()) {
					if (operation.equals(VERIFY)) {
						getNewUserID(response,operation);
					}
				}
			}
			return operation;
		}

		@Override
		protected void onPostExecute(String operation) {
			Util.dimissProDialog();
			if (response != null) {
				if (response.isSuccess()) {
					if (operation.equals(RESENT_VERIFICATION_CODE)) {
						Util.showCenteredToast(RegVerificationActivity.this, getResources().getString(R.string.resend_verification_success_msg));
					}else{
						if (url != null) {
							dialog = Util.ShowYesNoDialog(RegVerificationActivity.this, "To continue registration press OK!", getResources().getString(R.string.ok), getResources().getString(R.string.Cancel), "Confirm!!!");
						}
					}
				} else {
					Util.showCenteredToast(RegVerificationActivity.this, response.getErrMsg());
				}
			}
		}
	}
	
	private void getNewUserID(NetworkResponse networkResponse,String operation){
		if(operation.equals(VERIFY)) {
			try {
				JSONObject jsonObject = networkResponse.getJsonObject().getJSONObject(Constants.FLD_DATA);
				url = jsonObject.getString("weburl");
				Constants.NEW_USER_ID = jsonObject.getString(Constants.FLD_USER_ID);
			}catch(JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onBackPressed() {
		finishActivity();
	}

	private void finishActivity() {
		RegVerificationActivity.this.finish();
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}
}
