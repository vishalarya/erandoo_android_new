package erandoo.app.mqtt.interfecs;

import erandoo.app.mqtt.MqttException;
import erandoo.app.mqtt.MqttPersistenceException;

public interface IMqttClient {
	public void setCallback(IMqttCallback callback) throws MqttException;

	public void publish(IMqttTopic topic, IMqttMessage message)
			throws MqttException;

	public void subscribe(IMqttTopic topic) throws IllegalArgumentException,
			MqttException;

	public void subscribe(IMqttTopic[] topics) throws IllegalArgumentException,
			MqttException;

	public boolean isConnected();

	public void connect(IMqttConnectOptions options) throws MqttException;

	public void disconnect() throws MqttException, MqttPersistenceException;

	public void ping() throws MqttException;
}
