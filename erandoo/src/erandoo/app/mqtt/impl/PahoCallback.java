package erandoo.app.mqtt.impl;

import erandoo.app.mqtt.interfecs.IMqttCallback;
import erandoo.app.mqtt.interfecs.IMqttMessage;
import erandoo.app.mqtt.interfecs.IMqttTopic;

public class PahoCallback implements IMqttCallback {

	@Override
	public void messageArrived(IMqttTopic topic, IMqttMessage message)
			throws Exception {

		String msg = ((PahoMqttMessageWrapper) message).toString();
		System.out.println("messageArrived() [" + msg + "]");
	}

	@Override
	public void connectionLost(Throwable throwable) {
		System.out.println("connectionLost() " + throwable.getMessage());
	}
}
