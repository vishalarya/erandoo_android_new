package erandoo.app.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Dialog;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import erandoo.app.R;
import erandoo.app.adapters.SearchDoerListAdapter;
import erandoo.app.cmds.Cmd;
import erandoo.app.cmds.CmdFactory;
import erandoo.app.config.Config;
import erandoo.app.custom.AppHeaderView;
import erandoo.app.custom.BaseFragActivity;
import erandoo.app.database.Project;
import erandoo.app.database.TaskDoer;
import erandoo.app.network.NetworkMgr;
import erandoo.app.network.NetworkResponse;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.Util;

public class SearchDoerActivity extends BaseFragActivity implements OnClickListener {
	
	private ListView lvSDList;
	private ImageButton imgBtnSDClearSearch;
	private EditText eTextSDListSearch;
	private TextView txtSDTotalProjects;
	private ProgressBar pBarSMembers;
	private boolean isMoreRecords;
	private int pageCount = 0;

	private int currentFirstVisibleItem = 0;
	private int currentVisibleItemCount = 0;
	private int totalItemCount1 = 0;
	private int currentScrollState = 0;

	private SearchDoerListAdapter adapter;
	private ArrayList<TaskDoer> searchDoerDataList = new ArrayList<TaskDoer>();

	private SearchDoerTask searchDoerTask;
	private AppHeaderView appHeaderView;
	private boolean onScroll = false;

	// -------------FILTER AND SORT RELATED---------------
	private static final String FLD_QUICK_FILTER = "quick_filter";
//	private String highlyRated = "task_done_rank";
	private String highlyRated = "rating_avg_as_tasker";
	private String mostExperinced = "task_done_cnt";
	private String previouslyHired = "previously_worked";
	private Map<String, String> selectedChildNameIdMap = null;
	private static int sortByFlag;

	private Dialog customDialogForFilter;
	
	// ----------------- Variables for Sort -----------------------
	private ImageView imgVBackSortFilter;
	private TextView txtTitleSortFilter;
	private TextView txtSortBySortFilter;
	private TextView txtMExpLowToH;
	private TextView txtMExpHighToL;
	private TextView txtMRatLowTo;
	private TextView txtMRatHighToL;
	
	// ----------------- Variables for Filtering -----------------------
	private TextView txtMTitle;
	private TextView txtMReset;
	private TextView txtMDone;
	private ImageView imgMBack;

