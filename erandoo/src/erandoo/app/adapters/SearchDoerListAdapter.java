package erandoo.app.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.cmds.Cmd;
import erandoo.app.cmds.CmdFactory;
import erandoo.app.config.Config;
import erandoo.app.database.DatabaseMgr;
import erandoo.app.database.Project;
import erandoo.app.database.TaskDoer;
import erandoo.app.main.AppGlobals;
import erandoo.app.main.ChatViewActivity;
import erandoo.app.main.ProfileActivity;
import erandoo.app.mqtt.ErandooMqttMessage;
import erandoo.app.network.NetworkMgr;
import erandoo.app.network.NetworkResponse;
import erandoo.app.projects.ProjectListDialog;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.Util;

public class SearchDoerListAdapter extends ArrayAdapter<TaskDoer>{
	
	private ArrayList<TaskDoer> _data; 
	private Context _context;
	private int _layoutRID;
	private boolean isFromProfile = false;
	
    private String filterBy = "o";
	
	private final String TAG_UNPOTENTIAL = "unpotential";
	private final String TAG_POTENTIAL = "potential";
	private ArrayList<Project> projectList = new ArrayList<Project>();
	
	public static String ascending = "ascending";
	public static String descending = "descending";
	private static String ascOrDesc = ascending;
	
	private ViewHolder messageView;
	
