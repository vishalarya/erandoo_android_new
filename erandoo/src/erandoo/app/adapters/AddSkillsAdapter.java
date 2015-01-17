package erandoo.app.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.database.Skill;

public class AddSkillsAdapter extends BaseAdapter {
	private ArrayList<Skill> skillList;
	private Context _context;

//	HashMap<String, Object> skillOrCatHashMap;
	private boolean showChkBx;
	AddSkillsAdapter adapterContext;

	public AddSkillsAdapter(Context context, ArrayList<Skill> skillList,
			boolean showChkBx) {

		this.skillList = skillList;

		_context = context;

		this.showChkBx = showChkBx;
//		this.skillOrCatHashMap = skillOrCatHashMap;
		adapterContext = this;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub

		return skillList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub

		return skillList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub

		return position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder holder;
		if (view == null) {
			LayoutInflater mLayoutInflater = ((Activity) _context)
					.getLayoutInflater();
			view = mLayoutInflater.inflate(R.layout.ed_ddata_detail_row,
					parent, false);
			holder = new ViewHolder();
			holder.txtDDataDetail = (TextView) view
					.findViewById(R.id.txtDDataDetail);
			holder.imgVDDelete = (ImageView) view
					.findViewById(R.id.imgVDDelete);
			holder.chkBxCatSkill = (CheckBox) view
					.findViewById(R.id.chkBxCatSkill);
			holder.rela1 = (RelativeLayout) view.findViewById(R.id.rela1);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		holder.rela1.setPadding(5, 5, 5, 5);

		holder.txtDDataDetail.setText(skillList.get(position).skill_desc);
		

		if (showChkBx) {

			holder.chkBxCatSkill.setVisibility(View.VISIBLE);
			holder.imgVDDelete.setVisibility(View.GONE);

			holder.chkBxCatSkill.setChecked(skillList.get(position).isSelected);
			holder.chkBxCatSkill.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					setOnCheck(position);
				}

			});

			holder.rela1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					setOnCheck(position);
				}

			});
		} else {
			holder.chkBxCatSkill.setVisibility(View.GONE);
			holder.imgVDDelete.setVisibility(View.VISIBLE);

			holder.imgVDDelete.setTag(position);
			holder.imgVDDelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					skillList.remove(position);

					notifyDataSetChanged();
				}

			});
		}
		return view;
	}

	private void setOnCheck(int position) {
		// TODO Auto-generated method stub

		if (skillList.get(position).isSelected) {
			skillList.get(position).isSelected = false;
//			skillOrCatHashMap.remove(skillList.get(position).skill_id);
		} else {
			skillList.get(position).isSelected = true;
//			skillOrCatHashMap.put(skillList.get(position).skill_id,
//					skillList.get(position));
		}
		notifyDataSetChanged();
	}

	static class ViewHolder {
		TextView txtDDataDetail;
		ImageView imgVDDelete;
		CheckBox chkBxCatSkill;
		RelativeLayout rela1;

	}

}
