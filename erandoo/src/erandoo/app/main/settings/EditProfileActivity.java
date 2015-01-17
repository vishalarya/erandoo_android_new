package erandoo.app.main.settings;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.adapters.AddSkillsAdapter;
import erandoo.app.cmds.Cmd;
import erandoo.app.cmds.CmdFactory;
import erandoo.app.config.Config;
import erandoo.app.custom.AppHeaderView;
import erandoo.app.custom.BaseFragActivity;
import erandoo.app.database.DatabaseMgr;
import erandoo.app.database.Profile;
import erandoo.app.database.Skill;
import erandoo.app.main.AppGlobals;
import erandoo.app.main.registration.RegProfileImageDialog;
import erandoo.app.network.NetworkMgr;
import erandoo.app.network.NetworkResponse;
import erandoo.app.tempclasses.ProfileData;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.Util;

public class EditProfileActivity extends BaseFragActivity implements OnClickListener {
	private ImageView imvEProfileUserImage;
	private EditText etxtEFirstName;
	private EditText etxtELastName;
	private EditText etxtEMainPh;
	private EditText etxtEAlternatePh;
	private EditText eTxtEAboutMe;
	private TextView txtEViewSkills;
	private TextView txtEViewLoc;
	private TextView txtEAddLoc;
	private Profile profile;
	private AppHeaderView appHeaderView;
	private DatabaseMgr dbMgr;
	private ArrayList<Skill> skillList = new ArrayList<Skill>();
	private ArrayList<Skill> selectedSkillList = new ArrayList<Skill>();
	private int keyDel = 0;
	private String a;
	private LinearLayout llSListSearchBar;
	private TextView txtSimpleListTitle;

	private TextView txtSListDone;
	private ImageButton imgBtnSimpleListBack;
	private ListView listVSimpleList;
	private EditText eTextSimpleListSearch;
	private String encodedImage = "";
	private String imagepath;
	
