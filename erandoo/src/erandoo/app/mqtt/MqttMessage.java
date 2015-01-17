package erandoo.app.mqtt;

import erandoo.app.mqtt.interfecs.IMqttMessage;

public class MqttMessage implements IMqttMessage {

	private byte[] payload;
	private int qos = 2;
	
	public MqttMessage(String content) {
		this(content.getBytes());
	}
	
	public MqttMessage(byte[] payload) {
		this.payload = payload;
	}

	@Override
	public int getQoS() {
		return qos;
	}

	@Override
	public byte[] getPayload() throws MqttException {
		return this.payload;
	}

	@Override
	public boolean isRetained() {
		return false;
	}

	@Override
	public boolean isDuplicate() {
		return false;
	}

	public void setQoS(int qos) {
		this.qos = qos;
	}
}
