package erandoo.app.main;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.adapters.RatingAdapter;
import erandoo.app.cmds.Cmd;
import erandoo.app.cmds.CmdFactory;
import erandoo.app.config.Config;
import erandoo.app.custom.AppHeaderView;
import erandoo.app.custom.BaseFragActivity;
import erandoo.app.database.Project;
import erandoo.app.database.Rating;
import erandoo.app.database.TaskDoer;
import erandoo.app.network.NetworkMgr;
import erandoo.app.network.NetworkResponse;
import erandoo.app.tempclasses.ProfileRatingData;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.SyncAppData;
import erandoo.app.utilities.Util;

public class RatingActivity extends BaseFragActivity implements OnClickListener {

	private TextView txtRTitle;
	private TextView txtRStartDate;
	private TextView txtRPostedDate;
	private TextView txtRBudget;
	private TextView txtRUserName;
	private static RatingBar rtPCOverall;
	
	private TextView txtRExpUsername;
	
	private ImageView imvUserImage;

	private Button btnRSubmit;
	private RatingAdapter ratingAdapter;

	private ListView listRating;
	private ProfileRatingData profileRatingData;

	private AppHeaderView appHeaderView;
	private static ArrayList<Rating> ratingData = new ArrayList<Rating>();
	
	private Project project;
	static String overallRating = "";
	private JSONArray arr;
	private boolean isArray = false;
	private String isPoster;
	private TaskDoer doer;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.ed_rating);
		project = (Project) getIntent().getSerializableExtra(Constants.SERIALIZABLE_DATA);
		isPoster = (String) getIntent().getSerializableExtra(Constants.MY_PROJECT_AS_POSTER);
		if(isPoster.equals(Constants.MY_PROJECT_AS_POSTER)){
			doer = (TaskDoer) getIntent().getSerializableExtra("Doer");
		}
		initialize();
	}

	private void initialize() {
		txtRTitle = (TextView) findViewById(R.id.txtRTitle);
		txtRStartDate = (TextView) findViewById(R.id.txtRStartDate);
		txtRPostedDate = (TextView) findViewById(R.id.txtRPostedDate);
		txtRBudget = (TextView) findViewById(R.id.txtRBudget);
		btnRSubmit = (Button) findViewById(R.id.btnRSubmit);
		txtRUserName = (TextView) findViewById(R.id.txtRUserName);
		listRating = (ListView) findViewById(R.id.listRating);
		rtPCOverall = (RatingBar) findViewById(R.id.rtPCOverall);
		txtRExpUsername = (TextView) findViewById(R.id.txtRExpUsername);
		imvUserImage = (ImageView) findViewById(R.id.imvUserImage);
		
		btnRSubmit.setOnClickListener(this);
		setHeaderView();
		loadData();
		setData();
	}

	private void loadData() {
		if (Util.isDeviceOnline(this)) {
			new RatingTask().execute();
		} else {
			Util.showCenteredToast(this, getResources().getString(R.string.msg_Connect_internet));
		}
	}

	private class RatingTask extends AsyncTask<String, Void, NetworkResponse> {

		@Override
		protected void onPreExecute() {
			ratingData.clear();
			Util.showProDialog(RatingActivity.this);
		}

		@Override
		protected NetworkResponse doInBackground(String... params) {
			Cmd cmd = CmdFactory.getRating();
			if (isPoster.equals(Constants.MY_PROJECT_AS_POSTER)) {
				cmd.addData("rating_for", "doer");
			} else {
				cmd.addData("rating_for", "poster");
			}
			
			NetworkResponse response = NetworkMgr.httpPost(Config.API_URL,
					cmd.getJsonData());
			if (response != null) {
				if (response.isSuccess()) {
					getRatingData(response);
				}
			}
			return response;
		}

		protected void onPostExecute(NetworkResponse response) {
			Util.dimissProDialog();
			setRatingData(profileRatingData);
		}
	}

	private void getRatingData(NetworkResponse response) {

		try {
			ProfileRatingData prcData = (ProfileRatingData) Util
					.getJsonToClassObject(response.getJsonObject().toString(),
							ProfileRatingData.class);
			if (prcData != null) {
				for (Rating rating : prcData.data) {			
					ratingData.add(rating);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setRatingData(ProfileRatingData prdata) {
		// if (prdata.category_rating.size() > 0) {
		ratingAdapter = new RatingAdapter(this, R.layout.ed_rating_row,
				ratingData, false, true);
		listRating.setAdapter(ratingAdapter);
	}

	public static void setOverallRating(float rating, ArrayList<Rating> data) {
		overallRating = String.valueOf(rating);
		ratingData = data;
		rtPCOverall.setRating(rating);
	}

	private void setHeaderView() {
		appHeaderView = (AppHeaderView) findViewById(R.id.appHeaderView);
		appHeaderView.setHeaderContent(
				getResources().getDrawable(R.drawable.ic_back), getResources()
						.getString(R.string.rating), null, null,
				Gravity.LEFT);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == appHeaderView.txtHLeft.getId()) {
			finishActivity();
		} else if (v.getId() == R.id.btnRSubmit) {
			UploadCompleteData();
		}
	}
	
	private void UploadCompleteData(){
		try {
			arr = new JSONArray();
			JSONObject object = new JSONObject();
			for(Rating rating : ratingData){
				if(rating.rating != null){
					isArray = true;
					object.put(rating.rating_id, rating.rating);
				}
			}
			arr.put(object);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(Util.isDeviceOnline(this)){
			new ProjectCompleteTask().execute();
		} else {
			Util.showCenteredToast(this, getResources().getString(R.string.msg_Connect_internet));
		}
	}
	
	private class ProjectCompleteTask extends AsyncTask<String, Void, NetworkResponse> {

		@Override
		protected void onPreExecute() {
			Util.showProDialog(RatingActivity.this);
		}
		
		@Override
		protected NetworkResponse doInBackground(String... params) {
			Cmd cmd = null;
			if(isPoster.equals(Constants.MY_PROJECT_AS_POSTER)){
				cmd = CmdFactory.getProjectCompletePoster();
				cmd.addData(TaskDoer.FLD_TASK_TASKER_ID, doer.task_tasker_id);
			}else{
				cmd = CmdFactory.getProjectCompleteDoer();
				cmd.addData(TaskDoer.FLD_TASK_TASKER_ID, project.task_tasker_id);
			}
			
			if(overallRating != null){
				cmd.addData("total_rating", overallRating);
			}else{
				cmd.addData("total_rating", "");
			}
			if(!isArray){
				cmd.addData("all_rating", "");
			}else{
				cmd.addData("all_rating", arr);
			}
			
			NetworkResponse response = NetworkMgr.httpPost(Config.API_URL,
					cmd.getJsonData());
			SyncAppData.syncProjectData(RatingActivity.this);
			return response;
		}
		
		@Override
		protected void onPostExecute(NetworkResponse networkResponse) {
			Util.dimissProDialog();
			if (networkResponse != null) {
				if (networkResponse.isSuccess()){
					if(isPoster.equals(Constants.MY_PROJECT_AS_DOER)){
						setResult(RESULT_OK);
					}
					finishActivity();
				}else{
					Util.showCenteredToast(RatingActivity.this, networkResponse.getErrMsg()); 
				}
			}
		}
	}
	
	private void setData(){
		txtRTitle.setText(project.title);
		txtRStartDate.setText(Util.setProjectDateWithMonth(project.project_start_date, false));
		txtRPostedDate.setText(Util.setProjectDateWithMonth(project.created_at, false));
		txtRBudget.setText(Util.getBudgetString(project));
		if(isPoster.equals(Constants.MY_PROJECT_AS_POSTER)){
			Util.loadImage(imvUserImage, doer.userimage, R.drawable.ic_launcher);
			txtRUserName.setText(doer.fullname);
			txtRExpUsername.setText("Rate Your Experience with " + doer.fullname);
		}else{
			txtRUserName.setText(project.name);
			Util.loadImage(imvUserImage, project.userimage, R.drawable.ic_launcher);
			txtRExpUsername.setText("Rate Your Experience with " + project.name);
		}
		
	}

	@Override
	public void onBackPressed() {
		finishActivity();
	}

	private void finishActivity() {
		RatingActivity.this.finish();
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}
}
