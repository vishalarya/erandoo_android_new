package erandoo.app.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.cmds.Cmd;
import erandoo.app.cmds.CmdFactory;
import erandoo.app.config.Config;
import erandoo.app.custom.IVCheckListener;
import erandoo.app.database.Project;
import erandoo.app.database.Proposal;
import erandoo.app.main.CraftABidActivity;
import erandoo.app.main.ProjectDetailsActivity;
import erandoo.app.network.NetworkMgr;
import erandoo.app.network.NetworkResponse;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.Util;
import erandoo.app.utilities.LicenseValidation;

public class SearchProjectListAdapter extends ArrayAdapter<Project> {
	private final String TAG_DELETE = "delete";
	private final String TAG_SAVE = "save";
	private ArrayList<Project> _data;
	private Context _context;
	private int _layoutRID;
	public SearchProjectListAdapter(Context context, int layoutID,ArrayList<Project> data) {
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
			holder.txtPLTitle = (TextView) view.findViewById(R.id.txtPLTitle);
			holder.txtPLStartDate = (TextView) view.findViewById(R.id.txtPLStartDate);
			holder.txtPLLocation = (TextView) view.findViewById(R.id.txtPLLocation);
			holder.txtPLDescription = (TextView) view.findViewById(R.id.txtPLDescription);
			holder.txtPLCategoryName = (TextView) view.findViewById(R.id.txtPLCategoryName);
			holder.txtPLPaymentMode = (TextView) view.findViewById(R.id.txtPLPaymentMode);
			holder.txtPLProposalCount = (TextView) view.findViewById(R.id.txtPLProposalCount);
			holder.txtPLSkillNames = (TextView) view.findViewById(R.id.txtPLSkillNames);
			holder.txtPLView = (TextView) view.findViewById(R.id.txtPLView);
			holder.txtPLShare = (TextView) view.findViewById(R.id.txtPLShare);
			holder.txtPLSave = (TextView) view.findViewById(R.id.txtPLSave);
			holder.txtPLApply = (TextView) view.findViewById(R.id.txtPLApply);
			holder.txtPLType = (TextView) view.findViewById(R.id.txtPLType);
			holder.imvPLPremium = (ImageView)view.findViewById(R.id.imvPLPremium);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		final Project project = _data.get(position); 
		holder.txtPLTitle.setText(project.title); 
		holder.txtPLStartDate.setText(Util.setProjectDateWithMonth(project.bid_close_dt, true)); 
		holder.txtPLLocation.setText(project.projectLocationNames); 
		holder.txtPLDescription.setText(project.description); 
		holder.txtPLCategoryName.setText(project.category.category_name); 
		holder.txtPLProposalCount.setText(project.proposals_cnt); 
		holder.txtPLSkillNames.setText(project.projectSkillNames); 
		holder.txtPLType.setText(Util.getProjectTypeName(project.task_kind)); 
		holder.txtPLPaymentMode.setText(Util.getBudgetString(project));
		
		if(project.is_premium.equals("0")){
	    	holder.imvPLPremium.setVisibility(View.INVISIBLE);
	    }else{
	    	holder.imvPLPremium.setVisibility(View.VISIBLE);
	    }
		
		holder.txtPLView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(_context, ProjectDetailsActivity.class);
				intent.putExtra(Constants.PARENT_VIEW, Constants.VIEW_SEARCH_PROJECTS);
				intent.putExtra(Constants.SERIALIZABLE_DATA, project);
				_context.startActivity(intent);
				((Activity) _context).overridePendingTransition(R.anim.right_in, R.anim.left_out);			
			}
		});
		
		holder.txtPLApply.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LicenseValidation validation = LicenseValidation.getInstance(_context, LicenseValidation.VALIDATION_ON_BID, project.task_id,new IVCheckListener() {
					@Override
					public void onValidationChecked(String isPercent,String planValue) {
						goToCraftABit(project,isPercent,planValue);
					}
				});
				validation.execute();
			}
		});
		
		holder.txtPLShare.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Util.shareProjectLink(_context, project.title,project.project_detail_url);
 			}
		});
		
		holder.txtPLSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				bookmarkProject(project.task_id,position,v.getTag().toString());
			}
		});
		
		if(project.is_highlighted.equals("1")){
			view.setBackgroundResource(R.drawable.ed_bg_hlight_card_view);
		}else{
	    	view.setBackgroundResource(R.drawable.ed_bg_card_view);
		}
		
		if(project.bookmark_project.equals("1")){
			holder.txtPLSave.setTag(TAG_DELETE); 
			holder.txtPLSave.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_check_yes, 0, 0, 0);
		}else{
			holder.txtPLSave.setTag(TAG_SAVE); 
			holder.txtPLSave.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		}
		
		return view;
	}
	
	@Override 
	public boolean isEnabled(int position) {
		return false;
	};
	
	private void goToCraftABit(Project project ,String isPercent,String planValue){
		Proposal proposal = new Proposal();
		proposal.projectData = project;
		proposal.plan_value_is_percent = isPercent;
		proposal.questions = project.multicatquestion;
		proposal.plan_value = planValue;
		proposal.viewMode = "apply";
	    Intent intent = new Intent(_context, CraftABidActivity.class);
		intent.putExtra(Constants.SERIALIZABLE_DATA, proposal);
		_context.startActivity(intent);
		((Activity) _context).overridePendingTransition(R.anim.right_in, R.anim.left_out);	
	}
	
	private void bookmarkProject(final Long taskId,final int position, final String tag){
		new AsyncTask<Void, Void, NetworkResponse>(){
			@Override
			protected void onPreExecute() {
				Util.showProDialog(_context); 
			}
			@Override
			protected NetworkResponse doInBackground(Void... params) {
				 Cmd cmd = null;
				if(tag.equals(TAG_DELETE)){ 
					 cmd = CmdFactory.createDeleteBookmarkCmd();
				}else{
					 cmd = CmdFactory.createSaveBookmarkCmd();
				}
			    cmd.addData(Constants.FLD_OPERATION, "project_bookmark");
			    cmd.addData(Project.FLD_TASK_ID, taskId);
			    NetworkResponse response = NetworkMgr.httpPost(Config.API_URL,cmd.getJsonData());
				if (response != null) {
					if (response.isSuccess()) {
						if(tag.equals(TAG_DELETE)){
							_data.get(position).bookmark_project = "0";
						}else{
							_data.get(position).bookmark_project = "1";
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
		TextView txtPLTitle;
		TextView txtPLStartDate;
		TextView txtPLLocation;
		TextView txtPLDescription;
		TextView txtPLCategoryName;
		TextView txtPLPaymentMode;
		TextView txtPLProposalCount;
		TextView txtPLSkillNames;
		
		TextView txtPLView;
		TextView txtPLShare;
		TextView txtPLSave;
		TextView txtPLApply;
		TextView txtPLType;
		ImageView imvPLPremium;
	}
}
