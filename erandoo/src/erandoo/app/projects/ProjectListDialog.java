package erandoo.app.projects;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.adapters.ProjectListAdapter;
import erandoo.app.cmds.Cmd;
import erandoo.app.cmds.CmdFactory;
import erandoo.app.custom.CustomDialog;
import erandoo.app.database.Project;
import erandoo.app.network.NetworkMgr;
import erandoo.app.network.NetworkResponse;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.SyncAppData;
import erandoo.app.utilities.Util;

public class ProjectListDialog extends CustomDialog implements OnClickListener {
	private View view;
	private LinearLayout llSListSearchBar;
	private ImageButton imgBtnSimpleListBack;
	private TextView txtSimpleListTitle;
	private ListView listVSimpleList;
	private ArrayList<Project> projectList;
	private ProjectListAdapter adapter;
	private Project project;

	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.ed_simple_list, container, false);
		projectList = (ArrayList<Project>) getArguments().getSerializable(Constants.SERIALIZABLE_DATA);
		initialize();
		return view;
	}

	private void initialize() {
		llSListSearchBar = (LinearLayout) view.findViewById(R.id.llSListSearchBar);
		imgBtnSimpleListBack = (ImageButton) view.findViewById(R.id.imgBtnSimpleListBack);
		txtSimpleListTitle = (TextView) view.findViewById(R.id.txtSimpleListTitle);
		listVSimpleList = (ListView) view.findViewById(R.id.listVSimpleList);

		llSListSearchBar.setVisibility(View.GONE);
		txtSimpleListTitle.setText("Project List");
		imgBtnSimpleListBack.setOnClickListener(this);
		setAdapter();

		listVSimpleList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				project = (Project) adapter.getItemAtPosition(position);
				if (!project.isSelected) {
					loadData();
				}
			}
		});
	}

	private void loadData() {
		if (Util.isDeviceOnline(getActivity())) {
			new InviteMemberTask().execute();
		} else {
			Util.showCenteredToast(getActivity(),
					getResources().getString(R.string.msg_Connect_internet));
		}
	}

	private class InviteMemberTask extends AsyncTask<String, Void, NetworkResponse> {

		@Override
		protected void onPreExecute() {
			Util.showProDialog(getActivity());
		}

		@Override
		protected NetworkResponse doInBackground(String... params) {
			Cmd cmd = CmdFactory.inviteProjectDoerCmd();
			cmd.addData(Project.FLD_TASK_ID, project.task_id);
			cmd.addData("tasker_id", project.tasker_id);

			NetworkResponse response = NetworkMgr.httpPost(erandoo.app.config.Config.API_URL, cmd.getJsonData());
			if (response != null) {
				if (response.isSuccess()) {
					SyncAppData.syncProjectData(getActivity());
				} 
			}
			return response;
		}

		@Override
		protected void onPostExecute(NetworkResponse response) {
			Util.dimissProDialog();
			if (response != null) {
				if (!response.isSuccess()) {
					Util.showCenteredToast(getActivity(), response.getErrMsg());
				} else {
					dismissDialog();
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.imgBtnSimpleListBack) {
			dismissDialog();
		}
	}

	private void setAdapter() {
		adapter = new ProjectListAdapter(getActivity(), R.layout.ed_simple_list_row, projectList);
		listVSimpleList.setAdapter(adapter);
	}

	private void dismissDialog() {
		ProjectListDialog.this.dismiss();
	}

}
