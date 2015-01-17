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
import erandoo.app.database.Project;
import erandoo.app.utilities.Util;

public class MyProjectListAdapter extends ArrayAdapter<Project> {
	private ArrayList<Project> _data;
	private Context _context;
	private int _layoutRID;
	public MyProjectListAdapter(Context context, int layoutID,ArrayList<Project> data) {
		super(context, layoutID, data);
		_context = context;
		_layoutRID = layoutID;
		_data = data;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder holder;
		if (view == null) {
			LayoutInflater mLayoutInflater = ((Activity) _context).getLayoutInflater();
		 	view = mLayoutInflater.inflate(_layoutRID, parent, false);
			holder = new ViewHolder();
			holder.txtMPTitle = (TextView) view.findViewById(R.id.txtMPTitle);
			holder.txtMPDesc = (TextView) view.findViewById(R.id.txtMPDesc);
			holder.txtMPBudget = (TextView) view.findViewById(R.id.txtMPBudget);
			holder.txtMPPostedDate = (TextView) view.findViewById(R.id.txtMPPostedDate);
			holder.txtMPProposals = (TextView) view.findViewById(R.id.txtMPProposals);
			holder.imvMPPremium = (ImageView) view.findViewById(R.id.imvMPPremium);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		Project projectDetails = (_data.get(position));
		
		holder.txtMPTitle.setText(projectDetails.title); 
		holder.txtMPProposals.setText(projectDetails.proposals_cnt); 
		holder.txtMPPostedDate.setText(Util.setProjectDateWithMonth(projectDetails.bid_start_dt, false)); 
		holder.txtMPDesc.setText(projectDetails.description); 
		holder.txtMPBudget.setText(Util.getBudgetString(projectDetails)); 

		if(projectDetails.is_premium.equals("0")){
			holder.imvMPPremium.setVisibility(View.INVISIBLE);
		}else{
			holder.imvMPPremium.setVisibility(View.VISIBLE);
		}
		
		if(projectDetails.is_highlighted.equals("1")){
			view.setBackgroundResource(R.drawable.ed_bg_hlight_card_view);
		}else{
	    	view.setBackgroundResource(R.drawable.ed_bg_card_view);
		}
		
		return view;
	}

	static class ViewHolder {
		TextView txtMPTitle;
		TextView txtMPBudget;
		TextView txtMPProposals;
		TextView txtMPPostedDate;
		TextView txtMPDesc;
		ImageView imvMPPremium;
	}
}
