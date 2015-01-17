package erandoo.app.main;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.adapters.AttachmentsAdapter;
import erandoo.app.adapters.SelectDoerAdapter;
import erandoo.app.custom.AppHeaderView;
import erandoo.app.custom.BaseFragActivity;
import erandoo.app.custom.HListView;
import erandoo.app.custom.IDataChangedListener;
import erandoo.app.custom.IVCheckListener;
import erandoo.app.database.Attachment;
import erandoo.app.database.Country;
import erandoo.app.database.DatabaseMgr;
import erandoo.app.database.MessageDetail;
import erandoo.app.database.Profile;
import erandoo.app.database.Project;
import erandoo.app.database.Proposal;
import erandoo.app.database.Skill;
import erandoo.app.database.TableType;
import erandoo.app.database.TaskDoer;
import erandoo.app.main.message.MessageDetailActivity;
import erandoo.app.projects.CancelProjectDialog;
import erandoo.app.projects.CancelResponseDialog;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.DownloadHelper;
import erandoo.app.utilities.LicenseValidation;
import erandoo.app.utilities.Util;

public class ProjectDetailsActivity extends BaseFragActivity implements OnClickListener {
	
	//for multiple doers at same project
	private SelectDoerAdapter adapter;
	private TaskDoer taskDoer;
	
	private TextView txtSelectUser;
	private ArrayList<TaskDoer> doersList;
	
	//Custom Dialog Components.
	private LinearLayout llSListSearchBar;
	private TextView txtSimpleListTitle;
	private ImageButton imgBtnSimpleListBack;
	private ListView listVSimpleList;
	private Dialog customDialog;
	
	private TextView txtPDProjectTitle;
	private TextView txtPDPostedBy;
	private TextView txtPDPostedDate;
	private TextView txtPDStartDate;
	private TextView txtPDCategory;
	private TextView txtPDType;
	private TextView txtPDLocation;
	private TextView txtPDSkills;
    private TextView txtPDCancel;
	private TextView txtPLPaymentMode;
	
	private TextView txtPDInvoice;
	private TextView txtPDPayment;
	private TextView txtPDProposal;
	//private TextView txtPDFiles;
	private TextView txtPDComplete;
	
	private TextView txtPDProposalCount;
	private TextView txtPDAveragePrice;

	private TextView txtPDPDesc;
	private TextView txtReadMore;

	private Button btnPDApply;

	private ImageView imvPDSave;
	private ImageView imvPDEdit;
	//private ImageView imvPDShare;

	private LinearLayout llPDBtn;
	private LinearLayout llPDpostedby;
	private RelativeLayout rlPDFooter;

	private Project projectDetails;
    private String parentView;
    public static String projectAs;
    private DatabaseMgr databaseMgr;
    private TaskDoer taskTasker = null;
    private final String PROJECT_AWARDED = "a";
    private final String PROJECT_SELECTED = "s";
    private final String SELECTION_TYPE_BID = "bid";
    public final int BID_REQUEST_CODE = 111;
    private final int CANCEL_RESULT_CODE = 111;
	private final int BID_RESULT_CODE = 112;
    private Long userId;
	private AppHeaderView appHeaderView;
	
	private static Long user_id;
	private HListView hPDListView;
	private LinearLayout llPDHlistView;
	
	//public static Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ed_project_details);

		parentView = getIntent().getStringExtra(Constants.PARENT_VIEW);
		projectDetails = (Project)getIntent().getSerializableExtra(Constants.SERIALIZABLE_DATA);

