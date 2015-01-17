package erandoo.app.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.database.Rating;
import erandoo.app.main.RatingActivity;

public class RatingAdapter extends ArrayAdapter<Rating> {
	private ArrayList<Rating> _data;
	private Context _context;
	private int _resource;
	private boolean isDashboard;
	private boolean isComplete;

	public RatingAdapter(Context context, int resource, ArrayList<Rating> data, boolean isDashboard, boolean isComplete) {
		super(context, resource, data);
		_data = data;
		_context = context;
		_resource = resource;
		this.isDashboard = isDashboard;
		this.isComplete = isComplete;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder holder;
		if (view == null) {
			LayoutInflater mLayoutInflater = ((Activity) _context).getLayoutInflater();
			view = mLayoutInflater.inflate(_resource, parent, false);
			holder = new ViewHolder();
			holder.txtRatingName = (TextView) view.findViewById(R.id.txtRatingName);
			holder.rtbRatingPoints = (RatingBar) view.findViewById(R.id.rtbRatingPoints);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		if (isDashboard) {
			holder.txtRatingName.setTextColor(Color.WHITE);
		}
		
		holder.txtRatingName.setText(_data.get(position).rating_desc);
		
		if(isComplete){
			holder.rtbRatingPoints.setIsIndicator(false);
			holder.rtbRatingPoints.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
				@Override
				public void onRatingChanged(RatingBar ratingBar, float rating,boolean fromUser) {
					_data.get(position).rating = String.valueOf(holder.rtbRatingPoints.getRating());
					notifyDataSetChanged();
					RatingActivity.setOverallRating(calculateOverAllRating(_data), _data);
				}
			});
		}else{
			holder.rtbRatingPoints.setRating(Float.parseFloat(_data.get(position).rating));
		}
		
		return view;
	}
	
	static class ViewHolder{
		TextView txtRatingName;
		RatingBar rtbRatingPoints;
	}
	
	private float calculateOverAllRating(ArrayList<Rating> values){ 
		float sum = 0;
		float rating = 0;
		try{
			for (Rating value: values) {
				if(value.rating != null){
					sum = sum + Float.valueOf(value.rating);
				}
			}
			rating = sum/values.size();
		}catch(Exception e){
			e.printStackTrace();
		}
		return rating;
	}
}
