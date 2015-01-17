package erandoo.app.main;

import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import erandoo.app.R;
import erandoo.app.adapters.RatingAdapter;
import erandoo.app.cmds.Cmd;
import erandoo.app.cmds.CmdFactory;
import erandoo.app.config.Config;
import erandoo.app.custom.AppHeaderView;
import erandoo.app.custom.CircularImageView;
import erandoo.app.custom.PieGraph;
import erandoo.app.custom.PieSlice;
import erandoo.app.database.Crown;
import erandoo.app.database.DatabaseMgr;
import erandoo.app.database.Membership;
import erandoo.app.database.Money;
import erandoo.app.database.Profile;
import erandoo.app.database.Project;
import erandoo.app.database.UserDetail;
import erandoo.app.main.message.MessageActivity;
import erandoo.app.main.notification.NotificationActivity;
import erandoo.app.mqtt.ChatMgr;
import erandoo.app.mqtt.util.MqttUtil;
import erandoo.app.network.NetworkMgr;
import erandoo.app.network.NetworkResponse;
import erandoo.app.tempclasses.DashboardData;
import erandoo.app.tempclasses.ProfileRatingData;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.SyncAppData;
import erandoo.app.utilities.Util;

public class MainActivity extends SlidingFragmentActivity implements OnClickListener {
	private SlidingMenu slidingMenu;
	private FragSlideMenu mFrag;
	private GoogleCloudMessaging gcm;
	private AppHeaderView appHeaderView;
//	private Button btnDBProfileUpdateNow;

	//--------------Dash-board Related----------
	private CircularImageView cImgVDBProfileImage;
	private TextView txtDBProfileName;
	private TextView txtDBProfileLUDate;
	private TextView txtDBActiveProject;
	private TextView txtDBAvailBids;
	private TextView txtDBSkillAvail;
	private TextView txtDBTotalProjects;
	private TextView txtDBPoster;
	private TextView txtDBDoer;
	private TextView txtDBWallet;
	private TextView txtDBEarned;
	private TextView txtDBSpent;
	private ListView lvDBRating;
	private RatingBar rtbDBOverallRating;
	private Crown crown;
	private Membership membership;
	private Project project;
	private Profile profile;
	private ProfileRatingData rating;
	private Money money;
	private RatingAdapter ratingAdapter;
	private TextView txtDBDoerone;
	
	//PieChart
	private TextView txtPostedColor;
	private TextView txtPosted;
	private TextView txtWorkingColor;
	private TextView txtWorking;
	private TextView txtBidsColor;
	private TextView txtBids;
	private TextView txtPieGraphValue;
	
	//Money
	private LinearLayout llDBWallet;
	private LinearLayout llDBEarned;
	private LinearLayout llDBSpent;
	private TextView txtWallet;
	private TextView txtEarned;
	private TextView txtSpent;
	
	private RelativeLayout rlDBSFP;
	private LinearLayout llDBSFP;
	private LinearLayout llDBSFPTotal;
	private TextView txtSFPMessage;
	