		initialize();
	}

	private void initialize() {
		userId =  AppGlobals.userDetail.user_id;
		databaseMgr = DatabaseMgr.getInstance(this);
		txtPDProjectTitle = (TextView) findViewById(R.id.txtPDProjectTitle);
		txtPDPostedBy = (TextView) findViewById(R.id.txtPDPostedBy);
		txtPDPostedDate = (TextView) findViewById(R.id.txtPDPostedDate);
		txtPDStartDate = (TextView) findViewById(R.id.txtPDStartDate);
		txtPDCategory = (TextView) findViewById(R.id.txtPDCategory);
		txtPDType = (TextView) findViewById(R.id.txtPDType);
		txtPDLocation = (TextView) findViewById(R.id.txtPDLocation);
		txtPDSkills = (TextView)findViewById(R.id.txtPDSkills);
		txtPDProposalCount = (TextView) findViewById(R.id.txtPDProposalCount);
		txtPDAveragePrice = (TextView) findViewById(R.id.txtPDAveragePrice);
		txtPDPDesc = (TextView) findViewById(R.id.txtPDPDesc);
		txtPDCancel = (TextView)findViewById(R.id.txtPDCancel);
		txtReadMore = (TextView) findViewById(R.id.txtReadMore);
		//txtPDFiles = (TextView) findViewById(R.id.txtPDFiles);
		txtPDProposal = (TextView) findViewById(R.id.txtPDProposal);
		txtPDInvoice = (TextView) findViewById(R.id.txtPDInvoice);
		txtPDPayment = (TextView) findViewById(R.id.txtPDPayment);
		txtPDComplete = (TextView) findViewById(R.id.txtPDComplete);
		
		btnPDApply = (Button) findViewById(R.id.btnPDApply);
		imvPDSave = (ImageView) findViewById(R.id.imvPDSave);
		imvPDEdit = (ImageView)findViewById(R.id.imvPDEdit);
		//imvPDShare = (ImageView) findViewById(R.id.imvPDShare);
		txtPLPaymentMode = (TextView) findViewById(R.id.txtPLPaymentMode);
		llPDBtn = (LinearLayout) findViewById(R.id.llPDBtn);
		llPDpostedby = (LinearLayout)findViewById(R.id.llPDpostedby);
		rlPDFooter = (RelativeLayout)findViewById(R.id.rlPDFooter);
		txtSelectUser = (TextView) findViewById(R.id.txtSelectUser);
		
		hPDListView = (HListView) findViewById(R.id.hPDListView);
		llPDHlistView = (LinearLayout) findViewById(R.id.llPDHlistView);
		
		setHeaderView();
		
		txtSelectUser.setOnClickListener(this);
		txtPDCancel.setOnClickListener(this);
		txtPDProposal.setOnClickListener(this);
		//txtPDFiles.setOnClickListener(this);
		txtPDInvoice.setOnClickListener(this);
		txtPDPayment.setOnClickListener(this);
		txtPDComplete.setOnClickListener(this);
		btnPDApply.setOnClickListener(this);
		imvPDEdit.setOnClickListener(this);
		imvPDSave.setOnClickListener(this);
		//imvPDShare.setOnClickListener(this);
		txtReadMore.setOnClickListener(this);
		txtPDPostedBy.setOnClickListener(this);
		
		if(parentView.equals(Constants.VIEW_SEARCH_PROJECTS)){ 
			imvPDEdit.setVisibility(View.GONE); 
			llPDBtn.setVisibility(View.GONE);
			txtPDCancel.setVisibility(View.GONE);
			appHeaderView.txtHRight2.setVisibility(View.GONE);
			appHeaderView.vRightLine.setVisibility(View.GONE);
		}else{
			projectAs = getIntent().getStringExtra(Constants.MY_PROJECT_AS_POSTER);
		}
		
		hPDListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,long id) {
				Attachment file = (Attachment) adapter.getItemAtPosition(position);
				DownloadHelper.downloadFile(ProjectDetailsActivity.this, file.file_url, file.file_name); 
			}
		});
	}
	
	private void setHeaderView() {
		appHeaderView = (AppHeaderView) findViewById(R.id.appHeaderView);
		appHeaderView.setHeaderContent(
				getResources().getDrawable(R.drawable.ic_back), getResources()
						.getString(R.string.project_detail), getResources()
						.getDrawable(R.drawable.ic_msg), null, Gravity.LEFT);
	}
	
    @Override
    protected void onResume() {
    	new ProjectDetailTask().execute();
    	super.onResume();
    }
    
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.txtReadMore) {
			readMoreClickHandler();
		} else if (v.getId() == R.id.btnPDApply) {
			applyForProject();
		} else if (v.getId() == R.id.imvPDSave) {
			// TODO..
			Util.showCenteredToast(ProjectDetailsActivity.this, Constants.msg);
		} else if (v.getId() == R.id.imvPDEdit) {
			//goToEditProjectView();////
			/*if(Util.isDeviceOnline(getApplicationContext())){
				createUrl("edit_project",null);
			}else{
				Util.showCenteredToast(getApplicationContext(), getResources().getString(R.string.msg_Connect_internet));
			}*/
		} else if (v.getId() == R.id.txtPDProposal) {
			if(Util.isDeviceOnline(getApplicationContext())){
				goToProposal();
			}else{
				Util.showCenteredToast(getApplicationContext(), getResources().getString(R.string.msg_Connect_internet));
			}
		} else if(v.getId() == appHeaderView.txtHLeft.getId()){
			//TODO menu button
			finishActivity();
		} else if(v.getId() == appHeaderView.txtHRight1.getId()){
			//TODO message button
			goToMessageView();
		} else if(v.getId() == appHeaderView.txtHRight2.getId()){
			//TODO notification button
			goToNotificationView();
		} else if (v.getId() == R.id.txtPDCancel) {
			txtPDCancelClick();
		} else if (v.getId() == R.id.txtSelectUser) {
			customDialog();
		} else if (v.getId() == R.id.txtPDPostedBy) {
				if(Util.isDeviceOnline(getApplicationContext())){
					goToProfileActivity();
				}else{
					Util.showCenteredToast(getApplicationContext(), getResources().getString(R.string.msg_Connect_internet));
				}
		} else if (v.getId() == R.id.txtPDComplete) {
			goToComplete();
		} else if (v.getId() == R.id.txtPDInvoice) {
			if (projectAs.equals(Constants.MY_PROJECT_AS_DOER)) {
				createUrl("create_invoice",null);
			} else if (projectAs.equals(Constants.MY_PROJECT_AS_POSTER)) {
				if (!txtSelectUser.getText().toString().trim().equals(getResources().getString(R.string.select_doer))) {
					TaskDoer doer = getDoer();
					createUrl("view_invoice",doer);
				}else{
					Util.showCenteredToast(getApplicationContext(), "Select Doer");
				}
			}
		}
	}
	
	private void goToEditProjectView(){
		Intent intent = new Intent();
		intent.putExtra(Constants.PROJECT_TYPE, projectDetails.task_kind);
		intent.putExtra(Constants.VIEW_MODE, Constants.VIEW_MODE_EDIT);
		intent.putExtra(Constants.SERIALIZABLE_DATA, projectDetails);
		intent.setClass(ProjectDetailsActivity.this, CreateProjectActivity.class);
		startActivity(intent);
	}
	
	private void createUrl(String action, TaskDoer doer){
		try {
			JSONObject reqData = new JSONObject();
			JSONObject param = new JSONObject();
			reqData.put("action", action);
			reqData.put("params", param.put("task_id", projectDetails.task_id));
			if (action.equals("view_invoice")) {
				reqData.put("params", param.put("user_id", doer.user_id));
			}
			reqData.put("user_id", AppGlobals.userDetail.user_id);
			reqData.put("user_device_id", AppGlobals.userDetail.user_device_id);
			String url = "http://54.84.193.236/index/web?data=" + reqData.toString();
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void goToProfileActivity(){
		Intent intent = new Intent(ProjectDetailsActivity.this,ProfileActivity.class);
		intent.putExtra(Constants.SERIALIZABLE_DATA, projectDetails.creator_user_id);
		startActivity(intent);
	}
	
	private void txtPDCancelClick(){
		if (txtPDCancel.getText().toString().equals(getResources().getString(R.string.cancellation_request))) {
			cancelProjectResponse();
		}else{
			if (projectAs.equals(Constants.MY_PROJECT_AS_POSTER)) {
				if (txtPDCancel.getText().toString().equalsIgnoreCase(getResources().getString(R.string.Cancel))) {
					if (projectDetails.state.equals("a")) {
						if (txtSelectUser.getText().toString().trim().equals(getResources().getString(R.string.select_doer))) {
							Util.showCenteredToast(ProjectDetailsActivity.this, getResources().getString(R.string.select_doer));
						}else{
							cancelProject();
						}
					}else{
						cancelProject();
					}
				}
			} else if (projectAs.equals(Constants.MY_PROJECT_AS_DOER)) {
				if (txtPDCancel.getText().toString().equalsIgnoreCase(getResources().getString(R.string.Cancel))) {
					cancelProject();
				}
			}
		}
	}
	
	private void applyForProject(){
		if(projectDetails.hiring_closed.equals("0")){ 
			LicenseValidation validation = LicenseValidation.getInstance(this, LicenseValidation.VALIDATION_ON_BID, projectDetails.task_id,new IVCheckListener() {
				@Override
				public void onValidationChecked(String isPercent,String planValue) {
					goToCraftABit(isPercent,planValue);
				}
			});
			validation.execute();
		} else{
			Util.showCenteredToast(this, getResources().getString(R.string.msg_Bid_closed));  
		}
	}
	
	private void goToCraftABit(String isPercent,String planValue){
		Proposal proposal = new Proposal();
		proposal.projectData = projectDetails;
		proposal.plan_value_is_percent = isPercent;
		proposal.plan_value = planValue;
		
		if(parentView.equals(Constants.VIEW_SEARCH_PROJECTS)){ 
			proposal.viewMode = "apply";
		} else{
			proposal.task_tasker_id = taskTasker.task_tasker_id;
			proposal.viewMode = "edit";
		}
		
		if(projectDetails._Id != null ){
			proposal.questions = databaseMgr.getQuestion(projectDetails._Id);
		} else{
			proposal.questions = projectDetails.multicatquestion;
		}
		Intent intent = new Intent(ProjectDetailsActivity.this, CraftABidActivity.class);
		intent.putExtra(Constants.SERIALIZABLE_DATA, proposal);
		startActivityForResult(intent,BID_REQUEST_CODE);
		overridePendingTransition(R.anim.right_in, R.anim.left_out);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK && requestCode == BID_REQUEST_CODE){
			setResult(BID_RESULT_CODE); 
			finishActivity();
		}
	}
	
	private void goToProposal(){
		Intent intent = null;
		if(projectAs.equals(Constants.MY_PROJECT_AS_POSTER)){
			intent = new Intent(ProjectDetailsActivity.this, ProposalListActivity.class);
		} else{
			intent = new Intent(ProjectDetailsActivity.this, ProposalDetailActivity.class);
		}
		intent.putExtra(Constants.SERIALIZABLE_DATA, projectDetails);
		intent.putExtra( Constants.MY_PROJECT_AS_POSTER, projectAs);
		startActivity(intent);
		overridePendingTransition(R.anim.right_in, R.anim.left_out);
	}
	
	private void setReadMoreBtnVisibility(){
		txtPDPDesc.post(new Runnable() {
		    @Override
		    public void run() {
		        if(txtPDPDesc.getLineCount() == 1){
		        	txtReadMore.setVisibility(View.GONE);
		        } else{
		        	txtReadMore.setVisibility(View.VISIBLE);
		        }
		    }
		});
	}

	private void readMoreClickHandler() {
		if (txtReadMore.getText().toString()
				.equals(getResources().getString(R.string.read_more))) {
			txtPDPDesc.setMaxLines(Integer.MAX_VALUE);
			LinearLayout.LayoutParams Params1 = new LinearLayout.LayoutParams(
	
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			Params1.setMargins(0, 14, 0, 0);
			txtPDPDesc.setLayoutParams(Params1);
			txtReadMore.setText(getResources().getString(R.string.read_less));
		} else if (txtReadMore.getText().toString()
				.equals(getResources().getString(R.string.read_less))) {
			txtPDPDesc.setMaxLines(1);
			LinearLayout.LayoutParams Params1 = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			Params1.setMargins(0, 14, 0, 0);
			txtPDPDesc.setLayoutParams(Params1);
			txtReadMore.setText(getResources().getString(R.string.read_more));
		}
	}

	private void goToNotificationView() {
		//TODO---
		Util.showCenteredToast(ProjectDetailsActivity.this, Constants.msg);
	}

	private void setProjectDetails() {
		txtPDProjectTitle.setText(projectDetails.title);
		txtPDPostedDate.setText(Util.setProjectDateWithMonth(projectDetails.created_at, false));
		if(parentView.equals(Constants.VIEW_MY_PROJECTS)){
			
			if(projectAs.equals(Constants.MY_PROJECT_AS_POSTER)){ //Poster
				llPDpostedby.setVisibility(View.GONE); 
				btnPDApply.setVisibility(View.GONE);
				imvPDSave.setVisibility(View.GONE);

				if(projectDetails.proposals_cnt.equals("0")){ 
					rlPDFooter.setVisibility(View.GONE);
				}else{
					imvPDEdit.setVisibility(View.GONE);
					if(projectDetails.state.equals(PROJECT_AWARDED)){ 
						//TODO
					}else{
						txtPDInvoice.setVisibility(View.GONE);
						txtPDPayment.setVisibility(View.GONE);
						txtPDComplete.setVisibility(View.GONE);
					}
				}
			}else{//Doer
				imvPDEdit.setVisibility(View.GONE);
				if(taskTasker.status.equals(PROJECT_SELECTED)){
					imvPDSave.setVisibility(View.GONE);
					btnPDApply.setVisibility(View.GONE);
					
					txtPDCancel.setVisibility(View.VISIBLE);
					txtPDInvoice.setVisibility(View.VISIBLE);
					txtPDPayment.setVisibility(View.VISIBLE);
					txtPDComplete.setVisibility(View.VISIBLE);
					
				}else{
					txtPDCancel.setVisibility(View.GONE);
					txtPDInvoice.setVisibility(View.GONE);
					txtPDPayment.setVisibility(View.GONE);
					txtPDComplete.setVisibility(View.GONE);
					
					if(taskTasker.selection_type.equals(SELECTION_TYPE_BID)){
						btnPDApply.setVisibility(View.GONE);
					}else{
						if(projectDetails.state.equals("o") || 
								projectDetails.state.equals("a") || 
								projectDetails.state.equals("d")){
							txtPDProposal.setVisibility(View.GONE);
						}else{
							btnPDApply.setVisibility(View.GONE);
						}
					}
				}
			}
			if(projectDetails.task_kind.equals(Constants.VIRTUAL)){ 
				txtPDLocation.setText(projectDetails.projectLocationNames);
			}else{
				if(projectDetails.workLocation.address != null){
					if(projectDetails.workLocation.address.equals("")){
						txtPDLocation.setText(getResources().getString(R.string.Anywhere)); 
					}else{
						txtPDLocation.setText(projectDetails.workLocation.address);
					}
				}else{
					txtPDLocation.setText(getResources().getString(R.string.Anywhere)); 
				}
			}
			
		}else{
			if(projectDetails.task_kind.equals(Constants.VIRTUAL)){ 
				txtPDLocation.setText(projectDetails.projectLocationNames);
			}else{
				if(projectDetails.tasklocation.size() > 0){
					txtPDLocation.setText(projectDetails.tasklocation.get(0).location_geo_area);
				}else{
					txtPDLocation.setText(getResources().getString(R.string.Anywhere)); 
				}
			}
		}
		
		
		if(projectDetails.attachments.size() > 0 ){
			llPDHlistView.setVisibility(View.VISIBLE);
			AttachmentsAdapter adapter  = new AttachmentsAdapter(this, R.layout.ed_attach_list_row, projectDetails.attachments);
			hPDListView.setAdapter(adapter);
		}
		
		
		
		txtPDType.setText(Util.getProjectTypeName(projectDetails.task_kind)); 
		txtPDSkills.setText(projectDetails.projectSkillNames); 
		txtPDCategory.setText(projectDetails.category.category_name); 
		//txtPDBudget.setText("$" + projectDetails.price); 
		txtPDProposalCount.setText(projectDetails.proposals_cnt);
		txtPDAveragePrice.setText("$" + projectDetails.proposals_avg_price);
		txtPDPDesc.setText(projectDetails.description);
		txtPDStartDate.setText(Util.setProjectDateWithMonth(projectDetails.project_start_date, false));
		txtPDPostedBy.setText(projectDetails.name);
		txtPLPaymentMode.setText(Util.getBudgetString(projectDetails));
	}
	
	private void goToMessageView(){
		if(parentView.equals(Constants.VIEW_MY_PROJECTS)){ 
			if (projectAs.equals(Constants.MY_PROJECT_AS_POSTER)) {
				if (projectDetails.state.equals("a")) {
					if (!txtSelectUser.getText().toString().trim().equals(getResources().getString(R.string.select_doer))) {
						MessageDetail message = new MessageDetail();
						message.task_id = projectDetails.task_id;
						message.from_user_id = projectDetails.creator_user_id;
						message.to_user_ids = getDoer().user_id;
						message.title = projectDetails.title;
						goToMessageThreadActivity(message);
					} else{
						Util.showCenteredToast(getApplicationContext(), getResources().getString(R.string.select_doer));
					}
				} else {
					goToPublicMessageActivity();
				}
			} else if (projectAs.equals(Constants.MY_PROJECT_AS_DOER)) {
				if (projectDetails.state.equals("a")) {
					MessageDetail message = new MessageDetail();
					message.task_id = projectDetails.task_id;
					message.from_user_id = projectDetails.creator_user_id;
					message.to_user_ids = AppGlobals.userDetail.user_id;
					message.title = projectDetails.title;
					goToMessageThreadActivity(message);
				} else{
					goToPublicMessageActivity();
				}
			}
		} else if(parentView.equals(Constants.VIEW_SEARCH_PROJECTS)) {
			goToPublicMessageActivity();
		}
	}
	
	private TaskDoer getDoer(){
		if (doersList.size() > 1) {
			for (TaskDoer doer : doersList) {
				if (doer.user_id.equals(user_id)) {
					return doer; 
				}
			}
		}else{
			return doersList.get(0);
		}
		return null;
	}
	
	private void goToPublicMessageActivity(){
		Intent intent = new Intent(ProjectDetailsActivity.this, PublicMessageActivity.class);
		intent.putExtra(Project.FLD_TASK_ID, projectDetails.task_id);
		intent.putExtra(Profile.FLD_USER_ID, projectDetails.creator_user_id);
		startActivity(intent);  
		overridePendingTransition(R.anim.right_in, R.anim.left_out);
	}
	
	private void goToMessageThreadActivity(MessageDetail message){
		Intent intent = new Intent(ProjectDetailsActivity.this, MessageDetailActivity.class);
		intent.putExtra(Constants.SERIALIZABLE_DATA, message);
		startActivity(intent);  
		overridePendingTransition(R.anim.right_in, R.anim.left_out);
	}
	
	private class ProjectDetailTask extends AsyncTask<String, Void, Void> {
		@Override
		protected void onPreExecute() {
			Util.showProDialog(ProjectDetailsActivity.this);
		}

		@Override
		protected Void doInBackground(String... params) {
			if(parentView.equals(Constants.VIEW_MY_PROJECTS)){
				projectDetails = databaseMgr.getProjectDetail(projectDetails.task_id);
				projectDetails.category = databaseMgr.getProjectCategory(projectDetails.category_id);
				
				projectDetails.projectSkillNames = databaseMgr.getGroupConcatOnTaskTables(TableType.TaskSkillTable,
																						  Skill.FLD_SKILL_DESC,String.valueOf(projectDetails._Id));
				
				if(projectDetails.projectSkillNames == null){
					projectDetails.projectSkillNames = getResources().getString(R.string.No_skill_specified);
				}

				if(projectDetails.task_kind.equals(Constants.VIRTUAL)){ 
					projectDetails.projectLocationNames = databaseMgr.getGroupConcatOnTaskTables(TableType.TaskLocationTable,
																								 Country.FLD_COUNTRY_NAME,String.valueOf(projectDetails._Id));
					if(projectDetails.projectLocationNames == null){
						projectDetails.projectLocationNames = getResources().getString(R.string.Anywhere); 
					}
				}else{
					projectDetails.workLocation = databaseMgr.getTaskWLocation(String.valueOf(projectDetails._Id));
				}
				
				projectDetails.attachments = databaseMgr.getAttachment(projectDetails._Id);
				
				if(projectAs.equals(Constants.MY_PROJECT_AS_DOER)){
					taskTasker = databaseMgr.getTaskTasker(userId, projectDetails._Id);
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			Util.dimissProDialog();
			txtPDPDesc.setMaxLines(1);
			setProjectDetails();
			setReadMoreBtnVisibility();
			cancelButtonText();
		}
	}
	
	
	public void getDoerList(Long mTaskId){
		doersList = new ArrayList<TaskDoer>();
		Cursor cursor = null;
		try {
			cursor = databaseMgr.queryTable(TableType.TaskDoerTable,
					new String[] { TaskDoer.FLD_TASK_TASKER_ID,
							TaskDoer.FLD_FULL_NAME, TaskDoer.FLD_USER_IMAGE,
							TaskDoer.FLD_USER_ID, TaskDoer.FLD_PROJECT_STATUS,
							TaskDoer.FLD_TASK_COMPLETE_MARKED,
							TaskDoer.FLD_TASK_COMPLETE_MARK_BY,
							TaskDoer.FLD_TASK_COMPLETE_CONFIRM_BY,
							TaskDoer.FLD_CANCEL_REQ_BY,
							TaskDoer.FLD_CANCEL_REQ_DATE,
							TaskDoer.FLD_CANCEL_REASON_BY_POSTER,
							TaskDoer.FLD_CANCEL_REASON_BY_DOER,
							TaskDoer.FLD_CANCEL_REFUND_DEMAND_BY_POSTER,
							TaskDoer.FLD_CANCEL_REFUND_OFFER_BY_DOER },
					TaskDoer.FLD_MOBILE_REC_ID + "=?" + " AND "
							+ TaskDoer.FLD_STATUS + "=?",
					new String[] { String.valueOf(mTaskId), "s" }, null, null,
					null);
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				do {
					TaskDoer doer = new TaskDoer();
					doer.user_id = cursor.getLong(cursor.getColumnIndex(TaskDoer.FLD_USER_ID));
					doer.task_tasker_id = cursor.getLong(cursor.getColumnIndex(TaskDoer.FLD_TASK_TASKER_ID));
					doer.fullname = cursor.getString(cursor.getColumnIndex(TaskDoer.FLD_FULL_NAME));
					doer.userimage = cursor.getString(cursor.getColumnIndex(TaskDoer.FLD_USER_IMAGE));
					doer.project_status = cursor.getString(cursor.getColumnIndex(TaskDoer.FLD_PROJECT_STATUS));
					doer.task_complete_marked = cursor.getInt(cursor.getColumnIndex(TaskDoer.FLD_TASK_COMPLETE_MARKED));
					doer.task_complete_mark_by = cursor.getLong(cursor.getColumnIndex(TaskDoer.FLD_TASK_COMPLETE_MARK_BY));
					doer.task_complete_confirm_by = cursor.getLong(cursor.getColumnIndex(TaskDoer.FLD_TASK_COMPLETE_CONFIRM_BY));
					doer.cancel_req_by = cursor.getLong(cursor.getColumnIndex(TaskDoer.FLD_CANCEL_REQ_BY));
					doer.cancel_req_date = cursor.getString(cursor.getColumnIndex(TaskDoer.FLD_CANCEL_REQ_DATE));
					doer.cancel_reason_by_poster = cursor.getString(cursor.getColumnIndex(TaskDoer.FLD_CANCEL_REASON_BY_POSTER));
					doer.cancel_reason_by_doer = cursor.getString(cursor.getColumnIndex(TaskDoer.FLD_CANCEL_REASON_BY_DOER));
					doer.cancel_refund_demand_by_poster = cursor.getLong(cursor.getColumnIndex(TaskDoer.FLD_CANCEL_REFUND_DEMAND_BY_POSTER));
					doer.cancel_refund_offer_by_doer = cursor.getLong(cursor.getColumnIndex(TaskDoer.FLD_CANCEL_REFUND_OFFER_BY_DOER));
					doersList.add(doer);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}
	
	private void cancelButtonText(){
		if(parentView.equals(Constants.VIEW_MY_PROJECTS)){
			getDoerList(projectDetails._Id);
			if (projectAs.equals(Constants.MY_PROJECT_AS_POSTER)) {
				if (doersList.size() == 1) {
					taskDoer = doersList.get(0);
					setCancelBtnText(taskDoer);
					txtSelectUser.setText("");
					txtSelectUser.setVisibility(View.GONE);
				} else{
					if (!txtSelectUser.getText().toString().equals(getResources().getString(R.string.select_doer))) {
						setCancelBtnText(getDoer());
					} else if(doersList.size() > 1 ){
						txtSelectUser.setVisibility(View.VISIBLE);
					}
				}
			}else{
				setCancelBtnText(taskTasker);
			}
		}
	}
	private void cancelProject(){
		CancelProjectDialog dialog = CancelProjectDialog.getInstance(new IDataChangedListener() {
			@Override
			public void onDataChanged() {
				if (projectAs.equals(Constants.MY_PROJECT_AS_POSTER)) {
					if (projectDetails.state.equals("a")) {
						new ProjectDetailTask().execute();
					}else{
						setResult(CANCEL_RESULT_CODE);
						finishActivity();
					}
				}else if (projectAs.equals(Constants.MY_PROJECT_AS_DOER)) {
					new ProjectDetailTask().execute();
				}
			}
		});
		
		Bundle bundle = new  Bundle();
		bundle.putSerializable(Constants.SERIALIZABLE_DATA, projectDetails);
		if (projectAs.equals(Constants.MY_PROJECT_AS_POSTER)) {
			if (doersList.size() == 1) {
				bundle.putSerializable("Doer", doersList.get(0));
			}else{
				bundle.putSerializable("Doer", taskDoer);
			}
		}else{
			bundle.putSerializable("Doer", taskTasker);
		}
		
		bundle.putString("ProjectAS", projectAs);
		dialog.setArguments(bundle);
		dialog.show(getSupportFragmentManager(), "cancel_project");
	}
	
	private void cancelProjectResponse(){
		CancelResponseDialog dialog = CancelResponseDialog.getInstance(new IDataChangedListener() {
			@Override
			public void onDataChanged() {
				new ProjectDetailTask().execute();
			}
		});
		
		Bundle bundle = new  Bundle();
		bundle.putSerializable(Constants.SERIALIZABLE_DATA, projectDetails);
		if (projectAs.equals(Constants.MY_PROJECT_AS_POSTER)) {
			if (doersList.size() == 1) {
				bundle.putSerializable("Doer", doersList.get(0));
			}else{
				bundle.putSerializable("Doer", taskDoer);
			}
		}else{
			bundle.putSerializable("Doer", taskTasker);
		}
		
		bundle.putString("ProjectAS", projectAs);
		dialog.setArguments(bundle);
		dialog.show(getSupportFragmentManager(), "cancel_project");
	}
	
	private void customDialog(){
		customDialog = new Dialog(ProjectDetailsActivity.this);
		customDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		customDialog.setContentView(R.layout.ed_simple_list);
		
		llSListSearchBar = (LinearLayout) customDialog.findViewById(R.id.llSListSearchBar);
		txtSimpleListTitle = (TextView) customDialog.findViewById(R.id.txtSimpleListTitle);
		imgBtnSimpleListBack = (ImageButton) customDialog.findViewById(R.id.imgBtnSimpleListBack);
		listVSimpleList = (ListView) customDialog.findViewById(R.id.listVSimpleList);
		
		txtSimpleListTitle.setText(getResources().getString(R.string.select_doer));
		
		adapter = new SelectDoerAdapter(ProjectDetailsActivity.this, R.layout.ed_simple_list_row, doersList);
		listVSimpleList.setAdapter(adapter);
		
		listVSimpleList.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				taskDoer = new TaskDoer();
				taskDoer = (TaskDoer) adapter.getItemAtPosition(position);
				user_id = taskDoer.user_id;
				txtSelectUser.setText(taskDoer.fullname);
				customDialog.dismiss();
				setCancelBtnText(taskDoer);
			}
		});
		
		imgBtnSimpleListBack.setVisibility(View.GONE);
		llSListSearchBar.setVisibility(View.GONE);
		
		customDialog.setCancelable(true);
		customDialog.show();
	}

	/*private void setCancelBtnText(TaskDoer doer){
		if (doer.cancel_req_by == 0 || doer.cancel_req_by.equals(AppGlobals.userDetail.user_id)) {
			if (doer.project_status.equals("u")) {
				txtPDCancel.setText(getResources().getString(R.string.under_cancellation));
			} else if(doer.project_status.equals("c")){
				txtPDCancel.setText(getResources().getString(R.string.cancelled));
			} else if(doer.project_status.equals("d")){
				txtPDCancel.setText("Under Dispute");
			} else{
				txtPDCancel.setText(getResources().getString(R.string.Cancel));
			}
		} else if (!doer.cancel_req_by.equals(AppGlobals.userDetail.user_id)){
			if(doer.project_status.equals("c")){
				txtPDCancel.setText(getResources().getString(R.string.cancelled));
			} else if(doer.project_status.equals("d")){
				txtPDCancel.setText("Under Dispute");
			} else{
				txtPDCancel.setText(getResources().getString(R.string.cancellation_request));
			}
		}
	}*/
	
	private void goToComplete(){
		Intent intent = null;
		if(projectAs.equals(Constants.MY_PROJECT_AS_POSTER)){
//			projectDetails.task_tasker_id = doersList;
			if(txtSelectUser.getText().toString().trim().equals(getResources().getString(R.string.select_doer))){
				Util.showCenteredToast(ProjectDetailsActivity.this, getResources().getString(R.string.select_doer));
			}else{
				intent = new Intent(this, RatingActivity.class);
				intent.putExtra(Constants.MY_PROJECT_AS_POSTER, projectAs);
				intent.putExtra(Constants.SERIALIZABLE_DATA, projectDetails);
				if (doersList.size() == 1) {
					intent.putExtra("Doer", doersList.get(0));
				}else{
					intent.putExtra("Doer", taskDoer);
				}
				startActivity(intent);
				overridePendingTransition(R.anim.right_in, R.anim.left_out);
			}
		}else{
			projectDetails.task_tasker_id = taskTasker.task_tasker_id;
			intent = new Intent(this, ProjectCompleteActivity.class);
			intent.putExtra(Constants.MY_PROJECT_AS_POSTER, projectAs);
			intent.putExtra(Constants.SERIALIZABLE_DATA, projectDetails);
			startActivity(intent);
			overridePendingTransition(R.anim.right_in, R.anim.left_out);
		}
	}
	
	private void setCancelBtnText(TaskDoer doer){
		if (doer.cancel_req_by == 0 || doer.cancel_req_by.equals(AppGlobals.userDetail.user_id)) {
			if (doer.project_status.equals("u")) {
				txtPDCancel.setText(getResources().getString(R.string.under_cancellation));
				txtPDComplete.setVisibility(View.GONE);
			} else if(doer.project_status.equals("c")){
				txtPDCancel.setText(getResources().getString(R.string.cancelled));
				txtPDComplete.setVisibility(View.GONE);
			} else if(doer.project_status.equals("d")){
				txtPDCancel.setText("Under Dispute");
				txtPDComplete.setVisibility(View.GONE);
			} else{
				txtPDCancel.setText(getResources().getString(R.string.Cancel));
				if(projectAs.equals(Constants.MY_PROJECT_AS_POSTER)){
					txtPDComplete.setVisibility(View.GONE);
					if(doer.task_complete_marked == 1){
						txtPDComplete.setVisibility(View.VISIBLE);
						txtPDCancel.setVisibility(View.GONE);
						if(doer.task_complete_confirm_by != 0){
							txtPDComplete.setText("Completed");
							txtPDComplete.setEnabled(false);
						}
					}
				}else{
					if(doer.task_complete_marked == 1){
						if(doer.task_complete_mark_by.equals(AppGlobals.userDetail.user_id)){
							txtPDComplete.setText("Completed");
							txtPDComplete.setEnabled(false);
							txtPDCancel.setVisibility(View.GONE);
						}
					}
				}
			}
		} else if (!doer.cancel_req_by.equals(AppGlobals.userDetail.user_id)){
			txtPDComplete.setVisibility(View.GONE);
			if(doer.project_status.equals("c")){
				txtPDCancel.setText(getResources().getString(R.string.cancelled));
			} else if(doer.project_status.equals("d")){
				txtPDCancel.setText("Under Dispute");
			} else{
				txtPDCancel.setText(getResources().getString(R.string.cancellation_request));
			}
		}
	}
	
	@Override
	public void onBackPressed() {
		finishActivity();
	}
	
	private void finishActivity() {
		ProjectDetailsActivity.this.finish();
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}
}
