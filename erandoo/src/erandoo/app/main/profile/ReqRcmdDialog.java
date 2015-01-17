package erandoo.app.main.profile;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import erandoo.app.R;
import erandoo.app.adapters.ProfileRecommandationAdapter;
import erandoo.app.adapters.SearchDoerListAdapter;
import erandoo.app.cmds.Cmd;
import erandoo.app.cmds.CmdFactory;
import erandoo.app.config.Config;
import erandoo.app.custom.CustomDialog;
import erandoo.app.database.Profile;
import erandoo.app.database.Recommandation;
import erandoo.app.database.TaskDoer;
import erandoo.app.main.AppGlobals;
import erandoo.app.network.NetworkMgr;
import erandoo.app.network.NetworkResponse;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.Util;

public class ReqRcmdDialog extends CustomDialog implements OnClickListener{
	private ImageView imgDBack;
	private TextView txtDHTitle;
	private TextView txtDHRight;
	
	private View view;
	private ListView lvRcmdList;
	private Button btnRRSubmit;
	private Button btnRRCancel;
	
	private EditText eTextRRSearch;
	private ImageButton imgBtnRRSearchClear;
	
	private LinearLayout llRcmdSearch;
	private RelativeLayout rlRcmdBottom;
	
	private ArrayList<Recommandation> recommandations;
	private ProfileRecommandationAdapter adapter;
	
	private String flag;
	
	private boolean isMoreRecords;
	private int pageCount = 0;
	
	private SearchDoerListAdapter sdadapter;
	private ArrayList<TaskDoer> searchDoerDataList = new ArrayList<TaskDoer>();
	
	private SearchDoerTask searchDoerTask;
	private Cmd searchDoerCmd;
	
	private int currentFirstVisibleItem = 0;
	private int currentVisibleItemCount = 0;
	private int totalItemCount1 = 0;
	private int currentScrollState = 0;
	
	private ProgressBar pBarRR;
	
