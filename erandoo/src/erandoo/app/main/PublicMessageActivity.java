package erandoo.app.main;

import java.util.ArrayList;

import org.json.JSONArray;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
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
import erandoo.app.database.MessageDetail;
import erandoo.app.database.Profile;
import erandoo.app.database.Project;
import erandoo.app.network.NetworkMgr;
import erandoo.app.network.NetworkResponse;
import erandoo.app.tempclasses.MessageData;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.Util;

public class PublicMessageActivity extends BaseFragActivity implements OnClickListener{
	
  	private final String OPERATION_GET_PUBLIC_MSG = "get_public_msg";
  	private final String OPERATION_MSG_REPLAY = "msg_reply";
  	private final String OPERATION_PARTICULAR_MSG_REPLAY = "particular_msg_reply";
  	private final String OPERATION_MARK_READ = "mark_read";
  	private final String OPERATION_MARK_PUBLIC = "mark_public";

	private TextView txtMDMore;
	private EditText eTextReplyMessage;
	private Button btnMDReply;
	private ImageView imvMDAttachment;
	private ImageView imvMDSend;
	private ListView lvMDList;
	
	private RelativeLayout rlReplyMessageSection;
	private RelativeLayout rlMoreOptionsLayout;
	private LinearLayout llSearch;
	
	private AppHeaderView appHeaderView;
	
	private MessageListAdapter adapter;
	private ArrayList<MessageDetail> details;
	
	private Long task_id;
	private Long user_id;
	private String  operation;
	
//	private MessageDetail detail;
	private MessageDetail newMsg;
	private NetworkResponse response = null;
	
	private JSONArray array;
	
	private int currentFirstVisibleItem = 0;
	private int currentVisibleItemCount = 0;
	private int totalItemCount1 = 0;
	private int currentScrollState = 0;
	private boolean isMoreRecords;
	private int pageCount = 0;
	private boolean onScroll = false;
	
