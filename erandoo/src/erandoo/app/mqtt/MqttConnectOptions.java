package erandoo.app.mqtt;

import erandoo.app.mqtt.interfecs.IMqttConnectOptions;

public class MqttConnectOptions implements IMqttConnectOptions
{
	private short keepAliveSeconds;
	private boolean cleanStart;
	private String userName;
	private char[] password;
	
	@Override
	public void setCleanSession(boolean cleanStart)
	{
       this.cleanStart = cleanStart;		
	}

	@Override
	public void setKeepAliveInterval(short keepAliveSeconds)
	{
		this.keepAliveSeconds = keepAliveSeconds;	
	}

	@Override
	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	@Override
	public void setPassword(char[] password)
	{
	   this.password = password;	
	}

	@Override
	public boolean getCleanSession()
	{
		return cleanStart;
	}

	@Override
	public int getKeepAliveInterval()
	{
		return keepAliveSeconds;
	}

	@Override
	public String getUserName()
	{
		return userName;
	}

	@Override
	public char[] getPassword()
	{
		return password;
	}
}
