package erandoo.app.mqtt;

import java.util.ArrayList;
import java.util.HashMap;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.Util;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

public class ChatMgr {
	private static HashMap<Long, ArrayList<ErandooMqttMessage>> chatsMap = new HashMap<Long, ArrayList<ErandooMqttMessage>>(0); 
	private static HashMap<Long, Integer> notifiIdsMap = new HashMap<Long, Integer>(0); 
	private static Long currentChatUserId;
	private static LocalBroadcastManager broadcastManager;
	public static Context mainContext;
	public ChatMgr(){}

	public static ArrayList<ErandooMqttMessage> getUserChats(ErandooMqttMessage eMqttMessage){
		ArrayList<ErandooMqttMessage> chatList = chatsMap.get(eMqttMessage.getFriendUserId());
		if(chatList == null){
			chatList = new ArrayList<ErandooMqttMessage>(0);
		}
		return chatList;
	}
	
	public static void removeUserChats(ErandooMqttMessage eMqttMessage){
		chatsMap.remove(eMqttMessage.getFriendUserId());
	}
	
	
	public static void addUserChat(ErandooMqttMessage eMqttMessage){
		ArrayList<ErandooMqttMessage> chatsList = null;
		Long userId  = eMqttMessage.getFriendUserId();
		chatsList = chatsMap.get(userId);
		if(chatsList == null){
			chatsList = new ArrayList<ErandooMqttMessage>();
			chatsMap.put(userId, chatsList);
		}
		chatsList.add(eMqttMessage);
	}
	
	public static Integer createChatNotificationId(Long userId, Integer notifiId){
		Integer nId = notifiIdsMap.get(userId);
		if(nId == null){
			notifiIdsMap.put(userId, notifiId);
		}else{
			notifiId = nId;
		}
		return notifiId;
	}
	
	public static String createChatNotificationMsg(ErandooMqttMessage eMqttMessage){
		try{
			int count = chatsMap.get(eMqttMessage.getFriendUserId()).size();
			if(count == 1){
				return eMqttMessage.msg;
			}else{
				return count + " New Messages";
			}
		}catch(Exception e){
			//e.printStackTrace();
			return eMqttMessage.msg;
		}
	}
	
	public static void removeChatNotificationId(Long userId){
		notifiIdsMap.remove(userId);
	}
	
	public static Long getCurrentChatUserId(){
		return currentChatUserId;
	}
	
	public static void setCurrentChatUserId(Long userId){
		currentChatUserId = userId;
	}
	
	public static void regisChatReceiver(){
		broadcastManager = LocalBroadcastManager.getInstance(mainContext);
		broadcastManager.registerReceiver(mBroadcastReceiver, new IntentFilter(Constants.LOCAL_BROADCAST_FOR_MAIN));
	}
	
	private static BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			ErandooMqttMessage eMqttMessage = (ErandooMqttMessage)intent.getSerializableExtra(Constants.SERIALIZABLE_DATA);
			//if(!Config.getUserId().equals(eMqttMessage.from_id)){ 
				ChatMgr.addUserChat(eMqttMessage);	
				//Util.sendNotification(eMqttMessage,intent.getAction());
				Util.sendNotification(eMqttMessage);
				broadcastManager.unregisterReceiver(mBroadcastReceiver); 
			//}
		}
	};
}
