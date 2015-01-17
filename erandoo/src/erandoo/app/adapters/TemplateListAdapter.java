package erandoo.app.adapters;

import java.util.ArrayList;

import erandoo.app.R;
import erandoo.app.database.Template;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TemplateListAdapter extends ArrayAdapter<Template> {
	private ArrayList<Template> _data;
	private Context _context;
	private int _layoutRID;

	public TemplateListAdapter(Context context, int layoutID,ArrayList<Template> data) {
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
			holder.txtSimpleRow = (TextView) view.findViewById(R.id.txtSimpleRow);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		holder.txtSimpleRow.setText(_data.get(position).getTitle());

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
