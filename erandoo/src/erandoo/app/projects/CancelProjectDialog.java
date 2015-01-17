package erandoo.app.projects;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.cmds.Cmd;
import erandoo.app.cmds.CmdFactory;
import erandoo.app.config.Config;
import erandoo.app.custom.CircularImageView;
import erandoo.app.custom.CustomDialog;
import erandoo.app.custom.IDataChangedListener;
import erandoo.app.database.DatabaseMgr;
import erandoo.app.database.Project;
import erandoo.app.database.TableType;
import erandoo.app.database.TaskDoer;
import erandoo.app.network.NetworkMgr;
import erandoo.app.network.NetworkResponse;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.SyncAppData;
import erandoo.app.utilities.Util;

public class CancelProjectDialog extends CustomDialog implements OnClickListener {
	
	private View view;
	private LinearLayout llPCancel;
	private LinearLayout llPSubmit;
	private LinearLayout llRefund;
	
	private TextView txtPCMSG;
	private TextView txtPCWarrningMSG;
	private TextView txtPCNote;
	
	private TextView txtPCTitle;
	private CircularImageView imvPCUserImage;
	private TextView txtPCUserName;
	private CheckBox chkPCRefundAmount;
	private EditText eTextRefundAmount;
	
	private EditText eTextReason;
	private CheckBox chkPCAccept;
	
	private Project project;
	private String projectAs;
	private TaskDoer taskDoer;
	
	private IDataChangedListener target;
	private DatabaseMgr databaseMgr;
	
	public static boolean isCancelled = false;
	
