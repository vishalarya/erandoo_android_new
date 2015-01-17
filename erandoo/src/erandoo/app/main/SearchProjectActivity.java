package erandoo.app.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import erandoo.app.R;
import erandoo.app.adapters.SearchProjectListAdapter;
import erandoo.app.cmds.Cmd;
import erandoo.app.cmds.CmdFactory;
import erandoo.app.config.Config;
import erandoo.app.custom.AppHeaderView;
import erandoo.app.custom.BaseFragActivity;
import erandoo.app.database.Country;
import erandoo.app.database.DatabaseMgr;
import erandoo.app.database.Project;
import erandoo.app.database.Skill;
import erandoo.app.database.WorkLocation;
import erandoo.app.network.NetworkMgr;
import erandoo.app.network.NetworkResponse;
import erandoo.app.tempclasses.ProjectData;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.Util;

public class SearchProjectActivity extends BaseFragActivity implements
		OnClickListener,OnTouchListener {
	private ArrayList<Project> projectList = new ArrayList<Project>();
	private ImageButton imgBtnPLClearSearch;
	private EditText eTextPLListSearch;
	private TextView txtPLTotalProjects;
	private ListView lvProjectList;
	private int currentFirstVisibleItem = 0;
	private int currentVisibleItemCount = 0;
	private int totalItemCount1 = 0;
	private int currentScrollState = 0;
	private SearchProjectListAdapter adapter;
	private DatabaseMgr dataBase;
	private boolean isMoreRecords;
	private int pageCount = 0;
	private ProjectListTask projectListTask;
	private ProgressBar pBarSProjects;
	private AppHeaderView appHeaderView;
	private boolean onScroll = false;

	// --------------FILTER AND SORT RELATED-----------------------
	public static String ascending = "ascending";
	public static String descending = "descending";
	private static String ascOrDesc = ascending;
	// private boolean SODIsVisible;
	private String FLD_RELATIONSHIPS = "relationships";
	private String FLD_TASK_KIND = "task_kind";
	private String FLD_QUICK_FILTER = "quick_filter";
	private String bookmark_subtype = "bookmark_subtype";
	private String task_kind = "";
	private String taskKindTemp = "";
	public Map<String, String> selectedChildNameIdMap = null;
	public Map<String, String> selectedChildNameIdMapForSend = null;
	private boolean doneTextIsClicked;
	Dialog customDialogForFilter;
	 private String sortValue="";

	private String[] relationshipsIdArray;
	private String[] projectTypeIdArray;

	// --------------variables for Sort Dialog---------------------------
	ImageView imgBackSort;
	TextView txtTitleSort;
	TextView txtNewestSort;
	TextView txtOldestSort;
	// -----------------variables for Filter Dialog--------------------------
	TextView txtTitleO;
	TextView txtReset;
	TextView txtDoneO;
	ImageView imgBackO;
	TextView txtRAll;
	TextView txtRConnect;
	TextView txtRWorkWith;
	TextView txtSavedProject;
	TextView txtPTAll;
	TextView txtPTVirtual;
	TextView txtPTInPers;
	private boolean txtSavedBackgrDefa;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ed_search_projects);
		initialize();
	}

	private void initialize() {
		ascOrDesc = ascending;
		task_kind = "";
		selectedChildNameIdMap = new HashMap<String, String>();
		selectedChildNameIdMapForSend=new HashMap<String, String>();
		dataBase = DatabaseMgr.getInstance(this);
		imgBtnPLClearSearch = (ImageButton) findViewById(R.id.imgBtnPLClearSearch);
		eTextPLListSearch = (EditText) findViewById(R.id.eTextPLListSearch);
		txtPLTotalProjects = (TextView) findViewById(R.id.txtPLTotalProjects);
		lvProjectList = (ListView) findViewById(R.id.lvProjectList);
		pBarSProjects = (ProgressBar) findViewById(R.id.pBarSProjects);
		setHeaderView();
		imgBtnPLClearSearch.setOnClickListener(this);
		setScrollListenerOnProjectList();
		eTextPLListSearch.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,KeyEvent event) {
				if(Util.isDeviceOnline(getApplicationContext())){
					if (actionId == EditorInfo.IME_ACTION_SEARCH) {
						Util.hideKeypad(SearchProjectActivity.this,eTextPLListSearch);
						resetSearchData();
					}
				}else{
					Util.showCenteredToast(getBaseContext(), getResources().getString(R.string.msg_Connect_internet));
				}
				return true;
			}
		});

	}

	private void setHeaderView() {
		appHeaderView = (AppHeaderView) findViewById(R.id.appHeaderView);
		appHeaderView.setHeaderContent(
				getResources().getDrawable(R.drawable.ic_menu), getResources()
						.getString(R.string.Search_Projects), getResources()
						.getDrawable(R.drawable.ic_filter), getResources()
						.getDrawable(R.drawable.ic_sort), Gravity.LEFT);
	}

	@Override
	public void onClick(View v) {
		int vId = v.getId();
		if (vId == R.id.imgBtnPLClearSearch) {
			clearSearch();
		} else if (vId == appHeaderView.txtHLeft.getId()) {
			finishActivity();
		} else if (vId == appHeaderView.txtHRight1.getId()) {
			if (projectListTask != null) {
				if (projectListTask.getStatus() == AsyncTask.Status.FINISHED) {
					doneTextIsClicked = false;
					showSortFilterDialog(false);
				}
			}

		} else if (v.getId() == appHeaderView.txtHRight2.getId()) {
			if (projectListTask != null) {
				if (projectListTask.getStatus() == AsyncTask.Status.FINISHED) {
					showSortFilterDialog(true);
				}
			}
		}
		// --------------------
		if (txtDoneO != null) {
			if (vId == R.id.txtDoneO) {
				task_kind=taskKindTemp;
				if(!Util.isDeviceOnline(this))
				{
					Util.showCenteredToast(this,getResources().getString(R.string.msg_Connect_internet));
				}
				else if (selectedChildNameIdMap.size() != 0 ) {
					onDone();
				}
				else {
					if(task_kind.trim().length() != 0)
					{
						onDone();	
					}
					else
					{
					Util.showCenteredToast(this,
							getResources().getString(R.string.msg_select_at_least));
					}
				}
			}

			else if (vId == R.id.imgBackO) {
				customDialogForFilter.dismiss();
				taskKindTemp="";
			} else if (vId == R.id.txtReset) {
				task_kind = "";
				selectedChildNameIdMapForSend.clear();
				customDialogForFilter.dismiss();
				refreshingDataForFilter();
			}

			else if (vId ==R.id. txtRAll) {
				if (selectedChildNameIdMap.containsKey(relationshipsIdArray[0])) {
					setUnChecked(relationshipsIdArray[0], txtRAll);
				} else {
					setUncheckedAllField(relationshipsIdArray[0]);
					setChecked(relationshipsIdArray[0], txtRAll);
				}

			} else if (vId == R.id.txtRConnect) {
				if (selectedChildNameIdMap.containsKey(relationshipsIdArray[1])) {
					setUnChecked(relationshipsIdArray[1], txtRConnect);
				} else {
					setUncheckedAllField(relationshipsIdArray[1]);
					setChecked(relationshipsIdArray[1], txtRConnect);
				}

			} else if (vId == R.id.txtRWorkWith) {
				if (selectedChildNameIdMap.containsKey(relationshipsIdArray[2])) {
					setUnChecked(relationshipsIdArray[2], txtRWorkWith);
				} else {
					setUncheckedAllField(relationshipsIdArray[2]);
					setChecked(relationshipsIdArray[2], txtRWorkWith);
				}
			} else if (vId == R.id.txtSavedProject) {
				setUncheckedAllField(bookmark_subtype);
				if (selectedChildNameIdMap.containsKey(bookmark_subtype)) {
					txtSavedProject.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.ic_chk_default, 0);
					selectedChildNameIdMap.remove(bookmark_subtype);
				} else {
					txtSavedBackgrDefa=false;
					selectedChildNameIdMap.clear();
					txtSavedProject.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.ic_chk_selected, 0);
					selectedChildNameIdMap.put(bookmark_subtype,bookmark_subtype);
				}
			} else if (vId == R.id.txtPTAll) {
				taskKindTemp = projectTypeIdArray[0];
				txtPTAll.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rd_setected, 0, 0, 0);
				txtPTVirtual.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rd_default, 0, 0, 0);
				txtPTInPers.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rd_default, 0, 0, 0);
			}
			else if (vId == R.id.txtPTVirtual) {
				taskKindTemp = projectTypeIdArray[1];
				txtPTAll.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rd_default, 0, 0, 0);
				txtPTVirtual.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rd_setected, 0, 0, 0);
				txtPTInPers.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rd_default, 0, 0, 0);
			} else if (vId == R.id.txtPTInPers) {
				taskKindTemp = projectTypeIdArray[2];
				txtPTAll.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rd_default, 0, 0, 0);
				txtPTVirtual.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rd_default, 0, 0, 0);
				txtPTInPers.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rd_setected, 0, 0, 0);
			}
		}
		if (txtNewestSort != null) {
			if (vId == txtNewestSort.getId()) {
				txtNewestSort.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rd_setected, 0, 0, 0);
				txtOldestSort.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rd_default, 0, 0, 0);
				ascOrDesc = ascending;
				Collections.sort(projectList, sortingByDate);
				adapter.notifyDataSetChanged();
				lvProjectList.setSelection(0);
				customDialogForFilter.dismiss();
			} else if (vId == txtOldestSort.getId()) {
				txtNewestSort.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rd_default, 0, 0, 0);
				txtOldestSort.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rd_setected, 0, 0, 0);
				ascOrDesc = descending;
				Collections.sort(projectList, sortingByDate);
				adapter.notifyDataSetChanged();
				lvProjectList.setSelection(0);
				customDialogForFilter.dismiss();
			}
			else if (vId == imgBackSort.getId()) {
				customDialogForFilter.dismiss();
			}
		}
	}
	
	private void onDone()
	{
		selectedChildNameIdMapForSend.clear();
		for (Entry<String, String> entry : selectedChildNameIdMap.entrySet()) {
			selectedChildNameIdMapForSend.put(selectedChildNameIdMap.get(entry.getKey()), selectedChildNameIdMap.get(entry.getKey()));
		}
		doneTextIsClicked = true;
		customDialogForFilter.dismiss();
		clearSearch();
		refreshingDataForFilter();	
		selectedChildNameIdMap.clear();
	}
	@Override
	protected void onResume() {
		resetSearchData();
		super.onResume();
	}
	
	private void clearSearch() {
		if(Util.isDeviceOnline(getApplicationContext())){
			eTextPLListSearch.setText("");
			resetSearchData();
		}else{
			Util.showCenteredToast(getBaseContext(), getResources().getString(R.string.msg_Connect_internet));
		}
	}
	
	private void resetSearchData() {
		txtPLTotalProjects.setText("");
		projectList.clear();
		pageCount = 0;
		onScroll = false;
		if(adapter != null)
			adapter.notifyDataSetChanged();
		loadProjectListData();
	}

	private void loadProjectListData() {
		if (Util.isDeviceOnline(this)) {
			Util.clearAsync(projectListTask);
			projectListTask = new ProjectListTask();
			projectListTask.execute();
		} else {
			Util.showCenteredToast(this,getResources().getString(R.string.msg_Connect_internet));
		}
	}

	private void isSearchInProgress(boolean isSearch) {
		if (isSearch) {
			pBarSProjects.setVisibility(View.VISIBLE);
			imgBtnPLClearSearch.setVisibility(View.GONE);
		} else {
			pBarSProjects.setVisibility(View.GONE);
			imgBtnPLClearSearch.setVisibility(View.VISIBLE);
		}
	}

	// ----------------TASK TO GET PROJECT LIST --------------------------------
	private class ProjectListTask extends AsyncTask<String, Void, NetworkResponse> {
		@Override
		protected void onPreExecute() {
			isSearchInProgress(true);
		}

		@Override
		protected NetworkResponse doInBackground(String... params) {
			Cmd cmd = CmdFactory.getProjectListCmd();
			setDataToCmd(cmd);
			NetworkResponse response = NetworkMgr.httpPost(Config.API_URL,cmd.getJsonData());
			if (response != null) {
				if (response.isSuccess()) {
					pageCount = pageCount + 1;
					getProjectList(response);
				}
			}
			return response;
		}

		@Override
		protected void onPostExecute(NetworkResponse networkResponse) {
			isSearchInProgress(false);
			if (networkResponse != null) {
				if (!networkResponse.isSuccess()) {
					isMoreRecords = false;
					Util.showCenteredToast(SearchProjectActivity.this,networkResponse.getErrMsg());
					if (doneTextIsClicked) {
						projectList.clear();
						adapter.notifyDataSetChanged();
					}
				} else {
					setProjectListData();
				}
			}
			doneTextIsClicked = false;
		}
	}

	private Cmd setDataToCmd(Cmd cmd) {
		StringBuffer selectedChildNameId = new StringBuffer();
		for (Entry<String, String> entry : selectedChildNameIdMapForSend.entrySet()) {
			String value = selectedChildNameIdMapForSend.get(entry.getKey());
			if (value != null) {
				selectedChildNameId.append(value);
			}
		}

		if (selectedChildNameIdMapForSend.containsKey(bookmark_subtype)) {
			cmd.addData(FLD_QUICK_FILTER, selectedChildNameId.toString());
		} else if (selectedChildNameId.toString().trim().length() != 0) {
			cmd.addData(FLD_RELATIONSHIPS, selectedChildNameId.toString());
		}
		if (task_kind.trim().length() != 0) {
			cmd.addData(FLD_TASK_KIND, task_kind);
		}
		if(sortValue.trim().length()!=0)
		{
			cmd.addData(Project.FLD_SORT, sortValue);
		}

		cmd.addData(Project.FLD_SEARCH_KEY, eTextPLListSearch.getText()
				.toString().trim());
		cmd.addData(Project.FLD_PAGE, pageCount);
		return cmd;
	}

	public void getProjectList(NetworkResponse response) {
		try {
			if (response.isSuccess()) {
				ProjectData projectData = (ProjectData) Util.getJsonToClassObject(response.getJsonObject().toString(), ProjectData.class);
				isMoreRecords = projectData.is_more.equals("1") ? true : false;
				if (projectData != null) {
					for (Project project : projectData.data) {
						project.category = dataBase
								.getProjectCategory(project.category_id);

						if (project.multiskills.size() > 0) {
							for (Skill skill : project.multiskills) {
								if (project.projectSkillNames == null) {
									project.projectSkillNames = skill.skill_desc;
								} else {
									project.projectSkillNames = project.projectSkillNames
											+ ", " + skill.skill_desc;
								}
							}
						} else {
							project.projectSkillNames = getResources()
									.getString(R.string.No_skill_specified);
						}

						if (project.task_kind.equals(Constants.VIRTUAL)) {
							if (project.multilocations.size() > 0) {
								for (Country country : project.multilocations) {
									if (project.projectLocationNames == null) {
										project.projectLocationNames = country.country_name;
									} else {
										project.projectLocationNames = project.projectLocationNames
												+ ", " + country.country_name;
									}
								}
							} else {
								project.projectLocationNames = getResources()
										.getString(R.string.Anywhere);
							}
						} else {
							if (project.tasklocation.size() > 0) {
								for (WorkLocation workLocation : project.tasklocation) {
									if (project.projectLocationNames == null) {
										if(workLocation.location_geo_area.equals("")){
											project.projectLocationNames = getResources()
													.getString(R.string.Anywhere);
										}else{
											project.projectLocationNames = workLocation.location_geo_area;
										}
									} else {
										project.projectLocationNames = project.projectLocationNames
												+ ", " + workLocation.location_geo_area;
									}
								}
							} else {
								project.projectLocationNames = getResources()
										.getString(R.string.Anywhere);
							}
						}

						projectList.add(project);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setProjectListData() {
		if (projectList.size() > 0) {
			if (!onScroll) {
				adapter = new SearchProjectListAdapter(SearchProjectActivity.this,R.layout.ed_search_project_row, projectList);
				adapter.setNotifyOnChange(true);
				lvProjectList.setAdapter(adapter);
			} else {
				adapter.notifyDataSetChanged();
			}
		}
		txtPLTotalProjects.setText(projectList.size() + " "+ getResources().getString(R.string.Result_founds));
	}

	// ----------------List View ScrollListener----------------------
	private void setScrollListenerOnProjectList() {
		lvProjectList.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				currentScrollState = scrollState;
				isScrollCompleted();
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				currentFirstVisibleItem = firstVisibleItem;
				currentVisibleItemCount = visibleItemCount;
				totalItemCount1 = totalItemCount;
			}
		});
	}
	private void isScrollCompleted() {
		if (currentVisibleItemCount > 0
				&& currentScrollState == OnScrollListener.SCROLL_STATE_IDLE
				&& totalItemCount1 == (currentFirstVisibleItem + currentVisibleItemCount)) {
			/***
			 * In this way I detect if there's been a scroll which has completed
			 ***/
			/*** do the work for load more date! ***/
			if (isMoreRecords) {
				onScroll = true;
				loadProjectListData();
			} else {
				// Util.showCenteredToast(SearchProjectActivity.this,
				// getResources().getString(R.string.No_more_records));
			}
		}
	}

	private void setUncheckedAllField(String valueIs) {
		if (valueIs.equals(bookmark_subtype)) {
			txtRAll.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.ic_chk_default, 0, 0, 0);
			txtRConnect.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.ic_chk_default, 0, 0, 0);
			txtRWorkWith.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.ic_chk_default, 0, 0, 0);
		} else {
			if(!txtSavedBackgrDefa)
			{
			txtSavedProject.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_chk_default, 0);
			selectedChildNameIdMap.clear();
			txtSavedBackgrDefa=true;
			}
		}

	}

	private void setChecked(String key, TextView textView) {
		textView.setCompoundDrawablesWithIntrinsicBounds(
				R.drawable.ic_chk_selected, 0, 0, 0);
		selectedChildNameIdMap.put(key, key);
	}

	private void setUnChecked(String key, TextView textView) {
		textView.setCompoundDrawablesWithIntrinsicBounds(
				R.drawable.ic_chk_default, 0, 0, 0);
		selectedChildNameIdMap.remove(key);
	}

	// --
	private void refreshingDataForFilter() {
		eTextPLListSearch.setText("");
		resetSearchData();
	}

	private Dialog showSortFilterDialog(boolean isSort) {
		String title;
		txtSavedBackgrDefa=false;
		customDialogForFilter = new Dialog(this);
		customDialogForFilter.getWindow().requestFeature(
				Window.FEATURE_NO_TITLE);
		customDialogForFilter.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		if (isSort) {
			title = getResources().getString(R.string.Sort_By);
			customDialogForFilter.setContentView(R.layout.ed_sproject_sorting);
			inItSortDialog(title);
		} else {
			title = getResources().getString(R.string.filter_by);
			customDialogForFilter.setContentView(R.layout.ed_sproject_filter);
			inItFilterDialog(title);
		}
		customDialogForFilter.show();
		return customDialogForFilter;
	}

	private void inItSortDialog(String title) {
		imgBackSort = (ImageView) customDialogForFilter.findViewById(R.id.imgBackSort);
		txtTitleSort = (TextView) customDialogForFilter.findViewById(R.id.txtTitleSort);
		txtNewestSort = (TextView) customDialogForFilter.findViewById(R.id.txtNewestSort);
		txtOldestSort = (TextView) customDialogForFilter.findViewById(R.id.txtOldestSort);
		txtTitleSort.setText(title);
		imgBackSort.setOnClickListener(this);
		txtNewestSort.setOnClickListener(this);
		txtOldestSort.setOnClickListener(this);
		if (ascOrDesc.equals(ascending)) {
			txtNewestSort.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rd_setected, 0, 0, 0);
			txtOldestSort.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rd_default, 0, 0, 0);
			sortValue="t.task_id ASC";
		} else {
			txtNewestSort.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rd_default, 0, 0, 0);
			txtOldestSort.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rd_setected, 0, 0, 0);
            sortValue="t.task_id ASC";
		}

	}
	private void inItFilterDialog(String title) {
		relationshipsIdArray = getResources().getStringArray(R.array.Relationships_id);
		projectTypeIdArray = getResources().getStringArray(R.array.projType_id);
		txtTitleO = (TextView) customDialogForFilter.findViewById(R.id.txtTitleO);
		imgBackO = (ImageView) customDialogForFilter.findViewById(R.id.imgBackO);
		txtReset = (TextView) customDialogForFilter.findViewById(R.id.txtReset);
		txtDoneO = (TextView) customDialogForFilter.findViewById(R.id.txtDoneO);
		txtTitleO.setText(title);
		txtRAll = (TextView) customDialogForFilter.findViewById(R.id.txtRAll);
		txtRConnect = (TextView) customDialogForFilter.findViewById(R.id.txtRConnect);
		txtRWorkWith = (TextView) customDialogForFilter.findViewById(R.id.txtRWorkWith);
		txtSavedProject = (TextView) customDialogForFilter.findViewById(R.id.txtSavedProject);
		txtPTAll = (TextView) customDialogForFilter.findViewById(R.id.txtPTAll);
		txtPTVirtual = (TextView) customDialogForFilter.findViewById(R.id.txtPTVirtual);
		txtPTInPers = (TextView) customDialogForFilter.findViewById(R.id.txtPTInPers);
		txtDoneO.setOnClickListener(this);
		imgBackO.setOnClickListener(this);
		txtReset.setOnClickListener(this);
		txtReset.setVisibility(View.VISIBLE);
		txtRAll.setOnClickListener(this);
		txtRConnect.setOnClickListener(this);
		txtRWorkWith.setOnClickListener(this);
		txtSavedProject.setOnClickListener(this);
		txtPTAll.setOnClickListener(this);
		txtPTVirtual.setOnClickListener(this);
		txtPTInPers.setOnClickListener(this);
		selectedChildNameIdMap.clear();
			setCheckOrUnCheckFilter();
	}

	static final Comparator<Project> sortingByDate = new Comparator<Project>() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		public int compare(Project ord1, Project ord2) {
			int result=1;
			if(ord1.created_at!=null&&ord2.created_at!=null)
			{
			Date d1 = null;
			Date d2 = null;
			try {
				d1 = sdf.parse(ord1.created_at);
				d2 = sdf.parse(ord2.created_at);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (ascOrDesc.equals(ascending)) {
				result= (d1.getTime() > d2.getTime() ? -1 : 1); // descending
			}
			else {
				result= (d1.getTime() > d2.getTime() ? 1 : -1); // ascending
			}
		}
			return result;
		}
	};

	// -------------------------------------------
	private void setCheckOrUnCheckFilter()
	{
		for (Entry<String, String> entry : selectedChildNameIdMapForSend.entrySet()) {
			selectedChildNameIdMap.put(selectedChildNameIdMapForSend.get(entry.getKey()), selectedChildNameIdMapForSend.get(entry.getKey()));
		}
		if (selectedChildNameIdMapForSend.containsKey(relationshipsIdArray[0])) {
			txtRAll.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_chk_selected, 0, 0, 0);
		} 
		
		if (selectedChildNameIdMapForSend.containsKey(relationshipsIdArray[1])) {
			txtRConnect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_chk_selected, 0, 0, 0);
		} 	
		if (selectedChildNameIdMapForSend.containsKey(relationshipsIdArray[2])) {
			txtRWorkWith.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_chk_selected, 0, 0, 0);
		}

		if (selectedChildNameIdMapForSend.containsKey(bookmark_subtype)) {
			txtSavedProject.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.ic_chk_selected, 0);
		} 
		if(task_kind.equals(projectTypeIdArray[0]))
		{
			txtPTAll.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rd_setected, 0, 0, 0);
		}
		else if(task_kind.equals(projectTypeIdArray[1]))
		{
			txtPTVirtual.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rd_setected, 0, 0, 0);
		}
		else if(task_kind.equals(projectTypeIdArray[2]))
		{
			txtPTInPers.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rd_setected, 0, 0, 0);
		}
		
		
	}
	@Override
	public void onBackPressed() {
		finishActivity();
	}

	private void finishActivity() {
		Util.clearAsync(projectListTask);
		SearchProjectActivity.this.finish();
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		
		return false;
	}
}
