package erandoo.app.main;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.adapters.ProfileCMAdapter;
import erandoo.app.adapters.ProfileSkillAdapter;
import erandoo.app.cmds.Cmd;
import erandoo.app.cmds.CmdFactory;
import erandoo.app.config.Config;
import erandoo.app.custom.AppHeaderView;
import erandoo.app.custom.BaseFragActivity;
import erandoo.app.custom.HListView;
import erandoo.app.database.Profile;
import erandoo.app.main.profile.LocationWorkedDialog;
import erandoo.app.main.profile.PortfolioDialog;
import erandoo.app.main.profile.RatingDialog;
import erandoo.app.main.profile.ReqRcmdDialog;
import erandoo.app.main.profile.WorkHistoryDailog;
import erandoo.app.network.NetworkMgr;
import erandoo.app.network.NetworkResponse;
import erandoo.app.tempclasses.ProfileData;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.Util;

public class ProfileActivity extends BaseFragActivity implements OnClickListener {
	private ImageView imvRating;
	private ImageView imvPortfolio;
	private ImageView imvWorkHistory;
	private ImageView imvRecommandation;
	private ImageView imvLocationWorked;
	private ImageView imvWhyIMAsesome;
	
	private ImageView imvProfileUserImage;
	private ImageView imvIsInvited;
	private ImageView imvNotification;
	private TextView txtProfileUserName;
	private TextView txtProfileUserLocation;
	private TextView txtProfileHired;
	private TextView txtProfileNetwork;
	private TextView txtProfileJobs;
	private TextView txtWorkHistoryTitle;
	private TextView txtWorkHistoryUserName;
	private TextView txtWorkHistoryDate;
	private TextView txtWorkHistoryPrice;
	
	private TextView txtReqName;
	private TextView txtReqDesc;
	private ImageView imvReqImage;
	private TextView txtRRcmd;
	
	private TextView txtNoSkill;
	private TextView txtNoCMember;
	private TextView txtNoWorkHistory;
	private TextView txtNoRecommandation;
	private TextView txtNoPortfolio;
	
	private LinearLayout llWH;
	private LinearLayout llWhyIMAwesome;
	private LinearLayout llRecommandation;
	private ScrollView svProfile;
	
	private TextView txtProfileWhyIMAwesome;
	private RatingBar rBarProfileRating;
	
	private HListView HLVProfileSkills;
	private HListView HLVProfileConnectedMember;
	
	private Profile profile;
	private ProfileSkillAdapter profileSkillAdapter;
	private ProfileCMAdapter cmAdapter;
	private AppHeaderView appHeaderView;
	private boolean isDescribe = false;
	
