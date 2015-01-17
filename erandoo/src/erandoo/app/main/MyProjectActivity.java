package erandoo.app.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.adapters.MsgSortFilterAdapter;
import erandoo.app.adapters.MyProjectListAdapter;
import erandoo.app.custom.AppHeaderView;
import erandoo.app.custom.BaseFragActivity;
import erandoo.app.database.DatabaseMgr;
import erandoo.app.database.Project;
import erandoo.app.database.SortFilterModel;
import erandoo.app.network.NetworkResponse;
import erandoo.app.projects.CancelProjectDialog;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.SyncAppData;
import erandoo.app.utilities.Util;

//SELECT * FROM task t INNER JOIN task_doer td on t._id=td._id WHERE status=inv
public class MyProjectActivity extends BaseFragActivity implements OnClickListener, TextWatcher {
	private final String GET_PROJECT_LIST_FROM_LOCAL = "p_list_local";
	private final String SYNC_PROJECT_LIST_FROM_SERVER = "p_list_sync";
	private final int REQUEST_CODE = 111;
	private final int CANCEL_RESULT_CODE = 111;
	private final int BID_RESULT_CODE = 112;
	
	private ImageButton imgBtnMPClearSearch;
	private EditText eTextMPSearch;
	private Button btnPostedProjects;
	private Button btnDoingProjects;
	private TextView txtMPTotalProjects;
	private ListView lvProjectsList;
	private Button currSelectTab;
	private MyProjectListAdapter adapter;
	private ArrayList<Project> myProjectList = new ArrayList<Project>(0);
	private DatabaseMgr database;
	private String projectAs = Constants.MY_PROJECT_AS_POSTER;
	private int textlength = 0;
	private ArrayList<Project> teamList;
	private ArrayList<Project> searchedList = new ArrayList<Project>(0);
	private AppHeaderView appHeaderView;

