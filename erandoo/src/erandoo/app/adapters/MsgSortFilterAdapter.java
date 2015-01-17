package erandoo.app.adapters;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.database.SortFilterModel;

public class MsgSortFilterAdapter extends ArrayAdapter<SortFilterModel> {
	public ArrayList<SortFilterModel> _data;
	private Context _context;
	private int _resource;

	public MsgSortFilterAdapter(Context context, int resource,ArrayList<SortFilterModel> data) {
		super(context, resource, data);
		_data = data;
		_context = context;
		_resource = resource;

	}

	@Override
	public SortFilterModel getItem(int position) {
		// TODO Auto-generated method stub
		return super.getItem(position);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder holder;
		if (view == null) {
			LayoutInflater mLayoutInflater = ((Activity) _context)
					.getLayoutInflater();
			view = mLayoutInflater.inflate(_resource, parent, false);
			holder = new ViewHolder();
			holder.txtSimpleRow = (TextView) view
					.findViewById(R.id.txtSimpleRow);
			holder.imgVSListRow = (ImageView) view
					.findViewById(R.id.imgVSListRow);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		SortFilterModel messageFilterObj = _data.get(position);

		holder.imgVSListRow.setVisibility(View.GONE);
		holder.txtSimpleRow.setText(messageFilterObj.name);
		if (messageFilterObj.isChecked) {
			holder.txtSimpleRow.setTextColor(_context.getResources().getColor(
					R.color.red_dark));
			holder.txtSimpleRow.setTypeface(null, Typeface.BOLD);
		} else {

			holder.txtSimpleRow.setTextColor(_context.getResources().getColor(
					R.color.ed_color_gray_light_hint));
			holder.txtSimpleRow.setTypeface(null, Typeface.NORMAL);
		}
		return view;
	}

	static class ViewHolder {
		TextView txtSimpleRow;
		ImageView imgVSListRow;
	}
}