	public static CancelProjectDialog getInstance(IDataChangedListener target) {
		CancelProjectDialog fragment;
		fragment = new CancelProjectDialog();
		fragment.target = target;
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.ed_cancel_project, container, false);
		getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		databaseMgr = DatabaseMgr.getInstance(getActivity());
		project = (Project) getArguments().getSerializable(Constants.SERIALIZABLE_DATA);
		taskDoer = (TaskDoer) getArguments().getSerializable("Doer");
		projectAs = getArguments().getString("ProjectAS");
		initialize();
		return view;
	}
	
	private void initialize() {
		llPCancel = (LinearLayout) view.findViewById(R.id.llPCancel);
		llPSubmit = (LinearLayout) view.findViewById(R.id.llPSubmit);
		llRefund = (LinearLayout) view.findViewById(R.id.llRefund);
		
		txtPCMSG = (TextView) view.findViewById(R.id.txtPCMSG);
		txtPCNote = (TextView) view.findViewById(R.id.txtPCNote);
		txtPCWarrningMSG = (TextView) view.findViewById(R.id.txtPCWarrningMSG);
		chkPCRefundAmount = (CheckBox) view.findViewById(R.id.chkPCRefundAmount);
		eTextRefundAmount = (EditText) view.findViewById(R.id.eTextRefundAmount);
		
		eTextReason = (EditText) view.findViewById(R.id.eTextReason);
		chkPCAccept = (CheckBox) view.findViewById(R.id.chkPCAccept);

		txtPCTitle = (TextView) view.findViewById(R.id.txtPCTitle);
		txtPCUserName = (TextView) view.findViewById(R.id.txtPCUserName);
		imvPCUserImage = (CircularImageView) view.findViewById(R.id.imvPCUserImage);

		llPCancel.setOnClickListener(this);
		llPSubmit.setOnClickListener(this);
		
		setData();
		
		chkPCRefundAmount.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if (isChecked) {
					eTextRefundAmount.setVisibility(View.VISIBLE);
				}else{
					eTextRefundAmount.setVisibility(View.GONE);
				}
			}
		});
	}

	private void setData() {
		if (projectAs.equals(Constants.MY_PROJECT_AS_POSTER)) {
			if (project.state.equals("a")) {
				comman();
				Util.loadImage(imvPCUserImage, taskDoer.userimage, R.drawable.ic_launcher);
				txtPCUserName.setText(taskDoer.fullname);
				txtPCNote.setText(getResources().getString(R.string.msg_for_cancel_project) + " " + taskDoer.fullname);
			}else{
				txtPCMSG.setText(Html.fromHtml(getResources().getString(R.string.project_cancel_reminder) + " <font color=#e74c3c> <b>" + project.title + "</b></font>"));
				txtPCWarrningMSG.setText(getResources().getString(R.string.project_cancel_msg));
				txtPCNote.setText(getResources().getString(R.string.msg_for_cancel_project)+" all");
			}
		}else if (projectAs.equals(Constants.MY_PROJECT_AS_DOER)) {
			comman();
			Util.loadImage(imvPCUserImage, project.userimage, R.drawable.ic_launcher);
			txtPCUserName.setText(project.name);
			txtPCNote.setVisibility(View.GONE);
			chkPCRefundAmount.setText(getResources().getString(R.string.refund_amount_offer));
		}
		txtPCTitle.setText(project.title);
	}
	
	private void comman(){
		txtPCTitle.setVisibility(View.VISIBLE);
		txtPCUserName.setVisibility(View.VISIBLE);
		imvPCUserImage.setVisibility(View.VISIBLE);
		llRefund.setVisibility(View.VISIBLE);
		txtPCMSG.setText(Html.fromHtml(getResources().getString(R.string.project_cancel_awarded_reminder) + " <font color=#e74c3c> <b>" + project.title + "</b></font> " + getResources().getString(R.string.project_cancel_awarded_reminder_more) + " <font color=#e74c3c> <b>" + project.title + "</b></font> " + getResources().getString(R.string.project_cancel_awarded_reminder_still_more)));
		txtPCWarrningMSG.setText(getResources().getString(R.string.project_cancel_awarded_msg));
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.llPCancel) {
			Util.hideKeypad(getActivity(), eTextReason);
			dismiss();
		}else if (view.getId() == R.id.llPSubmit) {
			submit();
		}
	}
	
	private void submit(){
		if (eTextReason.getText().toString().length() == 0) {
			Util.showCenteredToast(getActivity(), getResources().getString(R.string.enter_reason));
		}else if (projectAs.equals(Constants.MY_PROJECT_AS_POSTER)){
			if (project.state.equals("a")) {
				if (chkPCRefundAmount.isChecked() && eTextRefundAmount.getText().toString().length() == 0) {
					Util.showCenteredToast(getActivity(), getResources().getString(R.string.enter_refund_amount));
				}else if (!chkPCAccept.isChecked()){
					Util.showCenteredToast(getActivity(), getResources().getString(R.string.accept_terms));
				}else{
					if(Util.isDeviceOnline(getActivity())){
						new CancelProjectTask().execute();
					}else{
						Util.showCenteredToast(getActivity(), getResources().getString(R.string.msg_Connect_internet));
					}
				}
			}else if (!chkPCAccept.isChecked()){
				Util.showCenteredToast(getActivity(), getResources().getString(R.string.accept_terms));
			}else{
				if(Util.isDeviceOnline(getActivity())){
					new CancelProjectTask().execute();
				}else{
					Util.showCenteredToast(getActivity(), getResources().getString(R.string.msg_Connect_internet));
				}
			}
		}else if (!chkPCAccept.isChecked()){
			Util.showCenteredToast(getActivity(), getResources().getString(R.string.accept_terms));
		}else{
			if(Util.isDeviceOnline(getActivity())){
				new CancelProjectTask().execute();
			}else{
				Util.showCenteredToast(getActivity(), getResources().getString(R.string.msg_Connect_internet));
			}
		}
	}
	
	private class CancelProjectTask extends AsyncTask<String, Void, NetworkResponse>{
		
		@Override
		protected void onPreExecute() {
			Util.showProDialog(getActivity());
		}

		@Override
		protected NetworkResponse doInBackground(String... params) {
			NetworkResponse response = null;
			Cmd cmd = null;
			if (Util.isDeviceOnline(getActivity())) {
				if (projectAs.equals(Constants.MY_PROJECT_AS_POSTER)) {
					if (project.state.equals("a")) {
						cmd = CmdFactory.createGetCancelProjectAfterAwardCmd();
						cmd.addData("trno", 0);
						cmd.addData("task_tasker_id", taskDoer.task_tasker_id);
						cmd.addData("task_cancel_reason", eTextReason.getText().toString());
						if (chkPCRefundAmount.isChecked()) {
							cmd.addData("cancel_refund_demand_by_poster", eTextRefundAmount.getText().toString());
						}
					}else{
						cmd = CmdFactory.createGetCancelProjectBeforeAwardCmd();
						cmd.addData(Project.FLD_TRNO, 0); 
						cmd.addData(Project.FLD_TASK_ID, project.task_id);
						cmd.addData("task_cancel_reason", eTextReason.getText().toString());
					}
				}else if (projectAs.equals(Constants.MY_PROJECT_AS_DOER)) {
					cmd = CmdFactory.createGetCancelProjectAfterAwardCmd();
					cmd.addData("trno", 0);
					cmd.addData("task_tasker_id", taskDoer.task_tasker_id);
					cmd.addData("task_cancel_reason", eTextReason.getText().toString());
					if (chkPCRefundAmount.isChecked()) {
						cmd.addData("cancel_refund_offer_by_doer", eTextRefundAmount.getText().toString());
					}
				}
				response = NetworkMgr.httpPost(Config.API_URL, cmd.getJsonData());
				if (response != null) {
					if (response.isSuccess()) {
						getPCResponseData(response);
					}
				}
				 
				SyncAppData.syncProjectData(getActivity());
			}
			return response;
		}
		
		@Override
		protected void onPostExecute(NetworkResponse response) {
			Util.dimissProDialog();
			if (response != null) {
				if (!response.isSuccess()) {
					Util.showCenteredToast(getActivity(), response.getErrMsg());
				}else{
					if (projectAs.equals(Constants.MY_PROJECT_AS_POSTER)) {
						if (project.state.equals("a")) {
							Util.showCenteredToast(getActivity(), getResources().getString(R.string.msg_project_cancelled_success));
						}else{
							Util.showCenteredToast(getActivity(), getResources().getString(R.string.msg_project_cancelled));
						}
					}else{
						Util.showCenteredToast(getActivity(), getResources().getString(R.string.msg_project_cancelled_success));
					}
					
					isCancelled = true;
					Util.hideKeypad(getActivity(), eTextReason);
					dismiss();
					target.onDataChanged();
				}
			}
		}
	}
	
	private void getPCResponseData(NetworkResponse response){
		try {
			JSONObject jsonObject = response.getJsonObject().getJSONObject("data");
			if (projectAs.equals(Constants.MY_PROJECT_AS_POSTER)) {
				if (project.state.equals("a")) {
					String task_tasker_id = jsonObject.getString("task_tasker_id");
					String project_status = jsonObject.getString("project_status");
					ContentValues contentValues = new ContentValues();
					contentValues.put(TaskDoer.FLD_PROJECT_STATUS, project_status);
					databaseMgr.sqLiteDb.update(TableType.TaskDoerTable.getTableName(), contentValues, "task_tasker_id=?", new String[]{task_tasker_id});
				}else{
					String task_id = jsonObject.getString("task_id");
					deleteTaskData(Integer.parseInt(task_id));
				}
			}else if (projectAs.equals(Constants.MY_PROJECT_AS_DOER)) {
				String task_tasker_id = jsonObject.getString("task_tasker_id");
				String project_status = jsonObject.getString("project_status");
				ContentValues contentValues = new ContentValues();
				contentValues.put(TaskDoer.FLD_PROJECT_STATUS, project_status);
				databaseMgr.sqLiteDb.update(TableType.TaskDoerTable.getTableName(), contentValues, "task_tasker_id=?", new String[]{task_tasker_id});
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void deleteTaskData(int taskId){
		Cursor cursor = null;
		int mobile_rec_id = 0;
		try {
			cursor = databaseMgr.queryTable(TableType.TaskTable, new String[] {Project.FLD_ID}, Project.FLD_TASK_ID + "=?", new String[] { String.valueOf(taskId) }, null, null, null);
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				mobile_rec_id = cursor.getInt(cursor.getColumnIndex(Project.FLD_ID));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		databaseMgr.deleteTableRow(TableType.TaskSkillTable, "mobile_rec_id=?",	new String[]{String.valueOf(mobile_rec_id)});
		databaseMgr.deleteTableRow(TableType.TaskLocationTable, "mobile_rec_id=?",	new String[]{String.valueOf(mobile_rec_id)});
		databaseMgr.deleteTableRow(TableType.TaskQuestionTable, "mobile_rec_id=?",	new String[]{String.valueOf(mobile_rec_id)});
		databaseMgr.deleteTableRow(TableType.TaskDoerTable, "mobile_rec_id=?",	new String[]{String.valueOf(mobile_rec_id)});
		databaseMgr.deleteTableRow(TableType.TaskTable, "task_id=?", new String[]{String.valueOf(taskId)});
	}
}
