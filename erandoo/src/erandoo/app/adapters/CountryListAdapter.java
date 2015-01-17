package erandoo.app.adapters;

import java.util.ArrayList;
import java.util.HashSet;
import erandoo.app.R;
import erandoo.app.database.Country;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CountryListAdapter extends ArrayAdapter<Country> {
	private ArrayList<Country> _data;
	private Context _context;
	private int _resource;
	private HashSet<String> selectedCountryCode = new HashSet<String>();
	public CountryListAdapter(Context context, int resource, ArrayList<Country> data,ArrayList<Country> selectedCountries) {
		super(context, resource, data);
		_data = data;
		_context = context;
		_resource = resource;
		
		if(selectedCountries != null){
			for (Country country: selectedCountries) {
				selectedCountryCode.add(country.country_code);
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

		holder.imgVSListRow.setVisibility(View.VISIBLE); 
		
		Country country = _data.get(position);
		boolean alreadySelected = selectedCountryCode.contains(country.country_code);
		
		if(country.isSelected || selectedCountryCode.contains(country.country_code)){
			if(alreadySelected){
				country.isSelected = true; 
				selectedCountryCode.remove(country.country_code);
			}
			holder.imgVSListRow.setImageResource(R.drawable.ic_list_check);
		}else{
			holder.imgVSListRow.setImageResource(R.drawable.ic_list_uncheck);
		}
		holder.txtSimpleRow.setText(country.country_name);

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
