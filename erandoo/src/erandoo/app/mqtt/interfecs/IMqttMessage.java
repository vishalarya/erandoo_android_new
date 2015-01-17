package erandoo.app.mqtt.interfecs;

import erandoo.app.mqtt.MqttException;

public interface IMqttMessage
{
	public int getQoS();

	public byte[] getPayload() throws MqttException;
	public boolean isRetained();	
	public boolean isDuplicate();
}
