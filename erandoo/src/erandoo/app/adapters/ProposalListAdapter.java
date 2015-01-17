package erandoo.app.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.cmds.Cmd;
import erandoo.app.cmds.CmdFactory;
import erandoo.app.config.Config;
import erandoo.app.custom.IDataChangedListener;
import erandoo.app.database.Proposal;
import erandoo.app.main.ProfileActivity;
import erandoo.app.main.ProposalDetailActivity;
import erandoo.app.main.ProposalListActivity;
import erandoo.app.network.NetworkMgr;
import erandoo.app.network.NetworkResponse;
import erandoo.app.projects.ProjectAwardDoerDialog;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.Util;

public class ProposalListAdapter extends ArrayAdapter<Proposal>  {

	Context _context;
	int _resource;
	ArrayList<Proposal> _data;
	private final String TAG_UNPOTENTIAL = "unpotential";
	private final String TAG_POTENTIAL = "potential";

	public ProposalListAdapter(Context context, int resource,ArrayList<Proposal> data) {
		super(context, resource, data);

		_context = context;
		_resource = resource;
		_data = data;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View view = convertView;
		ViewHolder holder;
		if (view == null) {
			LayoutInflater mLayoutInflater = ((Activity) _context)
					.getLayoutInflater();
			view = mLayoutInflater.inflate(_resource, parent, false);
			holder = new ViewHolder();
			holder.imvPRRImage = (ImageView) view.findViewById(R.id.imvPRRImage);
			holder.txtPRRName = (TextView) view.findViewById(R.id.txtPRRName);
			holder.txtPRRPrice = (TextView) view.findViewById(R.id.txtPRRPrice);
			holder.txtPRRLocation = (TextView) view.findViewById(R.id.txtPRRLocation);
			holder.txtPRRHired = (TextView) view.findViewById(R.id.txtPRRHired);
			holder.txtPRRNetwork = (TextView) view.findViewById(R.id.txtPRRNetwork);
			holder.txtPRRJobs = (TextView) view.findViewById(R.id.txtPRRJobs);
			holder.txtPRRMessage = (TextView) view.findViewById(R.id.txtPRRMessage);
			holder.txtPRRHire = (TextView) view.findViewById(R.id.txtPRRHire);
			holder.txtPRRPotential = (TextView) view.findViewById(R.id.txtPRRPotential);
			holder.rBarPRRRating = (RatingBar) view.findViewById(R.id.rBarPRRRating);
			holder.imvPLPremium = (ImageView) view.findViewById(R.id.imvPLPremium);
//			holder.txtPRRView = (TextView)view.findViewById(R.id.txtPRRView);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		final Proposal proposal = _data.get(position);

		Util.loadImage(holder.imvPRRImage, proposal.task_doer.userimage, R.drawable.ic_launcher);
		holder.txtPRRName.setText(proposal.task_doer.fullname);
		holder.txtPRRLocation.setText(proposal.task_doer.work_location);
		holder.txtPRRHired.setText(proposal.task_doer.hired);
		holder.txtPRRNetwork.setText(proposal.task_doer.network);
		holder.txtPRRJobs.setText(proposal.task_doer.task_done_cnt);
		holder.rBarPRRRating.setRating(Float.valueOf(proposal.task_doer.rating_avg_as_tasker));
		
		if(proposal.projectData.payment_mode.toString().equals("h")){
			holder.txtPRRPrice.setText("$" + proposal.poster_pay + " (" + _context.getResources().getString(R.string.hourly)+")");
		}else{
			holder.txtPRRPrice.setText("$" + proposal.poster_pay + " (" + _context.getResources().getString(R.string.fixed)+")");
		}
		
		if(proposal.status.equals("a")){
			holder.txtPRRHire.setText(_context.getResources().getString(R.string.HireMe));
		}else if(proposal.status.equals("h")){
			holder.txtPRRHire.setText(_context.getResources().getString(R.string.OfferSent));
			holder.txtPRRHire.setEnabled(false);
		}else if(proposal.status.equals("s")){
			holder.txtPRRHire.setText(_context.getResources().getString(R.string.Hired));
			holder.txtPRRHire.setEnabled(false);
		}else if(proposal.status.equals("r")){
			holder.txtPRRHire.setText(_context.getResources().getString(R.string.Rejected));
			holder.txtPRRHire.setEnabled(false);
		}
		
		 if(proposal.task_doer.is_premiumdoer_license.equals("0")){
		    	holder.imvPLPremium.setVisibility(View.INVISIBLE);
		    }else{
		    	holder.imvPLPremium.setVisibility(View.VISIBLE);
		    }
		
		holder.txtPRRHire.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				ProjectAwardDoerDialog dialog = ProjectAwardDoerDialog.getInstance(proposal, new IDataChangedListener() {
					@Override
					public void onDataChanged() {
						((ProposalListActivity)_context).loadProposalListData();
					}
				});
				dialog.show(((FragmentActivity) _context).getSupportFragmentManager(), _context.getResources().getString(R.string.Hired));
			}
		});
		
		holder.txtPRRPrice.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(_context,ProposalDetailActivity.class);
				intent.putExtra(Constants.SERIALIZABLE_DATA, proposal);
				intent.putExtra(Constants.MY_PROJECT_AS_POSTER, Constants.MY_PROJECT_AS_POSTER);
				_context.startActivity(intent);
				((Activity)_context).overridePendingTransition(R.anim.right_in, R.anim.left_out);
			}
		});
		
		holder.txtPRRName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(Util.isDeviceOnline(_context)){
					Intent intent = new Intent(_context,ProfileActivity.class);
					intent.putExtra(Constants.SERIALIZABLE_DATA, proposal.task_doer.user_id);
					_context.startActivity(intent);
					((Activity)_context).overridePendingTransition(R.anim.right_in, R.anim.left_out);
				}else{
					Util.showCenteredToast(_context, _context.getResources().getString(R.string.msg_Connect_internet));
				}
 			}
		});
		
		holder.txtPRRPotential.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				bookmarkProposal(proposal.projectData.task_id, proposal.task_doer.user_id,position,v.getTag().toString());
			}
		});
		
		if(proposal.bookmark_user.equals("1")){
			holder.txtPRRPotential.setTag(TAG_UNPOTENTIAL); 
			holder.txtPRRPotential.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_check_yes, 0, 0, 0);
		}else{
			holder.txtPRRPotential.setTag(TAG_POTENTIAL); 
			holder.txtPRRPotential.setCompoundDrawablesWithIntrinsicBounds( 0, 0, 0, 0);
		}
		return view;
	}
	
	@Override 
	public boolean isEnabled(int position) {
		return false;
	};
	
	private void bookmarkProposal(final Long taskId,final Long taskerID,final int position, final String tag){
		new AsyncTask<Void, Void, NetworkResponse>(){
			@Override
			protected void onPreExecute() {
				Util.showProDialog(_context); 
			}
			@Override
			protected NetworkResponse doInBackground(Void... params) {
				 Cmd cmd = null;
				if(tag.equals(TAG_UNPOTENTIAL)){ 
					 cmd = CmdFactory.createDeleteBookmarkCmd();
				}else{
					 cmd = CmdFactory.createSaveBookmarkCmd();
				}
			    cmd.addData(Constants.FLD_OPERATION, "proposal_bookmark");
			    cmd.addData("task_id", taskId);
			    cmd.addData("bookmark_user_id", taskerID);
			    NetworkResponse response = NetworkMgr.httpPost(Config.API_URL,cmd.getJsonData());
				if (response != null) {
					if (response.isSuccess()) {
						if(tag.equals(TAG_UNPOTENTIAL)){
							_data.get(position).bookmark_user = "0";
						}else{
							_data.get(position).bookmark_user = "1";
						} 
					}
				}
				return response;
			}
			
			@Override
			protected void onPostExecute(NetworkResponse response) {
				super.onPostExecute(response);
				Util.dimissProDialog();
				if (response != null) {
					if (!response.isSuccess()) {
						Util.showCenteredToast(_context,
								response.getErrMsg());
					}
				}
				notifyDataSetChanged(); 
			}
		}.execute();
	}
	

	static class ViewHolder {
		ImageView imvPRRImage;
		TextView txtPRRName;
		TextView txtPRRView;
		TextView txtPRRPrice;
		TextView txtPRRLocation;
		TextView txtPRRHired;
		TextView txtPRRNetwork;
		TextView txtPRRJobs;
		TextView txtPRRMessage;
		TextView txtPRRHire;
		TextView txtPRRPotential;
		RatingBar rBarPRRRating;
		ImageView imvPLPremium;
	}
}