	private DatabaseMgr database;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSavedUserDetailData(savedInstanceState);
		setContentView(R.layout.ed_main);
		addSlideMenus(savedInstanceState);
		initialize();
	}

	private void initialize() {
		database = DatabaseMgr.getInstance(this);
		setHeaderView();
		setSlideSetting();
		
		//------------Dash-board-related--------
		cImgVDBProfileImage = (CircularImageView)findViewById(R.id.cImgVDBProfileImage);
		txtDBProfileName = (TextView)findViewById(R.id.txtDBProfileName);
		txtDBProfileLUDate = (TextView)findViewById(R.id.txtDBProfileLUDate);
		txtDBActiveProject = (TextView)findViewById(R.id.txtDBActiveProject);
		txtDBAvailBids = (TextView)findViewById(R.id.txtDBAvailBids);
		txtDBSkillAvail = (TextView)findViewById(R.id.txtDBSkillAvail);
		txtDBTotalProjects = (TextView)findViewById(R.id.txtDBTotalProjects);
		txtDBPoster = (TextView)findViewById(R.id.txtDBPoster);
		txtDBDoer = (TextView)findViewById(R.id.txtDBDoer);
		txtDBWallet = (TextView)findViewById(R.id.txtDBWallet);
		txtDBEarned = (TextView)findViewById(R.id.txtDBEarned);
		txtDBSpent = (TextView)findViewById(R.id.txtDBSpent);
		lvDBRating = (ListView) findViewById(R.id.lvDBRating);
		rtbDBOverallRating = (RatingBar) findViewById(R.id.rtbDBOverallRating);
//		btnDBProfileUpdateNow = (Button)findViewById(R.id.btnDBProfileUpdateNow);
		
		txtDBDoerone = (TextView) findViewById(R.id.txtDBDoerone);
		
		//PieChart
		txtPostedColor = (TextView)findViewById(R.id.txtPostedColor);
		txtPosted = (TextView)findViewById(R.id.txtPosted);
		txtWorkingColor = (TextView)findViewById(R.id.txtWorkingColor);
		txtWorking = (TextView)findViewById(R.id.txtWorking);
		txtBidsColor = (TextView)findViewById(R.id.txtBidsColor);
		txtBids = (TextView)findViewById(R.id.txtBids);
		txtPieGraphValue = (TextView)findViewById(R.id.txtPieGraphValue);
		
		//Money
		llDBWallet = (LinearLayout) findViewById(R.id.llDBWallet);
		llDBEarned = (LinearLayout) findViewById(R.id.llDBEarned);
		llDBSpent = (LinearLayout) findViewById(R.id.llDBSpent);
		txtWallet = (TextView) findViewById(R.id.txtWallet);
		txtEarned = (TextView) findViewById(R.id.txtEarned);
		txtSpent = (TextView) findViewById(R.id.txtSpent);
		
		rlDBSFP = (RelativeLayout) findViewById(R.id.rlDBSFP);
		llDBSFP = (LinearLayout) findViewById(R.id.llDBSFP);
		llDBSFPTotal = (LinearLayout) findViewById(R.id.llDBSFPTotal);
		txtSFPMessage = (TextView) findViewById(R.id.txtSFPMessage);
		
		//Pie Chart
		txtPosted.setOnClickListener(this);
		txtWorking.setOnClickListener(this);
		txtBids.setOnClickListener(this);
		
		// --------------LOAD MASTER DATA----------------
		if (Util.isDeviceOnline(this)) {
			new SyncMasterDataTask().execute();
		} else {
			Util.showCenteredToast(this,getResources().getString(R.string.msg_Connect_internet));
			database.getCountryListData();
		}
	}
	
	private void setHeaderView() {
		appHeaderView = (AppHeaderView) findViewById(R.id.appHeaderView);
		appHeaderView.setHeaderContent(
				getResources().getDrawable(R.drawable.ic_menu), getResources()
						.getString(R.string.app_name), getResources()
						.getDrawable(R.drawable.ic_msg), getResources() 
						.getDrawable(R.drawable.ic_notifi), Gravity.CENTER);
	}
	
	@Override
	protected void onResume() {
		ChatMgr.mainContext = MainActivity.this;
		super.onResume();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle state) {
		super.onSaveInstanceState(state);
		state.putSerializable(Constants.SERIALIZABLE_DATA, AppGlobals.userDetail);
	}
	
	private void getSavedUserDetailData(Bundle savedInstanceState){
		if(savedInstanceState != null){
			Config.init(this);
			UserDetail userDetail = (UserDetail)savedInstanceState.getSerializable(Constants.SERIALIZABLE_DATA);
			if(userDetail != null){
				AppGlobals.userDetail = userDetail;
			}else{
				Config.setUserDetails(false);
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == appHeaderView.txtHLeft.getId()){
			//TODO menu button
			slidingMenu.showMenu(true);
		} else if(v.getId() == appHeaderView.txtHRight1.getId()){
			//TODO message button
			goToNewWindow(Constants.VIEW_MESSAGE);
		} else if(v.getId() == appHeaderView.txtHRight2.getId()){
			//TODO notification button
			goToNewWindow(Constants.VIEW_NOTIFICATION);
		} else if(v.getId() == R.id.txtPosted){
			txtPieGraphValue.setText("");
			txtPieGraphValue.setText("Posted: "+crown.posted);
		} else if(v.getId() == R.id.txtWorking){
			txtPieGraphValue.setText("");
			txtPieGraphValue.setText("Working: "+crown.working);
		} else if(v.getId() == R.id.txtBids){
			txtPieGraphValue.setText("");
			txtPieGraphValue.setText("Bids: "+crown.bids);
		}
	}
	
	private void goToNewWindow(String type) {
		Intent intent = new Intent();
		if (type.equals(Constants.VIEW_NOTIFICATION)) {
			intent.setClass(MainActivity.this, NotificationActivity.class);
			
		} else if (type.equals(Constants.VIEW_MESSAGE)) {
			intent.setClass(MainActivity.this, MessageActivity.class); 
			
		}
		startActivity(intent);
		overridePendingTransition(R.anim.right_in, R.anim.left_out);
	}
	
	private void setSlideSetting() {
		slidingMenu = getSlidingMenu();
		slidingMenu.setShadowWidthRes(R.dimen.slide_shadow_width);
		slidingMenu.setShadowDrawable(R.drawable.ed_slide_shadow);
		slidingMenu.setBehindOffsetRes(R.dimen.sliding_menu_offset);
		slidingMenu.setFadeDegree(0.35f);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
	}
	
	private void addSlideMenus(Bundle savedInstanceState) {
		// -------------Left Slide Menu Bar ---------------------
		setBehindContentView(R.layout.ed_slide_frame);
		if (savedInstanceState == null) {
			FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
			mFrag = new FragSlideMenu();
			t.replace(R.id.slide_frame, mFrag);
			t.commit();
		} else {
			mFrag = (FragSlideMenu) this.getSupportFragmentManager().findFragmentById(R.id.slide_frame);
		}
	}
	
	// ----------------TASK TO GET MASTER DATA --------------------------------
	private class SyncMasterDataTask extends AsyncTask<Void, Integer, NetworkResponse> {
		@Override
		protected void onPreExecute() {
			Util.showProDialog(MainActivity.this);
			Util.progressDialog.setMessage(getResources().getString(R.string.msg_Loading_dashboard_data)); 
		}
	
		@Override
		protected NetworkResponse doInBackground(Void... params) {
			NetworkResponse response = null;
			
			//-----------GET DASH-BOARD DATA------------
			Cmd cmd = CmdFactory.getDashboardData();
			response = NetworkMgr.httpPost(Config.API_URL,cmd.getJsonData());
			if(response != null){
				if(response.isSuccess()){
					getDashboard(response);
				}
			}
			onProgressUpdate(new Integer[]{0});
			
			//---------------REGISTER DEVICE ON GCM ---------------
			registerDeviceOnGCM();
			onProgressUpdate(new Integer[]{1});
			//-----------CONNECT TO ACTIVE MQ SERVER ------------
			MqttUtil.connectToMqtt();
			
			
			//-----------UPDATE MASTER DATA------------
			SyncAppData.syncMasterData(MainActivity.this);
			database.getCountryListData();
			database.getWorkLocationList();
			onProgressUpdate(new Integer[]{2});
			
			//------------UPDATE MESSAGE DATA---------
			SyncAppData.syncInboxData(MainActivity.this);
			onProgressUpdate(new Integer[]{3});
			
			//-----------UPDATE PROJECT DATA------------
			SyncAppData.syncProjectData(MainActivity.this);
			
			//Util.writeDBfileOnSdcard(MainActivity.this); 
			return response;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			final Integer value = values[0]; 
			runOnUiThread(new Runnable() {
				public void run() {
					if(value.equals(0)){ 
						Util.progressDialog.setMessage(getResources().getString(R.string.msg_Connecting_with_chat_server)); 
						
					}else if(value.equals(1)){
						Util.progressDialog.setMessage(getResources().getString(R.string.msg_updating_application_data)); 
						
					}else if(value.equals(2)){
						Util.progressDialog.setMessage(getResources().getString(R.string.msg_updating_message_data)); 
						
					}else if(value.equals(3)){
						Util.progressDialog.setMessage(getResources().getString(R.string.msg_updating_project_data)); 
					}
				} 
			}); 
		}
		
		@Override
		protected void onPostExecute(NetworkResponse networkResponse) {
			Util.dimissProDialog();
			Config.setUserDetails(false);
		}
	}
	
	private void registerDeviceOnGCM(){
		try {
			if (gcm == null) {
				gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
				String regId = SyncAppData.getGCMRegistrationId(MainActivity.this);
				if (regId.isEmpty()) {
					AppGlobals.userDetail.cloud_key_to_notify = gcm.register(Config.APP_TOKEN_IN_NUMBERS);
					Config.setGcmRegisteredID(AppGlobals.userDetail.cloud_key_to_notify); 
					Config.savePreferencese();
					Log.v("GCM Registered Id:->", "Device registered, registration success");
	            }else{
	            	AppGlobals.userDetail.cloud_key_to_notify = regId; 
	            }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getDashboard(NetworkResponse response) {
		DashboardData dashboard = (DashboardData) Util.getJsonToClassObject(response.getJsonObject().toString(), DashboardData.class);
		crown = dashboard.data.crown;
		membership = dashboard.data.membership;
		project = dashboard.data.projects;
		profile = dashboard.data.profile;
		rating = dashboard.data.rating;
		money = dashboard.data.money;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				setDashboardData();
			}
		});
	}

	private void setDashboardData(){
		try {
			Util.loadImage(cImgVDBProfileImage, AppGlobals.userDetail.userimage,R.drawable.ic_launcher);
			txtDBProfileName.setText(AppGlobals.userDetail.fullname); 
			txtDBProfileLUDate.setText(""+profile.last_accessed_at);
			
			if (membership.active_projects != null && membership.bid_this_month != null && membership.skill_avaliable != null) {
				txtDBActiveProject.setText(""+membership.active_projects);
				txtDBAvailBids.setText(""+membership.bid_this_month);
				txtDBSkillAvail.setText(""+membership.skill_avaliable);
			}
			
			if (project.poster != null && project.doer != null) {
				int poster,doer, posterPercentage, posterValue = 0, doerValue = 0;
				poster = Integer.parseInt(project.poster);
				doer = Integer.parseInt(project.doer);
				
				if (poster > 0) {
					posterPercentage = (poster * 100) / (Integer.parseInt(project.total_project));
					posterValue = (int) ((posterPercentage * getResources().getDimension(R.dimen.db_SFP_width)) / 100);
					txtDBPoster.setWidth(posterValue);
					txtDBPoster.setBackgroundColor(getResources().getColor(R.color.ed_color_orange_light));
					txtDBPoster.setText("" + poster);
				}
				
				if (doer > 0) {
					doerValue = (int) (getResources().getDimension(R.dimen.db_SFP_width)-posterValue);
					txtDBDoer.setWidth(doerValue);
					txtDBDoer.setBackgroundColor(getResources().getColor(R.color.red));
					txtDBDoer.setText("" + doer);
					txtDBDoerone.setVisibility(View.GONE);
				}else{
					txtDBDoerone.setVisibility(View.VISIBLE);
					txtDBDoerone.setText("0");
				}
				
				if (poster == 0 && doer == 0) {
					rlDBSFP.setVisibility(View.GONE);
					llDBSFP.setVisibility(View.GONE);
					llDBSFPTotal.setVisibility(View.GONE);
					txtSFPMessage.setVisibility(View.VISIBLE);
					txtSFPMessage.setText("So far, you have not completed any projects. Get started today!");
				}else{
					txtDBTotalProjects.setText(""+project.total_project);
				}
			}
			
			money();
			pieChart();
			
			ratingAdapter = new RatingAdapter(MainActivity.this, R.layout.ed_rating_row, rating.category_rating,true,false);
			lvDBRating.setAdapter(ratingAdapter);
			rtbDBOverallRating.setRating(Float.parseFloat(rating.overall_rating));
			getListViewSize(lvDBRating);
			
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	private void money(){
		float total, wallet, earned, spent, walletPercentage, earnedPercentage, walletValue = 0, earnedValue = 0, spentValue;
		wallet = money.wallet;
		earned = money.earn;
		spent = money.spent;
		total = wallet+earned+spent;
		
		if (wallet > 0) {
			walletPercentage = (wallet * 100) / total;
			walletValue = (int) ((walletPercentage * getResources().getDimension(R.dimen.db_money_height)) / 100);
			llDBWallet.setVisibility(View.VISIBLE);
			txtDBWallet.setVisibility(View.VISIBLE);
			txtDBWallet.setHeight((int) walletValue);
			txtDBWallet.setBackgroundColor(getResources().getColor(R.color.red));
			txtWallet.setText("Wallet ($"+wallet+")");
		}
		
		if (earned > 0) {
			earnedPercentage = (earned * 100) / total;
			earnedValue = (int) ((earnedPercentage * getResources().getDimension(R.dimen.db_money_height)) / 100);
			llDBEarned.setVisibility(View.VISIBLE);
			txtDBEarned.setVisibility(View.VISIBLE);
			txtDBEarned.setHeight((int) earnedValue);
			txtDBEarned.setBackgroundColor(getResources().getColor(R.color.green));
			txtEarned.setText("Earned ($"+earned+")");
		}
		
		if (spent > 0) {
			spentValue = (int) (getResources().getDimension(R.dimen.db_money_height) - walletValue - earnedValue);
			llDBSpent.setVisibility(View.VISIBLE);
			txtDBSpent.setVisibility(View.VISIBLE);
			txtDBSpent.setHeight((int) spentValue);
			txtDBSpent.setBackgroundColor(getResources().getColor(R.color.yellow));
			txtSpent.setText("Spent ($"+spent+")");
		}
	}
	
	private void pieChart(){
		Resources resources = getResources();
		PieGraph pg = (PieGraph) findViewById(R.id.piegraph);
		PieSlice slice = new PieSlice();
		
		if (crown.posted > 0) {
			slice.setColor(resources.getColor(R.color.sky_blue_light));
			slice.setValue(crown.posted);
			pg.addSlice(slice);
			txtPostedColor.setVisibility(View.VISIBLE);
			txtPosted.setVisibility(View.VISIBLE);
			txtPieGraphValue.setText("Posted: "+crown.posted);
		}
		
		if (crown.working > 0) {
			slice = new PieSlice();
			slice.setColor(resources.getColor(R.color.green));
			slice.setValue(crown.working);
			pg.addSlice(slice);
			txtWorkingColor.setVisibility(View.VISIBLE);
			txtWorking.setVisibility(View.VISIBLE);
			if (txtPieGraphValue.getText().toString().trim().length() == 0) {
				txtPieGraphValue.setText("Working: "+crown.working);
			}
		}
		
		if (crown.bids > 0) {
			slice = new PieSlice();
			slice.setColor(resources.getColor(R.color.red));
			slice.setValue(crown.bids);
			pg.addSlice(slice);
			txtBidsColor.setVisibility(View.VISIBLE);
			txtBids.setVisibility(View.VISIBLE);
			if (txtPieGraphValue.getText().toString().trim().length() == 0) {
				txtPieGraphValue.setText("Bids: "+crown.bids);
			}
		}
	}
	
	private void getListViewSize(ListView myListView) {
        ListAdapter myListAdapter = myListView.getAdapter();
        if (myListAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int size = 0; size < myListAdapter.getCount(); size++) {
            View listItem = myListAdapter.getView(size, null, myListView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = myListView.getLayoutParams();
        params.height = totalHeight + (myListView.getDividerHeight() * (myListAdapter.getCount() - 1));
        myListView.setLayoutParams(params);
    }

	
	@Override
	public void finish() {
		MqttUtil.mqttDisconnect();
		super.finish();
	}
}
