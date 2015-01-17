package erandoo.app.main.message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.adapters.MessageListAdapter;
import erandoo.app.cmds.Cmd;
import erandoo.app.cmds.CmdFactory;
import erandoo.app.config.Config;
import erandoo.app.custom.AppHeaderView;
import erandoo.app.custom.BaseFragActivity;
import erandoo.app.database.DatabaseMgr;
import erandoo.app.database.MessageDetail;
import erandoo.app.database.TableType;
import erandoo.app.main.AppGlobals;
import erandoo.app.network.NetworkMgr;
import erandoo.app.network.NetworkResponse;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.Util;

public class MessageDetailActivity extends BaseFragActivity implements OnClickListener, TextWatcher {

  	private final String OPERATION_GET_MSG_THREAD = "get_msg_thread";
  	private final String OPERATION_MSG_REPLAY = "msg_reply";
  	private final String OPERATION_MSG_MORE = "msg_more";
  	
	private ImageButton imgBtnMsgDClearSearch;
	private ImageView imvMDAttachment;
	private ImageView imvMDSend;
	private TextView txtMDTaskTitle;
	private TextView txtMDMore;
	private EditText eTextMDSearch;
	private EditText eTextReplyMessage;
	private ListView lvMDList;
	private Button btnMDReply;
	private Button btnMDDone;
	private RelativeLayout rlMoreOptionsLayout;
	private RelativeLayout rlReplyMessageSection;
	private LinearLayout llMoreOptions;
	private TextView txtMDDelete;
	private TextView txtMDMarkRead;
	private TextView txtMDMarkUnread;
	public static CheckBox chkSelectAll;
	
	private MessageDetail recentMsg;
	private MessageDetail newMsg;
	private MessageListAdapter adapter;
	private DatabaseMgr databaseMgr;
	private Long maxTrno = null;
	private String operationType = "";
	//private boolean isVisible = false;
    
    //Delete Message Section.
  	private JSONArray msg_id_array;
  	private JSONArray mobile_rec_array;
 
