package erandoo.app.main;

import java.util.ArrayList;

import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.adapters.AttachmentsAdapter;
import erandoo.app.cmds.Cmd;
import erandoo.app.cmds.CmdFactory;
import erandoo.app.custom.AppHeaderView;
import erandoo.app.custom.BaseFragActivity;
import erandoo.app.custom.HListView;
import erandoo.app.custom.IDataChangedListener;
import erandoo.app.database.Attachment;
import erandoo.app.database.Project;
import erandoo.app.database.Proposal;
import erandoo.app.database.Question;
import erandoo.app.network.NetworkMgr;
import erandoo.app.network.NetworkResponse;
import erandoo.app.projects.ProjectAwardAcceptDialog;
import erandoo.app.projects.QuestionAnswerDialog;
import erandoo.app.tempclasses.ProposalData;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.DownloadHelper;
import erandoo.app.utilities.SyncAppData;
import erandoo.app.utilities.Util;

public class ProposalDetailActivity extends BaseFragActivity implements
		OnClickListener {
	/* Poster Side */
	private ImageView imvPRDtImage;
	private TextView txtPRDtName;
	private TextView txtPRDtLocation;
	private TextView txtPRDtHired;
	private TextView txtPRDtNetwork;
	private TextView txtPRDtJobs;
	private RatingBar rBarPRDtDRating;

	/* Doer Side */
	private TextView txtPRDtTitle;
	private TextView txtPRDtBudget;
	private TextView txtPRDtPostedDate;
	private TextView txtPRDtStartDate;
	private TextView txtPRDtType;
	private TextView txtPRDtTotalProposal;
	private RatingBar rBarPRDtRating;
	private TextView txtPRDtAveragePrice;

	/* Common */
	private TextView txtMyPay;
	private TextView txtPRDtReadMore;
	private TextView txtPRDtDesc;
	private TextView txtPRDtEdit;

	private LinearLayout llPosterProposal;
	private LinearLayout llDoerProposal;
	private TextView txtPRDtQuestion;

	private Proposal proposal;
	private Project projectData;
	private String viewProposalAs;
	private String trno = "0";
    public final int BID_REQUEST_CODE = 111;

    ArrayList<Question> questionPro = new ArrayList<Question>();
	ArrayList<Question> questionDetail = new ArrayList<Question>();
	
	private AppHeaderView appHeaderView;
	private HListView hListViewPRD;
	private LinearLayout llHlistView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ed_proposal_details);

		initialize();
	}

	public void initialize() {
		viewProposalAs = getIntent().getStringExtra(Constants.MY_PROJECT_AS_POSTER);
		imvPRDtImage = (ImageView) findViewById(R.id.imvPRDtImage);
		txtPRDtName = (TextView) findViewById(R.id.txtPRDtName);
		txtPRDtLocation = (TextView) findViewById(R.id.txtPRDtLocation);
		txtPRDtHired = (TextView) findViewById(R.id.txtPRDtHired);
		txtPRDtNetwork = (TextView) findViewById(R.id.txtPRDtNetwork);
		txtPRDtDesc = (TextView) findViewById(R.id.txtPRDtDesc);
		txtPRDtJobs = (TextView) findViewById(R.id.txtPRDtJobs);
		txtPRDtReadMore = (TextView) findViewById(R.id.txtPRDtReadMore);
		rBarPRDtDRating = (RatingBar) findViewById(R.id.rBarPRDtDRating);
		txtMyPay = (TextView) findViewById(R.id.txtMyPay);

		/* Doer Side */
		txtPRDtTitle = (TextView) findViewById(R.id.txtPRDtTitle);
		txtPRDtBudget = (TextView) findViewById(R.id.txtPRDtBudget);
		txtPRDtPostedDate = (TextView) findViewById(R.id.txtPRDtPostedDate);
		txtPRDtStartDate = (TextView) findViewById(R.id.txtPRDtStartDate);
		txtPRDtType = (TextView) findViewById(R.id.txtPRDtType);
		txtPRDtTotalProposal = (TextView) findViewById(R.id.txtPRDtTotalProposal);
		txtPRDtAveragePrice = (TextView) findViewById(R.id.txtPRDtAveragePrice);
		rBarPRDtRating = (RatingBar) findViewById(R.id.rBarPRDtRating);

		txtPRDtEdit = (TextView) findViewById(R.id.txtPRDtEdit);
		llPosterProposal = (LinearLayout) findViewById(R.id.llPosterProposal);
		llDoerProposal = (LinearLayout) findViewById(R.id.llDoerProposal);
		txtPRDtQuestion = (TextView) findViewById(R.id.txtPRDtQuestion);
		
		hListViewPRD = (HListView) findViewById(R.id.hListViewPRD);
		llHlistView = (LinearLayout) findViewById(R.id.llHlistView);

		setHeaderView();
		
		txtPRDtDesc.setMaxLines(1);
		txtPRDtEdit.setOnClickListener(this);
		txtPRDtReadMore.setOnClickListener(this);
		txtPRDtQuestion.setOnClickListener(this);
		
		loadProposalData(false);
		hListViewPRD.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,long id) {
				Attachment file = (Attachment) adapter.getItemAtPosition(position);
				DownloadHelper.downloadFile(ProposalDetailActivity.this, file.file_url, file.file_name); 
			}
		});
		
	}
	
	private void setHeaderView() {
		appHeaderView = (AppHeaderView) findViewById(R.id.appHeaderView);
		appHeaderView.setHeaderContent(
				getResources().getDrawable(R.drawable.ic_back), getResources()
						.getString(R.string.Proposal_details), null, null, Gravity.LEFT);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.txtPRDtReadMore) {
			readMoreAction();
		} else if (v.getId() == R.id.txtPRDtEdit) {
			goToEditProposal();
		} else if(v.getId() == appHeaderView.txtHLeft.getId()){
			//TODO back button
			finishActivity();
		}else if(v.getId() == R.id.txtPRDtQuestion){
			//TODO back button
			goToQuestion();
		}
	}
	
	
	private void goToQuestion(){
		if(proposal.questions.size() != 0 ){
			QuestionAnswerDialog dialog = new QuestionAnswerDialog();
			Bundle bundle = new Bundle();
			bundle.putSerializable(Constants.SERIALIZABLE_DATA, proposal.questions);
			bundle.putString(Constants.PARENT_VIEW, "proposalDetail");
			dialog.setArguments(bundle);
			dialog.show(getSupportFragmentManager(), "question");
		}else{
			Util.showCenteredToast(this, getResources().getString(R.string.noQuestions));
		}
	}

	private void goToEditProposal(){
		proposal.projectData = projectData;
		if(txtPRDtEdit.getTag().equals("modified")){
			ProjectAwardAcceptDialog dialog = ProjectAwardAcceptDialog.getInstance(new IDataChangedListener() {
				@Override
				public void onDataChanged() {
					loadProposalData(true);
				}
			});
			Bundle bundle = new Bundle();
			bundle.putSerializable(Constants.SERIALIZABLE_DATA, proposal);
			dialog.setArguments(bundle);
			dialog.show(getSupportFragmentManager(), "hire");
		}else{
			proposal.viewMode = "edit";
			Intent intent = new Intent(ProposalDetailActivity.this,CraftABidActivity.class);
			intent.putExtra(Constants.SERIALIZABLE_DATA, proposal);
			startActivityForResult(intent, BID_REQUEST_CODE);
			overridePendingTransition(R.anim.right_in, R.anim.left_out);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK && requestCode == BID_REQUEST_CODE){
			loadProposalData(true);
		}
	}

	private void loadProposalData(Boolean isProjectUpdated) {
		if (viewProposalAs.equals(Constants.MY_PROJECT_AS_POSTER)) {
			llPosterProposal.setVisibility(View.VISIBLE);
			llDoerProposal.setVisibility(View.GONE);
			txtPRDtEdit.setVisibility(View.GONE);
			proposal = (Proposal) getIntent().getSerializableExtra(Constants.SERIALIZABLE_DATA);
			setProposalData();
			setReadMoreBtnVisibility();
		} else {
			llPosterProposal.setVisibility(View.GONE);
			llDoerProposal.setVisibility(View.VISIBLE);
			txtPRDtEdit.setVisibility(View.VISIBLE);
			projectData = (Project) getIntent().getSerializableExtra(Constants.SERIALIZABLE_DATA);
			if (Util.isDeviceOnline(this)) {
				new ProposalDetailTask().execute(new Boolean[]{isProjectUpdated});
			} else {
				Util.showCenteredToast(this,getResources().getString(R.string.msg_Connect_internet));
			}
		}
	}

	private class ProposalDetailTask extends AsyncTask<Boolean, Void, NetworkResponse> {

		@Override
		protected void onPreExecute() {
			Util.showProDialog(ProposalDetailActivity.this);
		}

		@Override
		protected NetworkResponse doInBackground(Boolean... params) {
			NetworkResponse response = null;
			proposal = null;
			if(params[0].equals(true)){ 
				SyncAppData.syncProjectData(ProposalDetailActivity.this);
			}
			
			Cmd cmd = CmdFactory.getViewProposalCmd();
			cmd.addData(Project.FLD_TRNO, trno);
			cmd.addData("proposals_as", "tasker");
			cmd.addData(Project.FLD_TASK_ID, projectData.task_id);
			cmd.addData("tasker_id", AppGlobals.userDetail.user_id);
			response = NetworkMgr.httpPost(erandoo.app.config.Config.API_URL, cmd.getJsonData());
			if (response != null) {
				if (response.isSuccess()) {
					getviewProposalData(response);
				}
			}
			return response;
		}

		@Override
		protected void onPostExecute(NetworkResponse networkResponse) {
			Util.dimissProDialog();
			if (networkResponse != null) {
				if (!networkResponse.isSuccess()) {
					Util.showCenteredToast(ProposalDetailActivity.this,networkResponse.getErrMsg());
					txtPRDtEdit.setVisibility(View.GONE);
				}else{
					setProposalData();
					setReadMoreBtnVisibility();
				}
			}
		}
	}

	private void getviewProposalData(NetworkResponse response) {
		try {
			ProposalData proposalData = (ProposalData) Util.getJsonToClassObject(response.getJsonObject().toString(),ProposalData.class);
			proposal = proposalData.data.get(0); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setProposalData() {
		if (viewProposalAs.equals(Constants.MY_PROJECT_AS_POSTER)) {
			txtPRDtName.setText(proposal.task_doer.fullname);
			txtPRDtHired.setText(proposal.task_doer.hired);
			txtPRDtNetwork.setText(proposal.task_doer.network);
			txtPRDtJobs.setText(proposal.task_doer.task_done_cnt);
			txtPRDtLocation.setText(proposal.task_doer.work_location);
			rBarPRDtDRating.setRating(Float
					.parseFloat(proposal.task_doer.rating_avg_as_tasker));
			Util.loadImage(imvPRDtImage, proposal.task_doer.userimage,
					R.drawable.ic_launcher);
			
			if(proposal.projectData.payment_mode.equals("h")){
				txtMyPay.setText("$" + proposal.poster_pay + " (" +getResources().getString(R.string.hourly)+")");
			}else{
				txtMyPay.setText("$" + proposal.poster_pay + " (" +getResources().getString(R.string.fixed)+")");
			}
		} else {
			txtPRDtType.setText(Util.getProjectTypeName(projectData.task_kind)); 
			txtPRDtTitle.setText(projectData.title);
			txtPRDtPostedDate.setText(projectData.created_at);
			txtPRDtStartDate.setText(projectData.bid_close_dt);
			txtPRDtTotalProposal.setText(projectData.proposals_cnt);
			txtPRDtAveragePrice.setText("$" + projectData.proposals_avg_price);
			rBarPRDtRating.setRating(Util.toFloat(projectData.average_rating));
			txtPRDtBudget.setText(Util.getBudgetString(projectData));
			txtPRDtEdit.setTag("edit"); 
			
			if(proposal.status.equals("h")){
				txtPRDtEdit.setText(getResources().getString(R.string.accept));
				txtPRDtEdit.setTag("modified"); 
			}else if(proposal.status.equals("s")){
				txtPRDtEdit.setVisibility(View.GONE);
			}else if(proposal.status.equals("r")){
				txtPRDtEdit.setVisibility(View.GONE);
			}
			
			if(projectData.payment_mode.equals("h")){
				txtMyPay.setText("$" + proposal.my_pay + " "+ getResources().getString(R.string.hourly));
			}else{
				txtMyPay.setText("$" + proposal.my_pay + " "+ getResources().getString(R.string.fixed));
			}
			
		}

		if(proposal.attachments.size() > 0 ){
			llHlistView.setVisibility(View.VISIBLE);
			AttachmentsAdapter adapter  = new AttachmentsAdapter(this, R.layout.ed_attach_list_row, proposal.attachments);
			hListViewPRD.setAdapter(adapter);
		}

		txtPRDtDesc.setText(proposal.tasker_comments);
	}

	private void readMoreAction() {

		if (txtPRDtReadMore.getText().toString()
				.equals(getResources().getString(R.string.read_more))) {
			txtPRDtDesc.setMaxLines(Integer.MAX_VALUE);
			LinearLayout.LayoutParams Params1 = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			Params1.setMargins(0, 0, 0, 0);
			txtPRDtDesc.setLayoutParams(Params1);
			txtPRDtReadMore.setText(getResources()
					.getString(R.string.read_less));

		} else if (txtPRDtReadMore.getText().toString()
				.equals(getResources().getString(R.string.read_less))) {
			txtPRDtDesc.setMaxLines(1);
			LinearLayout.LayoutParams Params1 = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			Params1.setMargins(0, 0, 0, 0);
			txtPRDtDesc.setLayoutParams(Params1);
			txtPRDtReadMore.setText(getResources()
					.getString(R.string.read_more));
		}
	}

	private void setReadMoreBtnVisibility() {
		txtPRDtDesc.post(new Runnable() {
			@Override
			public void run() {
				if (txtPRDtDesc.getLineCount() == 1) {
					txtPRDtReadMore.setVisibility(View.GONE);
				} else {
					txtPRDtReadMore.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	@Override
	public void onBackPressed() {
		finishActivity();
	}

	private void finishActivity() {
		ProposalDetailActivity.this.finish();
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}

}
