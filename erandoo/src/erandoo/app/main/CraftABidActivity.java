package erandoo.app.main;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.cmds.Cmd;
import erandoo.app.cmds.CmdFactory;
import erandoo.app.config.Config;
import erandoo.app.custom.AppHeaderView;
import erandoo.app.custom.BaseFragActivity;
import erandoo.app.custom.IListItemClickListener;
import erandoo.app.database.Proposal;
import erandoo.app.database.Question;
import erandoo.app.database.TaskDoer;
import erandoo.app.network.NetworkMgr;
import erandoo.app.network.NetworkResponse;
import erandoo.app.projects.BidDurationDialog;
import erandoo.app.projects.QuestionAnswerDialog;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.LicenseValidation;
import erandoo.app.utilities.Util;

public class CraftABidActivity extends BaseFragActivity implements OnClickListener, TextWatcher {
	public static ArrayList<Question> questionList = new ArrayList<Question>();
	private TextView txtCBProjectName;
	private EditText eTextCBDescription;
	private EditText eTextCBMyPay;
	private EditText eTextCBPosterPay;
	private TextView eTextCBExpenses;
	private Button btnCBSubmit;
	private TextView txtCBSelectCompletionTime;
	private TextView txtCBAnswer;
	private TextView txtAttach;
	private TextView txtCBIsComplete;
	
	private CheckBox chkCBSealProposal;
	private CheckBox chkCBIsComplete;
	private CheckBox chkCBSatisfaction;
	private CheckBox chkCBExpenses;
	
	private LinearLayout llChkCBIsComplete;
	private LinearLayout llChkCBSatisfaction;
	private LinearLayout llChkCBExpenses;
	
