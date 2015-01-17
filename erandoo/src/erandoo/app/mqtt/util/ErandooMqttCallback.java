package erandoo.app.mqtt.util;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import erandoo.app.main.AppGlobals;
import erandoo.app.mqtt.ChatMgr;
import erandoo.app.mqtt.ErandooMqttMessage;
import erandoo.app.mqtt.impl.PahoMqttMessageWrapper;
import erandoo.app.mqtt.interfecs.IMqttCallback;
import erandoo.app.mqtt.interfecs.IMqttMessage;
import erandoo.app.mqtt.interfecs.IMqttTopic;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.Util;

public class ErandooMqttCallback implements IMqttCallback {

	@Override
	public void messageArrived(IMqttTopic topic, IMqttMessage message){
		try {
			Intent intent = null;
			ErandooMqttMessage eMqttMessage = (ErandooMqttMessage)Util.getJsonToClassObject(((PahoMqttMessageWrapper) message).toString(), ErandooMqttMessage.class);
			if(eMqttMessage.event_type.equals(Constants.MQTT_EVENT_TYPE_CHAT)){
				Long cUserId = ChatMgr.getCurrentChatUserId();
				
				if(eMqttMessage.from_id.equals(cUserId) || eMqttMessage.to_id.equals(cUserId)){
					intent = new Intent(Constants.LOCAL_BROADCAST_FOR_CHAT);
				}else{ 
					ChatMgr.regisChatReceiver();
					intent = new Intent(Constants.LOCAL_BROADCAST_FOR_MAIN);
				}
				
				intent.putExtra(Constants.SERIALIZABLE_DATA, eMqttMessage);
			    LocalBroadcastManager.getInstance(AppGlobals.appContext).sendBroadcast(intent);
			    
			}else if(eMqttMessage.event_type.equals(Constants.MQTT_EVENT_TYPE_INVITATION)){// for instant post result
				Util.sendNotification(eMqttMessage);
				intent = new Intent(Constants.LOCAL_BROADCAST_FOR_IRESULT);
				intent.putExtra(Constants.SERIALIZABLE_DATA, eMqttMessage);
			    LocalBroadcastManager.getInstance(AppGlobals.appContext).sendBroadcast(intent);
			}
			
			Log.v("ErandooMqttMessage -> ", ((PahoMqttMessageWrapper) message).toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void connectionLost(Throwable throwable) {
		Log.v("erandoo connectionLost() -> ", throwable.getMessage());
	}
	
	
/*	}else if(eMqttMessage.event_type.equals(Constants.MQTT_EVENT_TYPE_PUSH)){
		Util.sendNotification(eMqttMessage,Constants.MQTT_EVENT_TYPE_PUSH);
	}*/
}
