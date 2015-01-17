package erandoo.app.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.database.Project;

public class ProjectListAdapter extends ArrayAdapter<Project>{
	private Context _context;
	private int _resource;
	private ArrayList<Project> _data;

	public ProjectListAdapter(Context context, int resource, ArrayList<Project> data) {
		super(context, resource, data);
		_context = context;
		_resource = resource;
		_data = data;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		if(view == null){
			LayoutInflater mLayoutInflater = ((Activity)_context).getLayoutInflater();
			view = mLayoutInflater.inflate(_resource, parent, false);
			holder = new ViewHolder();
			holder.rlSimpleRowLayout = (RelativeLayout) view.findViewById(R.id.rlSimpleRowLayout);
			holder.txtSimpleRow = (TextView)view.findViewById(R.id.txtSimpleRow);
			view.setTag(holder);
		}else{
			holder = (ViewHolder) view.getTag();
		}
		
		final Project project = _data.get(position);
		holder.txtSimpleRow.setText(project.title);
		if(project.isSelected){
			holder.txtSimpleRow.setTextColor(_context.getResources().getColor(R.color.ed_color_orange_light));
		}else{
			holder.txtSimpleRow.setTextColor(_context.getResources().getColor(R.color.black));
			holder.rlSimpleRowLayout.setBackgroundResource(R.drawable.ed_btn_icon_selector);
		}
		
		return view;
	}
	
	static class ViewHolder{
		RelativeLayout rlSimpleRowLayout;
		TextView txtSimpleRow;
	}
}
