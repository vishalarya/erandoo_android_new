package erandoo.app.mqtt;

import erandoo.app.mqtt.interfecs.IMqttTopic;

public class MqttTopic implements IMqttTopic
{
	private String name;
	private int qos=2;
	
	public MqttTopic(String name){
		setName(name);
	}
	
	@Override
	public String getName()
	{
		return this.name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public int getQoS()
	{
		return this.qos;
	}
	
	public void setQoS(int qos)
	{
		this.qos = qos;
	}	
}
