package erandoo.app.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.database.Attachment;

public class AttachmentsAdapter extends ArrayAdapter<Attachment>{

	Context _context;
	int _resource;
	ArrayList<Attachment> _data;
	
	public AttachmentsAdapter(Context context, int resource, ArrayList<Attachment> data ) {
		super(context, resource, data);
		_context = context;
		_resource = resource;
		_data = data;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) { 
		View view = convertView;
		ViewHolder holder;
		if(view == null){
			LayoutInflater mLayoutInflator = ((Activity)_context).getLayoutInflater();
			view = mLayoutInflator.inflate(_resource, parent, false);
			holder = new ViewHolder();
			holder.imvAttach = (ImageView) view.findViewById(R.id.imvAttach);
			holder.txtAttachment = (TextView) view.findViewById(R.id.txtAttachment);
			holder.llAttachmentRow = (LinearLayout) view.findViewById(R.id.llAttachmentRow);
			view.setTag(holder);
		}else{
			holder = (ViewHolder) view.getTag();
		}
		Attachment attach = _data.get(position);
		holder.txtAttachment.setText(attach.file_name);
		return view;
	}
	
	static class ViewHolder{
		ImageView imvAttach;
		TextView txtAttachment;
		LinearLayout llAttachmentRow;
	}
}
