package erandoo.app.projects;

import java.util.ArrayList;
import java.util.HashSet;
import org.json.JSONArray;
import org.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import erandoo.app.R;
import erandoo.app.adapters.DoerListAdapter;
import erandoo.app.cmds.Cmd;
import erandoo.app.cmds.CmdFactory;
import erandoo.app.config.Config;
import erandoo.app.database.TaskDoer;
import erandoo.app.main.AppGlobals;
import erandoo.app.main.CreateProjectActivity;
import erandoo.app.main.InviteDoersActivity;
import erandoo.app.network.NetworkMgr;
import erandoo.app.network.NetworkResponse;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.Util;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;

public class FragInviteDoers extends Fragment implements OnClickListener{
	private View view;
	private EditText eTextInviteDoerSearch;
	private ImageButton imgBtnIDoerClearSearch;

	private LinearLayout lLInviteDoerSearch;
	private ListView listVInviteDoers;
	private ProgressBar pBarIDoers;

	private boolean isMoreRecords;
	private int pageCount = 0;

	private int currentFirstVisibleItem = 0;
	private int currentVisibleItemCount = 0;
	private int totalItemCount1 = 0;
	private int currentScrollState = 0;

	private ArrayList<TaskDoer> selectedDoers = new ArrayList<TaskDoer>(0);
	private HashSet<Long> selectedUserIdSet = new HashSet<Long>(0);
	private DoerListAdapter adapter;
	private String projectType;
	private InviteDoerTask inviteDoerTask;
	private boolean onScroll = false;
	private static String searchType = TaskDoer.FLD_SEARCH_KEY;
	private ArrayList<TaskDoer> invitedDoers;
	private boolean onFragLoad = true; 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.ed_cp_invite_doers_frag, container, false);
		initialize();
		return view;
	}

	private void initialize() {
		invitedDoers = new ArrayList<TaskDoer>(0);
		selectedDoers = CreateProjectActivity.updateInvitedDoerList(null);
		eTextInviteDoerSearch = (EditText) view.findViewById(R.id.eTextInviteDoerSearch);
		pBarIDoers = (ProgressBar)view.findViewById(R.id.pBarIDoers);
		listVInviteDoers = (ListView) view.findViewById(R.id.listVInviteDoers);
		lLInviteDoerSearch = (LinearLayout) view.findViewById(R.id.lLInviteDoerSearch);
		imgBtnIDoerClearSearch = (ImageButton)view.findViewById(R.id.imgBtnIDoerClearSearch);
		imgBtnIDoerClearSearch.setOnClickListener(this);
		searchType = CreateProjectActivity.invitedDoersType;
		
		eTextInviteDoerSearch.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_SEARCH){
					Util.hideKeypad(getActivity(), eTextInviteDoerSearch);
					searchType = TaskDoer.FLD_SEARCH_KEY;
					resetSearchData();
				}
				return false;
			}
		});
		
		listVInviteDoers.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,int position, long id) {
				if(invitedDoers.get(position).isSelected){
					invitedDoers.get(position).isSelected = false; 
				}else{
					invitedDoers.get(position).isSelected = true; 
				}
				TaskDoer doer = (TaskDoer)adapterView.getItemAtPosition(position);
				selectedUserIdSet.add(doer.user_id);
				selectedDoers = CreateProjectActivity.updateInvitedDoerList(doer); 
				adapter.selectedDoerIds(selectedDoers);
				adapter.notifyDataSetChanged();
			}
		});
		setSearchbarVisibility();
		setScrollListenerOnProjectList();
		resetSearchData();
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.imgBtnIDoerClearSearch){
			clearSearch();
		}
	}
	
	
	private void clearSearch(){
		if(eTextInviteDoerSearch.getText().toString().length()>0){
			eTextInviteDoerSearch.setText(""); 
			resetSearchData();
		}
	}
	
	private void resetSearchData() {
		invitedDoers.clear();
		pageCount = 0;
		onScroll = false;
		loadInviteDoerListData();
		if(adapter != null){
			adapter.notifyDataSetChanged();
		}
	}
	
	public void clearSelectedUserIds(){
		for (Long userId : selectedUserIdSet) {
			for(TaskDoer doer : selectedDoers){
				if(doer.user_id.equals(userId)){ 
					selectedDoers.remove(doer);
					break;
				}
			}
		}
		selectedUserIdSet.clear();
	}
	
	private void loadInviteDoerListData(){
		Util.clearAsync(inviteDoerTask);
		if(Util.isDeviceOnline(getActivity())){
			inviteDoerTask = new InviteDoerTask();
			inviteDoerTask.execute();
		}else{
			Util.showCenteredToast(view.getContext(), getResources().getString(R.string.msg_Connect_internet)); 
		}
	}
	
	private class InviteDoerTask extends AsyncTask<Void, Void,NetworkResponse>{
		@Override
		protected void onPreExecute() {
			if(onFragLoad){
				onFragLoad = false;
				Util.showProDialog(getActivity()); 
			}else{
				pBarIDoers.setVisibility(View.VISIBLE);
			}
		};
		
		@Override
		protected NetworkResponse doInBackground(Void... params) {
			Cmd cmd = CmdFactory.createSearchDoerCmd();
			if(searchType.equals(TaskDoer.FLD_SEARCH_KEY)){
				cmd.addData(TaskDoer.FLD_SEARCH_KEY,eTextInviteDoerSearch.getText().toString().trim()); 
			}else{
				cmd.addData(TaskDoer.FLD_QUICK_FILTER,searchType); 
			}
			cmd.addData(TaskDoer.FLD_PAGE,pageCount);
			cmd.addData(TaskDoer.FLD_PROJECT_TYPE, projectType); 
			cmd.addData(TaskDoer.FLD_LONGITUDE, AppGlobals.defaultWLocation.longitude);
			cmd.addData(TaskDoer.FLD_LATITUDE, AppGlobals.defaultWLocation.latitude);
			NetworkResponse response = NetworkMgr.httpPost(Config.API_URL, cmd.getJsonData());
			if(response != null){
				if(response.isSuccess()){
					pageCount = pageCount + 1;
					getInvitedDoerListData(response.getJsonObject());
				}
			}
			return response;
		}
		
		@Override
		protected void onPostExecute(NetworkResponse response) {
		 	super.onPostExecute(response);
			Util.dimissProDialog();
			pBarIDoers.setVisibility(View.GONE);
			if(response != null){
				if (!response.isSuccess()) {
					isMoreRecords = false;
					Util.showCenteredToast(view.getContext(),response.getErrMsg());
				} else {
					setInviteDoerListData();
				}
			}
		}
	}
	
	private void getInvitedDoerListData(JSONObject jsonObject){ 
		try{
			JSONArray array = jsonObject.getJSONArray(Constants.FLD_DATA);
			isMoreRecords = jsonObject.getInt(Constants.FLD_IS_MORE)==1?true:false;
			if(array.length()>0){
				for (int i = 0; i < array.length(); i++) {
					Gson gson = new GsonBuilder().create();
					TaskDoer doerDetail = gson.fromJson(array.getJSONObject(i).toString(), TaskDoer.class);
					invitedDoers.add(doerDetail);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void setInviteDoerListData(){
		if(invitedDoers.size()>0){
			if(!onScroll){
				adapter = new DoerListAdapter(view.getContext(), R.layout.ed_cp_invite_doer_row, invitedDoers);
				adapter.setNotifyOnChange(true); 
				adapter.selectedDoerIds(selectedDoers);
				listVInviteDoers.setAdapter(adapter);
			}else{
				adapter.notifyDataSetChanged();
			}
		}
	}
	
	private void setSearchbarVisibility(){
		if(searchType.equals(InviteDoersActivity.TAB_FEATURED)){
			lLInviteDoerSearch.setVisibility(View.GONE); 
		}else if(searchType.equals(InviteDoersActivity.TAB_PREVIOUS)){
			lLInviteDoerSearch.setVisibility(View.GONE); 
		}else if(searchType.equals(InviteDoersActivity.TAB_FAVORITE)){
			lLInviteDoerSearch.setVisibility(View.GONE); 
		}else if(searchType.equals(InviteDoersActivity.TAB_SEARCH)){
			lLInviteDoerSearch.setVisibility(View.VISIBLE); 
		}
	}
	
	//----------------List View ScrollListener----------------------
	
	private void setScrollListenerOnProjectList(){
		listVInviteDoers.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				currentScrollState = scrollState;
			    isScrollCompleted();
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				currentFirstVisibleItem = firstVisibleItem;
			    currentVisibleItemCount = visibleItemCount;
			    totalItemCount1 = totalItemCount;
			}
		});
	}
	
	private void isScrollCompleted() {
	    if (currentVisibleItemCount > 0 && currentScrollState == OnScrollListener.SCROLL_STATE_IDLE &&
	    		totalItemCount1 == (currentFirstVisibleItem + currentVisibleItemCount)) {
	        /*** In this way I detect if there's been a scroll which has completed ***/
	        /*** do the work for load more date! ***/
	        if (isMoreRecords) {
	        	onScroll = true;
	        	loadInviteDoerListData();
	        }else{
	        	Util.showCenteredToast(view.getContext(), getResources().getString(R.string.No_more_records));
	        }
	    }
	}
}
