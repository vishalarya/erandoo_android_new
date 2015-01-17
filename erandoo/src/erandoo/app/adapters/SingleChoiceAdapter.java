package erandoo.app.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.database.SingleChoice;

public class SingleChoiceAdapter extends ArrayAdapter<SingleChoice> {
	private ArrayList<SingleChoice> _data;
	private Context _context;
	private int _resource;
	private OnClickListener singleChoiceClick;
	String setName;

	public SingleChoiceAdapter(Context context, int resource, ArrayList<SingleChoice> data, OnClickListener singleChoiceClick, String setName) {
		super(context, resource, data);
		_data = data;
		_context = context;
		_resource = resource;
		this.singleChoiceClick = singleChoiceClick;
		this.setName = setName;

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder holder;
		if (view == null) {
			LayoutInflater mLayoutInflater = ((Activity) _context).getLayoutInflater();
			view = mLayoutInflater.inflate(_resource, parent, false);
			holder = new ViewHolder();
			holder.txtSingle = (TextView) view.findViewById(R.id.txtSingle);
			holder.radioBtnSingle = (RadioButton) view.findViewById(R.id.radioBtnSingle);
			holder.relSingle = (RelativeLayout) view.findViewById(R.id.relSingle);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		holder.txtSingle.setText(_data.get(position).name);
		holder.relSingle.setTag(_data.get(position));
		holder.radioBtnSingle.setTag(_data.get(position));
		holder.relSingle.setOnClickListener(singleChoiceClick);

		if (setName.equals(_data.get(position).name)) {
			holder.radioBtnSingle.setChecked(true);
		} else {
			holder.radioBtnSingle.setChecked(false);
		}
		holder.radioBtnSingle.setOnClickListener(singleChoiceClick);

		return view;
	}

	static class ViewHolder {
		TextView txtSingle;
		RadioButton radioBtnSingle;
		RelativeLayout relSingle;
	}

}
