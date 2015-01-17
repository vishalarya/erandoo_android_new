package erandoo.app.main.settings;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import erandoo.app.R;
import erandoo.app.adapters.SettingLocationAdapter;
import erandoo.app.custom.AppHeaderView;
import erandoo.app.custom.BaseFragActivity;
import erandoo.app.database.DatabaseMgr;
import erandoo.app.database.WorkLocation;
import erandoo.app.main.AppGlobals;
import erandoo.app.utilities.Util;

public class LocationsSettActivity extends BaseFragActivity implements
		OnClickListener {
	DatabaseMgr database;

	SettingLocationAdapter adapter;
	ListView listViewSLocation;
	GetLocationAsyncTask getLocationList;

	private AppHeaderView appHeaderView;
	 private OnClickListener viewClick;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);

		setContentView(R.layout.ed_setting_location);
		inIt();

		
	}

	private void inIt() {
		// TODO Auto-generated method stub
		appHeaderView = (AppHeaderView) findViewById(R.id.appHeaderView);
		listViewSLocation = (ListView) findViewById(R.id.listViewSLocation);
		viewClick=new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				WorkLocation workLocation=(WorkLocation) v.getTag();
				Intent in = new Intent(LocationsSettActivity.this,
						AddNewLocationAct.class);
				in.putExtra("location", workLocation);
				startActivityForResult(in, 11);
				overridePendingTransition(R.anim.right_in, R.anim.left_out);
				
			}
		};
		
		appHeaderView.setHeaderContent(
				getResources().getDrawable(R.drawable.ic_back), getResources()
						.getString(R.string.setting_locations), null,
				getResources().getString(R.string.Add_New), Gravity.CENTER);

		database = DatabaseMgr.getInstance(this);
		getLocationList = new GetLocationAsyncTask();
		if (getLocationList.getStatus() == AsyncTask.Status.PENDING) {
			getLocationList.execute();
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
			Intent in = new Intent(LocationsSettActivity.this,
					AddNewLocationAct.class);
			startActivityForResult(in, 11);
			overridePendingTransition(R.anim.right_in, R.anim.left_out);
		}

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finishActivity();
	}

	private void finishActivity() {
		LocationsSettActivity.this.finish();
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}

	private class GetLocationAsyncTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {

			Util.showProDialog(LocationsSettActivity.this);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			database.getWorkLocationList();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			adapter = new SettingLocationAdapter(LocationsSettActivity.this,
					R.layout.ed_setting_location_row, AppGlobals.workLocations,viewClick);
			listViewSLocation.setAdapter(adapter);
			Util.dimissProDialog();

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 11) {
			database = DatabaseMgr.getInstance(this);
			getLocationList = new GetLocationAsyncTask();
			if (getLocationList.getStatus() == AsyncTask.Status.PENDING) {
				getLocationList.execute();
			}
		}
	}
}