package erandoo.app.main;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

import erandoo.app.R;
import erandoo.app.cmds.Cmd;
import erandoo.app.cmds.CmdFactory;
import erandoo.app.config.Config;
import erandoo.app.database.DatabaseMgr;
import erandoo.app.network.NetworkMgr;
import erandoo.app.network.NetworkResponse;
import erandoo.app.tempclasses.SignInData;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.Util;

public class SignInActivity extends Activity implements OnClickListener {
	
	private EditText eTextLEmail, eTextLPassword;
	private Button btnSignIn;
	private TextView txtSignUp;
	private TextView txtForgotPass;
	private DatabaseMgr databaseMgr;
	private String userEmailId;
	private ImageView imvFBLogin;
	
	private Dialog dialog;
	private String url;
	
	//Face-book section
	private String FBAppId = "922047894479680";
	private Facebook facebook = new Facebook(FBAppId);
	private String fEmail;
	private String fbAccessToken = "";
	private String loginWith = "e";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Config.init(this); 
		if (Config.getUserId().equals(Constants.INVALID_ID)) {
			setContentView(R.layout.ed_sign_in);
			initialize();
		} else {
			Config.setUserDetails(false);
			Util.startActivity(SignInActivity.this, MainActivity.class);
		}
	}

	private void initialize() {
		databaseMgr = DatabaseMgr.getInstance(this);
		eTextLEmail = (EditText) findViewById(R.id.eTextLEmail);
		eTextLPassword = (EditText) findViewById(R.id.eTextLPassword);
		txtSignUp = (TextView) findViewById(R.id.txtSignUp);
		txtForgotPass = (TextView) findViewById(R.id.txtForgotPass);
		btnSignIn = (Button) findViewById(R.id.btnSignIn);
		imvFBLogin = (ImageView) findViewById(R.id.imvFBLogin);
		
		btnSignIn.setOnClickListener(this);
		txtSignUp.setOnClickListener(this);
		txtForgotPass.setOnClickListener(this);
		imvFBLogin.setOnClickListener(this);
		
		userEmailId = Config.getUserEmailId();
		if(userEmailId != null){
			eTextLEmail.setText(userEmailId);
		}
	}

	@Override 
	public void onClick(View v) {
		if (v.getId() == R.id.btnSignIn) {
			getUserSignIn();
		} else if (v.getId() == R.id.txtSignUp) {
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse("http://54.84.193.236/register/index"));
			startActivity(i);
		} else if (v.getId() == R.id.txtDialogYes) {
			dialog.dismiss();
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
		} else if (v.getId() == R.id.txtForgotPass) {
			forgotPass();
		} else if (v.getId() == R.id.imvFBLogin) {
			FBLogin();
		}
	}
	
	private void forgotPass(){
		/*Intent in = new Intent(SignInActivity.this,ForgotPasswordActivity.class);
		startActivity(in);
		overridePendingTransition(R.anim.right_in, R.anim.left_out);*/
	}

	private void getUserSignIn() {
		if (!eTextValidation()) {
			if (Util.isDeviceOnline(this)) {
				new SignInTask().execute();
			} else {
				Util.showCenteredToast(this,
						getResources().getString(R.string.msg_Connect_internet));
			}
		}
	}

	private boolean eTextValidation() {
		boolean isEmpty = false;
		if ((eTextLEmail.getText().toString().trim().length() < 1) || (eTextLPassword.getText().toString().trim().length() < 1)) {
			isEmpty = true;
			Util.showCenteredToast(SignInActivity.this,getResources().getString(R.string.msg_Username_Password_cannot_be_blank)); 
		} else if (!Util.isEmailValid(eTextLEmail.getText().toString().trim())) {
			isEmpty = true;
			Util.showCenteredToast(SignInActivity.this, getResources().getString(R.string.msg_Enter_a_valid_Email)); 
		}
		return isEmpty;
	}

	private class SignInTask extends AsyncTask<Void, Void, NetworkResponse> {
		String uEmailId;
		String password;
		
		@Override
		protected void onPreExecute() {
			Util.showProDialog(SignInActivity.this);
		}

		@Override
		protected NetworkResponse doInBackground(Void... params) {
			if (loginWith.equals("e")) {
				uEmailId = eTextLEmail.getText().toString().trim();
				password = eTextLPassword.getText().toString().trim();
	            if(isUserChanged(uEmailId)){
	            	databaseMgr.clearUserData();
	            }
			} else if (loginWith.equals("f")) {
				uEmailId = fEmail;
				password = "";
			}
			
			Cmd cmd = CmdFactory.createSignInCmd(uEmailId, password ,loginWith);
			NetworkResponse response = NetworkMgr.httpPost(Config.API_URL,cmd.getJsonData());
			if (response != null) {
				if (response.isSuccess()) {
					setUserDetails(response);
				}
			}
			return response;
		}

		@Override
		protected void onPostExecute(NetworkResponse networkResponse) {
			Util.dimissProDialog();
			String is_registered = null;
			if (networkResponse != null) {
				if (networkResponse.isSuccess()) {
					try {
						is_registered = AppGlobals.userDetail.is_profile_complete;
					
						if (is_registered.equals("1")) {
							Config.setUserToken(AppGlobals.userDetail.user_token);
							Config.setUserId(AppGlobals.userDetail.user_id);
							Config.setUserDeviceId(AppGlobals.userDetail.user_device_id);
							Config.setUserName(AppGlobals.userDetail.fullname); 
							Config.setUserEmailId(AppGlobals.userDetail.email); 
							Config.setUserImage(AppGlobals.userDetail.userimage); 
							Config.savePreferencese();
							AppGlobals.userDetail.lang_code = Config.getLangCode();
							Util.startActivity(SignInActivity.this, MainActivity.class);
						}else if (is_registered.equals("0")) {
							url = networkResponse.getJsonObject().getJSONObject(Constants.FLD_DATA).getString("weburl");
							dialog = Util.ShowYesNoDialog(SignInActivity.this, getResources().getString(R.string.msg_registration_incomplete), getResources().getString(R.string.ok), getResources().getString(R.string.Cancel), "Confirm!!!");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					Util.showCenteredToast(SignInActivity.this, networkResponse.getErrMsg());
				}
			}
		}
	}
	
	private void setUserDetails(NetworkResponse response) {
		try {
			SignInData signInData = (SignInData)Util.getJsonToClassObject(response.getJsonObject().toString(), SignInData.class);
			AppGlobals.userDetail = signInData.data;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isUserChanged(String uEmailId){
		if(userEmailId != null){
			if(!userEmailId.equals(uEmailId)){ 
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		View view = getCurrentFocus();
		boolean ret = super.dispatchTouchEvent(event);
		if (view instanceof EditText) {
			View w = getCurrentFocus();
			int scrcoords[] = new int[2];
			w.getLocationOnScreen(scrcoords);
			float x = event.getRawX() + w.getLeft() - scrcoords[0];
			float y = event.getRawY() + w.getTop() - scrcoords[1];
			if (event.getAction() == MotionEvent.ACTION_DOWN && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom())) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
			}
		}
		return ret;
	}
	
	
	/**	Facebook Login	 **/
	private void FBLogin(){
		try {
			if (Util.isDeviceOnline(this)) {
				if (android.os.Build.VERSION.SDK_INT > 9) {
					StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
					StrictMode.setThreadPolicy(policy);
				}
				loginWithFacebook(facebook);
			}else {
				Util.showCenteredToast(SignInActivity.this, getResources().getString(R.string.msg_Connect_internet));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loginWithFacebook(final Facebook facebook) {
		if (fbAccessToken.equals("")) {
			facebook.authorize(SignInActivity.this, new String[] { "email", "publish_stream" }, Facebook.FORCE_DIALOG_AUTH,
				new DialogListener() {
				@Override
				public void onComplete(Bundle values) {
					String jsonUser;
					try {
						jsonUser = facebook.request("me");
						JSONObject obj = com.facebook.android.Util.parseJson(jsonUser);
						fEmail = obj.getString("email");
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								loginWith = "f";
								new SignInTask().execute();
							}
						});
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (FacebookError e) {
						e.printStackTrace();
					}catch (JSONException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onCancel() {}
				@Override
				public void onFacebookError(FacebookError e) {}
				@Override
				public void onError(DialogError e) {}
			});
		}
	}
}
