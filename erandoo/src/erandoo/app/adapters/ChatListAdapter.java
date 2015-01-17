package erandoo.app.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.main.AppGlobals;
import erandoo.app.mqtt.ErandooMqttMessage;
import erandoo.app.utilities.Util;

public class ChatListAdapter extends ArrayAdapter<ErandooMqttMessage> {
	
	private ArrayList<ErandooMqttMessage> _data; 
	private Context _context;
	private int _layoutRID;
	private static String currentDate;
	//private Long userId;
	public ChatListAdapter(Context context, int layoutID, ArrayList<ErandooMqttMessage> data) {
		super(context, layoutID, data);
		_context = context;
		_layoutRID = layoutID;
		_data = data;
		currentDate = Util.getCurrentDateWithMonth();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
	    final ViewHolder holder;
	    if(view==null){
	    	LayoutInflater mLayoutInflater = ((Activity)_context).getLayoutInflater();
	        view = mLayoutInflater.inflate(_layoutRID, parent, false);
	        holder = new ViewHolder();
	        
	        holder.imvChatToUserImage = (ImageView) view.findViewById(R.id.imvChatToUserImage);
	        holder.txtChatToUserMessage = (TextView) view.findViewById(R.id.txtChatToUserMessage);
	        holder.txtChatFromUserMessage = (TextView) view.findViewById(R.id.txtChatFromUserMessage);
	        holder.txtChatToUserTime = (TextView) view.findViewById(R.id.txtChatToUserTime);
	        holder.txtChatFromUserTime = (TextView) view.findViewById(R.id.txtChatFromUserTime);
	        holder.txtChatDate = (TextView) view.findViewById(R.id.txtChatDate);
	        holder.flChatFromUser = (LinearLayout) view.findViewById(R.id.flChatFromUser);
	        holder.llChatToUser = (LinearLayout) view.findViewById(R.id.llChatToUser);
	        holder.llChatDate = (LinearLayout) view.findViewById(R.id.llChatDate);
	        
	        view.setTag(holder);
	        
	    }else{
	    	 holder = (ViewHolder)view.getTag();
	    }
	    
	    ErandooMqttMessage message = _data.get(position);
	    String dateStr = "";
	    String timeStr = "";
	    if(message.timestamp != null){
	    	SimpleDateFormat formatUTC = new SimpleDateFormat("MMM dd, yyyy");
	    	SimpleDateFormat timeFormatUTC = new SimpleDateFormat("hh:mm a");
		   // formatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
		    Date date = new Date();
		    date.setTime(Long.parseLong(message.timestamp)); 
		    dateStr =  formatUTC.format(date);
		    timeStr = timeFormatUTC.format(date);
	    }else{
	    	dateStr = message.date;
	    	timeStr = message.time;
	    }
	    
	    if(compareTwoDates(dateStr, currentDate)){
	    	if(position == 0){
	    		holder.llChatDate.setVisibility(View.VISIBLE);
	    		holder.txtChatDate.setText(dateStr); 
 	    	}else{
 	    		holder.llChatDate.setVisibility(View.GONE);
 	    	}
	    }else{
	    	holder.llChatDate.setVisibility(View.VISIBLE);
    		holder.txtChatDate.setText(dateStr); 
	    }
	    
	    currentDate = dateStr;
	    
	    if(message.from_id.equals( AppGlobals.userDetail.user_id)){
	    	holder.llChatToUser.setVisibility(View.GONE); 
	    	holder.flChatFromUser.setVisibility(View.VISIBLE); 
	    	
	    	holder.txtChatFromUserMessage.setText(_data.get(position).msg); 
	    	holder.txtChatFromUserTime.setText(timeStr);
	    }else{
	    	holder.llChatToUser.setVisibility(View.VISIBLE); 
	    	holder.flChatFromUser.setVisibility(View.GONE); 
	    	
	    	holder.txtChatToUserMessage.setText(_data.get(position).msg);
	    	holder.txtChatToUserTime.setText(timeStr);
	    }
	  
	  
	    return view;
	}
	
	static class ViewHolder{
		ImageView imvChatToUserImage;
		TextView txtChatToUserMessage;
		TextView txtChatFromUserMessage;
		TextView txtChatToUserTime;
		TextView txtChatFromUserTime;
		TextView txtChatDate;
		LinearLayout flChatFromUser;
		LinearLayout llChatToUser;
		LinearLayout llChatDate;
	}
	
	private boolean compareTwoDates(String chatDate1 , String currentDate) {
		boolean setDatebooleanValue = false;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
			Date current = formatter.parse(currentDate);
			Date userDate = formatter.parse(chatDate1);
			int compareValue = userDate.compareTo(current);

			if (compareValue == 0) {
				setDatebooleanValue = true;
			} else {
				setDatebooleanValue = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return setDatebooleanValue;
	}
}
