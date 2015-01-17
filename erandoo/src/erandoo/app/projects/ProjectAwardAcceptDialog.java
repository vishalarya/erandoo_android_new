package erandoo.app.projects;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.cmds.Cmd;
import erandoo.app.cmds.CmdFactory;
import erandoo.app.custom.CustomDialog;
import erandoo.app.custom.IDataChangedListener;
import erandoo.app.database.Proposal;
import erandoo.app.network.NetworkMgr;
import erandoo.app.network.NetworkResponse;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.Util;

public class ProjectAwardAcceptDialog extends CustomDialog implements OnClickListener{
	View view;
	private LinearLayout llPAADDecline;
	private LinearLayout llPAADAccept;
	private TextView txtPAADTitle;
	private TextView txtcongrats;
	private TextView txtPAADPTitle;
	private TextView txtPAADRate;
	private TextView txtPAADExpenses;
	private TextView txtPAADComplete;
	private TextView txtPAADStartDate;
	private TextView txtPAADCategory;
	private TextView txtPAADType;
	private TextView txtPosterName;
	private CheckBox chkPAAD;
	private TextView txtPAADMessage;
	private EditText etPAADReply;
	private Proposal proposalData;
	private boolean isAccept;
	private IDataChangedListener changedListener;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.ed_project_award_accept, container, false);
		proposalData  = (Proposal) getArguments().getSerializable(Constants.SERIALIZABLE_DATA);
		getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		initialize();
		return view;
	}
	
	private void initialize(){
		llPAADDecline = (LinearLayout)view.findViewById(R.id.llPAADDecline);
		llPAADAccept = (LinearLayout)view.findViewById(R.id.llPAADAccept);
		txtPAADTitle = (TextView)view.findViewById(R.id.txtPAADTitle);
		txtcongrats = (TextView)view.findViewById(R.id.txtcongrats);
		txtPAADPTitle = (TextView)view.findViewById(R.id.txtPAADPTitle);
		txtPAADRate = (TextView)view.findViewById(R.id.txtPAADRate);
		txtPAADExpenses = (TextView)view.findViewById(R.id.txtPAADExpenses);
		txtPAADComplete = (TextView)view.findViewById(R.id.txtPAADComplete);
		txtPAADStartDate = (TextView)view.findViewById(R.id.txtPAADStartDate);
		txtPAADCategory = (TextView)view.findViewById(R.id.txtPAADCategory);
		txtPAADType = (TextView)view.findViewById(R.id.txtPAADType);
		txtPAADMessage = (TextView)view.findViewById(R.id.txtPAADMessage);
		txtPosterName = (TextView)view.findViewById(R.id.txtPosterName);
		chkPAAD = (CheckBox)view.findViewById(R.id.chkPAAD);
		etPAADReply = (EditText)view.findViewById(R.id.etPAADReply);
		
		llPAADDecline.setOnClickListener(this);
		llPAADAccept.setOnClickListener(this);
		
		setData();
	}

	private void setData() {
		txtcongrats.setText(Html.fromHtml(getResources().getString(R.string.projectAwardAccept)
				+ " <font color=#e74c3c> <b>"
				+ proposalData.projectData.name
				+ "</b></font> "
				+ getResources().getString(R.string.projectAwardAcceptMore)
				+ " <font color=#e74c3c> <b>"
				+ proposalData.projectData.title
				+ "</b></font> "
				+ getResources().getString(R.string.projectAwardAcceptStillMore)));
		
		txtPAADTitle.setText(proposalData.projectData.title);
		txtPAADPTitle.setText(proposalData.projectData.title);
		txtPAADExpenses.setText("$"+proposalData.projectData.cash_required);
		txtPAADComplete.setText(proposalData.complete_date);
		txtPAADStartDate.setText(proposalData.projectData.project_start_date);
		txtPAADCategory.setText(proposalData.projectData.category.category_name);
		txtPAADType.setText(Util.getProjectTypeName(proposalData.projectData.task_kind));
		txtPAADMessage.setText(proposalData.poster_message);
		txtPosterName.setText(proposalData.projectData.name);
		if(proposalData.projectData.payment_mode.toString().equals("h")){
			txtPAADRate.setText("$" + proposalData.my_pay +"(Hourly)");
		}else{
			txtPAADRate.setText("$" + proposalData.my_pay +"(Fixed)");
		}
		
		chkPAAD.isChecked();
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.llPAADDecline){
			dialogbox();
			isAccept = false;
		}else if(v.getId() == R.id.llPAADAccept){
			if(isValidate()){
			isAccept = true;
			loadHireDoerData();
			}
		}
	}
	
	private void loadHireDoerData() {
		if(Util.isDeviceOnline(getActivity())){
			new HireDoerAsyntask().execute();
		} else {
			Util.showCenteredToast(getActivity(),
					getResources().getString(R.string.msg_Connect_internet));
		}
	}
	
	private class HireDoerAsyntask extends AsyncTask<String, Void, NetworkResponse>{

		@Override
		protected void onPreExecute() {
			Util.showProDialog(getActivity());
			super.onPreExecute();
		}
		
		@Override
		protected NetworkResponse doInBackground(String... arg0) {
			Cmd cmd = CmdFactory.hireDoer();
			if(isAccept){
			cmd.addData(Constants.FLD_OPERATION , "accept_hiring_offer");
			cmd.addData(Constants.MESSAGE_KEY, etPAADReply.getText().toString().trim());
			}else{
				cmd.addData(Constants.FLD_OPERATION , "decline_hiring_offer");
			}
			cmd.addData(Proposal.FLD_TASK_TASKER_ID, proposalData.task_tasker_id);
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
					dismissDialog();
				}
			}
		}
	}
	
	private boolean isValidate(){
		if (!chkPAAD.isChecked()) {
			Util.showCenteredToast(getActivity(), getResources().getString(R.string.agreeTerms));
		} else if(etPAADReply.getText().toString().trim().length() == 0){
			Util.showCenteredToast(getActivity(), getResources().getString(R.string.replyMessageToast));
		}else {
			return true;
		}
		return false;
	}
	
	public void dialogbox(){
		final Dialog dialog = new Dialog(getActivity());
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.ed_custom_dialog);
		TextView txtDialogNo = (TextView) dialog.findViewById(R.id.txtDialogNo);
		TextView txtDialogYes = (TextView) dialog.findViewById(R.id.txtDialogYes);
		
		txtDialogYes.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				loadHireDoerData();	
				dialog.dismiss();
			}
		});
		txtDialogNo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	// --------------------------CUSTOM EVENT
	public static ProjectAwardAcceptDialog getInstance(IDataChangedListener changedListener) {
		ProjectAwardAcceptDialog fragment;
		fragment = new ProjectAwardAcceptDialog();
		fragment.changedListener = changedListener;
		return fragment;
	}
	
	private void dismissDialog(){
		changedListener.onDataChanged();
		Util.hideKeypad(view.getContext(), etPAADReply);
		ProjectAwardAcceptDialog.this.dismiss();
	}

}
