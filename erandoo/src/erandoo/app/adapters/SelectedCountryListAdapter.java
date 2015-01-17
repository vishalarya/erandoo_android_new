package erandoo.app.adapters;

import java.util.ArrayList;

import erandoo.app.R;
import erandoo.app.custom.IDataChangedListener;
import erandoo.app.database.Country;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SelectedCountryListAdapter extends ArrayAdapter<Country> {
	private ArrayList<Country> _data; 
	private Context _context;
	private int _resource;
	private IDataChangedListener dataChangeListener;
	public SelectedCountryListAdapter(Context context, int resource, ArrayList<Country> data) {
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
			holder.txtDDataDetail = (TextView) view.findViewById(R.id.txtDDataDetail);
			holder.imgVDDelete = (ImageView)view.findViewById(R.id.imgVDDelete);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		holder.txtDDataDetail.setText(_data.get(position).country_name);
		
		holder.imgVDDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				_data.remove(position);
				notifyDataSetChanged();
				dataChangeListener.onDataChanged();
			}
		});
		
		if (position % 2 == 0) {
			view.setBackgroundColor(_context.getResources().getColor(R.color.text_Selection_color));
		} else {
			view.setBackgroundColor(_context.getResources().getColor(R.color.trans_color));
		}
		return view;
	}

	static class ViewHolder {
		TextView txtDDataDetail;
		ImageView imgVDDelete;
	}
	
	public void setDataChangeListener(IDataChangedListener dataChangeListener){
		this.dataChangeListener = dataChangeListener;
	}
}