	private OnClickListener markRead;
	private OnClickListener markPublic;
	private OnClickListener reply;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ed_message_details);
		
		task_id = getIntent().getExtras().getLong(Project.FLD_TASK_ID);
		user_id = getIntent().getExtras().getLong(Profile.FLD_USER_ID);
		
		initialize();
	}

	private void initialize() {
		txtMDMore = (TextView) findViewById(R.id.txtMDMore);
		eTextReplyMessage = (EditText) findViewById(R.id.eTextReplyMessage);
		btnMDReply = (Button) findViewById(R.id.btnMDReply);
		imvMDAttachment = (ImageView) findViewById(R.id.imvMDAttachment);
		imvMDSend = (ImageView) findViewById(R.id.imvMDSend);
		lvMDList = (ListView) findViewById(R.id.lvMDList);
		
		rlReplyMessageSection = (RelativeLayout) findViewById(R.id.rlReplyMessageSection);
		rlMoreOptionsLayout = (RelativeLayout) findViewById(R.id.rlMoreOptionsLayout);
		llSearch = (LinearLayout) findViewById(R.id.llSearch);
		
		btnMDReply.setOnClickListener(this);
		imvMDAttachment.setOnClickListener(this);
		imvMDSend.setOnClickListener(this);
		
		setHeaderView();
		
		txtMDMore.setVisibility(View.GONE);
		llSearch.setVisibility(View.GONE);
		imvMDAttachment.setVisibility(View.GONE);
		
		setScrollListenerOnProjectList();
		getPublicMessage(OPERATION_GET_PUBLIC_MSG);
		
		final InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		eTextReplyMessage.setOnFocusChangeListener(new OnFocusChangeListener() {
	        @Override
	        public void onFocusChange(View v, boolean hasFocus) {
	            if(hasFocus){
	                imm.showSoftInput(eTextReplyMessage, InputMethodManager.SHOW_IMPLICIT);
	            }
	        }
	    });
		
		reply = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Util.showCenteredToast(getApplicationContext(), "Under development");
				/*detail = (MessageDetail) v.getTag();
				array = new JSONArray();
				array.put(detail.msg_id);
				setReplyMsgView();*/
			}
		};
		
		markRead = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Util.showCenteredToast(getApplicationContext(), "Under development");
				//getPublicMessage(OPERATION_MARK_READ);
			}
		};
		
		markPublic = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Util.showCenteredToast(getApplicationContext(), "Under development");
				/*detail = (MessageDetail) v.getTag();
				array = new JSONArray();
				array.put(detail.msg_id);
				getPublicMessage(OPERATION_MARK_PUBLIC);*/
			}
		};	
	}
	
	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.btnMDReply) {
			setReplyMsgView();
		} else if (view.getId() == R.id.imvMDSend) {
			createNewMessage();
		} else if (view.getId() == appHeaderView.txtHRight2.getId()) {
			cancelSection();
		} else if(view.getId() == appHeaderView.txtHLeft.getId()){
			finishActivity();
		}
	}
	
	private void setReplyMsgView(){
		if (Util.isDeviceOnline(PublicMessageActivity.this)) {
			rlReplyMessageSection.setVisibility(View.VISIBLE);
			rlMoreOptionsLayout.setVisibility(View.GONE);
			appHeaderView.txtHRight1.setVisibility(View.GONE); 
			appHeaderView.txtHRight2.setVisibility(View.VISIBLE); 
			eTextReplyMessage.setText("");
			eTextReplyMessage.requestFocus();
		}else{
			Util.showCenteredToast(PublicMessageActivity.this, getResources().getString(R.string.msg_Connect_internet));
		}
	}
	
	private void createNewMessage(){
		if (Util.isDeviceOnline(PublicMessageActivity.this)) {
			if (eTextReplyMessage.getText().toString().trim().length() != 0) {
				newMsg = new MessageDetail();
				newMsg.trno = (long) 1;
				newMsg.task_id = task_id;
				newMsg.body = eTextReplyMessage.getText().toString();
				newMsg.msg_type = "proposal";
				if (AppGlobals.userDetail.user_id.equals(user_id)) {
					if (array != null) {
						newMsg.to_user_ids = user_id;
						newMsg.is_public = "0";
					}else{
						newMsg.is_public = "1";
					}
				} else{
					newMsg.to_user_ids = user_id;
					newMsg.is_public = "0";
				}
				new PublicMessageTask().execute(new String[]{OPERATION_MSG_REPLAY});
			}else{
				Util.showCenteredToast(getApplicationContext(), getResources().getString(R.string.msg_Message_can_not_be_blank));
			}
		}else{
			Util.showCenteredToast(PublicMessageActivity.this, getResources().getString(R.string.msg_Connect_internet));
		}
	}
	
	private void cancelSection(){
		appHeaderView.txtHRight1.setVisibility(View.VISIBLE); 
		appHeaderView.txtHRight2.setVisibility(View.GONE); 
		btnMDReply.setVisibility(View.VISIBLE);
		rlMoreOptionsLayout.setVisibility(View.VISIBLE);
		rlReplyMessageSection.setVisibility(View.GONE);
	}
	
	private void getPublicMessage(String operation) {
		if (Util.isDeviceOnline(this)) {
			new PublicMessageTask().execute(new String[]{operation});
		} else {
			Util.showCenteredToast(this,getResources().getString(R.string.msg_Connect_internet));
		}
	}
	
	public class PublicMessageTask extends AsyncTask<String, Void, String>{
		
		@Override
		protected void onPreExecute() {
			Util.showProDialog(PublicMessageActivity.this);
		}
		
		@Override
		protected String doInBackground(String... param) {
			operation = param[0]; 
			Cmd cmd = null;
			if(operation.equals(OPERATION_GET_PUBLIC_MSG)){
				cmd = CmdFactory.createGetProjectMessageCmd();
				//cmd.addData(Project.FLD_PAGE, pageCount);
				cmd.addData(Project.FLD_TASK_ID, task_id);
				cmd.addData(Profile.FLD_USER_ID, AppGlobals.userDetail.user_id);
				cmd.addData(MessageDetail.FLD_MESSAGE_TYPE, "proposal");
			} else if(operation.equals(OPERATION_MSG_REPLAY)){
				cmd = CmdFactory.createNewMessageCmd();
				cmd.addData(MessageDetail.FLD_TO_USER_ID, newMsg.to_user_ids);
				cmd.addData(MessageDetail.FLD_TASK_ID, newMsg.task_id);
				cmd.addData(MessageDetail.FLD_BODY, newMsg.body);
				cmd.addData(MessageDetail.FLD_MESSAGE_TYPE, newMsg.msg_type);
				cmd.addData(MessageDetail.FLD_IS_PUBLIC, newMsg.is_public);
				//cmd.addData(MessageDetail.FLD_MOBILE_REC_ID,Util.getMobileRecToken(newMsg.recId));
				cmd.addData(MessageDetail.FLD_TRNO, newMsg.trno); 
			} else if (operation.equals(OPERATION_MARK_READ) || operation.equals(OPERATION_MARK_PUBLIC)) {
				cmd = CmdFactory.createDeleteMessageCmd();
				cmd.addData(MessageDetail.FLD_OPERATION, Constants.MESSAGE_OPERATION_MARK_READ);
				cmd.addData(MessageDetail.FLD_MESSAGE_ID, array);
			} else if (operation.equals(OPERATION_MARK_PUBLIC)) {
				cmd = CmdFactory.createDeleteMessageCmd();
				cmd.addData(MessageDetail.FLD_OPERATION, Constants.MESSAGE_OPERATION_MARK_PUBLIC);
				cmd.addData(MessageDetail.FLD_MESSAGE_ID, array);
			} else if (operation.equals(OPERATION_PARTICULAR_MSG_REPLAY)) {
				//TODO..
			}
			
			response = NetworkMgr.httpPost(Config.API_URL, cmd.getJsonData());
			if (response != null) {
				if (response.isSuccess()) {
					pageCount = pageCount + 1;
					handleNetworkResponse(operation);
				}
			}
			return operation;
		}
		
		@Override
		protected void onPostExecute(String operation) {
			Util.dimissProDialog();
			if (response != null) {
				if (response.isSuccess()) {
					if (operation.equals(OPERATION_GET_PUBLIC_MSG)) {
						setListViewAdapter();
					} else{
						pageCount = 0;
						onScroll = false;
						new PublicMessageTask().execute(new String[]{OPERATION_GET_PUBLIC_MSG});
					}
					cancelSection();
				} else{
					Util.showCenteredToast(PublicMessageActivity.this, response.getErrMsg().toString());
				}
			}
		}
	}
	
	private void handleNetworkResponse(String operation){
		if (operation.equals(OPERATION_GET_PUBLIC_MSG)) {
			MessageData messageData = (MessageData)Util.getJsonToClassObject(response.getJsonObject().toString(), MessageData.class);
			details = messageData.data;
		}
	}
	
	// ----------------List View ScrollListener----------------------
	private void setScrollListenerOnProjectList() {
		lvMDList.setOnScrollListener(new OnScrollListener() {
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
				getPublicMessage(OPERATION_GET_PUBLIC_MSG);
			} else {
				// Util.showCenteredToast(SearchProjectActivity.this,
				// getResources().getString(R.string.No_more_records));
			}
		}
	}
	
	private void setListViewAdapter(){
		if (details.size() != 0) {
			if (!onScroll) {
				adapter = new MessageListAdapter(PublicMessageActivity.this, R.layout.ed_message_row, details, false, true,reply,markRead,markPublic);
				lvMDList.setAdapter(adapter);
			} else {
				adapter.notifyDataSetChanged();
			}
		} else{
			Util.showCenteredToast(PublicMessageActivity.this, "No message available");
		}
	}
		
	private void setHeaderView() {
		appHeaderView = (AppHeaderView) findViewById(R.id.appHeaderView);
		appHeaderView.setHeaderContent(
				getResources().getDrawable(R.drawable.ic_back), getResources()
						.getString(R.string.public_message), null, getResources()
						.getString(R.string.Cancel), Gravity.LEFT);
		appHeaderView.txtHRight2.setVisibility(View.GONE); 
		appHeaderView.vRightLine.setVisibility(View.GONE); 
	}

	@Override
	public void onBackPressed() {
		finishActivity();
	}
	
	private void finishActivity(){
		setResult(RESULT_OK);
		PublicMessageActivity.this.finish();
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}
}
