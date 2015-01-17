package erandoo.app.adapters;

import java.util.ArrayList;

import erandoo.app.R;
import erandoo.app.config.Config;
import erandoo.app.custom.HListView;
import erandoo.app.database.Category;
import erandoo.app.utilities.Util;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class HListAdapter extends ArrayAdapter<Category>{
	private ArrayList<Category> _data;
	private Context _context;
	private int _layoutRID;
	private int selectedPos = -1;
	public HListAdapter(Context context, int layoutID, ArrayList<Category> data) {
		super(context, layoutID, data);
		_context = context;
		_layoutRID = layoutID;
		_data = data;
	}
	
	public void setSelectedPosition(int pos){
		selectedPos = pos;
		notifyDataSetChanged();
	}

	public int getSelectedPosition(){
		return selectedPos;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
	    final ViewHolder holder;
	    if(view==null){
	    	LayoutInflater mLayoutInflater = ((Activity)_context).getLayoutInflater();
	        view = mLayoutInflater.inflate(_layoutRID, parent, false);
	        holder = new ViewHolder();
	        holder.imgVHList = (ImageView)view.findViewById(R.id.imgVHList);
	        holder.txtHlist = (TextView)view.findViewById(R.id.txtHlist);
	        view.setTag(holder);
	     }else{
	    	 holder = (ViewHolder)view.getTag();
	     }
	    
	    holder.txtHlist.setText(_data.get(position).category_name); 
	     view.setLayoutParams(new HListView.LayoutParams((Config.width/4)-9,LayoutParams.MATCH_PARENT));
	     
	     Util.loadImage(holder.imgVHList,_data.get(position).category_image,R.drawable.ic_launcher); 
	     if(selectedPos==position){
	    	 view.setBackgroundColor(_context.getResources().getColor(R.color.cat_item_bg_selected)); 
         }else{
        	 view.setBackgroundColor(_context.getResources().getColor(R.color.cat_item_bg_default)); 
         }
	     return view;
	}
	
	static class ViewHolder{
		ImageView imgVHList;
		TextView txtHlist;
	}
}