  	private NetworkResponse networkResponse = null;
  	private ArrayList<MessageDetail> messageThreadList;
  	private AppHeaderView appHeaderView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ed_message_details);
		recentMsg = (MessageDetail) getIntent().getSerializableExtra(Constants.SERIALIZABLE_DATA);
		databaseMgr = DatabaseMgr.getInstance(MessageDetailActivity.this);
		
		initialize();
	}
	
	private void initialize(){
		imgBtnMsgDClearSearch = (ImageButton) findViewById(R.id.imgBtnMsgDClearSearch);
		imvMDAttachment = (ImageView) findViewById(R.id.imvMDAttachment);
		imvMDSend = (ImageView) findViewById(R.id.imvMDSend);
		txtMDTaskTitle = (TextView) findViewById(R.id.txtMDTaskTitle);
		txtMDDelete = (TextView) findViewById(R.id.txtMDDelete);
		txtMDMarkRead = (TextView) findViewById(R.id.txtMDMarkRead);
		txtMDMarkUnread = (TextView) findViewById(R.id.txtMDMarkUnread);
		txtMDMore = (TextView) findViewById(R.id.txtMDMore);
		eTextMDSearch = (EditText) findViewById(R.id.eTextMDSearch);
		eTextReplyMessage = (EditText) findViewById(R.id.eTextReplyMessage);
		lvMDList = (ListView) findViewById(R.id.lvMDList);
		btnMDReply = (Button) findViewById(R.id.btnMDReply);
		btnMDDone = (Button) findViewById(R.id.btnMDDone);
		rlMoreOptionsLayout = (RelativeLayout) findViewById(R.id.rlMoreOptionsLayout);
		rlReplyMessageSection = (RelativeLayout) findViewById(R.id.rlReplyMessageSection);
		llMoreOptions = (LinearLayout) findViewById(R.id.llMoreOptions);
		chkSelectAll = (CheckBox) findViewById(R.id.chkSelectAll);

		setHeaderView();
		
		imgBtnMsgDClearSearch.setOnClickListener(this);
		imvMDAttachment.setOnClickListener(this); 
		imvMDSend.setOnClickListener(this); 
		txtMDMore.setOnClickListener(this); 
		btnMDReply.setOnClickListener(this);
		txtMDDelete.setOnClickListener(this); 
		txtMDMarkRead.setOnClickListener(this); 
		txtMDMarkUnread.setOnClickListener(this);
		btnMDDone.setOnClickListener(this);
		eTextMDSearch.addTextChangedListener(this);
		chkSelectAll.setOnClickListener(this);
		
		final InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		eTextReplyMessage.setOnFocusChangeListener(new OnFocusChangeListener() {
	        @Override
	        public void onFocusChange(View v, boolean hasFocus) {
	            if(hasFocus){
	                imm.showSoftInput(eTextReplyMessage, InputMethodManager.SHOW_IMPLICIT);
	            }
	        }
	    });
		
		new MessageDetailTask().execute(new String[]{OPERATION_GET_MSG_THREAD}); 
	}
	
	private void setHeaderView() {
		appHeaderView = (AppHeaderView) findViewById(R.id.appHeaderView);
		appHeaderView.setHeaderContent(
				getResources().getDrawable(R.drawable.ic_back), getResources()
						.getString(R.string.conversation), getResources()
						.getDrawable(R.drawable.ic_filter), getResources()
						.getString(R.string.Cancel), Gravity.LEFT);
		appHeaderView.txtHRight2.setVisibility(View.GONE); 
		appHeaderView.vRightLine.setVisibility(View.GONE); 
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.imgBtnMsgDClearSearch){
			eTextMDSearch.getText().clear();
			
		}else if(v.getId() == R.id.imvMDAttachment){
			//TODO
		}else if(v.getId() == R.id.imvMDSend){
			createNewMessage();
			
		}else if(v.getId() == R.id.btnMDReply){
			setReplyMsgView();
			
		}else if(v.getId() == R.id.txtMDMore){
			setMoreOptionView();
			
		}else if(v.getId() == R.id.txtMDDelete){
			setEditableListView(Constants.MESSAGE_OPERATION_DELETE);
			
		}else if(v.getId() == R.id.txtMDMarkRead){
			setEditableListView(Constants.MESSAGE_OPERATION_MARK_READ);
			
		}else if(v.getId() == R.id.txtMDMarkUnread){
			setEditableListView(Constants.MESSAGE_OPERATION_MARK_UNREAD);
			
		}else if(v.getId() == R.id.btnMDDone){
			if (searchedList.size() > 0) {
				btnMDDoneClickHandler(searchedList);
			}else{
				btnMDDoneClickHandler(messageThreadList);
			}
		}else if(v.getId() == R.id.chkSelectAll){
			chkSelectAllClickHandler();
		}else if(v.getId() == appHeaderView.txtHLeft.getId()){
			//TODO back button
			finishActivity();
		} else if(v.getId() == appHeaderView.txtHRight1.getId()){
			//TODO filter button
		} else if(v.getId() == appHeaderView.txtHRight2.getId()){
			cancelSection();
		}
	}
	
	private void setReplyMsgView(){
		if (Util.isDeviceOnline(MessageDetailActivity.this)) {
			llMoreOptions.setVisibility(View.GONE);
			rlReplyMessageSection.setVisibility(View.VISIBLE);
			rlMoreOptionsLayout.setVisibility(View.GONE);
			appHeaderView.txtHRight1.setVisibility(View.GONE); 
			appHeaderView.txtHRight2.setVisibility(View.VISIBLE); 
			eTextReplyMessage.setText("");
			eTextReplyMessage.requestFocus();
		}else{
			Util.showCenteredToast(MessageDetailActivity.this, getResources().getString(R.string.msg_Connect_internet));
		}
	}
	
	private void setMoreOptionView(){
		if(llMoreOptions.getVisibility() == View.GONE){
			llMoreOptions.setVisibility(View.VISIBLE);
			Animation pull_in = null;
			pull_in = AnimationUtils.loadAnimation(MessageDetailActivity.this, R.anim.pull_in);
			llMoreOptions.startAnimation(pull_in);
		}else{
			Animation pull_out = null;
			pull_out = AnimationUtils.loadAnimation(MessageDetailActivity.this, R.anim.pull_out);
			llMoreOptions.startAnimation(pull_out);
			llMoreOptions.setVisibility(View.GONE);
		}
	}
	
	private void setEditableListView(String operType){
		if (Util.isDeviceOnline(MessageDetailActivity.this)) {
			operationType = operType;
			boolean isOperationValid = false;
			for (MessageDetail message: messageThreadList) {
				if(!isOperationValid){
					if (operationType.equals(Constants.MESSAGE_OPERATION_DELETE)) {
						if (message.is_delete.equals("0")) {
							isOperationValid = true;
						}else{
							continue;
						}
					}else if (operationType.equals(Constants.MESSAGE_OPERATION_MARK_READ)) {
						if (message.is_read.equals("0")) {
							isOperationValid = true;
						}else{
							continue;
						}
					}else if (operationType.equals(Constants.MESSAGE_OPERATION_MARK_UNREAD)) {
						if (message.is_read.equals("1")) {
							isOperationValid = true;
						}else{
							continue;
						}
					}
				}
				message.isEditable = true;
				message.operationType = operType;
			}
			
			if (isOperationValid) {
				setMoreOptionView();
				setEditableView();
			}else{
				Util.showCenteredToast(this, getResources().getString(R.string.msg_No_message_for_this_operation)); 
				setMoreOptionView();
			}
		}else{
			Util.showCenteredToast(MessageDetailActivity.this, getResources().getString(R.string.msg_Connect_internet));
		}
	}
	
	private void setEditableView(){
		appHeaderView.txtHRight1.setVisibility(View.GONE); 
		appHeaderView.txtHRight2.setVisibility(View.VISIBLE); 
		
		chkSelectAll.setVisibility(View.VISIBLE);
		txtMDMore.setVisibility(View.GONE);
		btnMDReply.setVisibility(View.GONE);
		btnMDDone.setVisibility(View.VISIBLE);
		adapter.notifyDataSetChanged();
	}
	
	private void cancelSection(){
		appHeaderView.txtHRight1.setVisibility(View.VISIBLE); 
		appHeaderView.txtHRight2.setVisibility(View.GONE); 

		btnMDDone.setVisibility(View.GONE);
		//when come from more cancel.
		btnMDReply.setVisibility(View.VISIBLE);
		txtMDMore.setVisibility(View.VISIBLE);
		//when come from reply cancel.
		rlMoreOptionsLayout.setVisibility(View.VISIBLE);
		rlReplyMessageSection.setVisibility(View.GONE);
		for (MessageDetail detail: messageThreadList) {
			detail.isSelected = false;
			detail.isEditable = false;
		}
		chkSelectAll.setChecked(false);
		chkSelectAll.setVisibility(View.GONE);
		adapter.notifyDataSetChanged();
	}
	
	private void chkSelectAllClickHandler(){
		for (int i = 0; i < messageThreadList.size(); i++) {
			MessageDetail detail = messageThreadList.get(i);
			detail.isSelected = chkSelectAll.isChecked();
		}
		adapter.notifyDataSetChanged();
	}
	
	private void createNewMessage(){
		if (Util.isDeviceOnline(MessageDetailActivity.this)) {
			if (eTextReplyMessage.getText().toString().trim().length() != 0) {
				newMsg = new MessageDetail();
				newMsg.body = eTextReplyMessage.getText().toString().trim();
				newMsg.from_user_id =  AppGlobals.userDetail.user_id;
				newMsg.title = recentMsg.title;
				if (recentMsg.from_user_id.equals( AppGlobals.userDetail.user_id)) {
					newMsg.to_user_ids = recentMsg.to_user_ids;
				}else{
					newMsg.to_user_ids = recentMsg.from_user_id;
				}
				
				newMsg.msg_from_name =  AppGlobals.userDetail.fullname;
				newMsg.task_id = recentMsg.task_id;
				newMsg.is_public = recentMsg.is_public;
				newMsg.msg_type = recentMsg.msg_type;
				maxTrno = Long.valueOf(databaseMgr.getMaxTrno(TableType.InboxUserTable, null, null));
				newMsg.trno = maxTrno;
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String date = df.format(Calendar.getInstance().getTime());
				newMsg.created_at = date;
				newMsg.delivery_status = Constants.DELIVERY_STATUS_PENDING;
				new MessageDetailTask().execute(new String[]{OPERATION_MSG_REPLAY});
			}else{
				Util.showCenteredToast(getApplicationContext(), getResources().getString(R.string.msg_Message_can_not_be_blank));
			}
		}else{
			Util.showCenteredToast(MessageDetailActivity.this, getResources().getString(R.string.msg_Connect_internet));
		}
	}
	
	//----------------TASK TO GET RECENT MESSAGES LIST----------------------//
	private class MessageDetailTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			Util.showProDialog(MessageDetailActivity.this);
		}

		@Override
		protected String doInBackground(String... params) {
			networkResponse = null;
			String  operation = params[0]; 
			if(operation.equals(OPERATION_GET_MSG_THREAD)){
				messageThreadList = databaseMgr.getMessagesThreadList(recentMsg);
				
			}else if(operation.equals(OPERATION_MSG_REPLAY)){
				networkResponse = databaseMgr.createMessage(newMsg);
				
			}else if(operation.equals(OPERATION_MSG_MORE)){
				maxTrno = Long.valueOf(databaseMgr.getMaxTrno(TableType.InboxUserTable, null, null));
				Cmd cmd = CmdFactory.createDeleteMessageCmd();
				cmd.addData(MessageDetail.FLD_TRNO, maxTrno);
				cmd.addData(MessageDetail.FLD_OPERATION, operationType);
				cmd.addData(MessageDetail.FLD_MOBILE_REC_ID, mobile_rec_array);
				cmd.addData(MessageDetail.FLD_MESSAGE_ID, msg_id_array);
				networkResponse = NetworkMgr.httpPost(Config.API_URL, cmd.getJsonData());
				if (networkResponse != null) {
					if(networkResponse.isSuccess()){
						databaseMgr.updateMessageData(networkResponse, null,maxTrno);
					}
				}
			}
//			Util.writeDBfileOnSdcard(MessageDetailActivity.this);
			return operation;
		}

		@Override
		protected void onPostExecute(String operation) {
			Util.dimissProDialog();
			if(operation.equals(OPERATION_GET_MSG_THREAD)){
				txtMDTaskTitle.setText(recentMsg.title);
				setMessageDetailsListData(false);
				
			}else if(operation.equals(OPERATION_MSG_REPLAY)){
				handleNetworkResponse(operation);
				
			}else if(operation.equals(OPERATION_MSG_MORE)){
				handleNetworkResponse(operation);
			}
		}
	}
		
	private void handleNetworkResponse(String operation){
		if(networkResponse != null){
			if(operation.equals(OPERATION_MSG_REPLAY)){
				if (!networkResponse.isSuccess()) {
					Util.showCenteredToast(MessageDetailActivity.this,networkResponse.getErrMsg());
				} else {
					Util.showCenteredToast(getApplicationContext(), getResources().getString(R.string.msg_Message_send_successfully));
				}
			}else if(operation.equals(OPERATION_MSG_MORE)){
				if (networkResponse.isSuccess()) {
					if (operationType.equals(Constants.MESSAGE_OPERATION_DELETE)) {
						Util.showCenteredToast(getApplicationContext(), getResources().getString(R.string.msg_deleted));
					}else if (operationType.equals(Constants.MESSAGE_OPERATION_MARK_READ)) {
						Util.showCenteredToast(getApplicationContext(), getResources().getString(R.string.msg_read));
					}else if (operationType.equals(Constants.MESSAGE_OPERATION_MARK_UNREAD)) {
						Util.showCenteredToast(getApplicationContext(), getResources().getString(R.string.msg_unread));
					}else{
						Util.showCenteredToast(getApplicationContext(), getResources().getString(R.string.msg_Message_status_updated));
					}
				}else{
					Util.showCenteredToast(getApplicationContext(), getResources().getString(R.string.msg_Unable_to_update));
				}
			}
		}
		new MessageDetailTask().execute(new String[]{OPERATION_GET_MSG_THREAD}); 
		cancelSection();
		networkResponse = null;
	}
	
	private void setMessageDetailsListData(boolean isSearch){
		if(isSearch){
			adapter = new MessageListAdapter(MessageDetailActivity.this, R.layout.ed_message_row, searchedList,true,false,null,null,null);
		}else{
			adapter = new MessageListAdapter(MessageDetailActivity.this, R.layout.ed_message_row, messageThreadList,true,false,null,null,null);
		}
		lvMDList.setAdapter(adapter);
	}
	
	private void btnMDDoneClickHandler(ArrayList<MessageDetail> messageThreadList){
		if(Util.isDeviceOnline(this)){ 
			try {
				msg_id_array = new JSONArray();
				mobile_rec_array = new JSONArray();
				for (MessageDetail detail: messageThreadList) {
					if (detail.isSelected) {
						msg_id_array.put(detail.msg_id);
						JSONObject object = new JSONObject();
						object.put(MessageDetail.FLD_MESSAGE_ID,detail.msg_id);
						object.put(MessageDetail.FLD_MOBILE_REC_ID, Util.getMobileRecToken(detail.recId));
						detail.isSelected = false;
						mobile_rec_array.put(object);
					}
					if (msg_id_array.length() > 0) {
						detail.isEditable = false;
					}
				}
				
				if (msg_id_array.length() > 0) {
					cancelSection();
					new MessageDetailTask().execute(new String[]{OPERATION_MSG_MORE});
				}else{
					Util.showCenteredToast(this,getResources().getString(R.string.msg_Please_select_at_least_one)); 
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			Util.showCenteredToast(this,getResources().getString(R.string.msg_Connect_internet)); 
		}
		
	}
	
	//----------------SEARCH SECTION IMPLEMENTATION----------------------//	
		private ArrayList<MessageDetail> searchedList = new ArrayList<MessageDetail>();
		private ArrayList<MessageDetail> tempList;
		private int textlength = 0;
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			tempList = messageThreadList; 
			try{
				textlength = eTextMDSearch.getText().toString().length();
				if(textlength>0){
					searchedList.clear();
		            for (int i = 0; i < tempList.size(); i++){
		            	if(tempList.get(i).body!= null){
		      			   if(tempList.get(i).body.toString().toLowerCase().contains(((CharSequence) eTextMDSearch.getText().toString().toLowerCase().subSequence(0, textlength)))){
		      				 searchedList.add(tempList.get(i));
		          		   }
		            	}
		            }
		            setMessageDetailsListData(true);
				}else{
					setMessageDetailsListData(false);
				}
				
			}catch (Exception e) {
				e.printStackTrace();
			}
		}

	//-------------------------Filter Popup ------------------------------------------
	/*MenuPopup customPopup;
	private void showFilterPopup(Context context,View v, String[] items){
		customPopup = new MenuPopup(context, v, items, new ItemClickListener() {
			@Override
			public void onClick(String str) {
				Util.showCenteredToast(getApplicationContext(), str);
			}
		});
	}*/
	
	@Override
	public void onBackPressed() {
		finishActivity();
	}
	
	private void finishActivity(){
		setResult(RESULT_OK);
 		MessageDetailActivity.this.finish();
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}
	
	@Override
	public void afterTextChanged(Editable s) {}
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
}