	public SearchDoerListAdapter(Context context, int layoutID, ArrayList<TaskDoer> data, boolean isFromProfile) {
		super(context, layoutID, data);
		_data = data;
		_context = context;
		_layoutRID = layoutID;
		this.isFromProfile = isFromProfile;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
	    final ViewHolder holder;
	    if(view==null){
	    	LayoutInflater mLayoutInflater = ((Activity)_context).getLayoutInflater();
	        view = mLayoutInflater.inflate(_layoutRID, parent, false);
	        holder = new ViewHolder();
	        holder.imvSDImage = (ImageView)view.findViewById(R.id.imvSDImage);
	        //holder.imvSDMore = (ImageView)view.findViewById(R.id.imvSDMore);
	        holder.txtSDName = (TextView)view.findViewById(R.id.txtSDName);
	        holder.txtSDLocation = (TextView)view.findViewById(R.id.txtSDLocation);
	        holder.txtSDHired = (TextView)view.findViewById(R.id.txtSDHired);
	        holder.txtSDNetwork = (TextView)view.findViewById(R.id.txtSDNetwork);
	        holder.txtSDJobs = (TextView)view.findViewById(R.id.txtSDJobs);
	        holder.txtSDInvite = (TextView)view.findViewById(R.id.txtSDInvite);
	        holder.txtSDMessage = (TextView)view.findViewById(R.id.txtSDMessage);
	        holder.txtSDPotential = (TextView)view.findViewById(R.id.txtSDPotential);
	        holder.rBarSDRating = (RatingBar)view.findViewById(R.id.rBarSDRating);
	        holder.llSDMain = (LinearLayout)view.findViewById(R.id.llSDMain);
	        holder.llSDOperation = (LinearLayout)view.findViewById(R.id.llSDOperation);
	        holder.llSDInfo = (LinearLayout)view.findViewById(R.id.llSDInfo);
	        holder.chkSDCheck = (CheckBox)view.findViewById(R.id.chkSDCheck);
	        holder.imvSDPremium = (ImageView)view.findViewById(R.id.imvSDPremium);
	        holder.imvSDOnlineStatus = (ImageView)view.findViewById(R.id.imvSDOnlineStatus);
	        holder.SDview1 = (View) view.findViewById(R.id.SDview1);
	        holder.SDview2 = (View) view.findViewById(R.id.SDview2);
	        view.setTag(holder);
	    } else{
	    	 holder = (ViewHolder)view.getTag();
	    }

	    
	    final TaskDoer searchDoerDetail = _data.get(position);
	    holder.txtSDName.setText(searchDoerDetail.fullname);
	    holder.txtSDLocation.setText(searchDoerDetail.work_location);
	    Util.loadImage(holder.imvSDImage,searchDoerDetail.userimage, R.drawable.ic_launcher);
	    holder.rBarSDRating.setRating(Float.valueOf(searchDoerDetail.rating_avg_as_tasker));
	    
	    //member have premium license or not.
	    if(searchDoerDetail.is_premiumdoer_license.equals("0")){
	    	holder.imvSDPremium.setVisibility(View.INVISIBLE);
	    }else{
	    	holder.imvSDPremium.setVisibility(View.VISIBLE);
	    }
	    
	    //member connection and message section.
	    if (searchDoerDetail.network_connection == null) {
	    	holder.txtSDMessage.setText(_context.getResources().getString(R.string.connect));
	    	holder.txtSDMessage.setTag(holder);
	    	
		} else if (searchDoerDetail.network_connection.approval_status.equals("p")) {
			if (searchDoerDetail.network_connection.invited_by_user_id.equals(AppGlobals.userDetail.user_id)) {
				holder.txtSDMessage.setText(_context.getResources().getString(R.string.invitation_sent));
				holder.txtSDMessage.setTag(holder);
			} else{
				holder.txtSDMessage.setText(_context.getResources().getString(R.string.accept_me));
				holder.txtSDMessage.setTag(holder);
			}
			
		} else if (searchDoerDetail.network_connection.approval_status.equals("r")) {
			holder.txtSDMessage.setText("Rejected");
			holder.txtSDMessage.setTag(holder);
			
		} else if (searchDoerDetail.network_connection.approval_status.equals("a")) {
			holder.txtSDMessage.setText(_context.getResources().getString(R.string.Messages));
			holder.txtSDMessage.setTag(holder);
			
		}
	    
	    //online status of particular member.
	    if (searchDoerDetail.online_status.equals("1")) {
			holder.imvSDOnlineStatus.setBackgroundResource(R.drawable.ic_rd_setected);
		} else{
			holder.imvSDOnlineStatus.setBackgroundResource(R.drawable.ic_rd_default);
		}
	    
	    //from where user is coming profile or search member section.
	    if (isFromProfile) {
	    	holder.llSDOperation.setVisibility(View.GONE);
	    	holder.llSDInfo.setVisibility(View.GONE);
	    	holder.chkSDCheck.setVisibility(View.VISIBLE);
	    	holder.SDview1.setVisibility(View.GONE);
	    	holder.SDview2.setVisibility(View.GONE);
	    	holder.imvSDOnlineStatus.setVisibility(View.GONE);
	    	
		}else{
		    holder.txtSDHired.setText(searchDoerDetail.hired);
		    holder.txtSDNetwork.setText(searchDoerDetail.network);
		    holder.txtSDJobs.setText(searchDoerDetail.task_done_cnt);
		}
	    
	    //potential data settings.
	    if(searchDoerDetail.bookmark_user.equals("1")){
			holder.txtSDPotential.setTag(holder); 
			holder.txtSDPotential.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_check_yes, 0, 0, 0);
		}else{
			holder.txtSDPotential.setTag(holder); 
			holder.txtSDPotential.setCompoundDrawablesWithIntrinsicBounds( 0, 0, 0, 0);
		}
	    
