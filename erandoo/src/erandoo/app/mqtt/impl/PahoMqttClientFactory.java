package erandoo.app.mqtt.impl;


import erandoo.app.mqtt.MqttException;
import erandoo.app.mqtt.interfecs.IMqttClient;
import erandoo.app.mqtt.interfecs.IMqttClientFactory;
import erandoo.app.mqtt.interfecs.IMqttPersistence;


public class PahoMqttClientFactory implements IMqttClientFactory
{	
	@Override
	public IMqttClient create(String host, int port, String clientId,
			IMqttPersistence persistence) throws MqttException
	{
		PahoMqttClientPersistence persistenceImpl = null;
		if(persistence != null){
			persistenceImpl = new PahoMqttClientPersistence(persistence);
		}
		
		// TODO Auto-generated method stub
		return new PahoMqttClientWrapper(
			"tcp://"+host+":"+port, clientId, persistenceImpl);
	}
}
