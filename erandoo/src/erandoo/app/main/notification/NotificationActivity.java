package erandoo.app.main.notification;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import erandoo.app.R;
import erandoo.app.adapters.NotificationAdapter;
import erandoo.app.cmds.Cmd;
import erandoo.app.cmds.CmdFactory;
import erandoo.app.config.Config;
import erandoo.app.custom.AppHeaderView;
import erandoo.app.custom.BaseFragActivity;
import erandoo.app.database.Notification;
import erandoo.app.network.NetworkMgr;
import erandoo.app.network.NetworkResponse;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.Util;

public class NotificationActivity extends BaseFragActivity implements OnClickListener{
	private ListView lvNotificationList;
	
	private ArrayList<Notification> notificationListData = new ArrayList<Notification>();
	private NotificationAdapter adapter;
	private AppHeaderView appHeaderView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ed_notifications);
		
		initialize();
	}
	
	private void initialize(){
		lvNotificationList = (ListView) findViewById(R.id.lvNotificationList);
		
		setHeaderView();
		
		new NotificationListTask().execute();
	}

	private void setHeaderView() {
		appHeaderView = (AppHeaderView) findViewById(R.id.appHeaderView);
		appHeaderView.setHeaderContent(
				getResources().getDrawable(R.drawable.ic_back), getResources()
						.getString(R.string.Notifications), getResources()
						.getDrawable(R.drawable.ic_sort), null, Gravity.LEFT);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == appHeaderView.txtHLeft.getId()){
			//TODO back button
			finishActivity();
		}
	}
	
	
	//----------------TASK TO GET ALL NOTIFICATION LIST----------------------//
	private class NotificationListTask extends AsyncTask<String, Void, NetworkResponse> {
		@Override
		protected void onPreExecute() {
			Util.showProDialog(NotificationActivity.this);
		}

		@Override
		protected NetworkResponse doInBackground(String... params) {
			NetworkResponse response = null;
			if(Util.isDeviceOnline(NotificationActivity.this)){
				Cmd cmd = CmdFactory.createGetNotificationCmd();
				response = NetworkMgr.httpPost(Config.API_URL, cmd.getJsonData());
				if (response != null) {
					if(response.isSuccess()){
						getNotificationList(response.getJsonObject());
					}
				}
			}
			return response;
		}

		@Override
		protected void onPostExecute(NetworkResponse networkResponse) {
			if(networkResponse != null){
				if (!networkResponse.isSuccess()) {
					Util.showCenteredToast(NotificationActivity.this,networkResponse.getErrMsg());
				}else{
					setNotificationDataToListView();
				}
			}
			Util.dimissProDialog();
		}
	}


	private void getNotificationList(JSONObject jsonObject) {
		try {
			JSONArray array = jsonObject.getJSONArray(Constants.FLD_DATA);
			if(array.length()>0){
				for (int i = 0; i < array.length(); i++) {
					Gson gson = new GsonBuilder().create();
					Notification notification = gson.fromJson(array.getJSONObject(i).toString(), Notification.class);
					notificationListData.add(notification);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setNotificationDataToListView() {
		adapter = new NotificationAdapter(NotificationActivity.this, R.layout.ed_notification_row, notificationListData);
		lvNotificationList.setAdapter(adapter);
	}
	
	@Override
	public void onBackPressed() {
		finishActivity();
	}
	
	private void finishActivity(){
		NotificationActivity.this.finish();
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}
}
