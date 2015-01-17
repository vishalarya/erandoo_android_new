package erandoo.app.adapters;

import java.util.ArrayList;
import java.util.HashSet;
import erandoo.app.R;
import erandoo.app.database.Skill;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SkillListAdapter extends ArrayAdapter<Skill> {
	private ArrayList<Skill> _data;
	private Context _context;
	private int _resource;
	private HashSet<String> selectedSkillId = new HashSet<String>();
	public SkillListAdapter(Context context, int resource, ArrayList<Skill> data,ArrayList<Skill> selectedSkills) {
		super(context, resource, data);
		_data = data;
		_context = context;
		_resource = resource;
		if(selectedSkills != null){
			for (Skill skill: selectedSkills) {
				selectedSkillId.add(skill.skill_id);
			}
		}
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder holder;
		if (view == null) {
			LayoutInflater mLayoutInflater = ((Activity) _context).getLayoutInflater();
			view = mLayoutInflater.inflate(_resource, parent, false);
			holder = new ViewHolder();
			holder.txtSimpleRow = (TextView) view.findViewById(R.id.txtSimpleRow);
			holder.imgVSListRow = (ImageView)view.findViewById(R.id.imgVSListRow);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		Skill skill = _data.get(position);
		boolean alreadySelected = selectedSkillId.contains(skill.skill_id);
		
		if(skill.isSelected || alreadySelected){
			if(alreadySelected){
				skill.isSelected = true;
				selectedSkillId.remove(skill.skill_id);
			}
			holder.imgVSListRow.setImageResource(R.drawable.ic_list_check);
		}else{
			holder.imgVSListRow.setImageResource(R.drawable.ic_list_uncheck);
		}
		
		holder.txtSimpleRow.setText(skill.skill_desc);
		holder.imgVSListRow.setVisibility(View.VISIBLE); 

		if (position % 2 == 0) {
			view.setBackgroundColor(_context.getResources().getColor(R.color.text_Selection_color));
		} else {
			view.setBackgroundColor(_context.getResources().getColor(R.color.trans_color));
		}
		return view;
	}

	static class ViewHolder {
		TextView txtSimpleRow;
		ImageView imgVSListRow;
	}
}