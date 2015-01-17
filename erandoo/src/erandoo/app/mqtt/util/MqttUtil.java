package erandoo.app.mqtt.util;

import erandoo.app.main.AppGlobals;
import erandoo.app.mqtt.MqttException;
import erandoo.app.mqtt.MqttPersistenceException;
import erandoo.app.mqtt.interfecs.IMqttClient;
import erandoo.app.utilities.Constants;

public class MqttUtil {

	private static IMqttClient mqttClient;

	public static IMqttClient mqttConnect(Long userId) throws MqttException {

		mqttClient = MqttMgr.getMqttClient(Constants.MQTT_BROKERNAME,
				Constants.MQTT_PORT, Constants.MQTT_USER,
				Constants.MQTT_PASSWORD,
				Constants.MQTT_CONNECTION_KEEP_ALIVE_IN_SECS, userId);
		return mqttClient;
	}

	public static void mqttDisconnect(){
		if (mqttClient != null && mqttClient.isConnected()) {
			try {
				mqttClient.disconnect();
			} catch (MqttPersistenceException e) {
				e.printStackTrace();
			}catch (MqttException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Function to subscriber a user on Active MQ
	 * @param topicId (user id)
	 * @param clientId (device id)
	 * @param broadcastType (broadcast type chat or push notification)
	 */
	public static void subscriber(Long topicId, Long clientId,String broadcastType) {

		try {
			MqttMgr.subscriber(Constants.MQTT_BROKERNAME, Constants.MQTT_PORT,
					Constants.MQTT_USER, Constants.MQTT_PASSWORD,
					Constants.MQTT_CONNECTION_KEEP_ALIVE_IN_SECS, topicId,
					clientId, broadcastType,new ErandooMqttCallback());
		} catch (Exception e) {
			e.printStackTrace();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Function to publish a topic on Active MQ
	 * @param topicId (user id)
	 * @param broadcastType (broadcast type chat or push notification)
	 * @param body (message)
	 */
	public static void publish(Long topicId, String broadcastType, String body) {

		try {
			MqttMgr.publish(Constants.MQTT_BROKERNAME, Constants.MQTT_PORT,
					Constants.MQTT_USER, Constants.MQTT_PASSWORD,
					Constants.MQTT_CONNECTION_KEEP_ALIVE_IN_SECS, topicId, broadcastType, body);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void  connectToMqtt(){
		try {
			if (mqttClient == null || !mqttClient.isConnected()) {
				MqttUtil.subscriber(AppGlobals.userDetail.user_id, AppGlobals.userDetail.user_device_id,Constants.MQTT_EVENT_TYPE_CHAT); 
				//MqttUtil.subscriber(Config.getUserId(), Config.getUserDeviceId(),Constants.MQTT_EVENT_TYPE_PUSH); 
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
