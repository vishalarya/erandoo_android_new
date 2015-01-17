package erandoo.app.adapters;

import java.util.ArrayList;
import erandoo.app.R;
import erandoo.app.database.Money;
import erandoo.app.utilities.Util;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MoneyAdapter extends ArrayAdapter<Money> {
	private ArrayList<Money> _data;
	private Context _context;
	private int _layoutRID;

	public MoneyAdapter(Context context, int layoutID, ArrayList<Money> data) {
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
			LayoutInflater mLayoutInflater = ((Activity) _context)
					.getLayoutInflater();
			view = mLayoutInflater.inflate(_layoutRID, parent, false);
			holder = new ViewHolder();
			holder.txtMDate = (TextView) view.findViewById(R.id.txtMDate);
			holder.txtMAmount = (TextView) view.findViewById(R.id.txtMAmount);
			holder.txtMStatus = (TextView) view.findViewById(R.id.txtMStatus);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		Money money = (Money) _data.get(position);
		String amount=money.debit;
		holder.txtMDate.setText(Util.getChangeDateFormat(money.created_at, "-", "-", false));
		
		if(amount.equals("-0"))
		{
			amount=money.credit;	
		}
		holder.txtMStatus.setText(money.status);
		holder.txtMAmount.setText(amount);
		return view;
	}

	static class ViewHolder {

		TextView txtMDate;
		TextView txtMAmount;
		TextView txtMStatus;
	}
}
