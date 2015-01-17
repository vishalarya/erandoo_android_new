package erandoo.app.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.custom.AppHeaderView;
import erandoo.app.custom.BaseFragActivity;
import erandoo.app.database.Project;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.Util;

public class ProjectCompleteActivity extends BaseFragActivity implements
		OnClickListener {
	private TextView txtPCtitle;
	private TextView txtCompleteInitial;
	private TextView txtPCStartDate;
	private TextView txtPCBudget;
	private TextView txtPCPostedDate;
	private ImageView imvUserImage;
	private TextView txtUsername;
	private Project projectComplete;
	private AppHeaderView appHeaderView;

	Intent intent = new Intent();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ed_complete);
		projectComplete = (Project) getIntent().getSerializableExtra(Constants.SERIALIZABLE_DATA);
		initialize();
	}

	private void initialize() {
		txtPCtitle = (TextView) findViewById(R.id.txtPCtitle);
		txtPCStartDate = (TextView) findViewById(R.id.txtPCStartDate);
		txtPCBudget = (TextView) findViewById(R.id.txtPCBudget);
		txtPCPostedDate = (TextView) findViewById(R.id.txtPCPostedDate);
		txtCompleteInitial = (TextView) findViewById(R.id.txtCompleteInitial);
		imvUserImage = (ImageView) findViewById(R.id.imvUserImage);
		txtUsername = (TextView) findViewById(R.id.txtUsername);

		txtCompleteInitial.setOnClickListener(this);
		setHeaderView();
		setData();
	}
	
	private void setHeaderView() {
		appHeaderView = (AppHeaderView) findViewById(R.id.appHeaderView);
		appHeaderView.setHeaderContent(
				getResources().getDrawable(R.drawable.ic_back), getResources()
						.getString(R.string.Project_Complete), null, null, Gravity.LEFT);
	}

	@Override
	public void onBackPressed() {
		finishActivity();
	}

	private void finishActivity() {
		ProjectCompleteActivity.this.finish();
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.txtCompleteInitial) {
			intent.setClass(ProjectCompleteActivity.this, RatingActivity.class);
			intent.putExtra(Constants.SERIALIZABLE_DATA, projectComplete);
			intent.putExtra(Constants.MY_PROJECT_AS_POSTER, Constants.MY_PROJECT_AS_DOER);
			startActivityForResult(intent, 1);
			overridePendingTransition(R.anim.right_in, R.anim.left_out);
		} else if (v.getId() == appHeaderView.txtHLeft.getId()) {
			finishActivity();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK && requestCode == 1){
			setResult(1); 
			finishActivity();
		}
	}

	private void setData() {
		txtPCtitle.setText(projectComplete.title);
		txtPCPostedDate.setText(Util.setProjectDateWithMonth(projectComplete.created_at, false));
		txtPCBudget.setText(Util.getBudgetString(projectComplete));
		txtPCStartDate.setText(Util.setProjectDateWithMonth(projectComplete.project_start_date, false));
		txtUsername.setText(projectComplete.name);
		Util.loadImage(imvUserImage, projectComplete.userimage, R.drawable.ic_launcher);
	}

}
