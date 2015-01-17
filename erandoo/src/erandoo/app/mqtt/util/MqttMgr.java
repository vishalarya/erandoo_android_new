package erandoo.app.mqtt.util;

import erandoo.app.mqtt.MqttConnectOptions;
import erandoo.app.mqtt.MqttException;
import erandoo.app.mqtt.MqttTopic;
import erandoo.app.mqtt.impl.PahoMqttClientFactory;
import erandoo.app.mqtt.impl.PahoMqttMessageWrapper;
import erandoo.app.mqtt.interfecs.IMqttCallback;
import erandoo.app.mqtt.interfecs.IMqttClient;
import erandoo.app.mqtt.interfecs.IMqttClientFactory;
import erandoo.app.mqtt.interfecs.IMqttConnectOptions;
import erandoo.app.mqtt.interfecs.IMqttMessage;
import erandoo.app.mqtt.interfecs.IMqttPersistence;
import erandoo.app.mqtt.interfecs.IMqttTopic;
import erandoo.app.utilities.Constants;

public class MqttMgr implements IMqttCallback {
	private static IMqttCallback mqttCallback;

	private static final IMqttPersistence usePersistence = null;
	private static final boolean cleanStart = true;

	private static IMqttClient mqttClient = null;
	private static IMqttClientFactory mqttClientFactory;

	private static MqttMgr instance = new MqttMgr();

	public static void subscriber(String brokerHostName, int brokerPortNumber,
			String username, String password, short keepAliveSeconds,
			Long topicId, Long clientId, String broadcastType,IMqttCallback mqttCallback)
			throws MqttException {

		try {
			String topicName = "";
			MqttMgr.mqttCallback = mqttCallback;
			if(broadcastType.equals(Constants.MQTT_EVENT_TYPE_CHAT)){ 
				topicName = getTopicForChat(topicId);
			}else{
				topicName = getTopicForPush(topicId);
			}
			// System.out.println("topicId ["+topicId+"]");
			IMqttClient mqttClient = getMqttClient(brokerHostName,
					brokerPortNumber, username, password, keepAliveSeconds,
					clientId);
			IMqttTopic topic = new MqttTopic(topicName);
			mqttClient.subscribe(topic);
		} catch (Exception e) {
			e.printStackTrace();
			// something went wrong!
			mqttClient = null;
			throw new MqttException(e);
		}
	}

	public static IMqttClient getMqttClient(String brokerHostName,
			int brokerPortNumber, String username, String password,
			short keepAliveSeconds, Long deviceId) throws MqttException {

		return getMqttClient(brokerHostName, brokerPortNumber, username,
				password, keepAliveSeconds, "client"+deviceId);

	}

	public static IMqttClient getMqttClient(String brokerHostName,
			int brokerPortNumber, String username, String password,
			short keepAliveSeconds, String clientId) throws MqttException {

		if (mqttClient == null || !mqttClient.isConnected()) {
			// define the connection to the broker
			mqttClientFactory = new PahoMqttClientFactory();
			mqttClient = mqttClientFactory.create(brokerHostName,
					brokerPortNumber, clientId, usePersistence);

			IMqttConnectOptions options = new MqttConnectOptions();
			options.setCleanSession(cleanStart);
			options.setKeepAliveInterval(keepAliveSeconds);
			options.setUserName(username);
			options.setPassword(password.toCharArray());

			// try to connect
			mqttClient.connect(options);

			// register this client app has being able to receive messages
			// mqttClient.registerSimpleHandler(this);
			mqttClient.setCallback(instance);
			System.out.println("subscriber() successful");
		}
		return mqttClient;
	}

	public static void publish(String brokerHostName, int brokerPortNumber,
			String username, String password, short keepAliveSeconds,
			Long topicId,String broadcastType,String body) throws MqttException {

		try {
			String topicName = "";
			if(broadcastType.equals(Constants.MQTT_EVENT_TYPE_CHAT)){ 
				topicName = getTopicForChat(topicId);
			}else{
				topicName = getTopicForPush(topicId);
			}
			IMqttClient mqttClient = getMqttClient(brokerHostName,
					brokerPortNumber, username, password, keepAliveSeconds,
					topicId);
			IMqttTopic topic = new MqttTopic(topicName);
			IMqttMessage msg = new erandoo.app.mqtt.MqttMessage(body);
			mqttClient.publish(topic, msg);
		} catch (Exception e) {
			e.printStackTrace();
			mqttClient = null;
			throw new MqttException(e);
		}
	}

	public static String getTopicForChat(Long topicId){
		return "ernd/c/u" + String.format("%012d", topicId);
	}
	
	public static String getTopicForPush(Long topicId){
		return "ernd/p/u" + String.format("%012d", topicId);
	}
	
	/*public static String getTopic(Long topicId) {
		return "client-" + String.format("%012d", topicId);
	}*/

	@Override
	public void messageArrived(IMqttTopic topic, IMqttMessage message)
			throws Exception {

		try {
			if (mqttCallback != null) {
				mqttCallback.messageArrived(topic, message);
			} else {
				String msg = ((PahoMqttMessageWrapper) message).toString();
				System.out.println("messageArrived() [" + msg + "]");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void connectionLost(Throwable throwable) {
		throwable.printStackTrace();

		if (mqttCallback != null) {
			mqttCallback.connectionLost(throwable);
		} else {
			System.out.println("connectionLost() " + throwable.getMessage());
		}
	}
}