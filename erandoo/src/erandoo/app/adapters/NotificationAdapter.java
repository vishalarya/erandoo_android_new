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
import erandoo.app.database.Notification;

public class NotificationAdapter extends ArrayAdapter<Notification>{

	private ArrayList<Notification> _notificationList;
	private Context _context;
	private int _layoutRID;
	
	public NotificationAdapter(Context context, int layoutID,ArrayList<Notification> notificationList) {
		super(context, layoutID, notificationList);
		_context = context;
		_layoutRID = layoutID;
		_notificationList = notificationList;
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder holder;
		if (view == null) {
			LayoutInflater mLayoutInflater = ((Activity) _context).getLayoutInflater();
			view = mLayoutInflater.inflate(_layoutRID, parent, false);
			holder = new ViewHolder();
			holder.txtName = (TextView) view.findViewById(R.id.txtName);
			holder.txtMessage = (TextView) view.findViewById(R.id.txtMessage);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		/*if(position % 2 == 0){
	    	view.setBackgroundColor(_context.getResources().getColor(R.color.text_Selection_color)); 
	    }else{
	    	view.setBackgroundColor(_context.getResources().getColor(R.color.trans_color)); 
	    }*/
		
		if (_notificationList.get(position).fullname != null && _notificationList.get(position).alert_desc != null) {
			holder.txtName.setText(_notificationList.get(position).fullname); 
			holder.txtMessage.setText(_notificationList.get(position).alert_desc); 
		}
		
		return view;
	}
	
	static class ViewHolder {
		TextView txtName;
		TextView txtMessage;
	}
}
