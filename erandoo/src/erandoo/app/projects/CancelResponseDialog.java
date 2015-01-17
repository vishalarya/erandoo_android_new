package erandoo.app.projects;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.cmds.Cmd;
import erandoo.app.cmds.CmdFactory;
import erandoo.app.config.Config;
import erandoo.app.custom.CustomDialog;
import erandoo.app.custom.IDataChangedListener;
import erandoo.app.database.Project;
import erandoo.app.database.TaskDoer;
import erandoo.app.main.AppGlobals;
import erandoo.app.network.NetworkMgr;
import erandoo.app.network.NetworkResponse;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.SyncAppData;
import erandoo.app.utilities.Util;

public class CancelResponseDialog extends CustomDialog implements OnClickListener{
	
	private View view;
	
	private LinearLayout llDoNotApprove;
	private LinearLayout llApprove;
	private TextView txtCancellationMessage;
	
	private TextView txtCRName;
	private TextView txtProjectName;
	private TextView txtCancellationReason;
	private EditText eTextViewMessage;
	private ImageView imvCRImage;

	private Project project;
	private String projectAs;
	private TaskDoer taskDoer;
	
	private IDataChangedListener target;
	
	private boolean fromCancel = false;
	
	public static CancelResponseDialog getInstance(IDataChangedListener target) {
		CancelResponseDialog fragment;
		fragment = new CancelResponseDialog();
		fragment.target = target;
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.ed_cancel_response, container, false);
		getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		project = (Project) getArguments().getSerializable(Constants.SERIALIZABLE_DATA);
		taskDoer = (TaskDoer) getArguments().getSerializable("Doer");
		projectAs = getArguments().getString("ProjectAS");
		initialize();
		return view;
	}

	private void initialize() {
		llDoNotApprove = (LinearLayout) view.findViewById(R.id.llDoNotApprove);
		llApprove = (LinearLayout) view.findViewById(R.id.llApprove);
		txtCancellationMessage = (TextView) view.findViewById(R.id.txtCancellationMessage);
		txtCRName = (TextView) view.findViewById(R.id.txtCRName);
		txtProjectName = (TextView) view.findViewById(R.id.txtProjectName);
		txtCancellationReason = (TextView) view.findViewById(R.id.txtCancellationReason);
		eTextViewMessage = (EditText) view.findViewById(R.id.eTextViewMessage);
		imvCRImage = (ImageView) view.findViewById(R.id.imvCRImage);
		
		llDoNotApprove.setOnClickListener(this);
		llApprove.setOnClickListener(this);
		
		setData();
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.llDoNotApprove) {
			if (eTextViewMessage.getText().toString().trim().length() == 0) {
				Util.showCenteredToast(getActivity(), "Cancel Reason Reply cannot be blank.");
			}else{
				fromCancel = false;
				if(Util.isDeviceOnline(getActivity())){
					new CancelProjectResponseTask().execute(new String[]{"0"});
				}else{
					Util.showCenteredToast(getActivity(), getResources().getString(R.string.msg_Connect_internet));
				}
			}
		} else if (view.getId() == R.id.llApprove){
			fromCancel = true;
			if(Util.isDeviceOnline(getActivity())){
			new CancelProjectResponseTask().execute(new String[]{"1"});
			}else{
				Util.showCenteredToast(getActivity(), getResources().getString(R.string.msg_Connect_internet));
			}
		}
	} 
	
	private void setData(){
		if (projectAs.equals(Constants.MY_PROJECT_AS_POSTER)) {
			txtCancellationMessage.setText(Html.fromHtml(" <font color=#e74c3c> <b>" + taskDoer.fullname + "</b></font>" + " has submitted to cancel " + " <font color=#e74c3c> <b>" + project.title + "."+ "</b></font>" + " If you'd like to learn more about the process of cancelling a project, please click on the following link "+ " <font color=#e74c3c> <b>" + " www.erandoo.com/cancel_the_project.xml" + "</b></font>"));
			txtCancellationReason.setText(taskDoer.cancel_reason_by_doer);
		} else if (projectAs.equals(Constants.MY_PROJECT_AS_DOER)) {
			txtCancellationMessage.setText(Html.fromHtml(" <font color=#e74c3c> <b>" + project.name + "</b></font>" + " has submitted to cancel " + " <font color=#e74c3c> <b>" + project.title + "."+ "</b></font>" + " If you'd like to learn more about the process of cancelling a project, please click on the following link "+ " <font color=#e74c3c> <b>" + " www.erandoo.com/cancel_the_project.xml" + "</b></font>"));
			txtCancellationReason.setText(taskDoer.cancel_reason_by_poster);
		}
		txtProjectName.setText(project.title);
		txtCRName.setText(AppGlobals.userDetail.fullname);
		Util.loadImage(imvCRImage, AppGlobals.userDetail.userimage,R.drawable.ic_launcher);
	}
	
	private class CancelProjectResponseTask extends AsyncTask<String, Void, NetworkResponse>{
		
		@Override
		protected void onPreExecute() {
			Util.showProDialog(getActivity());
		}

		@Override
		protected NetworkResponse doInBackground(String... params) {
			NetworkResponse response = null;
			Cmd cmd = null;
			if (Util.isDeviceOnline(getActivity())) {
				cmd = CmdFactory.createGetCancelProjectAfterAwardResponseCmd();
				cmd.addData("task_tasker_id", taskDoer.task_tasker_id);
				cmd.addData("is_approve", params[0]);
				if (params[0].equals("0")) {
					cmd.addData("task_cancel_reply", eTextViewMessage.getText().toString());
				}
				response = NetworkMgr.httpPost(Config.API_URL, cmd.getJsonData());
				SyncAppData.syncProjectData(getActivity());
			}
			return response;
		}
		
		@Override
		protected void onPostExecute(NetworkResponse response) {
			Util.dimissProDialog();
			if (response != null) {
				if (response.isSuccess()) {
					if (fromCancel) {
						Util.showCenteredToast(getActivity(), "Project cancelled successfully");
					}else{
						Util.showCenteredToast(getActivity(), "Project under dispute.");
					}
				}else{
					Util.showCenteredToast(getActivity(), response.getErrMsg());
				}
				Util.hideKeypad(getActivity(), eTextViewMessage);
				dismiss();
				target.onDataChanged();
			}
		}
	}
}
