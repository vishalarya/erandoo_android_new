package erandoo.app.mqtt.impl;

import org.eclipse.paho.client.mqttv3.MqttTopic;

import erandoo.app.mqtt.interfecs.IMqttTopic;


public class PahoMqttTopicWrapper implements IMqttTopic
{
	private MqttTopic topic;

	public PahoMqttTopicWrapper(MqttTopic topic)
	{
		this.topic = topic;
	}

	@Override
	public String getName()
	{
		return topic.getName();
	}

	@Override
	public int getQoS()
	{
		return 0;
	}
}
