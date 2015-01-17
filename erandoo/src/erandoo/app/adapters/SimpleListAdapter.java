package erandoo.app.adapters;

import erandoo.app.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SimpleListAdapter extends ArrayAdapter<String> {
	private String[] _data;
	private Context _context;
	private int _resource;
	public SimpleListAdapter(Context context, int resource, String[] data) {
		super(context, resource, data);
		_data = data;
		_context = context;
		_resource = resource;
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
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		holder.txtSimpleRow.setText(_data[position]);

		if (position % 2 == 0) {
			view.setBackgroundColor(_context.getResources().getColor(R.color.text_Selection_color));
		} else {
			view.setBackgroundColor(_context.getResources().getColor(R.color.trans_color));
		}
		return view;
	}

	static class ViewHolder {
		TextView txtSimpleRow;
	}
}
