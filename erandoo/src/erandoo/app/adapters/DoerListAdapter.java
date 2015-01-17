package erandoo.app.adapters;

import java.util.ArrayList;
import java.util.HashSet;
import erandoo.app.R;
import erandoo.app.database.TaskDoer;
import erandoo.app.main.AppGlobals;
import erandoo.app.utilities.Util;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

public class DoerListAdapter extends ArrayAdapter<TaskDoer>{
	private ArrayList<TaskDoer> _data; 
	private Context _context;
	private int _resource;
	private HashSet<Long> selectedDoersIds = new HashSet<Long>();
	public DoerListAdapter(Context context, int resource, ArrayList<TaskDoer> data) {
		super(context, resource, data);
		_data = data;
		_context = context;
		_resource = resource;
		
	}
	
	public void selectedDoerIds(ArrayList<TaskDoer> selectedDoers){
		if(selectedDoers != null){
			for (TaskDoer doerDetail: selectedDoers) {
				selectedDoersIds.add(doerDetail.user_id);
			}
		}
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder holder;
		if (view == null) {
			LayoutInflater mLayoutInflater = ((Activity) _context).getLayoutInflater();
			view = mLayoutInflater.inflate(_resource, parent, false);
			holder = new ViewHolder();
			
			holder.llDistancellIDoerRow = (LinearLayout)view.findViewById(R.id.llDistancellIDoerRow);
			holder.txtNameIDoerRow = (TextView) view.findViewById(R.id.txtNameIDoerRow);
			holder.txtHiredIDoerRow = (TextView) view.findViewById(R.id.txtHiredIDoerRow);
			holder.txtNetworkIDoerRow = (TextView) view.findViewById(R.id.txtNetworkIDoerRow);
			holder.txtJobsIDoerRow = (TextView) view.findViewById(R.id.txtJobsIDoerRow);
			holder.txtDistanceIDoerRow = (TextView) view.findViewById(R.id.txtDistanceIDoerRow);
			holder.imvIDoerPremium = (ImageView)view.findViewById(R.id.imvIDoerPremium);
			holder.imgVIDoerRow = (ImageView)view.findViewById(R.id.imgVIDoerRow);
			holder.imgVChkIDoerRow = (ImageView)view.findViewById(R.id.imgVChkIDoerRow);
			holder.rBarIDoerRow = (RatingBar)view.findViewById(R.id.rBarIDoerRow);
			 
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		 
		final TaskDoer doerDetail = _data.get(position);
		boolean alreadySelected = selectedDoersIds.contains(doerDetail.user_id);
		
		if(doerDetail.isSelected || alreadySelected){
			if(alreadySelected){
				doerDetail.isSelected = true;
				selectedDoersIds.remove(doerDetail.user_id);
			}
			holder.imgVChkIDoerRow.setImageResource(R.drawable.ic_list_check);
		}else{
			holder.imgVChkIDoerRow.setImageResource(R.drawable.ic_list_uncheck);
		}
		
		if(doerDetail.is_premiumdoer_license.equals("0")){
	    	holder.imvIDoerPremium.setVisibility(View.INVISIBLE);
	    }else{
	    	holder.imvIDoerPremium.setVisibility(View.VISIBLE);
	    }
		
		holder.txtNameIDoerRow.setText(doerDetail.fullname); 
		holder.txtHiredIDoerRow.setText(doerDetail.hired);
		holder.txtNetworkIDoerRow.setText(doerDetail.network);
		holder.txtJobsIDoerRow.setText(doerDetail.task_done_cnt);
		
		holder.rBarIDoerRow.setRating(Float.valueOf(doerDetail.rating_avg_as_tasker));  
		
		if(doerDetail.is_inpersondoer_license.equals("1")){
			holder.llDistancellIDoerRow.setVisibility(View.VISIBLE); 
			holder.txtDistanceIDoerRow.setText(getDistance(doerDetail.latitude,doerDetail.longitude)+" "+
			_context.getResources().getString(R.string.Miles));  
		}else{
			holder.llDistancellIDoerRow.setVisibility(View.GONE); 
		}
		
		Util.loadImage(holder.imgVIDoerRow,doerDetail.userimage, R.drawable.ic_launcher);

		return view;
	}
	
	static class ViewHolder {
		LinearLayout llDistancellIDoerRow;
		TextView txtNameIDoerRow;
		TextView txtHiredIDoerRow;
		TextView txtNetworkIDoerRow;
		TextView txtJobsIDoerRow;
		TextView txtDistanceIDoerRow;
		ImageView imgVIDoerRow;
		ImageView imgVChkIDoerRow;
		ImageView imvIDoerPremium;
		RatingBar rBarIDoerRow;
	}
	
	private String getDistance(String lat1,String lon1){
		return Util.getDistance(lat1, lon1, AppGlobals.defaultWLocation.latitude, AppGlobals.defaultWLocation.longitude);
	}
}
