package erandoo.app.adapters;

import java.util.ArrayList;
import erandoo.app.R;
import erandoo.app.database.Question;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SelectedQuestionListAdapter extends ArrayAdapter<Question> {
	private ArrayList<Question> _data; 
	private Context _context;
	private int _resource;
	public SelectedQuestionListAdapter(Context context, int resource, ArrayList<Question> data) {
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

		holder.txtDDataDetail.setText(_data.get(position).question_desc);
		
		holder.imgVDDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				_data.remove(position);
				notifyDataSetChanged();
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
}