	private TextView txtMHrated;
	private TextView txtMMExp;
	private TextView txtMPhired;
	private String recordPerPage ="10";
	private int clickedItemPosition=0;
	private String filterBy;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ed_search_doers);
		filterBy = "";
		initialize();
	}

	private void initialize() {
		sortByFlag = 0;
		selectedChildNameIdMap = new HashMap<String, String>();
		lvSDList = (ListView) findViewById(R.id.lvSDList);
		eTextSDListSearch = (EditText) findViewById(R.id.eTextSDListSearch);
		imgBtnSDClearSearch = (ImageButton) findViewById(R.id.imgBtnSDClearSearch);
		txtSDTotalProjects = (TextView) findViewById(R.id.txtSDTotalProjects);
		pBarSMembers = (ProgressBar) findViewById(R.id.pBarSMembers);

		setHeaderView();

		imgBtnSDClearSearch.setOnClickListener(this);

		eTextSDListSearch.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (Util.isDeviceOnline(getApplicationContext())) {
					if (actionId == EditorInfo.IME_ACTION_SEARCH) {
						Util.hideKeypad(SearchDoerActivity.this, eTextSDListSearch);
						resetSearchData();
					}
				}else{
					Util.showCenteredToast(getApplicationContext(), getResources().getString(R.string.msg_Connect_internet));
				}
				return false;
			}
		});
		loadSearchDoerListData();
		setScrollListenerOnSearchDoerList();
	}

	@Override
	public void onClick(View view) {
		int vId = view.getId();
		if (vId == R.id.imgBtnSDClearSearch) {
			clearSearch();
		} else if (vId == appHeaderView.txtHLeft.getId()) {
			finishActivity();
		} else if (vId == R.id.txtHRight1) {
			if (searchDoerTask != null) {
			if (searchDoerTask.getStatus() == AsyncTask.Status.FINISHED)
				showFilterSortDialog(false);
			}
		} else if (vId == R.id.txtHRight2) {
			if (searchDoerTask != null) {
			if (searchDoerTask.getStatus() == AsyncTask.Status.FINISHED)
				showFilterSortDialog(true);
			}
		} else if (vId == R.id.imgBtnSDClearSearch) {
			eTextSDListSearch.setText("");
		}
		
		if (imgVBackSortFilter != null) {
			if (vId == imgVBackSortFilter.getId()) {
				customDialogForFilter.dismiss();
			} else if (vId == txtSortBySortFilter.getId()) {
				if (searchDoerTask != null) {
				if (searchDoerTask.getStatus() == AsyncTask.Status.FINISHED)
					clickedItemPosition=0;
					sortData(clickedItemPosition);
				}
			} else if (vId == txtMExpLowToH.getId()) {
				if (searchDoerTask.getStatus() == AsyncTask.Status.FINISHED)
					clickedItemPosition=1;
					sortData(clickedItemPosition);
			} else if (vId == txtMExpHighToL.getId()) {
				if (searchDoerTask.getStatus() == AsyncTask.Status.FINISHED)
					clickedItemPosition=2;
					sortData(clickedItemPosition);
			} else if (vId == txtMRatLowTo.getId()) {
				if (searchDoerTask.getStatus() == AsyncTask.Status.FINISHED)
					clickedItemPosition=3;
					sortData(clickedItemPosition);

			} else if (vId == txtMRatHighToL.getId()) {
				if (searchDoerTask.getStatus() == AsyncTask.Status.FINISHED)
					clickedItemPosition=4;
					sortData(clickedItemPosition);
			}
		}

		if (imgMBack != null) {
			if (vId == imgMBack.getId()) {
				customDialogForFilter.dismiss();
			} else if (vId == txtMHrated.getId()) {
				if (searchDoerTask.getStatus() == AsyncTask.Status.FINISHED) {
					filterBy = highlyRated;
					setCheckOrUnCheck(highlyRated, txtMHrated, highlyRated);
				}
			} else if (vId == txtMMExp.getId()) {
				if (searchDoerTask.getStatus() == AsyncTask.Status.FINISHED) {
					filterBy = mostExperinced;
					setCheckOrUnCheck(mostExperinced, txtMMExp, mostExperinced);
				}
			} else if (vId == txtMPhired.getId()) {
				if (searchDoerTask.getStatus() == AsyncTask.Status.FINISHED) {
					filterBy = previouslyHired;
					setCheckOrUnCheck(previouslyHired, txtMPhired, previouslyHired);
				}
			} else if (vId == txtMReset.getId()) {
				selectedChildNameIdMap.clear();
				resetSearchDataForFilter();
			}
		}
	}

	private void resetSearchDataForFilter() {
		customDialogForFilter.dismiss();
		eTextSDListSearch.setText("");
//		selectedChildNameIdMap.clear();
		setTextColorCOdeValue();
		setScrollListenerOnSearchDoerList();
		resetSearchData();
	}

	private void clearSearch() {
		if (Util.isDeviceOnline(getApplicationContext())) {
			eTextSDListSearch.setText("");
			resetSearchData();
		}else{
			Util.showCenteredToast(getApplicationContext(), getResources().getString(R.string.msg_Connect_internet));
		}
	}

	private void resetSearchData() {
		searchDoerDataList.clear();
		txtSDTotalProjects.setText("");
		pageCount = 0;
		onScroll = false;
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
		loadSearchDoerListData();
	}

	private void setHeaderView() {
		appHeaderView = (AppHeaderView) findViewById(R.id.appHeaderView);
		appHeaderView.setHeaderContent(
				getResources().getDrawable(R.drawable.ic_menu), getResources()
						.getString(R.string.Members), getResources()
						.getDrawable(R.drawable.ic_filter), getResources()
						.getDrawable(R.drawable.ic_sort), Gravity.LEFT);
	}

	private void isSearchInProgress(boolean isSearch) {
		if (isSearch) {
			pBarSMembers.setVisibility(View.VISIBLE);
			imgBtnSDClearSearch.setVisibility(View.GONE);
		} else {
			pBarSMembers.setVisibility(View.GONE);
			imgBtnSDClearSearch.setVisibility(View.VISIBLE);
		}
	}

	private void loadSearchDoerListData() {
		Util.clearAsync(searchDoerTask);
		if (Util.isDeviceOnline(SearchDoerActivity.this)) {
			searchDoerTask = new SearchDoerTask();
			searchDoerTask.execute(new String[] { eTextSDListSearch.getText().toString().trim() });
		} else {
			Util.showCenteredToast(SearchDoerActivity.this, getResources().getString(R.string.msg_Connect_internet));
		}
	}

	// ----------------TASK TO GET SEARCH DOER LIST----------------------//
	private class SearchDoerTask extends AsyncTask<String, Void, NetworkResponse> {
		@Override
		protected void onPreExecute() {
			isSearchInProgress(true);
		}

		@Override
		protected NetworkResponse doInBackground(String... params) {
			NetworkResponse response = null;
			if (Util.isDeviceOnline(SearchDoerActivity.this)) {
				Cmd cmd = CmdFactory.createSearchDoerCmd();
				if (!(params[0].equalsIgnoreCase(""))) {
					cmd.addData(Project.FLD_SEARCH_KEY, params[0]);
				}
				cmd = setDataToCmd(cmd);
				response = NetworkMgr.httpPost(Config.API_URL, cmd.getJsonData());
				if (response != null) {
					if (response.isSuccess()) {
						pageCount = pageCount + 1;
						getSearchDoerData(response.getJsonObject());
						if (filterBy.equals(highlyRated) && clickedItemPosition == 0) {
							//TODO..
						}else{
							sortByFlag = clickedItemPosition;
							Collections.sort(searchDoerDataList, sortingByName);
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
			if (networkResponse != null) {
				if (!networkResponse.isSuccess()) {
					isMoreRecords = false;
					Util.showCenteredToast(SearchDoerActivity.this, networkResponse.getErrMsg());
				} else {
					setSearchDoerListData();
				}
			}
		}
	}

	// ----------------SAVING SEARCH DOER DATA INTO DATABASE----------------------//
	public void getSearchDoerData(JSONObject jsonObject) {
		try {
			JSONArray array = jsonObject.getJSONArray(Constants.FLD_DATA);
			isMoreRecords = jsonObject.getInt(Constants.FLD_IS_MORE) == 1 ? true : false;
			if (array.length() > 0) {
				for (int i = 0; i < array.length(); i++) {
					Gson gson = new GsonBuilder().create();
					TaskDoer doerDetail = gson.fromJson(array.getJSONObject(i).toString(), TaskDoer.class);
					searchDoerDataList.add(doerDetail);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ----------------List View ScrollListener----------------------
	private void setScrollListenerOnSearchDoerList() {
		lvSDList.setOnScrollListener(new OnScrollListener() {
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
		if (currentVisibleItemCount > 0
				&& currentScrollState == OnScrollListener.SCROLL_STATE_IDLE
				&& totalItemCount1 == (currentFirstVisibleItem + currentVisibleItemCount)) {
			/***
			 * In this way I detect if there's been a scroll which has completed
			 ***/
			/*** do the work for load more date! ***/
			if (isMoreRecords) {
				onScroll = true;
				loadSearchDoerListData();
			} else {
				//Util.showCenteredToast(SearchDoerActivity.this, getResources().getString(R.string.No_more_records));
			}
		}
	}

	private void setSearchDoerListData() {
		if (adapter != null) {
			Log.d("Search Doer Data", ""+adapter.getCount());
		}
		if (searchDoerDataList.size() > 0) {
			if (!onScroll) {
				adapter = new SearchDoerListAdapter(SearchDoerActivity.this, R.layout.ed_search_doer_row, searchDoerDataList, false);
				adapter.setNotifyOnChange(true);
				lvSDList.setAdapter(adapter);
			} else {
				adapter.notifyDataSetChanged();
			}
		} else{
			Util.showCenteredToast(getApplicationContext(), "No doer found");
		}
		if (adapter != null) {
			txtSDTotalProjects.setText(adapter.getCount() + " " + getResources().getString(R.string.Result_founds));
		}
	}

	// -----------------------FILTER AND SORT REALTED-------------------------
	private Dialog showFilterSortDialog(boolean isSort) {
		String title = null;
		customDialogForFilter = new Dialog(this);
		customDialogForFilter.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		customDialogForFilter.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		if (isSort) {
			title = getResources().getString(R.string.Sort_By);
			customDialogForFilter.setContentView(R.layout.ed_member_sorting);
			inItSortDialog(title);
		} else {
			title = getResources().getString(R.string.filter_by);
			customDialogForFilter.setContentView(R.layout.ed_member_filter);
			inItFilterMDialog(title);
		}
		customDialogForFilter.show();
		return customDialogForFilter;
	}

	private void inItSortDialog(String title) {
		imgVBackSortFilter = (ImageView) customDialogForFilter.findViewById(R.id.imgBackMSort);
		txtTitleSortFilter = (TextView) customDialogForFilter.findViewById(R.id.txtTitleMSort);
		txtSortBySortFilter = (TextView) customDialogForFilter.findViewById(R.id.txtMSortBy);
		txtMExpLowToH = (TextView) customDialogForFilter.findViewById(R.id.txtMExpLowToH);
		txtMExpHighToL = (TextView) customDialogForFilter.findViewById(R.id.txtMExpHighToL);
		txtMRatLowTo = (TextView) customDialogForFilter.findViewById(R.id.txtMRatLowTo);
		txtMRatHighToL = (TextView) customDialogForFilter.findViewById(R.id.txtMRatHighToL);
		txtMRatLowTo.setOnClickListener(this);
		txtMRatHighToL.setOnClickListener(this);
		imgVBackSortFilter.setOnClickListener(this);
		txtSortBySortFilter.setOnClickListener(this);
		txtMExpLowToH.setOnClickListener(this);
		txtMExpHighToL.setOnClickListener(this);
		txtTitleSortFilter.setText(title);
		setCheckedOrUncheckedForSorting();
	}

	private void setCheckedOrUncheckedForSorting() {
		if (sortByFlag == 1) {
			clearSortSelection();
			txtMExpLowToH.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rd_setected, 0, 0, 0);
		} else if (sortByFlag == 2) {
			clearSortSelection();
			txtMExpHighToL.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rd_setected, 0, 0, 0);
		} else if (sortByFlag == 3) {
			clearSortSelection();
			txtMRatLowTo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rd_setected, 0, 0, 0);
		} else if (sortByFlag == 4) {
			clearSortSelection();
			txtMRatHighToL.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rd_setected, 0, 0, 0);
		} else {
			clearSortSelection();
			txtSortBySortFilter.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rd_setected, 0, 0, 0);
		}
	}

	private void clearSortSelection() {
		txtSortBySortFilter.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rd_default, 0, 0, 0);
		txtMExpLowToH.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rd_default, 0, 0, 0);
		txtMExpHighToL.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rd_default, 0, 0, 0);
		txtMRatLowTo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rd_default, 0, 0, 0);
		txtMRatHighToL.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rd_default, 0, 0, 0);
	}

	private void inItFilterMDialog(String title) {
		txtMTitle = (TextView) customDialogForFilter.findViewById(R.id.txtMTitle);
		imgMBack = (ImageView) customDialogForFilter.findViewById(R.id.imgMBack);
		txtMReset = (TextView) customDialogForFilter.findViewById(R.id.txtMReset);
		txtMDone = (TextView) customDialogForFilter.findViewById(R.id.txtMDone);
		txtMTitle.setText(title);
		txtMHrated = (TextView) customDialogForFilter.findViewById(R.id.txtMHrated);
		txtMMExp = (TextView) customDialogForFilter.findViewById(R.id.txtMMExp);
		txtMPhired = (TextView) customDialogForFilter.findViewById(R.id.txtMPhired);
		txtMDone.setOnClickListener(this);
		imgMBack.setOnClickListener(this);
		txtMReset.setOnClickListener(this);
		txtMReset.setVisibility(View.VISIBLE);
		txtMHrated.setOnClickListener(this);
		txtMMExp.setOnClickListener(this);
		txtMPhired.setOnClickListener(this);
		setTextColorCOdeValue();
		if (selectedChildNameIdMap.size() != 0) {
			String keyNam = "";
			for (Entry<String, String> entry : selectedChildNameIdMap.entrySet()) {
				keyNam = entry.getKey();
			}
			if (keyNam.equals(highlyRated)) {
				setCheckOrUnCheck(keyNam, txtMHrated, "");
			} else if (keyNam.equals(mostExperinced)) {
				setCheckOrUnCheck(keyNam, txtMMExp, "");
			} else if (keyNam.equals(previouslyHired)) {
				setCheckOrUnCheck(keyNam, txtMPhired, "");
			}
		}
	}

	private Cmd setDataToCmd(Cmd cmd) {
		String keyVa = "";
		if (selectedChildNameIdMap.containsKey(highlyRated) || selectedChildNameIdMap.containsKey(mostExperinced) || selectedChildNameIdMap.containsKey(previouslyHired)) {
			Set<String> keys = selectedChildNameIdMap.keySet();
			for (String key : keys) {
				if (keyVa.trim().length() == 0) {
					keyVa = key;
				}
			}
			cmd.addData(FLD_QUICK_FILTER, selectedChildNameIdMap.get(keyVa));
		}
		cmd.addData(Constants.FLD_RECORDS_PER_PAGE, recordPerPage );
		cmd.addData(Constants.FLD_SETTING_PAGE, pageCount);
		return cmd;
	}

	private static final Comparator<TaskDoer> sortingByName = new Comparator<TaskDoer>() {
		public int compare(TaskDoer ord1, TaskDoer ord2) {
			int res = 0;
			if (sortByFlag == 0) {
				res = String.CASE_INSENSITIVE_ORDER.compare(ord1.fullname, ord2.fullname);
			} else if (sortByFlag == 1) {
				res = Integer.valueOf(ord1.task_done_cnt) > Integer.valueOf(ord2.task_done_cnt) ? 1 : -1; // For Ascending
			} else if (sortByFlag == 2) {
				res = Integer.valueOf(ord1.task_done_cnt) > Integer.valueOf(ord2.task_done_cnt) ? -1 : 1; // For Descending
			} else if (sortByFlag == 3) {
				res = Float.valueOf(ord1.rating_avg_as_tasker) > Float.valueOf(ord2.rating_avg_as_tasker) ? 1 : -1; // For Ascending
			} else if (sortByFlag == 4) {
				res = Float.valueOf(ord1.rating_avg_as_tasker) > Float.valueOf(ord2.rating_avg_as_tasker) ? -1 : 1; // For Descending
			}
			return res;
		}
	};

	private void setTextColorCOdeValue() {
		int colorCode = (getResources().getColor(R.color.ed_color_gray_light_hint));
		txtMHrated.setTextColor(colorCode);
		txtMMExp.setTextColor(colorCode);
		txtMPhired.setTextColor(colorCode);
		txtMHrated.setTypeface(null, Typeface.NORMAL);
		txtMMExp.setTypeface(null, Typeface.NORMAL);
		txtMPhired.setTypeface(null, Typeface.NORMAL);
	}

	private void setCheckOrUnCheck(String key, TextView textView, String fromW) {// SANJAY
		if (!selectedChildNameIdMap.containsKey(key)) {
			searchDoerTask= new SearchDoerTask();
			selectedChildNameIdMap.clear();
			if (searchDoerTask.getStatus() == AsyncTask.Status.PENDING) {
				selectedChildNameIdMap.put(key, key);
				textView.setTextColor(getResources().getColor(R.color.red_dark));
				textView.setTypeface(null, Typeface.BOLD);
				if (fromW.trim().length() != 0) {
					resetSearchDataForFilter();
				}
			}
		} else {
			textView.setTextColor(getResources().getColor(R.color.red_dark));
			textView.setTypeface(null, Typeface.BOLD);
			customDialogForFilter.dismiss();
		}
	}

	private void sortData(int sortValue) {
		if (searchDoerDataList != null) {
			if (searchDoerDataList.size() != 0) {
				if (filterBy.equals(highlyRated) || clickedItemPosition == 0 || clickedItemPosition == 3 || clickedItemPosition == 4) {
					//TODO..
				} else{
					Collections.sort(searchDoerDataList, sortingByName);
					adapter.notifyDataSetChanged();
					lvSDList.setSelection(0);
				}
				sortByFlag = sortValue;
				setCheckedOrUncheckedForSorting();
			}
			customDialogForFilter.dismiss();
		}
	}

	@Override
	public void onBackPressed() {
		finishActivity();
	}

	private void finishActivity() {
		Util.clearAsync(searchDoerTask);
		SearchDoerActivity.this.finish();
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}
}