	private boolean callToAsynTask = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ed_user_profile_edit);
		initialize();
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode,Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == Constants.PICK_PROFILE_IMAGE){
 			if (resultCode == RESULT_OK) {
				goToImageCropView(data.getData());
			}
		}else if(requestCode == Constants.CROP_PROFILE_IMAGE){
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
				Bitmap image = (Bitmap) extras.get("data");
				imagepath = getPath(Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), image, "profile_temp_img" + ".jpg", "Temp image for profile")));
				if (!(image == null)) {
					callToAsynTask = true;
					imvEProfileUserImage.setImageBitmap(image);
				}
			}
		}
	}
	
	public String getPath(Uri uri) {
		 if(uri != null){
			String[] projection = { MediaStore.Images.Media.DATA };
	        @SuppressWarnings("deprecation")
			Cursor cursor = managedQuery(uri, projection, null, null, null);
	        // IN CASE OF SELECTING IMAGE FROM FILE MANAGER CUSSER IS NULL
	        if(cursor != null) {
	        	 int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	             cursor.moveToFirst();
	             return cursor.getString(column_index);
	        }else{
	        	return null;
	        }
		 }else{
			 return null;
		 }
	}
	
	private void goToImageCropView(Uri fileUri){
		Intent intent = new Intent();
		// ******** code for crop image
		intent.setDataAndType(fileUri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 200);
		intent.putExtra("outputY", 200);
		try {
			intent.putExtra("return-data", true);
			intent.setAction("com.android.camera.action.CROP");
			startActivityForResult(intent, Constants.CROP_PROFILE_IMAGE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initialize() {
		imvEProfileUserImage = (ImageView) findViewById(R.id.imvEProfileUserImage);
		etxtEMainPh = (EditText) findViewById(R.id.etxtEMainPh);
		etxtEAlternatePh = (EditText) findViewById(R.id.etxtEAlternatePh);
		eTxtEAboutMe = (EditText) findViewById(R.id.eTxtEAboutMe);
		txtEViewSkills = (TextView) findViewById(R.id.txtEViewSkills);
		txtEViewLoc = (TextView) findViewById(R.id.txtEViewLoc);
		txtEAddLoc = (TextView) findViewById(R.id.txtEAddLoc);
		
		etxtEFirstName = (EditText) findViewById(R.id.etxtEFirstName);
		etxtELastName = (EditText) findViewById(R.id.etxtELastName);
		
		dbMgr = DatabaseMgr.getInstance(EditProfileActivity.this);
		txtEViewSkills.setOnClickListener(this);
		txtEViewLoc.setOnClickListener(this);
		txtEAddLoc.setOnClickListener(this);
		imvEProfileUserImage.setOnClickListener(this);
		
		setHeaderView();
		loadProfileData();
		
		etxtEAlternatePh.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				setPhoneNumberFormat(etxtEAlternatePh);
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			public void afterTextChanged(Editable s) {}
		});
		
		etxtEMainPh.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,int count) {
				setPhoneNumberFormat(etxtEMainPh);
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			public void afterTextChanged(Editable s) {}
		});
	}

	private void setHeaderView() {
		appHeaderView = (AppHeaderView) findViewById(R.id.appHeaderView);
		appHeaderView.setHeaderContent(
				getResources().getDrawable(R.drawable.ic_back), getResources()
						.getString(R.string.Profile), null, getResources()
						.getString(R.string.Update), Gravity.LEFT);
	}

	private void setPhoneNumberFormat(EditText eTxt) {
		boolean flag = true;
		if (flag) {
			eTxt.setOnKeyListener(new OnKeyListener() {
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_DEL)
						keyDel = 1;
					return false;
				}
			});
			if (keyDel == 0) {
				if (((eTxt.getText().length() + 1) % 4) == 0) {
					if (eTxt.getText().toString().split("-").length <= 2) {
						eTxt.setText(eTxt.getText() + "-");
						eTxt.setSelection(eTxt.getText().length());
					}
				}
				a = eTxt.getText().toString();
			} else {
				a = eTxt.getText().toString();
				keyDel = 0;
			}
		} else {
			eTxt.setText(a);
		}
	}

	@Override
	public void onClick(View view) {
		int vId = view.getId();
		if (vId == appHeaderView.txtHLeft.getId()) {
			finishActivity();
		} else if (vId == R.id.txtEViewSkills) {
			ArrayList<Skill> tempList = new ArrayList<Skill>(skillList);
			showSKillDialog(tempList);
		} else if (vId == appHeaderView.txtHRight2.getId()) {
			if (checkValidation()) {
				appHeaderView.txtHRight2.setEnabled(false);
				UpadateProfileAsync updateProfile = new UpadateProfileAsync();
				updateProfile.execute();
			}
		} else if (vId == R.id.txtEViewLoc) {
			if (Util.isDeviceOnline(EditProfileActivity.this)) {
				Intent in = new Intent(EditProfileActivity.this, LocationsSettActivity.class);
				startActivity(in);
				overridePendingTransition(R.anim.right_in, R.anim.left_out);
			} else {
				Util.showCenteredToast(EditProfileActivity.this, getResources().getString(R.string.msg_Connect_internet));
			}
		} else if (vId == R.id.txtEAddLoc) {
			Intent in = new Intent(EditProfileActivity.this, AddNewLocationAct.class);
			startActivity(in);
			overridePendingTransition(R.anim.right_in, R.anim.left_out);
		} else if (vId == R.id.imvEProfileUserImage) {
			RegProfileImageDialog dailog = new RegProfileImageDialog();
			dailog.show(getSupportFragmentManager(), null);
		}
	}

	private void loadProfileData() {
		if (Util.isDeviceOnline(EditProfileActivity.this)) {
			if (!callToAsynTask) {
				new ProfileDataTask().execute();
			}
		} else {
			Util.showCenteredToast(EditProfileActivity.this, getResources().getString(R.string.msg_Connect_internet));
		}
	}

	// ----------------TASK TO GET PROFILE DATA----------------------//
	private class ProfileDataTask extends AsyncTask<String, Void, NetworkResponse> {

		@Override
		protected void onPreExecute() {
			Util.showProDialog(EditProfileActivity.this);
		}

		@Override
		protected NetworkResponse doInBackground(String... params) {
			skillList = dbMgr.getSkillSListFromDB(skillList);
			NetworkResponse response = null;
			Cmd cmd = CmdFactory.createGetProfileCmd();
			cmd.addData(Profile.FLD_TRNO, 0);
			if (getIntent().getSerializableExtra(Constants.SERIALIZABLE_DATA) != null) {
				cmd.addData(Profile.FLD_VIEW_USER_ID, getIntent().getSerializableExtra(Constants.SERIALIZABLE_DATA));
			} else {
				cmd.addData(Profile.FLD_VIEW_USER_ID,AppGlobals.userDetail.user_id);
			}
			response = NetworkMgr.httpPost(Config.API_URL, cmd.getJsonData());
			if (response != null) {
				if (response.isSuccess()) {
					getProfileData(response);
				}
			}
			return response;
		}

		@Override
		protected void onPostExecute(NetworkResponse networkResponse) {
			Util.dimissProDialog();
			if (networkResponse != null) {
				if (!networkResponse.isSuccess()) {
					Util.showCenteredToast(EditProfileActivity.this, networkResponse.getErrMsg());
				} else {
					setProfileData();
				}
			}
		}
	}

	private void getProfileData(NetworkResponse response) {
		ProfileData profileData = (ProfileData) Util.getJsonToClassObject(response.getJsonObject().toString(), ProfileData.class);
		if (profileData != null) {
			profile = profileData.data;
			selectedSkillList.addAll(profile.multiskills);
		}
	}

	private void setProfileData() {
		if (profile != null) {
			Util.loadImage(imvEProfileUserImage, profile.userimage, R.drawable.ic_launcher);
			etxtEMainPh.setText(addSign(profile.primary_phone));
			etxtEAlternatePh.setText(addSign(profile.alternate_phone));
			eTxtEAboutMe.setText(profile.about_me);
			etxtEFirstName.setText(profile.firstname);
			etxtELastName.setText(profile.lastname);
		}
	}
	
	private String addSign(String str){
		if(!str.equals("")){
			String st1 = str.substring(0,3);
			String st2 = str.substring(3,6);
			String st3 = str.substring(6,str.length());
			str = st1+"-"+st2+"-"+st3;
		}
		return str;
	}
	

	@Override
	public void onBackPressed() {
		finishActivity();
	}

	private void finishActivity() {
		EditProfileActivity.this.finish();
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}

	private void showSKillDialog(final ArrayList<Skill> addSkillList) {
		final ArrayList<Object> searchList;
		final Dialog customDialog = new Dialog(EditProfileActivity.this);
		customDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		customDialog.setContentView(R.layout.ed_simple_list);
		llSListSearchBar = (LinearLayout) customDialog.findViewById(R.id.llSListSearchBar);
		txtSimpleListTitle = (TextView) customDialog.findViewById(R.id.txtSimpleListTitle);
		txtSListDone = (TextView) customDialog.findViewById(R.id.txtSListDone);
		imgBtnSimpleListBack = (ImageButton) customDialog.findViewById(R.id.imgBtnSimpleListBack);
		listVSimpleList = (ListView) customDialog.findViewById(R.id.listVSimpleList);
		eTextSimpleListSearch = (EditText) customDialog.findViewById(R.id.eTextSimpleListSearch);
		txtSimpleListTitle.setText(getResources().getString(R.string.Skills));
		imgBtnSimpleListBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				customDialog.dismiss();
			}
		});

		llSListSearchBar.setVisibility(View.VISIBLE);
		searchList = new ArrayList<Object>();
		searchList.addAll(addSkillList);
		eTextSimpleListSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				int textlength = eTextSimpleListSearch.getText().toString().length();
				if (textlength > 0) {
					addSkillList.clear();
					for (int i = 0; i < searchList.size(); i++) {
						Skill skillObj = (Skill) searchList.get(i);
						if (skillObj.skill_desc != null) {
							if (skillObj.skill_desc.toString().toLowerCase().startsWith(eTextSimpleListSearch.getText().toString())) {
								addSkillList.add(skillObj);
							}
						}
						setDataToSkillDialog(addSkillList);
					}
				} else {
					addSkillList.clear();
					for (int m = 0; m < searchList.size(); m++) {
						addSkillList.add((Skill) searchList.get(m));
						setDataToSkillDialog(addSkillList);
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override
			public void afterTextChanged(Editable s) {}
		});

		setDataToSkillDialog(addSkillList);
		txtSListDone.setVisibility(View.VISIBLE);
		customDialog.setCancelable(true);
		txtSListDone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectedSkillList.clear();
				for (Skill entry : addSkillList) {
					if (entry.isSelected)
						selectedSkillList.add(entry);
				}
				customDialog.dismiss();
			}
		});
		customDialog.show();
	}

	private void setDataToSkillDialog(ArrayList<Skill> addSkillList) {
		AddSkillsAdapter addSkillAdapterShow = null;
		int oldSettedValue = 0;
		for (int j = 0; j < addSkillList.size(); j++) {
			boolean checkOrNot = false;
			for (int k = 0; k < selectedSkillList.size(); k++) {
				if (addSkillList.get(j).skill_id.equals(selectedSkillList.get(k).skill_id)) {
					checkOrNot = true;
				}
			}
			addSkillList.get(j).isSelected = checkOrNot;
			if (checkOrNot) {
				Collections.swap(addSkillList, oldSettedValue, j);
				oldSettedValue++;
			}
		}
		if (addSkillList != null) {
			addSkillAdapterShow = new AddSkillsAdapter(EditProfileActivity.this, addSkillList, true);
			listVSimpleList.setAdapter(addSkillAdapterShow);
		}
	}

	// ----------------TASK TO Update Profile setting --------------------------------
	private class UpadateProfileAsync extends AsyncTask<String, Void, NetworkResponse> {

		@Override
		protected void onPreExecute() {
			Util.showProDialog(EditProfileActivity.this);
		}

		@Override
		protected NetworkResponse doInBackground(String... params) {
			NetworkResponse response = null;
			if (Util.isDeviceOnline(EditProfileActivity.this)) {
				if (imagepath != null) {
					encodeImageToStringFormat();
				}
				Cmd cmd = null;
				String main = etxtEMainPh.getText().toString().replace("-", "");
				String alternative = etxtEAlternatePh.getText().toString().replace("-", "");
				cmd = CmdFactory.UpdateProfileCmd(main,alternative, eTxtEAboutMe.getText().toString(), setDataToJSonArray(), encodedImage, etxtEFirstName.getText().toString(), etxtELastName.getText().toString());
				response = NetworkMgr.httpPost(Config.API_URL,cmd.getJsonData());
				if (response != null && response.isSuccess()) {
					getProfileData(response);
					encodedImage = null;
				 }
			}
			return response;
		}

		@Override
		protected void onPostExecute(NetworkResponse networkResponse) {
			Util.dimissProDialog();
			appHeaderView.txtHRight2.setEnabled(true);
			if (networkResponse != null) {
				if (!networkResponse.isSuccess()) {
					Util.showCenteredToast(EditProfileActivity.this,networkResponse.getErrMsg());
				} else {
					finishActivity();
					Util.showCenteredToast(EditProfileActivity.this, getResources().getString(R.string.msg_profile_is_updation));
				}
			}
		}
	}
	

	public JSONArray setDataToJSonArray() {
		JSONArray notiSettSkillArray;
		notiSettSkillArray = new JSONArray();
		for (int l = 0; l < selectedSkillList.size(); l++) {
			notiSettSkillArray.put(selectedSkillList.get(l).skill_id);
		}
		return notiSettSkillArray;
	}
	
	private boolean checkValidation() {
		boolean checkValidation = false;
		if (etxtEMainPh.getText().toString().trim().length() == 0) {
			Util.showCenteredToast(EditProfileActivity.this, getResources().getString(R.string.msg_main_phone));
		} else if (etxtEFirstName.getText().toString().trim().length() == 0) {
			Util.showCenteredToast(EditProfileActivity.this, "First Name can't be empty");
		} else if (eTxtEAboutMe.getText().toString().trim().length() == 0) {
			Util.showCenteredToast(EditProfileActivity.this, getResources().getString(R.string.msg_about_me));
		} else if (etxtEMainPh.getText().toString().trim().length() != 12 || !PhoneNumberUtils.isGlobalPhoneNumber(etxtEMainPh.getText().toString())) {
			Util.showCenteredToast(EditProfileActivity.this, getResources().getString(R.string.msg_main_phone));
		} else if (etxtEAlternatePh.getText().toString().trim().length() != 0) {
			if (etxtEAlternatePh.getText().toString().trim().length() != 12 || (!PhoneNumberUtils.isGlobalPhoneNumber(etxtEAlternatePh.getText().toString()))) {
				Util.showCenteredToast(EditProfileActivity.this, getResources().getString(R.string.msg_alternate_phone));
			}else{
				checkValidation = true;
			}
		} else {
			checkValidation = true;
		}
		return checkValidation;
	}


	private void encodeImageToStringFormat() {
		Bitmap bmp = BitmapFactory.decodeFile(imagepath);
		ByteArrayOutputStream baos0 = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 70, baos0);
		byte[] imageBytes0 = baos0.toByteArray();
		encodedImage = Base64.encodeToString(imageBytes0, Base64.NO_WRAP);
	}
}
