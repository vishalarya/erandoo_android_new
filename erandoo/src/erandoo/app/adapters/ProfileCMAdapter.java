package erandoo.app.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.database.TaskDoer;
import erandoo.app.utilities.Util;

public class ProfileCMAdapter extends ArrayAdapter<TaskDoer> {
	private ArrayList<TaskDoer> _data;
	private Context _context;
	private int _resource;

	public ProfileCMAdapter(Context context, int resource, ArrayList<TaskDoer> data) {
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
			holder.txtMemberName = (TextView) view.findViewById(R.id.txtMemberName);
			holder.imvMemberImage = (ImageView) view.findViewById(R.id.imvMemberImage);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		holder.txtMemberName.setText(_data.get(position).fullname);
		Util.loadImage(holder.imvMemberImage,_data.get(position).userimage, R.drawable.ic_launcher);
		
		return view;
	}
	
	static class ViewHolder{
		ImageView imvMemberImage;
		TextView txtMemberName;
	}
}
