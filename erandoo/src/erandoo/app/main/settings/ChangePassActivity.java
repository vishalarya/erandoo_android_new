package erandoo.app.main.settings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import erandoo.app.R;
import erandoo.app.cmds.Cmd;
import erandoo.app.cmds.CmdFactory;
import erandoo.app.config.Config;
import erandoo.app.custom.AppHeaderView;
import erandoo.app.custom.BaseFragActivity;
import erandoo.app.network.NetworkMgr;
import erandoo.app.network.NetworkResponse;
import erandoo.app.utilities.Util;

public class ChangePassActivity extends BaseFragActivity implements
		OnClickListener {
	ChangePasswordAsyncTask changePassAsync;
	String eTextOldPassValue;
	String eTextpassValue;

	String eTextrepeatePassValue;

	EditText eTextOldPass;
	EditText eTextpass;
	EditText eTextrepeatePass;

	private AppHeaderView appHeaderView;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);

		setContentView(R.layout.ed_setting_email_pass);
		inIt();

		appHeaderView.setHeaderContent(
				getResources().getDrawable(R.drawable.ic_back), getResources()
						.getString(R.string.change_pass), null, getResources()
						.getString(R.string.save), Gravity.CENTER);

	}

	private void inIt() {
		// TODO Auto-generated method stub
		appHeaderView = (AppHeaderView) findViewById(R.id.appHeaderView);
		eTextOldPass = (EditText) findViewById(R.id.eTextOldPass);
		eTextpass = (EditText) findViewById(R.id.eTextpass);
		eTextrepeatePass = (EditText) findViewById(R.id.eTextrepeatePass);

	}

	private void getDataFromFieldsChangePass() {

		eTextOldPassValue = eTextOldPass.getText().toString();
		eTextpassValue = eTextpass.getText().toString();
		eTextrepeatePassValue = eTextrepeatePass.getText().toString();
//		if (eTextOldPassValue.trim().length() <= 3) {
//			Util.showCenteredToast(ChangePassActivity.this, getResources()
//					.getString(R.string.msg_password_must_error_msg));
//		} else if (eTextpassValue.trim().length() <= 3) {
//			Util.showCenteredToast(ChangePassActivity.this, getResources()
//					.getString(R.string.msg_password_must_error_msg));
//
//		}

		
	if (checkPasswordValidatation(ChangePassActivity.this,eTextOldPassValue, eTextpassValue, eTextrepeatePassValue)) {
			if (Util.isDeviceOnline(ChangePassActivity.this)) {
				changePassAsync = new ChangePasswordAsyncTask();
				if (changePassAsync.getStatus() == AsyncTask.Status.PENDING) {
					changePassAsync.execute();
				} 
			}
			else {
				Util.showCenteredToast(ChangePassActivity.this,
						getResources().getString(R.string.msg_Connect_internet));
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int clickedItemId = v.getId();
		if (clickedItemId == appHeaderView.txtHLeft.getId()) {
			finishActivity();
		}
		else if (clickedItemId == appHeaderView.txtHRight2.getId()) {
			getDataFromFieldsChangePass();
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finishActivity();
	}

	private void finishActivity() {
		ChangePassActivity.this.finish();
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}

	// ----------------TASK TO CHANGE PASSWORD --------------------------------
	private class ChangePasswordAsyncTask extends
			AsyncTask<Void, Void, NetworkResponse> {
		@Override
		protected void onPreExecute() {
			Util.showProDialog(ChangePassActivity.this);
		}

		@Override
		protected NetworkResponse doInBackground(Void... params) {
			Cmd cmd = CmdFactory.createChangePasswordCmd("0", eTextOldPassValue,
					eTextrepeatePassValue);
			NetworkResponse response = NetworkMgr.httpPost(Config.API_URL,
					cmd.getJsonData());
//			if (response != null) {
//				if (response.isSuccess()) {
//
//				}
//			}

			return response;
		}

		@Override
		protected void onPostExecute(NetworkResponse networkResponse) {
			Util.dimissProDialog();
			if (networkResponse != null) {
				if (!networkResponse.isSuccess()) {
					Util.showCenteredToast(ChangePassActivity.this,
							getResources().getString(R.string.msg_old_pass_is));
				} else {
					Util.showCenteredToast(ChangePassActivity.this,
							getResources().getString(R.string.msg_pass_changed));
					onBackPressed();

				}
			}
		}
	}
	
	public boolean checkPasswordValidatation(Context ctx,String oldPass,String password ,String confirmPassword)
	{
		boolean passwordIsValid=false;
		String specialChars = "[^A-Za-z0-9]";
		Pattern pattern = Pattern.compile(specialChars);
		Matcher matcher = pattern.matcher(password);
		if(oldPass.length() < 6){
			Util.showCenteredToast(ctx, getResources().getString(R.string.msg_old_password_must_six_character));
		}
		else if(password.length() < 6){
			Util.showCenteredToast(ctx, getResources().getString(R.string.msg_password_must_six_character));
		}
		else if(!password.matches(".*\\d.*") || !matcher.find()){
			Util.showCenteredToast(ctx, getResources().getString(R.string.msg_password_must_contain_numeric_special_character));
		}else if (!password.trim().equals(confirmPassword.trim())) {
			Util.showCenteredToast(ctx, getResources().getString(R.string.msg_password_does_not_match));
		}
		else
		{
			passwordIsValid=true;
		}
		return passwordIsValid;
	}
}
