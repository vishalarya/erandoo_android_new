package erandoo.app.adapters;

import java.util.ArrayList;
import java.util.HashMap;
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
import erandoo.app.database.Category;

public class AddCategoryAdapter extends BaseAdapter {

	private Context _context;
	private ArrayList<Category> categoryList;
	HashMap<String, Category> catHashMap;
	private boolean showChkBx;
	AddCategoryAdapter adapterContext;

	public AddCategoryAdapter(Context context,
			ArrayList<Category> categoryList,
			HashMap<String, Category> catHashMap, boolean showChkBx) {

		this.categoryList = categoryList;
		_context = context;

		this.showChkBx = showChkBx;
		this.catHashMap = catHashMap;
		adapterContext = this;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub

		return categoryList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub

		return categoryList.get(position);
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

		holder.txtDDataDetail.setText(categoryList.get(position).category_name);

		if (showChkBx) {

			boolean checked;
			holder.chkBxCatSkill.setVisibility(View.VISIBLE);
			holder.imgVDDelete.setVisibility(View.GONE);

			checked = categoryList.get(position).isChecked;

			holder.chkBxCatSkill.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					setOnCheck(position);
				}

			});

			holder.chkBxCatSkill.setChecked(checked);
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

					categoryList.remove(position);

					adapterContext.notifyDataSetChanged();
				}

			});
		}
		return view;
	}

	private void setOnCheck(int position) {
		// TODO Auto-generated method stub

		if (categoryList.get(position).isChecked) {
			categoryList.get(position).isChecked = false;
			catHashMap.remove(categoryList.get(position).category_id);
		} else {
			categoryList.get(position).isChecked = true;
			catHashMap.put(String.valueOf(categoryList.get(position).category_id),
					categoryList.get(position));
		}

		adapterContext.notifyDataSetChanged();
	}

	static class ViewHolder {
		TextView txtDDataDetail;
		ImageView imgVDDelete;
		CheckBox chkBxCatSkill;
		RelativeLayout rela1;

	}

}