	private Long userId;
	private boolean isViewProfile = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ed_user_profile);
		if(getIntent().getSerializableExtra(Constants.SERIALIZABLE_DATA) != null){
			userId = (Long) getIntent().getSerializableExtra(Constants.SERIALIZABLE_DATA);
			isViewProfile = true;
		}
		initialize();
	}
	
	private void initialize() {
		imvRating = (ImageView) findViewById(R.id.imvRating);
		imvPortfolio = (ImageView) findViewById(R.id.imvPortfolio);
		imvWorkHistory = (ImageView) findViewById(R.id.imvWorkHistory);
		imvRecommandation = (ImageView) findViewById(R.id.imvRecommandation);
		imvLocationWorked = (ImageView) findViewById(R.id.imvLocationWorked);
		imvWhyIMAsesome = (ImageView) findViewById(R.id.imvWhyIMAsesome);
		
		imvIsInvited = (ImageView) findViewById(R.id.imvIsInvited);
		imvNotification = (ImageView) findViewById(R.id.imvNotification);
		imvProfileUserImage = (ImageView) findViewById(R.id.imvProfileUserImage);
		txtProfileUserName = (TextView) findViewById(R.id.txtProfileUserName);
		txtProfileUserLocation = (TextView) findViewById(R.id.txtProfileUserLocation);
		txtProfileHired = (TextView) findViewById(R.id.txtProfileHired);
		txtProfileNetwork = (TextView) findViewById(R.id.txtProfileNetwork);
		txtProfileJobs = (TextView) findViewById(R.id.txtProfileJobs);
		
		txtReqName = (TextView) findViewById(R.id.txtReqName);
		txtReqDesc = (TextView) findViewById(R.id.txtReqDesc);
		imvReqImage = (ImageView) findViewById(R.id.imvReqImage);
		txtRRcmd = (TextView) findViewById(R.id.txtRRcmd);
		txtNoRecommandation = (TextView) findViewById(R.id.txtNoRecommandation);
		txtNoPortfolio = (TextView) findViewById(R.id.txtNoPortfolio);
		
		txtWorkHistoryTitle = (TextView) findViewById(R.id.txtWorkHistoryTitle);
		txtWorkHistoryUserName = (TextView) findViewById(R.id.txtWorkHistoryUserName);
		txtWorkHistoryDate = (TextView) findViewById(R.id.txtWorkHistoryDate);
		txtWorkHistoryPrice = (TextView) findViewById(R.id.txtWorkHistoryPrice);
		
		txtNoSkill = (TextView) findViewById(R.id.txtNoSkill);
		txtNoCMember = (TextView) findViewById(R.id.txtNoCMember);
		txtNoWorkHistory = (TextView) findViewById(R.id.txtNoWorkHistory);
		llWH = (LinearLayout) findViewById(R.id.llWH);
		llWhyIMAwesome = (LinearLayout) findViewById(R.id.llWhyIMAwesome);
		llRecommandation = (LinearLayout) findViewById(R.id.llRecommandation);
		svProfile = (ScrollView) findViewById(R.id.svProfile);
		
		txtProfileWhyIMAwesome = (TextView) findViewById(R.id.txtProfileWhyIMAwesome);
		rBarProfileRating = (RatingBar) findViewById(R.id.rBarProfileRating);
		HLVProfileSkills = (HListView) findViewById(R.id.HLVProfileSkills);
		HLVProfileConnectedMember = (HListView) findViewById(R.id.HLVProfileConnectedMember);
		
		setHeaderView();
		imvRating.setOnClickListener(this);
		imvPortfolio.setOnClickListener(this);
		imvWorkHistory.setOnClickListener(this);
		imvRecommandation.setOnClickListener(this);
		imvLocationWorked.setOnClickListener(this);
		imvWhyIMAsesome.setOnClickListener(this);
		txtRRcmd.setOnClickListener(this);
		
		loadProfileData();
		
		HLVProfileConnectedMember.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				/*TaskDoer taskDoer = (TaskDoer) adapter.getItemAtPosition(position);
				Util.showCenteredToast(getApplicationContext(), taskDoer.fullname);*/
			}
		});
	}

	private void setHeaderView() {
		appHeaderView = (AppHeaderView) findViewById(R.id.appHeaderView);
		if (isViewProfile) {
			appHeaderView.setHeaderContent(
					getResources().getDrawable(R.drawable.ic_back), "Profile", null, null, Gravity.LEFT);
		} else{
			appHeaderView.setHeaderContent(
					getResources().getDrawable(R.drawable.ic_back), getResources()
							.getString(R.string.my_Profile), null, null, Gravity.LEFT);
		}
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == appHeaderView.txtHLeft.getId()) {
			finishActivity();
			
		}else if (view.getId() == R.id.imvRating) {
			rating();
			
		}else if (view.getId() == R.id.imvWorkHistory) {
			workHistory();
			
		}else if (view.getId() == R.id.imvLocationWorked) {
			locationWorked();
			
		}else if (view.getId() == R.id.imvPortfolio) {
			portfolio();
			
		}else if (view.getId() == R.id.imvRecommandation) {
				requestRecommendataion();
		}else if (view.getId() == R.id.txtRRcmd) {
			if(Util.isDeviceOnline(getApplicationContext())){
				recommendataion();
			}else{
				Util.showCenteredToast(getApplicationContext(), getResources().getString(R.string.msg_Connect_internet));
			}
			
		}else if (view.getId() == R.id.imvWhyIMAsesome) {
			imvWhyIMAwesome();
			
		}
	}
	
	private void rating(){
		if (profile != null && profile.rating != null) {
			Bundle bundle = new  Bundle();
			RatingDialog dialog = new RatingDialog();
			bundle.putSerializable(Constants.SERIALIZABLE_DATA, profile.rating);
			bundle.putString("User_id", profile.user_id);
			dialog.setArguments(bundle);
			dialog.show(getSupportFragmentManager(), "rating_profile");
		}
	}
	
	private void workHistory(){
		if (profile != null && profile.work_history.size() > 0) {
			Bundle bundle = new  Bundle();
			WorkHistoryDailog dialog = new WorkHistoryDailog();
			bundle.putSerializable(Constants.SERIALIZABLE_DATA, profile.work_history);
			dialog.setArguments(bundle);
			dialog.show(getSupportFragmentManager(), "work_history_profile");
		}
	}
	
	private void locationWorked(){
		if (profile != null && profile.work_location.size() > 0) {
			if(Util.isDeviceOnline(getApplicationContext())){
				Bundle bundle = new  Bundle();
				LocationWorkedDialog dialog = new LocationWorkedDialog();
				bundle.putSerializable(Constants.SERIALIZABLE_DATA, profile.work_location);
				dialog.setArguments(bundle);
				dialog.show(getSupportFragmentManager(), "location_worked_profile");
			}else{
				Util.showCenteredToast(getApplicationContext(), getResources().getString(R.string.msg_Connect_internet));
			}
		}
	}
	
	private void portfolio(){
		if (profile != null && profile.portfolio != null) {
			Bundle bundle = new  Bundle();
			PortfolioDialog dialog = new PortfolioDialog();
			bundle.putSerializable(Constants.SERIALIZABLE_DATA, profile.portfolio);
			dialog.setArguments(bundle);
			dialog.show(getSupportFragmentManager(), "portfolio_profile");
		}
	}
	
	private void requestRecommendataion(){
		if (profile != null && profile.recommendation.size() > 0) {
			Bundle bundle = new  Bundle();
			ReqRcmdDialog dialog = new ReqRcmdDialog();
			bundle.putSerializable(Constants.SERIALIZABLE_DATA, profile.recommendation);
			bundle.putString(Profile.RECOMMANDATION_FLAG, Profile.RECOMMANDATION_FLAG);
			dialog.setArguments(bundle);
			dialog.show(getSupportFragmentManager(), "recommandation_more_profile");
		}
	}
	
	private void recommendataion(){
		Bundle bundle = new  Bundle();
		ReqRcmdDialog dialog = new ReqRcmdDialog();
		bundle.putString(Profile.RECOMMANDATION_FLAG, Profile.RECOMMANDATION_REQUEST_FLAG);
		dialog.setArguments(bundle);
		dialog.show(getSupportFragmentManager(), "recommandation_more_profile");
	}
	
	private void loadProfileData(){
		if (Util.isDeviceOnline(ProfileActivity.this)) {
			new ProfileDataTask().execute();
		}else{
			Util.showCenteredToast(ProfileActivity.this, getResources().getString(R.string.msg_Connect_internet));
		}
	}

	//----------------TASK TO GET PROFILE DATA----------------------//
	private class ProfileDataTask extends AsyncTask<String, Void, NetworkResponse> {
		
		@Override
		protected void onPreExecute() {
			Util.showProDialog(ProfileActivity.this);
		}
		
		@Override
		protected NetworkResponse doInBackground(String... params) {
			NetworkResponse response = null; 
			Cmd cmd = CmdFactory.createGetProfileCmd();
			if(isViewProfile){
				cmd.addData(Profile.FLD_VIEW_USER_ID, userId);
			}else{
				cmd.addData(Profile.FLD_VIEW_USER_ID, AppGlobals.userDetail.user_id);
			}
			cmd.addData(Profile.FLD_TRNO, 0);
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
					Util.showCenteredToast(ProfileActivity.this, networkResponse.getErrMsg());
				}else{
					setProfileData();
				}
			}
		}
	}
	
	private void getProfileData(NetworkResponse response){
		ProfileData profileData = (ProfileData) Util.getJsonToClassObject(response.getJsonObject().toString(), ProfileData.class);
		if (profileData != null) {
			profile =  profileData.data;
		}
	}
	
	private void setProfileData(){
		if (profile != null) {
			Util.loadImage(imvProfileUserImage,profile.userimage, R.drawable.ic_launcher);
			txtProfileUserName.setText(profile.fullname);
			txtProfileHired.setText(profile.hired_by_me_count);
			txtProfileJobs.setText(profile.task_done_cnt);
			txtProfileNetwork.setText(profile.network_count);
			rBarProfileRating.setRating(Float.parseFloat(profile.rating.overall_rating));
			
			//user Default Location. 
			if (AppGlobals.defaultWLocation != null && AppGlobals.defaultWLocation.address != null) {
				txtProfileUserLocation.setText(profile.work_location_name);
			} else{
				txtProfileUserLocation.setVisibility(View.INVISIBLE);
			}
			
			//hired Count 
			if (Integer.parseInt(profile.hired_by_me_count) > 0) {
				imvIsInvited.setImageResource(R.drawable.ic_hired_yellow);
			}else{
				imvIsInvited.setImageResource(R.drawable.ic_hired_gry);
			}
			
			//IsInvited 
			if (profile.is_invited_by_me.equals("true")) {
				imvNotification.setImageResource(R.drawable.ic_notification_yellow);
			}else{
				imvNotification.setImageResource(R.drawable.ic_notification_gry);
			}
			
			//portfolio Section
			if (profile.portfolio.category.size() > 0 ) {
				imvPortfolio.setVisibility(View.VISIBLE);
				txtNoPortfolio.setVisibility(View.GONE);
			}else{
				imvPortfolio.setVisibility(View.GONE);
				txtNoPortfolio.setVisibility(View.VISIBLE);
				txtNoPortfolio.setText(getResources().getString(R.string.no_project_completed));
			}
			
			//skill Section
			if (profile.multiskills.size() > 0) {
				profileSkillAdapter = new ProfileSkillAdapter(ProfileActivity.this, R.layout.ed_profile_skill_row, profile.multiskills);
				HLVProfileSkills.setAdapter(profileSkillAdapter);
			}else{
				HLVProfileSkills.setVisibility(View.GONE);
				txtNoSkill.setVisibility(View.VISIBLE);
				txtNoSkill.setText(getResources().getString(R.string.no_skill_specified));
			}
			
			//member Connected Section
			if (profile.network_members.size() > 0) {
				cmAdapter = new ProfileCMAdapter(ProfileActivity.this, R.layout.ed_profile_connected_member, profile.network_members);
				HLVProfileConnectedMember.setAdapter(cmAdapter);
			}else{
				HLVProfileConnectedMember.setVisibility(View.GONE);
				txtNoCMember.setVisibility(View.VISIBLE);
				if (isViewProfile) {
					txtNoCMember.setText(getResources().getString(R.string.no_member_connected_other));
				} else{
					txtNoCMember.setText(getResources().getString(R.string.no_member_connected));
				}
			}
			
			//worked Location Section
			if (profile.work_location.size() == 0) {
				imvLocationWorked.setVisibility(View.GONE);
			}
			
			//work History Section
			if (profile.work_history.size() > 0) {
				txtWorkHistoryTitle.setText(profile.work_history.get(0).title);
				txtWorkHistoryUserName.setText(profile.work_history.get(0).fullname);
				txtWorkHistoryDate.setText(profile.work_history.get(0).task_date);
				txtWorkHistoryPrice.setText("$"+profile.work_history.get(0).price);
			}else{
				imvWorkHistory.setVisibility(View.GONE);
				llWH.setVisibility(View.GONE);
				txtWorkHistoryTitle.setVisibility(View.GONE);
				txtNoWorkHistory.setVisibility(View.VISIBLE);
				txtNoWorkHistory.setText(getResources().getString(R.string.no_work_history));
			}
			
			//recommendation more section.
			if (profile.recommendation.size() > 0) {
				imvRecommandation.setVisibility(View.VISIBLE);
				txtReqDesc.setText(profile.recommendation.get(0).recommendation_desc);
				txtReqName.setText(profile.recommendation.get(0).fullname);
				Util.loadImage(imvReqImage,profile.recommendation.get(0).userimage, R.drawable.ic_launcher);
			}else{
				imvRecommandation.setVisibility(View.GONE);
				llRecommandation.setVisibility(View.GONE);
				txtNoRecommandation.setVisibility(View.VISIBLE);
				txtNoRecommandation.setText(getResources().getString(R.string.no_recommendation));
			}
			
			if(isViewProfile){
				txtRRcmd.setVisibility(View.GONE);
			}
			
			//why I'M Awesome Section
			txtProfileWhyIMAwesome.setMaxLines(1);
			if (profile.about_me.length() > 0) {
				txtProfileWhyIMAwesome.setText(profile.about_me);
			}else{
				llWhyIMAwesome.setVisibility(View.GONE);
			}
		}
	}
	
	private void imvWhyIMAwesome(){
		if (profile != null && profile.about_me.length() > 0) {
			if (isDescribe) {
				isDescribe = false;
				txtProfileWhyIMAwesome.setMaxLines(Integer.MAX_VALUE);
				LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				params.setMargins(8, 8, 8, 8);
				txtProfileWhyIMAwesome.setLayoutParams(params);
			}else{
				isDescribe = true;
				txtProfileWhyIMAwesome.setMaxLines(1);
				LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				params.setMargins(8, 8, 8, 8);
				txtProfileWhyIMAwesome.setLayoutParams(params);
				autoScrollView();
			}
		}
	}
	
	private void autoScrollView(){
		svProfile.post(new Runnable() {
			@Override
			public void run() {
				svProfile.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		finishActivity();
	}
	
	private void finishActivity(){
		ProfileActivity.this.finish();
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}
} 