	private Proposal proposal;
	private JSONArray arr;
	private AppHeaderView appHeaderView;
	char st;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ed_craft_bid);
		initialize();
	}

	private void initialize() {
		txtCBProjectName = (TextView) findViewById(R.id.txtCBProjectName);
		eTextCBDescription = (EditText) findViewById(R.id.eTextCBDescription);
		eTextCBMyPay = (EditText) findViewById(R.id.eTextCBMyPay);
		eTextCBPosterPay = (EditText) findViewById(R.id.eTextCBPosterPay);
		eTextCBExpenses = (TextView) findViewById(R.id.eTextCBExpenses);
		txtCBSelectCompletionTime = (TextView) findViewById(R.id.txtCBSelectCompletionTime);
		btnCBSubmit = (Button) findViewById(R.id.btnCBSubmit);
		txtCBAnswer = (TextView) findViewById(R.id.txtCBAnswer);
		txtCBIsComplete = (TextView) findViewById(R.id.txtCBIsComplete);
		
		chkCBExpenses = (CheckBox) findViewById(R.id.chkCBExpenses);
		chkCBSealProposal = (CheckBox) findViewById(R.id.chkCBSealProposal);
		chkCBIsComplete = (CheckBox) findViewById(R.id.chkCBIsComplete);
		chkCBSatisfaction = (CheckBox) findViewById(R.id.chkCBSatisfaction);
		
		txtAttach = (TextView) findViewById(R.id.txtAttach);
		
		llChkCBIsComplete = (LinearLayout) findViewById(R.id.llChkCBIsComplete);
		llChkCBSatisfaction = (LinearLayout) findViewById(R.id.llChkCBSatisfaction);
		llChkCBExpenses = (LinearLayout) findViewById(R.id.llChkCBExpenses);
		
		setHeaderView();

		txtCBSelectCompletionTime.setOnClickListener(this);
		btnCBSubmit.setOnClickListener(this);
		txtCBAnswer.setOnClickListener(this);
		txtAttach.setOnClickListener(this);
		
		llChkCBIsComplete.setOnClickListener(this);
		llChkCBSatisfaction.setOnClickListener(this);
		llChkCBExpenses.setOnClickListener(this);
		
		
		setEditTextFocusChangeListener();
		
		setPageViewData();
	}
	
	private void setHeaderView() {     
		appHeaderView = (AppHeaderView) findViewById(R.id.appHeaderView);
		appHeaderView.setHeaderContent(
				getResources().getDrawable(R.drawable.ic_back), getResources()
						.getString(R.string.craft_a_bid),null,null, Gravity.LEFT);
	}
	
	private void setPageViewData() {
		proposal = (Proposal) getIntent().getSerializableExtra(Constants.SERIALIZABLE_DATA);
		questionList = proposal.questions;
		txtCBProjectName.setText(proposal.projectData.title);
		eTextCBExpenses.setText(proposal.projectData.cash_required);
		
		txtCBIsComplete.setText(getResources().getString(R.string.Project_by_the_desired_date)+" "+proposal.projectData.end_date);
		if(proposal.projectData.cash_required.equals("0.00")){ 
			llChkCBExpenses.setVisibility(View.GONE);
		}
		if (proposal.viewMode.toString().equals("edit")) {
			setProposalData();
		}else if(proposal.viewMode.toString().equals("apply")){
			//TODO
		}
		
		if(proposal.task_doer != null){
			boolean seal = proposal.task_doer.seal_my_proposal.equals("1")?true:false;
			chkCBSealProposal.setChecked(seal);
			chkCBSealProposal.setEnabled(!seal); 
		}
	}

	@Override
	public void onClick(View v) {
		int vId = v.getId();
		if (vId == appHeaderView.txtHLeft.getId()) {
			setResult(RESULT_CANCELED);
			finishActivity();
		} else if (vId == R.id.txtCBSelectCompletionTime) {
			showBidDurationDialog();
		} else if (vId == R.id.btnCBSubmit) {
			uploadApplyData();
		} else if (vId == R.id.txtCBAnswer) {
			goToQuestion();
		} else if (vId == R.id.txtAttach) {
			// TODO
		} else if (vId == R.id.llChkCBExpenses){
			setCheckValue(chkCBExpenses);
			
		} else if (vId == R.id.llChkCBIsComplete){
			setCheckValue(chkCBIsComplete);
			
		} else if (vId == R.id.llChkCBSatisfaction){
			setCheckValue(chkCBSatisfaction);
		}
	}
	
	private void setCheckValue(CheckBox box){
		if(box.isChecked()){
			box.setChecked(false);
		}else{
			box.setChecked(true);
		}
	}
	
	private void setEditTextFocusChangeListener(){
		eTextCBMyPay.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					eTextCBMyPay.setFilters(new InputFilter[] { new InputFilter.LengthFilter(6) });
					eTextCBMyPay.addTextChangedListener(CraftABidActivity.this);
					eTextCBPosterPay.removeTextChangedListener(CraftABidActivity.this); 
				}
			}
		});
		eTextCBPosterPay.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					eTextCBPosterPay.setFilters(new InputFilter[] { new InputFilter.LengthFilter(6) });
					eTextCBMyPay.removeTextChangedListener(CraftABidActivity.this); 
					eTextCBPosterPay.addTextChangedListener(CraftABidActivity.this);
				}
			}
		});
	}
	
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if(eTextCBMyPay.hasFocus()){
			if(s.length() < 7 ){
			eTextCBPosterPay.setText(LicenseValidation.calculatePosterPay(eTextCBMyPay.getText().toString().trim(), 
					Util.toDouble(proposal.plan_value), Util.getBoolean(proposal.plan_value_is_percent))); 
			}else{
				eTextCBMyPay.setFilters(new InputFilter[] { new InputFilter.LengthFilter(6) });
				Util.showCenteredToast(getApplicationContext(), "Can't exceeds to 6 digits");
			}
		}else if(eTextCBPosterPay.hasFocus()){
			if(s.length() < 7 ){
				eTextCBMyPay.setText(LicenseValidation.calculateMyPay(eTextCBPosterPay.getText().toString().trim(), 
						Util.toDouble(proposal.plan_value), Util.getBoolean(proposal.plan_value_is_percent)));
			}else{
				eTextCBPosterPay.setFilters(new InputFilter[] { new InputFilter.LengthFilter(6) });
				Util.showCenteredToast(getApplicationContext(), "Can't exceeds to 6 digits");
			}

		}
	}

	private void showBidDurationDialog() {
		BidDurationDialog dialog = new BidDurationDialog(
				CraftABidActivity.this, new IListItemClickListener() {
					@Override
					public void onItemSelected(Object item, int position) {
						txtCBSelectCompletionTime.setText(item.toString());
					}
				}, null);
		
		dialog.show();
	}
	
	private void goToQuestion(){
		if (questionList.size() == 0) {
			Util.showCenteredToast(this, getResources().getString(R.string.noQuestions));
		} else {
			QuestionAnswerDialog dialog = new QuestionAnswerDialog();
			Bundle bundle = new Bundle();
			bundle.putString(Constants.PARENT_VIEW, "craftABid");
			dialog.setArguments(bundle);
			dialog.show(getSupportFragmentManager(), "question");
		}
	}

	private void setProposalData() {
		if (proposal.tasker_comments != null && proposal.my_pay != null && proposal.poster_pay != null && proposal.proposed_completion_date != null) {
			eTextCBDescription.setText(proposal.tasker_comments);
			eTextCBMyPay.setText(proposal.my_pay.replaceAll(",", ""));
			eTextCBPosterPay.setText(proposal.poster_pay.replaceAll(",", ""));
			txtCBSelectCompletionTime.setText(proposal.proposed_duration);
		}
	}
	
	private void uploadApplyData() { 
		if (isValidate()) {
			arr = new JSONArray();
			try {
				if(questionList.size() != 0){
					for(Question question : questionList){
						if(question.reply_desc != null && question.reply_desc.trim().length() != 0){
							JSONObject object = new JSONObject();
							object.put(Question.FLD_TASK_QUESTION_ID,question.task_question_id);
							object.put(Question.FLD_QUESTION_ID,question.question_id);
							object.put(Question.FLD_REPLY_DESC,question.reply_desc);
							arr.put(object);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			proposal.agree_for_project_complete = chkCBIsComplete.isChecked() ? "1" : "0";
			proposal.agree_for_satisfaction = chkCBSatisfaction.isChecked() ? "1" : "0";
			if(arr.length() == questionList.size()){
				if (Util.isDeviceOnline(this)) {
					new ApplyTask().execute();
				} else {
					Util.showCenteredToast(this,getResources().getString(R.string.msg_Connect_internet));
				}
			}else{
				Util.showCenteredToast(getApplicationContext(), "Answers can't be blank");
			}
			
		}
	}

	private class ApplyTask extends AsyncTask<String, Void, NetworkResponse> {
		@Override
		protected void onPreExecute() {
			Util.showProDialog(CraftABidActivity.this);
			super.onPreExecute();
		}

		@Override
		protected NetworkResponse doInBackground(String... params) {
			Cmd cmd = null;
			if(proposal.viewMode.toString().equals("apply")){
				cmd = CmdFactory.createProposalCmd();
			}else if(proposal.viewMode.toString().equals("edit")){
				cmd = CmdFactory.editProposalCmd();
				cmd.addData(Proposal.FLD_TASK_TASKER_ID, proposal.task_tasker_id);
			}
			cmd.addData(Proposal.FLD_TASK_ID, proposal.projectData.task_id);
			cmd.addData(Proposal.FLD_TASKER_COMMENTS, eTextCBDescription.getText().toString());
			cmd.addData(Proposal.FLD_MY_PAY, eTextCBMyPay.getText().toString());
			cmd.addData(Proposal.FLD_POSTER_PAY, eTextCBPosterPay.getText().toString());
			cmd.addData(Proposal.FLD_PROPOSED_DURATION, txtCBSelectCompletionTime.getText().toString());
			cmd.addData(Proposal.FLD_MULTIQUESTION_REPLY, arr);
		
			cmd.addData(Proposal.FLD_PLAN_VALUE_IS_PERCENT, proposal.plan_value_is_percent);
			cmd.addData(Proposal.FLD_PLAN_VALUE, proposal.plan_value);
			cmd.addData(TaskDoer.FLD_SEAL_MY_PROPOSAL, chkCBSealProposal.isChecked() ? "1" : "0");
			cmd.addData(Proposal.FLD_AGREE_FOR_PROJECT_COMPLETE, proposal.agree_for_project_complete);
			cmd.addData(Proposal.FLD_AGREE_FOR_SATISFACTION, proposal.agree_for_satisfaction);
			
			NetworkResponse response = NetworkMgr.httpPost(Config.API_URL, cmd.getJsonData());
			return response;
		}

		@Override
		protected void onPostExecute(NetworkResponse networkResponse) {
			Util.dimissProDialog();
			if (networkResponse != null) {
				if (networkResponse.isSuccess()){
					setResult(RESULT_OK);
 				//	Util.showCenteredToast(CraftABidActivity.this, getResources().getString(R.string.msg_ApplyCompleted));
					finishActivity();
				}else{
					Util.showCenteredToast(CraftABidActivity.this, networkResponse.getErrMsg()); 
				}
			}
		}
	}
	
	private boolean isValidate() {
		if (eTextCBDescription.getText().toString().trim().length() == 0) {
			Util.showCenteredToast(CraftABidActivity.this,getResources().getString(R.string.msg_ProposalBlank));
		} else if (eTextCBDescription.getText().toString().length() < 60) {
			Util.showCenteredToast(this,getResources().getString(R.string.msg_ProposalShort));
		} else if (eTextCBMyPay.getText().toString().trim().length() == 0 ) {
			Util.showCenteredToast(CraftABidActivity.this,"Invalid My Pay(Can't be 0 & exceed to 6 digits)");
		} else if (Double.parseDouble(eTextCBMyPay.getText().toString().trim())<= 0.00) {
			Util.showCenteredToast(CraftABidActivity.this,"My Pay cannot be 0.");
		} else if (eTextCBPosterPay.getText().toString().trim().length() == 0 ) {
			Util.showCenteredToast(CraftABidActivity.this,"Invalid Poster Pay(Can't be 0 & exceed to 6 digits)");
		} else if (Double.parseDouble(eTextCBPosterPay.getText().toString().trim()) <= 0.000) {
			Util.showCenteredToast(CraftABidActivity.this,"Poster Pay cannot be 0.");
		} else if (txtCBSelectCompletionTime.getText().toString().trim().length() == 0) {
			Util.showCenteredToast(CraftABidActivity.this,getResources().getString(R.string.msg_SelectTime));
		} else if (!chkCBIsComplete.isChecked()) {
			Util.showCenteredToast(CraftABidActivity.this,getResources().getString(R.string.msg_Project_CompleteAgreeTerms));
		}else if (!chkCBSatisfaction.isChecked()) {
			Util.showCenteredToast(CraftABidActivity.this,getResources().getString(R.string.msg_SatisfactionAgreeTerms));
		} else if (!proposal.projectData.cash_required.equals("0.00") && !chkCBExpenses.isChecked()) {
			Util.showCenteredToast(CraftABidActivity.this,getResources().getString(R.string.msg_Expected_ExpensesAgreeTerms));
		}else {
			return true;
		}
		return false;
	}
	
	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		finishActivity();
	}

	private void finishActivity() {
		CraftABidActivity.this.finish();
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,int after) {}

	@Override
	public void afterTextChanged(Editable s) {}

}
