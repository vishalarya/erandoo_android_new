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
import erandoo.app.custom.CircularImageView;
import erandoo.app.mqtt.DoerInfo;
import erandoo.app.utilities.Util;

public class IRGridViewAdapter extends ArrayAdapter<DoerInfo>{

	private Context _context;
	private ArrayList<DoerInfo> _list;
	private int _resource;
	
	public IRGridViewAdapter(Context context, int resource, ArrayList<DoerInfo> list) {
		super(context, resource, list);
		_context = context;
		_list = list;
		_resource = resource;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		View view = convertView;
		if (view == null) {
			LayoutInflater mLayoutInflator = ((Activity)_context).getLayoutInflater();
			view = mLayoutInflator.inflate(_resource, parent, false);
			holder = new ViewHolder();
			holder.cimgVIResultList = (CircularImageView) view.findViewById(R.id.cimgVIResultList);
			holder.txtIResulList = (TextView) view.findViewById(R.id.txtIResulList);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		holder.txtIResulList.setText(_list.get(position).nm);
		Util.loadImage(holder.cimgVIResultList, _list.get(position).pic,R.drawable.ic_launcher);
		return view;
	}
	
	public class ViewHolder{
		TextView txtIResulList;
		CircularImageView cimgVIResultList;
	}
}
