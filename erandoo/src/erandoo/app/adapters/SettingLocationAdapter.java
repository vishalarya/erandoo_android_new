package erandoo.app.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.database.WorkLocation;

public class SettingLocationAdapter extends ArrayAdapter<WorkLocation> {

	private Context context;

	private ArrayList<WorkLocation> locationList;
	private int _layoutRID;
private OnClickListener viewClick;
	public SettingLocationAdapter(Context context, int _layoutRID,
			ArrayList<WorkLocation> locationList,OnClickListener viewClick) {
		super(context, _layoutRID, locationList);
		this.context = context;
		this._layoutRID = _layoutRID;
		this.locationList = locationList;
		this.viewClick=viewClick;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater mLayoutInflater = ((Activity) context).getLayoutInflater();
			convertView = mLayoutInflater.inflate(_layoutRID, parent, false);
			holder = new ViewHolder();
			holder.txtStreetAdd = (TextView) convertView.findViewById(R.id.txtStreetAdd);
			holder.txtStreetCity = (TextView) convertView.findViewById(R.id.txtStreetCity);
			holder.txtStreetState = (TextView) convertView.findViewById(R.id.txtStreetState);
			holder.txtAction = (TextView) convertView.findViewById(R.id.txtAction);
			holder.linearMainLay = (LinearLayout) convertView.findViewById(R.id.linearMainLay);
			holder.linearView = (LinearLayout) convertView.findViewById(R.id.linearView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.txtAction.setVisibility(View.GONE);
		holder.linearView.setVisibility(ViewGroup.VISIBLE);
		holder.linearMainLay.setBackgroundColor(Color.TRANSPARENT);
		holder.txtStreetAdd.setText(locationList.get(position).address);
		holder.txtStreetCity.setText(locationList.get(position).city.city_name);
holder.txtStreetState.setText(locationList.get(position).state.state_name);
holder.linearView.setTag(locationList.get(position));
holder.linearView.setOnClickListener(viewClick);
		return convertView;
	}

	static class ViewHolder {
		TextView txtStreetAdd, txtStreetCity,txtStreetState,txtAction;
		LinearLayout linearView;
		LinearLayout linearMainLay;
	}
}
