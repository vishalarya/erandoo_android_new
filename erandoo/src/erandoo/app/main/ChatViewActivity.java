package erandoo.app.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.adapters.ChatListAdapter;
import erandoo.app.cmds.Cmd;
import erandoo.app.cmds.CmdFactory;
import erandoo.app.config.Config;
import erandoo.app.database.DatabaseMgr;
import erandoo.app.database.MessageDetail;
import erandoo.app.database.TableType;
import erandoo.app.mqtt.ChatMgr;
import erandoo.app.mqtt.ErandooMqttMessage;
import erandoo.app.mqtt.util.MqttUtil;
import erandoo.app.network.NetworkMgr;
import erandoo.app.network.NetworkResponse;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.Util;

public class ChatViewActivity extends Activity implements OnClickListener {
	private static final String MQTT_SUBSCRIBE = "subscribe";
	private static final String MQTT_PUBLISH = "publish";
	private static final String MQTT_SYNC_MSG_DATA = "msg_sync";

	private TextView txtChatUserName;
	private EditText eTextChatMessage;
	private ListView lvChatList;
	//private ImageView imvChatSmiley;
	private ImageView imvChatSend;
	private ImageView imvChat;
	private ImageView imvChatToUserImage;
	private ChatListAdapter adapter;
	private ErandooMqttMessage eMqttMessage;
	private ArrayList<ErandooMqttMessage> userChatList;
	private String toUserDisplayId;
	private LocalBroadcastManager lBroadcastManager;
    private DatabaseMgr databaseMgr;
    private NetworkResponse nResponse;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ed_chat_list_view);
		initialize();
	}

	private void initialize() {
		databaseMgr = DatabaseMgr.getInstance(this);
		lBroadcastManager = LocalBroadcastManager.getInstance(this);
		eMqttMessage = (ErandooMqttMessage) getIntent().getSerializableExtra(Constants.SERIALIZABLE_DATA);
		userChatList = new ArrayList<ErandooMqttMessage>(0);
		txtChatUserName = (TextView) findViewById(R.id.txtChatUserName);
		//imvChatSmiley = (ImageView) findViewById(R.id.imvChatSmiley);
		imvChatSend = (ImageView) findViewById(R.id.imvChatSend);
		imvChat = (ImageView) findViewById(R.id.imvChat);
		imvChatToUserImage = (ImageView) findViewById(R.id.imvChatToUserImage);
		eTextChatMessage = (EditText) findViewById(R.id.eTextChatMessage);
		lvChatList = (ListView) findViewById(R.id.lvChatList);

		//imvChatSmiley.setOnClickListener(this);
		imvChatSend.setOnClickListener(this);
		imvChat.setOnClickListener(this);
		
		if (eMqttMessage != null) {
			if (AppGlobals.userDetail.user_id.equals(eMqttMessage.to_id)) {
				ChatMgr.setCurrentChatUserId(eMqttMessage.from_id);
				toUserDisplayId = eMqttMessage.from_display_id;
				txtChatUserName.setText(eMqttMessage.from_display_id);

			} else {
				ChatMgr.setCurrentChatUserId(eMqttMessage.to_id);
				toUserDisplayId = eMqttMessage.to_display_id;
				txtChatUserName.setText(eMqttMessage.to_display_id);
			}

			Util.loadImage(imvChatToUserImage, eMqttMessage.user_image,R.drawable.ic_launcher);
		}
		
		new ChatAsyncTask().execute(new String[] {MQTT_SUBSCRIBE});
		regisLocalBroadcastManager();
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.imvChatSend) {
			publishChatMessage();
		} else if (view.getId() == R.id.imvChat) {
			new ChatAsyncTask().execute(new String[]{MQTT_SYNC_MSG_DATA});
		}
	}

	@Override
	protected void onResume() {
		AppGlobals.isChatOpened = true;
		refreshChatList();
		super.onResume();
	}
	
	

	private void regisLocalBroadcastManager() {
		lBroadcastManager.unregisterReceiver(mBroadcastReceiver);
		lBroadcastManager.registerReceiver(mBroadcastReceiver,
				new IntentFilter(Constants.LOCAL_BROADCAST_FOR_CHAT));
	}

	private void publishChatMessage() {
		if(eTextChatMessage.getText().toString().trim().length() > 0){
			if (Util.isDeviceOnline(this)) {
				new ChatAsyncTask().execute(new String[] { MQTT_PUBLISH });
			} else {
				Util.showCenteredToast(this,
						getResources().getString(R.string.msg_Connect_internet));
			}
		}
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (AppGlobals.isChatOpened) {
				ErandooMqttMessage eMqttMessage = (ErandooMqttMessage) intent.getSerializableExtra(Constants.SERIALIZABLE_DATA);
				userChatList.add(eMqttMessage);
				refreshChatList();
			}
		}
	};

	private void setChatListData() {
		userChatList.addAll(ChatMgr.getUserChats(eMqttMessage));
		ChatMgr.removeUserChats(eMqttMessage);
		ChatMgr.removeChatNotificationId(eMqttMessage.getFriendUserId());
		adapter = new ChatListAdapter(ChatViewActivity.this,R.layout.ed_chat_list_row, userChatList);
		adapter.setNotifyOnChange(true);
		lvChatList.setAdapter(adapter);
		refreshChatList();
	}

	private void refreshChatList() {
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
		lvChatList.setSelection(userChatList.size() - 1);
	}

	private class ChatAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			Util.showProDialog(ChatViewActivity.this);
		}

		@Override
		protected String doInBackground(String... params) {
			String param = params[0];
			try {
				if (param.equals(MQTT_PUBLISH)) {
					submitChat(ChatMgr.getCurrentChatUserId(), toUserDisplayId);
					
				} else if (param.equals(MQTT_SUBSCRIBE)) {
					getChatHistory();
					MqttUtil.connectToMqtt();
					
				}else if(param.equals(MQTT_SYNC_MSG_DATA)){
					syncMessageData();
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return param; 
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Util.dimissProDialog();
			if(result.equals(MQTT_SUBSCRIBE)){
				setChatListData();
			}else if(result.equals(MQTT_SYNC_MSG_DATA)){
				syncResponseHandler();
			}
			eTextChatMessage.getText().clear();
		}
	}
	
	private void submitChat(Long toId, String toDisplayId) {
		ErandooMqttMessage message = new ErandooMqttMessage();
		message.event_type = Constants.MQTT_EVENT_TYPE_CHAT;
		message.from_id =  AppGlobals.userDetail.user_id;
		message.from_display_id =  AppGlobals.userDetail.fullname;
		message.to_id = toId;
		message.to_display_id = toDisplayId;
        Date date = new Date();
		message.timestamp = String.valueOf(date.getTime());
		message.msg = eTextChatMessage.getText().toString().trim();
		message.sent_from_client_id = eTextChatMessage.getText().toString().trim();
		message.user_image = AppGlobals.userDetail.userimage;
		
		message.is_sender_msg = Constants.VAL_TRUE;
		MqttUtil.publish(AppGlobals.userDetail.user_id,Constants.MQTT_EVENT_TYPE_CHAT, getMessageBody(message));

		message.is_sender_msg = Constants.VAL_FALSE;
		MqttUtil.publish(toId, Constants.MQTT_EVENT_TYPE_CHAT,getMessageBody(message));
	}

	private String getMessageBody(ErandooMqttMessage mqttMessage) {
		JSONObject body = new JSONObject();
		try {
			body.put(ErandooMqttMessage.FLD_CMD, Constants.MQTT_EVENT_TYPE_CHAT);//command -> chat
			body.put(ErandooMqttMessage.FLD_EVENT_TYPE,Constants.MQTT_EVENT_TYPE_CHAT);
			body.put(ErandooMqttMessage.FLD_FROM_ID, mqttMessage.from_id);
			body.put(ErandooMqttMessage.FLD_FROM_DISPLAY_ID,mqttMessage.from_display_id);
			body.put(ErandooMqttMessage.FLD_TO_ID, mqttMessage.to_id);
			body.put(ErandooMqttMessage.FLD_TO_DISPLAY_ID,mqttMessage.to_display_id);
			body.put(ErandooMqttMessage.FLD_MSG, mqttMessage.msg);
			body.put(ErandooMqttMessage.FLD_TIMESTAMP, mqttMessage.timestamp);
			body.put(ErandooMqttMessage.FLD_USER_IMAGE, mqttMessage.user_image);
			body.put(ErandooMqttMessage.FLD_SENT_FROM_CLIENT_ID,mqttMessage.msg);
			body.put(ErandooMqttMessage.FLD_IS_SENDER_MSG,mqttMessage.is_sender_msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return body.toString();
	}
	
	private void getChatHistory(){
		userChatList.clear();
		Cursor cursor = null;
		try {
			String threadKey = Util.getMsgThreadKey(AppGlobals.userDetail.user_id, ChatMgr.getCurrentChatUserId());
			cursor = databaseMgr.queryTable(TableType.InboxUserTable, 
											new String[]{MessageDetail.FLD_FROM_USER_ID,MessageDetail.FLD_TO_USER_ID,
											MessageDetail.FLD_BODY,MessageDetail.FLD_CREATED_AT}, 
											"msg_type =? AND thread_key=?", new String[]{"chat",threadKey},
											null, null, "msg_id ASC");
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				while (!cursor.isAfterLast()) {
					ErandooMqttMessage eMqttMessage = new ErandooMqttMessage();
					eMqttMessage.from_id = cursor.getLong(cursor.getColumnIndex(MessageDetail.FLD_FROM_USER_ID));
					eMqttMessage.to_id = cursor.getLong(cursor.getColumnIndex(MessageDetail.FLD_TO_USER_ID));
					eMqttMessage.msg = cursor.getString(cursor.getColumnIndex(MessageDetail.FLD_BODY));
					String dataTime = cursor.getString(cursor.getColumnIndex(MessageDetail.FLD_CREATED_AT));
					eMqttMessage.date = getDateTimeString(dataTime,true);
					eMqttMessage.time = getDateTimeString(dataTime,false);
					eMqttMessage.event_type = Constants.MQTT_EVENT_TYPE_CHAT;
					userChatList.add(eMqttMessage);
					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}
	
	private String getDateTimeString(String dateTime, boolean isDate){
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //2014-12-12 08:30:22
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));  
			 Date date = sdf.parse(dateTime);
			if(isDate){
				SimpleDateFormat tdf = new SimpleDateFormat("MMM dd, yyyy");
				return tdf.format(date);
			}else{
				SimpleDateFormat tdf = new SimpleDateFormat("hh:mm a");
			    return tdf.format(date);
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	private void syncMessageData(){
		if(Util.isDeviceOnline(ChatViewActivity.this)){
			Long maxTrno = Long.valueOf(databaseMgr.getMaxTrno(TableType.InboxUserTable, null, null));
			Cmd cmd = CmdFactory.createGetMessageCmd();
			cmd.addData(MessageDetail.FLD_TRNO, maxTrno);
			nResponse = NetworkMgr.httpPost(Config.API_URL, cmd.getJsonData());
			if (nResponse != null) {
				if(nResponse.isSuccess()){
					databaseMgr.updateMessageData(nResponse, null,maxTrno);
				}
			}
		}
	}
	
	private void syncResponseHandler(){
		if(nResponse != null){
			if (!nResponse.isSuccess()) {
				Util.showCenteredToast(ChatViewActivity.this,nResponse.getErrMsg());
			} 
		}
		nResponse = null;
		finishActivity();
	}
	
	@Override
	public void onBackPressed() {
		new ChatAsyncTask().execute(new String[]{MQTT_SYNC_MSG_DATA});
	}

	private void finishActivity() {
		lBroadcastManager.unregisterReceiver(mBroadcastReceiver);
		ChatMgr.setCurrentChatUserId(null);
		Util.hideKeypad(this, eTextChatMessage);
		AppGlobals.isChatOpened = false;
		ChatViewActivity.this.finish();
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}
}
