package erandoo.app.main.settings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.adapters.MoneyAdapter;
import erandoo.app.cmds.Cmd;
import erandoo.app.cmds.CmdFactory;
import erandoo.app.config.Config;
import erandoo.app.custom.AppHeaderView;
import erandoo.app.custom.BaseFragActivity;
import erandoo.app.custom.DatePickerDailog;
import erandoo.app.custom.DatePickerDailog.DatePickerListner;
import erandoo.app.database.Money;
import erandoo.app.network.NetworkMgr;
import erandoo.app.network.NetworkResponse;
import erandoo.app.tempclasses.MoneyData;
import erandoo.app.utilities.Util;

public class MoneyActivity extends BaseFragActivity implements OnClickListener, OnItemClickListener {

	private AppHeaderView appHeaderView;
	private MoneyAdapter moneyAdapter;
	private TextView txtRemainAmount;
	private TextView txtStartD;
	private TextView txtEndD;
	private TextView txtSearchM;
	private TextView txtResetM;
	private ListView LVMoney;
	private DatePickerDailog dpDailog;
	private String records_per_page;
	private String start_date;
	private String end_date;
	private int page;
	private MoneyAsyncTask moneyAsyncTask;
	private MoneyData moneyDataList;
	private String RemaininAmount;

	// declare variables for scrolling listView
	private int currentFirstVisibleItem = 0;
	private int currentVisibleItemCount = 0;
	private int totalItemCount1 = 0;
	private int currentScrollState = 0;
	private boolean isMoreRecords;
	private boolean onScroll = false;
	private ArrayList<Money> moneyList = new ArrayList<Money>();

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.ed_setting_money);
		init();
		appHeaderView.setHeaderContent(
				getResources().getDrawable(R.drawable.ic_back), getResources()
						.getString(R.string.Money), null, null, Gravity.CENTER);
		setScrollListenerOnProjectList();
	}

	private void init() {
		appHeaderView = (AppHeaderView) findViewById(R.id.appHeaderView);
		txtRemainAmount = (TextView) findViewById(R.id.txtRemainAmount);
		txtStartD = (TextView) findViewById(R.id.txtStartD);
		txtEndD = (TextView) findViewById(R.id.txtEndD);
		txtSearchM = (TextView) findViewById(R.id.txtSearchM);
		txtResetM = (TextView) findViewById(R.id.txtResetM);
		LVMoney = (ListView) findViewById(R.id.LVMoney);
		txtStartD.setOnClickListener(this);
		txtEndD.setOnClickListener(this);
		txtSearchM.setOnClickListener(this);
		txtResetM.setOnClickListener(this);
		LVMoney.setOnItemClickListener(this);
		
		RemaininAmount = getResources().getString(R.string.remaining_wallet_amo) + " " + "0.00";
		reSetDefaultFields();
	}

	@Override
	public void onClick(View v) {
		int vId = v.getId();
		if (vId == appHeaderView.txtHLeft.getId()) {
			finishActivity();
		} else if (vId == R.id.txtStartD) {
			showDatePicker(txtStartD);
		} else if (vId == R.id.txtEndD) {
			showDatePicker(txtEndD);
		} else if (vId == R.id.txtSearchM) {
			page = 0;
			searchFunctionality();
		} else if (vId == R.id.txtResetM) {
			reSetDefaultFields();
		}
	}

	private void searchFunctionality() {
		start_date = txtStartD.getText().toString();
		end_date = txtEndD.getText().toString();
		if (start_date.equals(getResources().getString(R.string.start_date_money))) {
			Util.showCenteredToast(MoneyActivity.this, getResources().getString(R.string.msg_money_start_date_field_is));
		} else if (end_date.equals(getResources().getString(R.string.end_date))) {
			Util.showCenteredToast(MoneyActivity.this, getResources().getString(R.string.msg_money_end_date_is));
		} else {
			Date sDate = null;
			Date endDate = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			try {
				endDate = sdf.parse(end_date);
				sDate = sdf.parse(start_date);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			if (endDate.compareTo(sDate) >= 0) {
				moneyList.clear();
				loadMoneyListData();
			} else {
				Util.showCenteredToast(MoneyActivity.this, getResources().getString(R.string.msg_money_start_date_is));
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
		Money money = moneyAdapter.getItem(position);
		Intent intent;
		intent = new Intent(MoneyActivity.this, MoneyDetailActivity.class);
		intent.putExtra("data", money);
		startActivity(intent);
		txtStartD.setText(getResources().getString(R.string.start_date_money));
		txtEndD.setText(getResources().getString(R.string.end_date));
		overridePendingTransition(R.anim.right_in, R.anim.left_out);
	}

	@Override
	public void onBackPressed() {
		finishActivity();
	}

	private void finishActivity() {
		MoneyActivity.this.finish();
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}

	/** -------------DATE PICKER DAILOG FOR VIRTUAL AND IN-PERSON----------- */
	private void showDatePicker(final TextView txtV) {
		dpDailog = new DatePickerDailog(true, this, Calendar.getInstance(), new DatePickerListner() {
				@Override
				public void OnDoneButton(Dialog datedialog, Calendar c, String str) {
					txtV.setText(new SimpleDateFormat("yyyy-MM-dd").format(c.getTime()));
					dpDailog.dismiss();
				}

				@Override
				public void OnCancelButton(Dialog datedialog) {
					dpDailog.dismiss();
				}
			});
		dpDailog.show();
	}

	// ----------------TASK TO CHANGE PASSWORD --------------------------------
	private class MoneyAsyncTask extends AsyncTask<Void, Void, NetworkResponse> {

		@Override
		protected void onPreExecute() {
			Util.showProDialog(MoneyActivity.this);
		}

		@Override
		protected NetworkResponse doInBackground(Void... params) {
			JSONObject jSonData = null;
			Cmd cmd = CmdFactory.getMoneyCmd(String.valueOf(page), records_per_page, start_date, end_date);
			NetworkResponse response = NetworkMgr.httpPost(Config.API_URL, cmd.getJsonData());
			if (response != null) {
				if (response.isSuccess()) {
					page = page + 1;
					JSONObject jobj;
					try {
						jobj = new JSONObject(response.getJsonObject().toString());
						jSonData = jobj.getJSONObject("data");
						RemaininAmount = getResources().getString(R.string.remaining_wallet_amo) + " " + jSonData.getString("wallet_amount");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					moneyDataList = (MoneyData) Util.getJsonToClassObject(jSonData.toString(), MoneyData.class);
					if (moneyDataList != null) {
						if (moneyDataList.wallet_info.size() != 0) {
							isMoreRecords = true;
							for (Money moneyObj : moneyDataList.wallet_info) {
								moneyList.add(moneyObj);
							}
						} else {
							isMoreRecords = false;
						}
					}
				}
			}
			return response;
		}

		@Override
		protected void onPostExecute(NetworkResponse networkResponse) {
			Util.dimissProDialog();
			if (networkResponse != null) {
				if (!networkResponse.isSuccess()) {
					Util.showCenteredToast(MoneyActivity.this, networkResponse.getErrMsg());
				} else {
					setProjectListData();
				}
			}
		}
	}

	private void reSetDefaultFields() {
		txtStartD.setText(getResources().getString(R.string.start_date_money));
		txtEndD.setText(getResources().getString(R.string.end_date));
		records_per_page = "10";
		start_date = null;
		end_date = null;
		page = 0;
		onScroll = false;
		loadMoneyListData();
	}

	private void setProjectListData() {
		if (!onScroll) {
			moneyAdapter = new MoneyAdapter(MoneyActivity.this, R.layout.ed_setting_money_row, moneyList);
			LVMoney.setAdapter(moneyAdapter);
		} else {
			moneyAdapter.notifyDataSetChanged();
		}
		txtRemainAmount.setText(RemaininAmount);
	}

	// ----------------List View ScrollListener----------------------
	private void setScrollListenerOnProjectList() {
		
		LVMoney.setOnScrollListener(new OnScrollListener() {
			
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

	private void loadMoneyListData() {
		if (Util.isDeviceOnline(MoneyActivity.this)) {
			Util.clearAsync(moneyAsyncTask);
			moneyAsyncTask = new MoneyAsyncTask();
			moneyAsyncTask.execute();
		} else {
			Util.showCenteredToast(this, getResources().getString(R.string.msg_Connect_internet));
		}
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
				loadMoneyListData();
			}
		}
	}
}
