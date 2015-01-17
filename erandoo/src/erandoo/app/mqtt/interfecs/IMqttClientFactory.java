package erandoo.app.mqtt.interfecs;

import erandoo.app.mqtt.MqttException;

public interface IMqttClientFactory {
	public IMqttClient create(String host, int port, String clientId,
			IMqttPersistence persistence) throws MqttException;
}
