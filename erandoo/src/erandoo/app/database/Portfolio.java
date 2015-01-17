package erandoo.app.database;

import java.io.Serializable;
import java.util.ArrayList;

public class Portfolio implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ArrayList<Category> category;
	public ArrayList<Project> project;

	public Portfolio(){
		
	}
}
