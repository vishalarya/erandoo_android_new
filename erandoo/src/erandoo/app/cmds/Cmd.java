package erandoo.app.cmds;

import java.util.LinkedHashMap;

public interface Cmd {

	public void addData(String key, Object value);
	public String getJsonData(); 
	public LinkedHashMap<String, Object> getData(); 
}