	    //all clicks regarding buttons and text views.
	    holder.chkSDCheck.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				if (((CheckBox) view).isChecked()) {
					_data.get(position).isSelected = true;
				}else{
					_data.get(position).isSelected = false;
				}
			}
		});
	    
	    holder.txtSDInvite.setOnClickListener(new OnClickListener() {
   			@Override
   			public void onClick(View v) {
   				ascOrDesc = ascending;
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				DatabaseMgr database = DatabaseMgr.getInstance(_context);
				projectList = database.getMyTasks(Constants.DELIVERY_STATUS_SENT, Constants.MY_PROJECT_AS_POSTER, filterBy);
				ArrayList<Project> listing = new ArrayList<Project>();
				Collections.sort(projectList,sortingByDate);
				for (Project project : projectList) {
					try {
						Date currentDate = (Date) sdf.parse(Util.getCurrentDate());
						Date bidcloseDate = (Date) sdf.parse(project.bid_close_dt);
						if (project.hiring_closed.equals("0")) {
							if (bidcloseDate.after(currentDate)) {
								if (listing.size() < 5) {
									project.tasker_id = searchDoerDetail.user_id;
									listing.add(project);
									TaskDoer doer1 = database.getTaskTasker(project.tasker_id, project._Id);
									if (doer1.is_invited != null) {
										if (doer1.is_invited.equals("1")) {
											project.isSelected = true;
										}
									} else {
										project.isSelected = false;
									}
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				ProjectListDialog dialog = new ProjectListDialog();
				Bundle bundle = new Bundle();
				bundle.putSerializable(Constants.SERIALIZABLE_DATA, listing);
				dialog.setArguments(bundle);
				dialog.show(((FragmentActivity) _context).getSupportFragmentManager(), "ProjectList");
   			}
   		});
	    
	    
	    holder.txtSDMessage.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				if(Util.isDeviceOnline(((Activity) _context))){
					if (holder.txtSDMessage.getText().equals(_context.getResources().getString(R.string.Messages))) {
						Intent intant =  new Intent(_context, ChatViewActivity.class);
						ErandooMqttMessage message = new ErandooMqttMessage();
						message.to_id = searchDoerDetail.user_id;
						message.to_display_id = searchDoerDetail.fullname;
						message.user_image = searchDoerDetail.userimage;
						intant.putExtra(Constants.SERIALIZABLE_DATA, message);
						_context.startActivity(intant);
						((Activity) _context).overridePendingTransition(R.anim.right_in, R.anim.left_out);
						
					} else if (holder.txtSDMessage.getText().equals(_context.getResources().getString(R.string.connect))) {
						bookmarkMember(searchDoerDetail.user_id, position, _context.getResources().getString(R.string.connect));
						messageView = (ViewHolder) view.getTag();
						
					} else if (holder.txtSDMessage.getText().equals(_context.getResources().getString(R.string.accept_me))) {
						bookmarkMember(searchDoerDetail.network_connection.connection_id, position, _context.getResources().getString(R.string.accept_me));
						messageView = (ViewHolder) view.getTag();
						
					} else if (holder.txtSDMessage.getText().equals(_context.getResources().getString(R.string.invitation_sent))) {
						//TODO..
					}
					
				} else{
					Util.showCenteredToast(_context, _context.getResources().getString(R.string.msg_Connect_internet));
				}
			}
		});
	    
	    holder.txtSDPotential.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Util.isDeviceOnline((Activity) _context)) {
					if(searchDoerDetail.bookmark_user.equals("1")){
						bookmarkMember(searchDoerDetail.user_id,position,TAG_POTENTIAL);
						messageView = (ViewHolder) v.getTag();
					}else{
						bookmarkMember(searchDoerDetail.user_id,position,TAG_UNPOTENTIAL);
						messageView = (ViewHolder) v.getTag();
					}
					
				} else{
					Util.showCenteredToast(_context, _context.getResources().getString(R.string.msg_Connect_internet));
				}
			}
		});
	    
	    holder.txtSDName.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				if(!isFromProfile){
					if(Util.isDeviceOnline(((Activity) _context))){
						goToProfileActivity(_data.get(position).user_id);
					}else{
						Util.showCenteredToast(((Activity) _context), ((Activity) _context).getResources().getString(R.string.msg_Connect_internet));
					}
				}
			}
		});
	    
	    holder.imvSDImage.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				if(!isFromProfile){
					if(Util.isDeviceOnline(((Activity) _context))){
						goToProfileActivity(_data.get(position).user_id);
					}else{
						Util.showCenteredToast(((Activity) _context), ((Activity) _context).getResources().getString(R.string.msg_Connect_internet));
					}
				}
			}
		});
	    
		return view;
	}
	
	private void goToProfileActivity(Long user_id){
		Intent intent = new Intent(_context,ProfileActivity.class);
		intent.putExtra(Constants.SERIALIZABLE_DATA, user_id);
		_context.startActivity(intent);
	}
	
	@Override 
	public boolean isEnabled(int position) {
		return false;
	};
	
	private void bookmarkMember(final Long userId,final int position, final String tag){
		
		new AsyncTask<Void, Void, NetworkResponse>(){
			NetworkResponse response;
			@Override
			protected void onPreExecute() {
				Util.showProDialog(_context); 
			}
			
			@Override
			protected NetworkResponse doInBackground(Void... params) {
				 Cmd cmd = null;
				 //message section checking
				 if (tag.equals(_context.getResources().getString(R.string.connect)) || tag.equals(_context.getResources().getString(R.string.accept_me))) {
					 if (tag.equals(_context.getResources().getString(R.string.connect))) {
						 cmd = CmdFactory.createSendNetworkInvitation();
						 cmd.addData("to_user_id", userId);
						 
					 } else{
						 cmd = CmdFactory.createAcceptNetworkInvitation();
						 cmd.addData("connection_id", userId);
						 
					 }
					 
					 cmd.addData(Constants.FLD_USER_ID, AppGlobals.userDetail.user_id);
					 response = NetworkMgr.httpPost(Config.API_URL,cmd.getJsonData());
					 
				 } else{
					 //potential section checking
					 if(tag.equals(TAG_UNPOTENTIAL)){ 
						 cmd = CmdFactory.createDeleteBookmarkCmd();
						 
					 }else{
						 cmd = CmdFactory.createSaveBookmarkCmd();
						 
					 }
					 
					 cmd.addData(Constants.FLD_OPERATION, "user_bookmark");
					 cmd.addData("bookmark_user_id", userId);
					 response = NetworkMgr.httpPost(Config.API_URL,cmd.getJsonData());
				 }
				 return response;
			}
			
			@Override
			protected void onPostExecute(NetworkResponse response) {
				super.onPostExecute(response);
				Util.dimissProDialog();
				
				if (response != null) {
					
					if (!response.isSuccess()) {
						Util.showCenteredToast(_context, response.getErrMsg());
						
					} else{
						if (tag.equals(_context.getResources().getString(R.string.connect))) {
							Util.showCenteredToast(_context, "Invitation sent successfully!");
							messageView.txtSDMessage.setText(_context.getResources().getString(R.string.invitation_sent));
							
						} else if (tag.equals(_context.getResources().getString(R.string.accept_me))) {
							Util.showCenteredToast(_context, "Accepted successfully!");
							messageView.txtSDMessage.setText(_context.getResources().getString(R.string.Messages));
							
						} else if (tag.equals(TAG_UNPOTENTIAL)) {
							messageView.txtSDPotential.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_check_yes, 0, 0, 0);
							_data.get(position).bookmark_user = "1";
							
						} else if (tag.equals(TAG_POTENTIAL)) {
							messageView.txtSDPotential.setCompoundDrawablesWithIntrinsicBounds( 0, 0, 0, 0);
							_data.get(position).bookmark_user = "0";
							
						}
					}
				}
			}
		}.execute();
	}
	
	static class ViewHolder{
		ImageView imvSDImage;
		//ImageView imvSDMore;
		TextView txtSDName;
		TextView txtSDLocation;
		TextView txtSDHired;
		TextView txtSDNetwork;
		TextView txtSDJobs;
		TextView txtSDInvite;
		TextView txtSDMessage;
		TextView txtSDPotential;
		RatingBar rBarSDRating;
		LinearLayout llSDMain;
		LinearLayout llSDOperation;
		LinearLayout llSDInfo;
		CheckBox chkSDCheck;
		ImageView imvSDPremium;
		ImageView imvSDOnlineStatus;
		View SDview1;
		View SDview2;
	}
	
	private Comparator<Project> sortingByDate = new Comparator<Project>() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		public int compare(Project ord1, Project ord2) {
			int result = 1;
			if (ord1.created_at != null && ord2.created_at != null) {
				Date d1 = null;
				Date d2 = null;
				try {
					d1 = sdf.parse(ord1.created_at);
					d2 = sdf.parse(ord2.created_at);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (ascOrDesc.equals(ascending)) {
					result = (d1.getTime() > d2.getTime() ? -1 : 1); // descending
				} else {
					result = (d1.getTime() > d2.getTime() ? 1 : -1); // ascending
				}
			}
			return result;
		}
	};
}
