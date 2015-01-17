package erandoo.app.main.settings;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import erandoo.app.R;
import erandoo.app.adapters.SettingGridAdapter;
import erandoo.app.custom.AppHeaderView;
import erandoo.app.custom.BaseFragActivity;
import erandoo.app.utilities.Util;

public class SettingsActivity extends BaseFragActivity implements
		OnClickListener {

	GridView setting_gridView;
	// ImageButton settingBack;
	ArrayList<JSONObject> settingsNameArrayList;
	String[] settingNameArray;
	String[] settingImageArray;
	SettingGridAdapter gridAdapter;
	OnClickListener settingClickListener;
	private AppHeaderView appHeaderView;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.ed_setting_activity);
		inIt();

		appHeaderView.setHeaderContent(
				getResources().getDrawable(R.drawable.ic_menu), getResources()
						.getString(R.string.setting), null, null,
				Gravity.CENTER);
		settingNameArray = getResources().getStringArray(R.array.setting_array);
		settingImageArray = getResources().getStringArray(
				R.array.settig_img_array);
		settingsNameArrayList = new ArrayList<JSONObject>();

		for (int k = 0; k < settingNameArray.length; k++) {
			JSONObject jobje = new JSONObject();

			try {
				jobje.put("image_name", settingImageArray[k]);
				jobje.put("setting_name", settingNameArray[k]);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			settingsNameArrayList.add(jobje);
		}
		settingClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String clickedValue = (String) v.getTag();
				 if (clickedValue.equals(getResources().getString(
						R.string.change_pass))) {
					Intent in = new Intent(SettingsActivity.this,
							ChangePassActivity.class);
					startActivity(in);
					overridePendingTransition(R.anim.right_in, R.anim.left_out);
				} else if (clickedValue.equals(getResources().getString(
						R.string.setting_locations))) {

					if (Util.isDeviceOnline(SettingsActivity.this)) {
						Intent in = new Intent(SettingsActivity.this,
								LocationsSettActivity.class);
						startActivity(in);
						overridePendingTransition(R.anim.right_in,
								R.anim.left_out);
					} else {
						Util.showCenteredToast(
								SettingsActivity.this,
								getResources().getString(
										R.string.msg_Connect_internet));
					}

				} else if (clickedValue.equals(getResources().getString(
						R.string.setting_money))) {
					if (Util.isDeviceOnline(SettingsActivity.this)) {
						Intent in = new Intent(SettingsActivity.this,
								MoneyActivity.class);
						startActivity(in);
						overridePendingTransition(R.anim.right_in,
								R.anim.left_out);

					} else {
						Util.showCenteredToast(
								SettingsActivity.this,
								getResources().getString(
										R.string.msg_Connect_internet));
					}
				} else if (clickedValue.equals(getResources().getString(
						R.string.setting_notifications))) {
					if (Util.isDeviceOnline(SettingsActivity.this)) {
						Intent in = new Intent(SettingsActivity.this,
								NotificationsSettActivity.class);
						startActivity(in);
						overridePendingTransition(R.anim.right_in,
								R.anim.left_out);
					} else {
						Util.showCenteredToast(
								SettingsActivity.this,
								getResources().getString(
										R.string.msg_Connect_internet));
					}
				} else if (clickedValue.equals(getResources().getString(
						R.string.setting_profile))) {
					Intent in =  new Intent(SettingsActivity.this, EditProfileActivity.class);
					startActivity(in);
					overridePendingTransition(R.anim.right_in, R.anim.left_out);
				}
			}
		};
		gridAdapter = new SettingGridAdapter(SettingsActivity.this,
				settingsNameArrayList, settingClickListener);
		setting_gridView.setAdapter(gridAdapter);

	}

	private void inIt() {
		// TODO Auto-generated method stub

		appHeaderView = (AppHeaderView) findViewById(R.id.appHeaderView);
		setting_gridView = (GridView) findViewById(R.id.setting_gridView);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (v.getId() == appHeaderView.txtHLeft.getId()) {
			finishActivity();
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finishActivity();
	}

	private void finishActivity() {
		SettingsActivity.this.finish();
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}
}
