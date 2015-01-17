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
import erandoo.app.database.Skill;

public class ProfileSkillAdapter extends ArrayAdapter<Skill> {
	private ArrayList<Skill> _data;
	private Context _context;
	private int _resource;

	public ProfileSkillAdapter(Context context, int resource, ArrayList<Skill> data) {
		super(context, resource, data);
		_data = data;
		_context = context;
		_resource = resource;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder holder;
		if (view == null) {
			LayoutInflater mLayoutInflater = ((Activity) _context).getLayoutInflater();
			view = mLayoutInflater.inflate(_resource, parent, false);
			holder = new ViewHolder();
			holder.txtProfileSkill = (TextView) view.findViewById(R.id.txtProfileSkill);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		holder.txtProfileSkill.setText(_data.get(position).skill_desc);
		return view;
	}
	
	static class ViewHolder{
		TextView txtProfileSkill;
	}
}