	private boolean isSubmit = false;
	private JSONArray user_id_array = new JSONArray();
	
	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.ed_profile_request_rcmdt, container, false);
		searchDoerCmd = CmdFactory.createSearchDoerCmd();
		
		flag = getArguments().getString(Profile.RECOMMANDATION_FLAG);
		if (flag.equals(Profile.RECOMMANDATION_FLAG)) {
			recommandations = (ArrayList<Recommandation>) getArguments().getSerializable(Constants.SERIALIZABLE_DATA);
		}
		
		initialize();
		return view;
	}

	private void initialize() {
		imgDBack = (ImageView) view.findViewById(R.id.imgDBack);
		txtDHTitle = (TextView) view.findViewById(R.id.txtDHTitle);
		txtDHRight = (TextView) view.findViewById(R.id.txtDHRight);
		
		lvRcmdList = (ListView) view.findViewById(R.id.lvRcmdList);
		btnRRSubmit = (Button) view.findViewById(R.id.btnRRSubmit);
		btnRRCancel = (Button) view.findViewById(R.id.btnRRCancel);
		eTextRRSearch = (EditText) view.findViewById(R.id.eTextRRSearch);
		imgBtnRRSearchClear = (ImageButton) view.findViewById(R.id.imgBtnRRSearchClear);
		
		llRcmdSearch = (LinearLayout) view.findViewById(R.id.llRcmdSearch);
		rlRcmdBottom = (RelativeLayout) view.findViewById(R.id.rlRcmdBottom);
		
		pBarRR = (ProgressBar)view.findViewById(R.id.pBarRR);
		txtDHRight.setVisibility(View.GONE);
		txtDHTitle.setText(getResources().getString(R.string.recommandation));
		imgDBack.setOnClickListener(this);
		imgBtnRRSearchClear.setOnClickListener(this);
		btnRRSubmit.setOnClickListener(this);
		btnRRCancel.setOnClickListener(this);
		
		setData();
		
		eTextRRSearch.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_SEARCH){
					Util.hideKeypad(getActivity(), eTextRRSearch);
					searchDoerDataList.clear();
					pageCount = 0;
					searchDoerCmd.addData(TaskDoer.FLD_SEARCH_KEY,eTextRRSearch.getText().toString().trim()); 
					loadSearchMemberListData();
				}
				return false;
			}
		});
	}
	
	private void setData(){
		if (flag.equals(Profile.RECOMMANDATION_FLAG)) {
			adapter = new ProfileRecommandationAdapter(getActivity(), R.layout.ed_profile_req_rcmd_row, recommandations);
			lvRcmdList.setAdapter(adapter);
			llRcmdSearch.setVisibility(View.GONE);
			rlRcmdBottom.setVisibility(View.GONE);
		}else{
			llRcmdSearch.setVisibility(View.VISIBLE);
			rlRcmdBottom.setVisibility(View.VISIBLE);
			isSubmit = false;
			loadSearchMemberListData();
			setScrollListenerOnSearchDoerList();
		}
	}
	
	private void loadSearchMemberListData(){
		Util.clearAsync(searchDoerTask);
		if(Util.isDeviceOnline(getActivity())){
			searchDoerTask = new SearchDoerTask();
			searchDoerTask.execute();
		}else{
			Util.showCenteredToast(getActivity(), getResources().getString(R.string.msg_Connect_internet)); 
		}
	}
	
	//----------------SAVING SEARCH DOER DATA INTO DATABASE----------------------//
	public void getSearchDoerData(JSONObject jsonObject) {
		try{
			JSONArray array = jsonObject.getJSONArray(Constants.FLD_DATA);
			isMoreRecords = jsonObject.getInt(Constants.FLD_IS_MORE)==1?true:false;
			if(array.length()>0){
				for (int i = 0; i < array.length(); i++) {
					Gson gson = new GsonBuilder().create();
					TaskDoer doerDetail = gson.fromJson(array.getJSONObject(i).toString(), TaskDoer.class);
					searchDoerDataList.add(doerDetail);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void setSearchDoerListData(){
		try {
			if(searchDoerDataList.size() > 0){
				if(pageCount == 0){
					sdadapter = new SearchDoerListAdapter(getActivity(), R.layout.ed_search_doer_row, searchDoerDataList,true);
					sdadapter.setNotifyOnChange(true); 
					lvRcmdList.setAdapter(sdadapter);
//					pageCount = pageCount + 1;
				}else{
					sdadapter.notifyDataSetChanged();
//					pageCount = pageCount + 1;
				}
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	//----------------List View ScrollListener----------------------//
	private void setScrollListenerOnSearchDoerList(){
		lvRcmdList.setOnScrollListener(new OnScrollListener() {
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
	        	loadSearchMemberListData();
	        }else{
	        	Util.showCenteredToast(getActivity(), getResources().getString(R.string.No_more_records));
	        }
	    }
	}
	
	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.imgDBack) {
			dismiss();
			
		}else if (view.getId() == R.id.btnRRSubmit) {
			submit();
			
		}else if (view.getId() == R.id.btnRRCancel) {
			dismiss();
			
		}
	}
	
	private void submit(){
		isSubmit = true;
		isMoreRecords = false;
		
		for (TaskDoer doer : searchDoerDataList) {
			if (doer.isSelected) {
				user_id_array.put(doer.user_id);
			}
		}
		
		if (user_id_array != null) {
			new SearchDoerTask().execute();
		}
	}
	
	private void isSearchInProgress(boolean isSearch) {
		if (isSearch) {
			pBarRR.setVisibility(View.VISIBLE);
			imgBtnRRSearchClear.setVisibility(View.GONE);
		} else {
			pBarRR.setVisibility(View.GONE);
			imgBtnRRSearchClear.setVisibility(View.VISIBLE);
		}
	}
	
	
	//----------------TASK TO GET SEARCH DOER LIST----------------------//
	private class SearchDoerTask extends AsyncTask<String, Void, NetworkResponse> {
		@Override
		protected void onPreExecute() {
//			if(!isMoreRecords){
				isSearchInProgress(true);
//			}
		}

		@Override
		protected NetworkResponse doInBackground(String... params) {
			NetworkResponse response = null;
			if(Util.isDeviceOnline(getActivity())){
				if (isSubmit) {
					Cmd cmd = CmdFactory.createGetProfileRecommendationCmd();
					cmd.addData(Profile.OPERATION, Profile.OPERATION_REQUEST_RECOMMENDATION);
					cmd.addData(Profile.FLD_RECOMMENDATION, "this is just a recommendation");
					cmd.addData(Profile.FLD_RECOMMENDATION_VIEW_USER_ID,  AppGlobals.userDetail.user_id);
					cmd.addData(Profile.FLD_REQUEST_TO_USER, user_id_array);
					cmd.addData(Profile.FLD_TRNO, 0);
					response = NetworkMgr.httpPost(Config.API_URL, cmd.getJsonData());
				}else{
					searchDoerCmd.addData(TaskDoer.FLD_PAGE,pageCount);
					response = NetworkMgr.httpPost(Config.API_URL, searchDoerCmd.getJsonData());
					if (response != null) {
						if(response.isSuccess()){
							getSearchDoerData(response.getJsonObject());
						}
					}
				}
			}
			return response;
		}

		@Override
		protected void onPostExecute(NetworkResponse networkResponse) {
			super.onPostExecute(networkResponse);
			isSearchInProgress(false);
			if(networkResponse != null){
				if (!networkResponse.isSuccess()) {
					isMoreRecords = false;
					Util.showCenteredToast(getActivity(),networkResponse.getErrMsg());
				} else {
					if (isSubmit) {
						Util.showCenteredToast(getActivity(), getResources().getString(R.string.msg_recommendation_send_successfully));
						dismiss();
					}else{
						setSearchDoerListData();
						pageCount = pageCount + 1;
					}
				}
			}
		}
	}
}
