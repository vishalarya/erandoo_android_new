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
import erandoo.app.database.Recommandation;
import erandoo.app.utilities.Util;

public class ProfileRecommandationAdapter extends ArrayAdapter<Recommandation>{
	private ArrayList<Recommandation> _data;
	private Context _context;
	private int _resource;

	public ProfileRecommandationAdapter(Context context, int resource, ArrayList<Recommandation> data) {
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
			holder.txtRRFullName = (TextView) view.findViewById(R.id.txtRRFullName);
			holder.txtRRDesc = (TextView) view.findViewById(R.id.txtRRDesc);
			holder.imvRRUserImage = (ImageView) view.findViewById(R.id.imvRRUserImage);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		holder.txtRRFullName.setText(_data.get(position).fullname);
		holder.txtRRDesc.setText(_data.get(position).recommendation_desc);
		Util.loadImage(holder.imvRRUserImage,_data.get(position).userimage, R.drawable.ic_launcher);
		
		return view;
	}
	
	static class ViewHolder{
		ImageView imvRRUserImage;
		TextView txtRRFullName;
		TextView txtRRDesc;
	}
}
