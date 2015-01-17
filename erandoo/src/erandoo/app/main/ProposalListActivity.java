package erandoo.app.main;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.adapters.ProposalListAdapter;
import erandoo.app.cmds.Cmd;
import erandoo.app.cmds.CmdFactory;
import erandoo.app.config.Config;
import erandoo.app.custom.AppHeaderView;
import erandoo.app.custom.BaseFragActivity;
import erandoo.app.database.Project;
import erandoo.app.database.Proposal;
import erandoo.app.network.NetworkMgr;
import erandoo.app.network.NetworkResponse;
import erandoo.app.tempclasses.ProposalData;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.Util;

public class ProposalListActivity extends BaseFragActivity implements
		OnClickListener {
	private ArrayList<Proposal> proposalList = new ArrayList<Proposal>();
	
	private TextView txtPRTitle;
	
	private TextView txtPRPostedDate;
	private TextView txtPRCompleteDate;
	private TextView txtPRType;
	private TextView txtPRTotalProposal;
	private TextView txtPRBudget;
	private TextView txtPRAveragePrice;

	private RatingBar rBarPRRating;

	private ListView lvPRList;
	private Project proposaldata;

	private ProposalListAdapter adapter;
	private AppHeaderView appHeaderView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ed_proposal_list);
		proposaldata = (Project) getIntent().getSerializableExtra(Constants.SERIALIZABLE_DATA);
		initialize();
	}

	public void initialize() {
		txtPRTitle = (TextView) findViewById(R.id.txtPRTitle);
		txtPRPostedDate = (TextView) findViewById(R.id.txtPRPostedDate);
		txtPRCompleteDate = (TextView) findViewById(R.id.txtPRCompleteDate);
		txtPRType = (TextView) findViewById(R.id.txtPRType);
		txtPRTotalProposal = (TextView) findViewById(R.id.txtPRTotalProposal);
		txtPRBudget = (TextView) findViewById(R.id.txtPRBudget);
		txtPRAveragePrice = (TextView) findViewById(R.id.txtPRAveragePrice);
		lvPRList = (ListView) findViewById(R.id.lvPRList);
		rBarPRRating = (RatingBar) findViewById(R.id.rBarPRRating);

		setHeaderView();
		setProjectData();
		loadProposalListData();
	}
	
	private void setHeaderView() {
		appHeaderView = (AppHeaderView) findViewById(R.id.appHeaderView);
		appHeaderView.setHeaderContent(
				getResources().getDrawable(R.drawable.ic_back), getResources()
						.getString(R.string.proposal),null, null, Gravity.LEFT);
	}

	public void loadProposalListData() {
		proposalList.clear();
		if (Util.isDeviceOnline(this)) {
			new ProposalListTask().execute();
		} else {
			Util.showCenteredToast(this, getResources().getString(R.string.msg_Connect_internet));
		}
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == appHeaderView.txtHLeft.getId()){
			//TODO back button
			finishActivity();
		} 
	}

	private class ProposalListTask extends
			AsyncTask<String, Void, NetworkResponse> {
		@Override
		protected void onPreExecute() {
			Util.showProDialog(ProposalListActivity.this);
		}

		@Override
		protected NetworkResponse doInBackground(String... arg0) {
			Cmd cmd = CmdFactory.getProposalListCmd();
			//cmd.addData(Project.FLD_TRNO, 0);
			cmd.addData(Project.FLD_TASK_ID, proposaldata.task_id);
			NetworkResponse response = NetworkMgr.httpPost(Config.API_URL,
					cmd.getJsonData());
			if (response != null) {
				if (response.isSuccess()) {
					getProposalList(response);
				}
			}
			return response;
		}

		@Override
		protected void onPostExecute(NetworkResponse networkResponse) {
			Util.dimissProDialog();
			if (networkResponse != null) {
				if (!networkResponse.isSuccess()) {
					Util.showCenteredToast(ProposalListActivity.this,
							networkResponse.getErrMsg());
				} else {
					setProposalListData();
				}
			}
		}
	}

	public void getProposalList(NetworkResponse response) {
		try {
			ProposalData pListData = (ProposalData) Util.getJsonToClassObject(response.getJsonObject().toString(),ProposalData.class);
			if (pListData != null) {
				for (Proposal proposal : pListData.data) {
					proposal.projectData = proposaldata;
					proposalList.add(proposal);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setProposalListData() {
		adapter = new ProposalListAdapter(ProposalListActivity.this,R.layout.ed_proposal_row, proposalList);
		adapter.setNotifyOnChange(true);
		lvPRList.setAdapter(adapter);
	}

	private void setProjectData() {
		txtPRType.setText(Util.getProjectTypeName(proposaldata.task_kind)); 
		txtPRTitle.setText(proposaldata.title);
		txtPRPostedDate.setText(proposaldata.created_at);
		txtPRCompleteDate.setText(proposaldata.bid_close_dt);
		txtPRTotalProposal.setText(proposaldata.proposals_cnt);
		txtPRAveragePrice.setText("$" + proposaldata.proposals_avg_price);
		txtPRBudget.setText(Util.getBudgetString(proposaldata)); 
		rBarPRRating.setRating(Float.parseFloat(proposaldata.average_rating));
	}

	@Override
	public void onBackPressed() {
		finishActivity();
	}

	private void finishActivity() {
		ProposalListActivity.this.finish();
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}

}
