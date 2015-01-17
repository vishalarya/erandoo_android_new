package erandoo.app.main.message;

import java.util.ArrayList;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.adapters.MessageListAdapter;
import erandoo.app.adapters.MsgSortFilterAdapter;
import erandoo.app.custom.AppHeaderView;
import erandoo.app.custom.BaseFragActivity;
import erandoo.app.database.DatabaseMgr;
import erandoo.app.database.MessageDetail;
import erandoo.app.database.SortFilterModel;
import erandoo.app.network.NetworkResponse;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.SyncAppData;
import erandoo.app.utilities.Util;

public class MessageActivity extends BaseFragActivity implements OnClickListener, TextWatcher{
	private final int REQUEST_CODE = 1111;
	private final String GET_MSG_LIST_FROM_LOCAL = "msg_list_local";
	private final String SYNC_MSG_LIST_FROM_SERVER = "msg_list_sync";
	private final String DEFAULT_SORT_ORDER_DESC= "created_at desc";
	
	private ImageButton imgBtnMsgClearSearch;
	private EditText eTextMSearch;
	private ListView lvMessageList;
	private MessageListAdapter adapter;
	private DatabaseMgr databaseMgr;
	private ArrayList<MessageDetail> messageList;
	private int textlength = 0;
	private ArrayList<MessageDetail> teamList;
	private ArrayList<MessageDetail> searchedList = new ArrayList<MessageDetail>(0);
	private AppHeaderView appHeaderView;
	
