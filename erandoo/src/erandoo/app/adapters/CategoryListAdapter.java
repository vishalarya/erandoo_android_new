package erandoo.app.adapters;

import java.util.ArrayList;

import erandoo.app.R;
import erandoo.app.database.Category;
import erandoo.app.utilities.Util;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoryListAdapter extends ArrayAdapter<Category>{
	private ArrayList<Category> _data; 
	private Context _context;
	private int _layoutRID;
	public CategoryListAdapter(Context context, int layoutID, ArrayList<Category> data) {
		super(context, layoutID, data);
		_context = context;
		_layoutRID = layoutID;
		_data = data;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
	    final ViewHolder holder;
	    if(view==null){
	    	LayoutInflater mLayoutInflater = ((Activity)_context).getLayoutInflater();
	        view = mLayoutInflater.inflate(_layoutRID, parent, false);
	        holder = new ViewHolder();
	        holder.txtCatName = (TextView)view.findViewById(R.id.txtCatName);
	        holder.imgCatList = (ImageView)view.findViewById(R.id.imgCatList);
	        view.setTag(holder);
	    }else{
	    	 holder = (ViewHolder)view.getTag();
	    }
	    
	    holder.txtCatName.setText(_data.get(position).category_name); 
	    Util.loadImage(holder.imgCatList,_data.get(position).category_image, R.drawable.ic_launcher);
	    
	    if(position % 2 == 0){
	    	view.setBackgroundColor(_context.getResources().getColor(R.color.text_Selection_color)); 
	    }else{
	    	view.setBackgroundColor(_context.getResources().getColor(R.color.trans_color)); 
	    }
	    return view;
	}
	
	static class ViewHolder{
		TextView txtCatName;
		ImageView imgCatList;
	}
}
