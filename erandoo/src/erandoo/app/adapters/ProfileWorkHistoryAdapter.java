package erandoo.app.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.database.WorkHistory;
import erandoo.app.utilities.Util;

public class ProfileWorkHistoryAdapter extends ArrayAdapter<WorkHistory>{
	private ArrayList<WorkHistory> _data;
	private Context _context;
	private int _resource;

	public ProfileWorkHistoryAdapter(Context context, int resource, ArrayList<WorkHistory> data) {
		super(context, resource, data);
		_data = data;
		_context = context;
		_resource = resource;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder holder;
		if (view == null) {
			LayoutInflater mLayoutInflater = ((Activity) _context).getLayoutInflater();
			view = mLayoutInflater.inflate(_resource, parent, false);
			holder = new ViewHolder();
			holder.txtWHTitle = (TextView) view.findViewById(R.id.txtWHTitle);
			holder.txtWHName = (TextView) view.findViewById(R.id.txtWHName);
			holder.txtWHDate = (TextView) view.findViewById(R.id.txtWHDate);
			holder.txtWHPrice = (TextView) view.findViewById(R.id.txtWHPrice);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		holder.txtWHTitle.setText(_data.get(position).title);
		holder.txtWHName.setText(_data.get(position).fullname);
		holder.txtWHDate.setText(Util.setProjectDateWithMonth(_data.get(position).task_date, false));
		holder.txtWHPrice.setText("$"+_data.get(position).price);
		
		return view;
	}
	
	static class ViewHolder{
		TextView txtWHTitle;
		TextView txtWHName;
		TextView txtWHDate;
		TextView txtWHPrice;
	}

}
