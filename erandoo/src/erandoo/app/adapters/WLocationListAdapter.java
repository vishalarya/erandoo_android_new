package erandoo.app.adapters;

import java.util.ArrayList;
import erandoo.app.R;
import erandoo.app.database.WorkLocation;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WLocationListAdapter extends ArrayAdapter<WorkLocation> {
	private ArrayList<WorkLocation> _data; 
	private Context _context;
	private int _resource;
	public WLocationListAdapter(Context context, int resource, ArrayList<WorkLocation> data) {
		super(context, resource, data);
		_data = data;
		_context = context;
		_resource = resource;
	}
	
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder holder;
		if (view == null) {
			LayoutInflater mLayoutInflater = ((Activity) _context).getLayoutInflater();
			view = mLayoutInflater.inflate(_resource, parent, false);
			holder = new ViewHolder();
			holder.txtSimpleRow = (TextView) view.findViewById(R.id.txtSimpleRow);
			holder.imgVSListRow = (ImageView)view.findViewById(R.id.imgVSListRow);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		
		holder.imgVSListRow.setVisibility(View.GONE);
		if(_data.get(position).work_location_id == null){
 			holder.txtSimpleRow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location,0, 0, 0); 
		}
		holder.txtSimpleRow.setText(_data.get(position).location_name); 
		if (position % 2 == 0) {
			view.setBackgroundColor(_context.getResources().getColor(R.color.text_Selection_color));
		} else {
			view.setBackgroundColor(_context.getResources().getColor(R.color.trans_color));
		}
 		
		return view;
	}
	
	static class ViewHolder {
		TextView txtSimpleRow;
		ImageView imgVSListRow;
	}

}
