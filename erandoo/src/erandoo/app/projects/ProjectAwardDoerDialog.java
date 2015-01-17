package erandoo.app.projects;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.cmds.Cmd;
import erandoo.app.cmds.CmdFactory;
import erandoo.app.custom.CustomDialog;
import erandoo.app.custom.IDataChangedListener;
import erandoo.app.database.Proposal;
import erandoo.app.network.NetworkMgr;
import erandoo.app.network.NetworkResponse;
import erandoo.app.utilities.Util;

public class ProjectAwardDoerDialog extends CustomDialog implements OnClickListener{
	View view;

	private LinearLayout llbtnCancel;
	private LinearLayout llbtnSubmit;

	private TextView txtPADTitle;
	private TextView txtPADotitle;
	private TextView txtPADRate;
	private TextView txtPADExpenses;
	private TextView txtPADCompleteDate;
	private TextView txtPADStartDate;
	private TextView txtPADCategory;
	private TextView txtPADType;
	private TextView txtPADUserName;
	private TextView txtPADHired;
	private TextView txtPADNetwork;
	private TextView txtPADJobs;
	private TextView txtPADWorklocation;
	private TextView txtPADDeclaration;

	private CheckBox chkPAD;
	private EditText etPADMessage;
	private ImageView imvPADUserImage;
	private RatingBar rBarPADRating;
	private RadioButton rdPADYes;
	private RadioButton rdPADNo;
	private int isradioButton;	
	private Proposal proposalData;
	private IDataChangedListener changedListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.ed_project_award_doer, container, false);
		getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		initialize();
		return view;
	}

	private void initialize() {
		llbtnCancel = (LinearLayout) view.findViewById(R.id.llbtnCancel);
		llbtnSubmit = (LinearLayout) view.findViewById(R.id.llbtnSubmit);
		txtPADTitle = (TextView) view.findViewById(R.id.txtPADTitle);
		txtPADotitle = (TextView) view.findViewById(R.id.txtPADotitle);
		txtPADRate = (TextView) view.findViewById(R.id.txtPADRate);
		txtPADExpenses = (TextView) view.findViewById(R.id.txtPADExpenses);
		txtPADCompleteDate = (TextView) view.findViewById(R.id.txtPADCompleteDate);
		txtPADStartDate = (TextView) view.findViewById(R.id.txtPADStartDate);
		txtPADCategory = (TextView) view.findViewById(R.id.txtPADCategory);
		txtPADType = (TextView) view.findViewById(R.id.txtPADType);
		txtPADUserName = (TextView) view.findViewById(R.id.txtPADUserName);
		txtPADHired = (TextView) view.findViewById(R.id.txtPADHired);
		txtPADNetwork = (TextView) view.findViewById(R.id.txtPADNetwork);
		txtPADJobs = (TextView) view.findViewById(R.id.txtPADJobs);
		txtPADWorklocation = (TextView) view.findViewById(R.id.txtPADWorklocation);
		txtPADDeclaration = (TextView) view.findViewById(R.id.txtPADDeclaration);
		rdPADYes = (RadioButton)view.findViewById(R.id.rdPADYes);
		rdPADNo = (RadioButton)view.findViewById(R.id.rdPADNo);
		
		chkPAD = (CheckBox) view.findViewById(R.id.chkPAD);
		etPADMessage = (EditText) view.findViewById(R.id.etPADMessage);
		rBarPADRating = (RatingBar) view.findViewById(R.id.rBarPADRating);
		imvPADUserImage = (ImageView) view.findViewById(R.id.imvPADUserImage);
		
		llbtnCancel.setOnClickListener(this);
		llbtnSubmit.setOnClickListener(this);
		
		setProjectAwardDoerData();
		if(proposalData.projectData.hiring_closed.equals("1")){
			isradioButton = 1;
			rdPADNo.setChecked(true);
			rdPADYes.setChecked(false);
			rdPADYes.setEnabled(false);
			rdPADNo.setEnabled(false);
		}
	}
	
	private void setProjectAwardDoerData(){
		txtPADDeclaration.setText(Html.fromHtml(getResources().getString(R.string.projectAwardDoer)
				+ " <font color=#e74c3c> <b>"
				+ proposalData.task_doer.fullname
				+ "</b></font> "
				+ getResources().getString(R.string.projectAwardDoerMore)
				+ " <font color=#e74c3c> <b>"
				+ proposalData.projectData.title
				+ "</b></font> "
				+ getResources().getString(R.string.projectAwardDoerStillMore)));
		
		txtPADTitle.setText(proposalData.projectData.title);
		txtPADotitle.setText(proposalData.projectData.title);
		txtPADExpenses.setText("$" + proposalData.projectData.cash_required);
		txtPADCompleteDate.setText(proposalData.complete_date);
		txtPADStartDate.setText(proposalData.projectData.project_start_date);
		txtPADCategory.setText(proposalData.projectData.category.category_name);
		txtPADType.setText(Util.getProjectTypeName(proposalData.projectData.task_kind));
		txtPADUserName.setText(proposalData.task_doer.fullname);
		txtPADHired.setText(proposalData.task_doer.hired);
		txtPADNetwork.setText(proposalData.task_doer.network);
		txtPADJobs.setText(proposalData.task_doer.task_done_cnt);
		txtPADWorklocation.setText(proposalData.task_doer.work_location);
		rBarPADRating.setRating(Float.valueOf(proposalData.task_doer.rating_avg_as_tasker));
		Util.loadImage(imvPADUserImage, proposalData.task_doer.userimage, R.drawable.ic_launcher);
		if(proposalData.projectData.payment_mode.toString().equals("h")){
			txtPADRate.setText("$" + proposalData.my_pay +"(Hourly)");
		}else{
			txtPADRate.setText("$" + proposalData.my_pay +"(Fixed)");
		}
		
	}

	private void loadHireData() {
		
		if(Util.isDeviceOnline(getActivity())){
			new HireAsyntask().execute();
		}
		 else {
			Util.showCenteredToast(getActivity(), getResources().getString(R.string.msg_Connect_internet));
		}
		
	}
	
	private class HireAsyntask extends AsyncTask<String, Void, NetworkResponse>{

		@Override
		protected void onPreExecute() {
			Util.showProDialog(getActivity());
			super.onPreExecute();
		}
		
		@Override
		protected NetworkResponse doInBackground(String... arg0) {
			Cmd cmd = CmdFactory.hireDoer();
			cmd.addData("operation" , "request_hire_confirm_to_doer");
			cmd.addData("task_tasker_id", proposalData.task_tasker_id);
			cmd.addData("agree_for_terms", chkPAD.isChecked());
			cmd.addData("hiring_closed", isradioButton);
			cmd.addData("message", etPADMessage.getText().toString().trim());
			NetworkResponse response = NetworkMgr.httpPost(erandoo.app.config.Config.API_URL, cmd.getJsonData());
			return response;
		}
		
		@Override
		protected void onPostExecute(NetworkResponse networkResponse) {
			Util.dimissProDialog();
			if(networkResponse != null){
				if(!networkResponse.isSuccess()){
					Util.showCenteredToast(getActivity(), networkResponse.getErrMsg()); 
				}else {
				//	Util.showCenteredToast(getActivity(), getResources().getString(R.string.Hired));
					dismissDialog();
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.llbtnCancel){
			dismissDialog();
		}else if(v.getId() == R.id.llbtnSubmit){
			if(isValidate()){
				loadHireData();
			}
		}
	}
	
	private boolean isValidate(){
		if(rdPADYes.isChecked()){
			isradioButton = 0;
		}else{
			isradioButton = 1;
		}
		if (!chkPAD.isChecked()) {
			Util.showCenteredToast(getActivity(), getResources().getString(R.string.agreeTerms));
		} else if(etPADMessage.getText().toString().trim().length() == 0){
			Util.showCenteredToast(getActivity(), getResources().getString(R.string.MessageToast));
		}else {
			return true;
		}
		return false;
	}
	
	public static ProjectAwardDoerDialog getInstance(Proposal proposal, IDataChangedListener changedListener) {
		ProjectAwardDoerDialog fragment;
		fragment = new ProjectAwardDoerDialog();
		fragment.proposalData = proposal;
		fragment.changedListener = changedListener;
		return fragment;
	}
	
	private void dismissDialog(){
		changedListener.onDataChanged();
		Util.hideKeypad(view.getContext(), etPADMessage);
		ProjectAwardDoerDialog.this.dismiss();
	}
}