	// -------------Filter AND SORT RELATED------------------
	private ArrayList<SortFilterModel> filterArrayList;
	private ArrayList<SortFilterModel> sortArrayList;
	private MsgSortFilterAdapter msgSortFilterAdapter;
	private Dialog sortFilterDialog;
	private ImageView imgVBackSortFilterDialog;
	public static String sortBy;
	private final static String DEFAULT_SORT_ORDER_DESC = "created_at desc";
	private String filterBy;
	private MyProjectListTask myProjectListTask;
    public static MyProjectActivity instance;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ed_my_projects);
		initialize();
	}
	
	private void initialize() {
		instance = this;
		database = DatabaseMgr.getInstance(this);
		imgBtnMPClearSearch = (ImageButton) findViewById(R.id.imgBtnMPClearSearch);
		eTextMPSearch = (EditText) findViewById(R.id.eTextMPSearch);
		btnPostedProjects = (Button) findViewById(R.id.btnPostedProjects);
		btnDoingProjects = (Button) findViewById(R.id.btnDoingProjects);
		txtMPTotalProjects = (TextView) findViewById(R.id.txtMPTotalProjects);
		lvProjectsList = (ListView) findViewById(R.id.lvProjectsList);
		currSelectTab = btnPostedProjects;
		setHeaderView();
		imgBtnMPClearSearch.setOnClickListener(this);
		btnPostedProjects.setOnClickListener(this);
		btnDoingProjects.setOnClickListener(this);
		eTextMPSearch.addTextChangedListener(this);

		lvProjectsList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view,int position, long id) {
				Project data = (Project) adapter.getItemAtPosition(position);
				if (data.task_kind.equals(Constants.INSTANT)) {
					Util.showCenteredToast(getApplicationContext(), "Instant Project handling under development");
				} else{
					Intent intent = new Intent();
					intent.setClass(MyProjectActivity.this,ProjectDetailsActivity.class);
					intent.putExtra(Constants.PARENT_VIEW,Constants.VIEW_MY_PROJECTS);
					intent.putExtra(Constants.SERIALIZABLE_DATA, data);
					intent.putExtra(Constants.MY_PROJECT_AS_POSTER, projectAs);
					startActivityForResult(intent,REQUEST_CODE);
					overridePendingTransition(R.anim.right_in, R.anim.left_out);
				}
			}
		});

		setTabView();
	}

	private void setHeaderView() {
		appHeaderView = (AppHeaderView) findViewById(R.id.appHeaderView);
		appHeaderView.setHeaderContent(
				getResources().getDrawable(R.drawable.ic_menu), getResources()
						.getString(R.string.My_Projects), getResources()
						.getDrawable(R.drawable.ic_filter), getResources()
						.getDrawable(R.drawable.ic_sort), Gravity.LEFT);
	}
	
	@Override
	public void onClick(View view) {
		int vId = view.getId();
		if (vId == R.id.imgBtnMPClearSearch) {
			eTextMPSearch.setText("");
		} else if (view.getId() == R.id.btnPostedProjects) {
			projectAs = Constants.MY_PROJECT_AS_POSTER;
			setTabView();
		} else if (vId == R.id.btnDoingProjects) {
			projectAs = Constants.MY_PROJECT_AS_DOER;
			setTabView();
		} else if (view.getId() == appHeaderView.txtHLeft.getId()) {
			finishActivity();
		} else if (vId == appHeaderView.txtHRight1.getId()) {
			sortFilterDialog = showSortFilterDialog(false);		
		} else if (vId == appHeaderView.txtHRight2.getId()) {
			sortFilterDialog = showSortFilterDialog(true);
		}else if(vId==imgVBackSortFilterDialog.getId()){
			sortFilterDialog.dismiss();
		}
	}

	private void setTabView() {
		if (projectAs.equals(Constants.MY_PROJECT_AS_POSTER)) {
			btnPostedProjects.setEnabled(false);
			btnDoingProjects.setEnabled(true);
			changeTabColor(btnPostedProjects);
			myProjectList.clear();
		} else {
			btnPostedProjects.setEnabled(true);
			btnDoingProjects.setEnabled(false);
			changeTabColor(btnDoingProjects);
			myProjectList.clear();
		}
		filterBy=null;
		sortBy = DEFAULT_SORT_ORDER_DESC;
		setDataToFilterList();
		eTextMPSearch.getText().clear();
		setProjectListData(false);
		loadProjectListData(SYNC_PROJECT_LIST_FROM_SERVER);
	}
	
	
	private void loadProjectListData(String loadFrom){
		myProjectList.clear();
		Util.clearAsync(myProjectListTask);
		myProjectListTask = new MyProjectListTask();
		myProjectListTask.execute(new String[]{loadFrom});
	}

	private void changeTabColor(Button tab) {
		currSelectTab.setSelected(false);
		tab.setSelected(true);
		currSelectTab = tab;
	}
	
	private void setDataToFilterList() {
		filterArrayList = new ArrayList<SortFilterModel>();
		sortArrayList = new ArrayList<SortFilterModel>();
		String[] filterArray = getResources().getStringArray(R.array.MyProjectFilterArray);
		String[] filterIdArray = getResources().getStringArray(R.array.MyProjectFilterIdArray);
		String[] sortArray = getResources().getStringArray(R.array.MyProjectSortArray);
		String[] sortIdArray = getResources().getStringArray(R.array.MyProjectSortIdArray);
		int sortIdIndex = 0;
		for (String name : filterArray) {
			SortFilterModel msgFObj = new SortFilterModel();
			msgFObj.name = name;
			msgFObj.id=filterIdArray[sortIdIndex];
			if (name.equals(filterArray[0])) {
				msgFObj.isChecked = true;
			}
			
			if (projectAs.equals(Constants.MY_PROJECT_AS_DOER)) {
				filterArrayList.add(msgFObj);
			}
			else 
			{
				if (!name.equals(filterArray[3])) {
					filterArrayList.add(msgFObj);
				}
			}
			
			sortIdIndex++;
		}
		sortIdIndex = 0;
		for (String name : sortArray) {
			
			SortFilterModel msgFObj = new SortFilterModel();
			msgFObj.name = name;
			msgFObj.id = sortIdArray[sortIdIndex];
			if (name.equals(sortArray[0])) {
				msgFObj.isChecked = true;
			}
			sortArrayList.add(msgFObj);
			sortIdIndex++;
		}
	}


	// ----------------TASK TO GET MY PROJECT LIST
	// --------------------------------
	private class MyProjectListTask extends AsyncTask<String, Void, NetworkResponse> {

		@Override
		protected void onPreExecute() {
			Util.showProDialog(MyProjectActivity.this);
		}

		@Override
		protected NetworkResponse doInBackground(String... params) {
			NetworkResponse response = null;
			myProjectList = database.getMyTasks(
					Constants.DELIVERY_STATUS_SENT ,projectAs, filterBy);
			if(params[0].equals(SYNC_PROJECT_LIST_FROM_SERVER)){ 
			
				if (Util.isDeviceOnline(MyProjectActivity.this)) {
					response = SyncAppData.syncProjectData(MyProjectActivity.this);
				}
				
				myProjectList.clear();
				myProjectList = database.getMyTasks(Constants.DELIVERY_STATUS_SENT , projectAs, filterBy);
			}
			Collections.sort(myProjectList, sortingByDate);
			return response;
		}

		@Override
		protected void onPostExecute(NetworkResponse networkResponse) {
			Util.dimissProDialog();
			if (networkResponse != null) {
				if (!networkResponse.isSuccess()) {
					Util.showCenteredToast(MyProjectActivity.this,
							networkResponse.getErrMsg());
				}
			}
			setProjectListData(false);
		}
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		teamList = myProjectList;
		try {
			textlength = eTextMPSearch.getText().toString().length();
			if (textlength > 0) {
				searchedList.clear();
				for (int i = 0; i < teamList.size(); i++) {
					if (teamList.get(i).title != null) {
						if (teamList.get(i).title.toString().toLowerCase().contains(((CharSequence) eTextMPSearch.getText()
												.toString().toLowerCase()
												.subSequence(0, textlength)))) {
							searchedList.add(teamList.get(i));
						}
					}
				}
				setProjectListData(true);
			} else {
				setProjectListData(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_CODE){
			if(resultCode == CANCEL_RESULT_CODE){
				if (CancelProjectDialog.isCancelled) {
					CancelProjectDialog.isCancelled = false;
					loadProjectListData(GET_PROJECT_LIST_FROM_LOCAL); 
				}
			}else if(resultCode ==  BID_RESULT_CODE){
				loadProjectListData(SYNC_PROJECT_LIST_FROM_SERVER); 
			}
		}
	}

	@Override
	public void onBackPressed() {
		finishActivity();
	}

	private void finishActivity() {
		instance = null;
		MyProjectActivity.this.finish();
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}
	
	@Override
	protected void onDestroy() {
		instance = null;
		super.onDestroy();
	}

	private void setProjectListData(boolean isSearch) {
		if (isSearch) {
			adapter = new MyProjectListAdapter(MyProjectActivity.this,
					R.layout.ed_my_projects_row, searchedList);
			txtMPTotalProjects.setText(searchedList.size() + " "
					+ getResources().getString(R.string.Result_founds));
		} else {
			adapter = new MyProjectListAdapter(MyProjectActivity.this,
					R.layout.ed_my_projects_row, myProjectList);
			txtMPTotalProjects.setText(myProjectList.size() + " "
					+ getResources().getString(R.string.Result_founds));
		}
		adapter.setNotifyOnChange(true);
		lvProjectsList.setAdapter(adapter);
	}

	// --------------------FILTER AND SORT REALTED --------------------------------//
	private Dialog showSortFilterDialog(boolean isSort) {
		sortFilterDialog = new Dialog(this);
		sortFilterDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		sortFilterDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		sortFilterDialog.setContentView(R.layout.ed_msg_sort_filter);
		inItSortFilterDialog(isSort);
		sortFilterDialog.show();
		return sortFilterDialog;
	}

	private void inItSortFilterDialog(final boolean isSort) {
		TextView txtTitleSortFilterDialog = (TextView) sortFilterDialog.findViewById(R.id.txtTitleMessageF);
		imgVBackSortFilterDialog = (ImageView) sortFilterDialog.findViewById(R.id.imgMBackMessageF);
		ListView lVSortFilterDialog = (ListView) sortFilterDialog.findViewById(R.id.LVMessageF);

		imgVBackSortFilterDialog.setOnClickListener(this);

		String title = null;
		if (isSort) {
			title = getResources().getString(R.string.Sort_By);
			msgSortFilterAdapter = new MsgSortFilterAdapter(this,R.layout.ed_simple_list_row, sortArrayList);
		} else {
			title = getResources().getString(R.string.filter_by);
			msgSortFilterAdapter = new MsgSortFilterAdapter(this,R.layout.ed_simple_list_row, filterArrayList);
		}

		txtTitleSortFilterDialog.setText(title);

		lVSortFilterDialog.setAdapter(msgSortFilterAdapter);

		lVSortFilterDialog.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,long arg3) {
				SortFilterModel msgFObj = msgSortFilterAdapter.getItem(pos);
				if (!msgFObj.isChecked) {
					msgSortFilterAdapter.getItem(pos).isChecked = true;
					if (isSort) {
						doSorting(msgFObj);
					} else {
						doFiltering(msgFObj);
					}
					clearSortFilterSelection(pos);
				}
				msgSortFilterAdapter.notifyDataSetChanged();
				sortFilterDialog.dismiss();
			}
		});
	}

	private void clearSortFilterSelection(int pos) {
		for (SortFilterModel model : msgSortFilterAdapter._data) {
			int index = msgSortFilterAdapter._data.indexOf(model);
			if(index != pos){
				msgSortFilterAdapter._data.get(index).isChecked = false;
			}
		}
	}

	private void doSorting(SortFilterModel msgFObj) {
		sortBy = msgFObj.id;
		Collections.sort(myProjectList, sortingByDate);
		adapter.notifyDataSetChanged();
	}

	private void doFiltering(SortFilterModel msgFObj) {
		filterBy=msgFObj.id;
		if(filterBy.equals("ap")){
			filterBy=null;	
		}
		loadProjectListData(GET_PROJECT_LIST_FROM_LOCAL); 
	}
	
	// -----------------------------------------------------------------------------

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,int after) {}

	@Override
	public void afterTextChanged(Editable s) {}

	static final Comparator<Project> sortingByDate = new Comparator<Project>() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		public int compare(Project ord1, Project ord2) {
			Date d1 = null;
			Date d2 = null;
			try {
				d1 = sdf.parse(ord1.created_at);
				d2 = sdf.parse(ord2.created_at);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (sortBy.equals(DEFAULT_SORT_ORDER_DESC)) {
				return (d1.getTime() > d2.getTime() ? -1 : 1); // descending
			}else {
				return (d1.getTime() > d2.getTime() ? 1 : -1); // ascending
			}

		}
	};
	
	public Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			Log.v("My Project Activity", "Project List Updated");
			loadProjectListData(GET_PROJECT_LIST_FROM_LOCAL); 
		}
		
	};

}
