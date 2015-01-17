package erandoo.app.tempclasses;

import java.io.Serializable;
import java.util.ArrayList;

import erandoo.app.database.Category;
import erandoo.app.database.Rating;

public class ProfileRatingData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String overall_rating;
	public ArrayList<Rating> category_rating;
	public ArrayList<Category> rating_category;
	public ArrayList<Rating> data;
	
	public ProfileRatingData(){
		
	}
}