	//-------------Filter AND SORT RELATED------------------
	private ArrayList<SortFilterModel> filterArrayList;
	private ArrayList<SortFilterModel> sortArrayList;
	private MsgSortFilterAdapter msgSortFilterAdapter;
	private Dialog sortFilterDialog;
	private ImageView imgVBackSortFilterDialog;
	public static String sortBy;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ed_message);
		databaseMgr = DatabaseMgr.getInstance(MessageActivity.this);
		initialize();
	}

	private void initialize(){
		sortBy = DEFAULT_SORT_ORDER_DESC;
		imgBtnMsgClearSearch = (ImageButton) findViewById(R.id.imgBtnMsgClearSearch);
		eTextMSearch = (EditText) findViewById(R.id.eTextMSearch);
		lvMessageList = (ListView) findViewById(R.id.lvMessageList);
		
		imgBtnMsgClearSearch.setOnClickListener(this); 
		eTextMSearch.addTextChangedListener(this);
		
		lvMessageList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,long arg3) {
				MessageDetail data = (MessageDetail)adapter.getItemAtPosition(position); 
				Intent intent = new Intent(MessageActivity.this, MessageDetailActivity.class);
				intent.putExtra(Constants.SERIALIZABLE_DATA, data);
				startActivityForResult(intent, REQUEST_CODE);
				overridePendingTransition(R.anim.right_in, R.anim.left_out);
			}
		});
		
		setHeaderView();
		loadMessageData(SYNC_MSG_LIST_FROM_SERVER);
		setDataToFilterList();
	}
	
	private void loadMessageData(String operation){
		if (Util.isDeviceOnline(getApplicationContext())) {
			new MessageDataTask().execute(new String[]{operation});
		} else {
			Util.showCenteredToast(getApplicationContext(), getResources().getString(R.string.msg_Connect_internet));
		}
	}
	
	private void setHeaderView() {
		appHeaderView = (AppHeaderView) findViewById(R.id.appHeaderView);
		appHeaderView.setHeaderContent(
				getResources().getDrawable(R.drawable.ic_back), getResources()
						.getString(R.string.Messages), getResources()
						.getDrawable(R.drawable.ic_filter), getResources()
						.getDrawable(R.drawable.ic_sort), Gravity.LEFT);
		appHeaderView.txtHRight1.setVisibility(View.GONE); // Need to handle in next version
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		eTextMSearch.getText().clear();
		setDataToFilterList();
	}
	
	private void setDataToFilterList() {
		filterArrayList = new ArrayList<SortFilterModel>();
		sortArrayList = new ArrayList<SortFilterModel>();
		String[] filterArray = getResources().getStringArray(R.array.MessageFilterArray);
		String[] sortArray = getResources().getStringArray(R.array.MessageSortArray);
		
		for (String name :  filterArray) {
			SortFilterModel msgFObj = new SortFilterModel();
			msgFObj.forWhat = getResources().getString(R.string.filter_by);
			msgFObj.name = name;
		    if(name.equals(filterArray[0])){ 
		    	msgFObj.isChecked = true;
		    }
		    filterArrayList.add(msgFObj);
		}

		for (String name :  sortArray) {
			SortFilterModel msgFObj = new SortFilterModel();
			msgFObj.forWhat = getResources().getString(R.string.Sort_By);
			msgFObj.name = name;
		    if(name.equals(sortArray[0])){ 
		    	msgFObj.isChecked = true;
		    }
		    sortArrayList.add(msgFObj);
		}
	}
	
	@Override
	public void onClick(View view) {
		int vId = view.getId();
		if (vId == R.id.txtHLeft) {
			finishActivity();
		} else if (vId == R.id.imgBtnMsgClearSearch) {
			eTextMSearch.getText().clear();
		} else if (vId == appHeaderView.txtHLeft.getId()) {
			finishActivity();
		} else if (vId == appHeaderView.txtHRight1.getId()) {
			//TODO
			///sortFilterDialog = showSortFilterDialog(false);
		} else if (vId == appHeaderView.txtHRight2.getId()) {
			sortFilterDialog = showSortFilterDialog(true);
		}

		if (imgVBackSortFilterDialog != null) {
			if (vId == imgVBackSortFilterDialog.getId()) {
				sortFilterDialog.dismiss();
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_CODE){
			if(resultCode == RESULT_OK){
				sortBy = DEFAULT_SORT_ORDER_DESC;
				new MessageDataTask().execute(new String[]{GET_MSG_LIST_FROM_LOCAL});
			}
		}
	}
	
	//----------------TASK TO GET ALL RECENT MESSAGES LIST----------------------//
	private class MessageDataTask extends AsyncTask<String, Void, NetworkResponse> {
		@Override
		protected void onPreExecute() {
			Util.showProDialog(MessageActivity.this);
		}

		@Override
		protected NetworkResponse doInBackground(String... params) {
			NetworkResponse response = null;
			messageList = databaseMgr.getMessageList(Constants.DELIVERY_STATUS_ALL,sortBy);
			if(params[0].equals(SYNC_MSG_LIST_FROM_SERVER)){ 
				if(Util.isDeviceOnline(MessageActivity.this)){
					response = SyncAppData.syncInboxData(MessageActivity.this);
					messageList = databaseMgr.getMessageList(Constants.DELIVERY_STATUS_ALL,sortBy);
				}
			}
			return response;
		}

		@Override
		protected void onPostExecute(NetworkResponse networkResponse) {
			Util.dimissProDialog();
			if(networkResponse != null){
				if (!networkResponse.isSuccess()) {
					Util.showCenteredToast(MessageActivity.this,networkResponse.getErrMsg());
				} 
			}
			setMessageListData(false);
		}
	}

	//----------------SEARCH SECTION IMPLEMENTATION----------------------//	
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		teamList = messageList; 
		try{
			textlength = eTextMSearch.getText().toString().length();
			if(textlength>0){
				searchedList.clear();
	            for (int i = 0; i < teamList.size(); i++){
	            	if(teamList.get(i).title!= null){
	      			   if(teamList.get(i).title.toString().toLowerCase().contains(((CharSequence) eTextMSearch.getText().toString().toLowerCase().subSequence(0, textlength)))){
	     				   searchedList.add(teamList.get(i));
	          		   }
	            	}
	            }
	            setMessageListData(true);
			}else{
				setMessageListData(false);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setMessageListData(boolean isSearch){
		if(isSearch){
			adapter = new MessageListAdapter(MessageActivity.this, R.layout.ed_message_row, searchedList,false,false,null,null,null);
		}else{
			adapter = new MessageListAdapter(MessageActivity.this, R.layout.ed_message_row, messageList,false,false,null,null,null);
		}
		lvMessageList.setAdapter(adapter);
	}
	
	//--------------------FILTER AND SORT REALTED --------------------------------//
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
		if(isSort){
			title = getResources().getString(R.string.Sort_By);
			msgSortFilterAdapter = new MsgSortFilterAdapter(this,R.layout.ed_simple_list_row, sortArrayList);
		}else{
			title = getResources().getString(R.string.filter_by);
			msgSortFilterAdapter = new MsgSortFilterAdapter(this,R.layout.ed_simple_list_row, filterArrayList);
		}

		txtTitleSortFilterDialog.setText(title);
		
		lVSortFilterDialog.setAdapter(msgSortFilterAdapter);

		lVSortFilterDialog.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,int pos, long arg3) {
				clearSortFilterSelection();
				SortFilterModel msgFObj = msgSortFilterAdapter.getItem(pos);
				if (!msgFObj.isChecked) {
					msgSortFilterAdapter.getItem(pos).isChecked = true;
					if(isSort) {
						doSorting(msgFObj);
					}else{
						doFiltering(msgFObj);
					}
				}
				msgSortFilterAdapter.notifyDataSetChanged();
			}
		});
	}
	
	private void clearSortFilterSelection(){
		for (SortFilterModel model : msgSortFilterAdapter._data) {
			int index = msgSortFilterAdapter._data.indexOf(model);
			msgSortFilterAdapter._data.get(index).isChecked = false;
		}
	}

	private void doSorting(SortFilterModel msgFObj) {
		if (msgFObj.name.equals(getResources().getString(R.string.Default))) {
			sortBy = DEFAULT_SORT_ORDER_DESC;
		} else if (msgFObj.name.equals(getResources().getString(R.string.Project_ASC))) {
			sortBy = "title asc";
		} else if (msgFObj.name.equals(getResources().getString(R.string.Project_DESC))) {
			sortBy = "title desc";
		} else if (msgFObj.name.equals(getResources().getString(R.string.Project_Date_ASC))) {
			sortBy = "created_at asc";
		} else if (msgFObj.name.equals(getResources().getString(R.string.Project_Date_DESC))) {
			sortBy = "created_at desc";
		} else if (msgFObj.name.equals(getResources().getString(R.string.Message_Date_ASC))) {
			sortBy = "msg_id asc";
		} else if (msgFObj.name.equals(getResources().getString(R.string.Message_Date_DESC))) {
			sortBy = "msg_id desc";
		}
		sortFilterDialog.dismiss();
		new MessageDataTask().execute(new String[] { GET_MSG_LIST_FROM_LOCAL });
	}

	private void doFiltering(SortFilterModel msgFObj) {
		//TODO
	}
	
	@Override
	public void onBackPressed() {
		finishActivity();
	}
	
	private void finishActivity(){
		MessageActivity.this.finish();
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}
	
	@Override
	public void afterTextChanged(Editable s) {}
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
}
