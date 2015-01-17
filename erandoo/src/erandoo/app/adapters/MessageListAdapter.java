package erandoo.app.adapters;

import java.util.ArrayList;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.database.MessageDetail;
import erandoo.app.main.AppGlobals;
import erandoo.app.main.message.MessageDetailActivity;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.Util;

public class MessageListAdapter extends ArrayAdapter<MessageDetail> {
	
	private ArrayList<MessageDetail> _messageList;
	private Context _context;
	private int _layoutRID;
	private boolean isDetails;
	private boolean isPublic;
	private RelativeLayout.LayoutParams layoutParams;
	private ViewHolder holder;
	private View view;
	private OnClickListener reply;
	private OnClickListener markRead;
	private OnClickListener markPublic;
	
	public MessageListAdapter(Context context, int layoutID,
			ArrayList<MessageDetail> messageList, boolean isDetails,
			boolean isPublic, OnClickListener reply, OnClickListener markRead,
			OnClickListener markPublic) {
		super(context, layoutID, messageList);
		_context = context;
		_layoutRID = layoutID;
		_messageList = messageList;
		this.isDetails = isDetails;
		this.isPublic = isPublic;
		this.reply = reply;
		this.markRead = markRead;
		this.markPublic = markPublic;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		view = convertView;
		
		if (view == null) {
			LayoutInflater mLayoutInflater = ((Activity) _context).getLayoutInflater();
			view = mLayoutInflater.inflate(_layoutRID, parent, false);
			holder = new ViewHolder();
			holder.txtMessageTitle = (TextView) view.findViewById(R.id.txtMessageTitle);
			holder.txtMUserName = (TextView) view.findViewById(R.id.txtMUserName);
			holder.txtMDate = (TextView) view.findViewById(R.id.txtMDate);
			holder.txtMTime = (TextView) view.findViewById(R.id.txtMTime);
			holder.txtMDescription = (TextView) view.findViewById(R.id.txtMDescription);
			holder.imvMArrow = (ImageView) view.findViewById(R.id.imvMArrow);
			holder.chkDeleteMessage = (CheckBox) view.findViewById(R.id.chkDeleteMessage);
			holder.llMsgMain = (LinearLayout) view.findViewById(R.id.llMsgMain);
			holder.llPMMore = (LinearLayout) view.findViewById(R.id.llPMMore);
			holder.txtPMReply = (TextView) view.findViewById(R.id.txtPMReply);
			holder.txtPMMarkAsRead = (TextView) view.findViewById(R.id.txtPMMarkAsRead);
			holder.txtPMMarkPublic = (TextView) view.findViewById(R.id.txtPMMarkPublic);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		/*if(position % 2 == 0){
	    	view.setBackgroundColor(_context.getResources().getColor(R.color.text_Selection_color)); 
	    }else{
	    	view.setBackgroundColor(_context.getResources().getColor(R.color.trans_color)); 
	    }*/
		
		if (isPublic) {
			holder.txtMessageTitle.setText(_messageList.get(position).user_name);
			holder.txtMDate.setText(_messageList.get(position).created_at); 
			holder.txtMDescription.setText(_messageList.get(position).body);
			holder.imvMArrow.setVisibility(View.GONE);
			if (_messageList.get(position).user_name.equals(AppGlobals.userDetail.fullname)) {
				holder.llPMMore.setVisibility(View.GONE);
			}else{
				holder.llPMMore.setVisibility(View.VISIBLE);
			}
		} else{
			setRowData(position);
			holder.llPMMore.setVisibility(View.GONE);
		}
		
		holder.txtPMReply.setTag(_messageList.get(position));
		holder.txtPMMarkAsRead.setTag(_messageList.get(position));
		holder.txtPMMarkPublic.setTag(_messageList.get(position));
		
		holder.txtPMReply.setOnClickListener(reply);
		holder.txtPMMarkAsRead.setOnClickListener(markRead);
		holder.txtPMMarkPublic.setOnClickListener(markPublic);

		holder.chkDeleteMessage.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				boolean isAny = true;
				if (((CheckBox) view).isChecked()) {
					_messageList.get(position).isSelected = true;
				}else{
					_messageList.get(position).isSelected = false;
				}
				for (int i = 0; i < _messageList.size(); i++) {
					if (!_messageList.get(i).isSelected) {
						isAny = false;
						break;
					}
				}
				if (isAny) {
					MessageDetailActivity.chkSelectAll.setChecked(true);
				}else{
					MessageDetailActivity.chkSelectAll.setChecked(false);
				}
			}
		});
		return view;
	}
	
	@Override 
	public boolean isEnabled(int position) {
		if (isPublic) {
			return false;
		} else{
			return true;
		}
	};
	
	static class ViewHolder {
		TextView txtMessageTitle;
		TextView txtMUserName;
		TextView txtMDate;
		TextView txtMTime;
		TextView txtMDescription;
		ImageView imvMArrow;
		CheckBox chkDeleteMessage;
		LinearLayout llMsgMain;
		LinearLayout llPMMore;
		TextView txtPMReply;
		TextView txtPMMarkAsRead;
		TextView txtPMMarkPublic;
	}
	
	private void setRowData(int position){
		MessageDetail msg = _messageList.get(position);
		if (!isDetails) {
			holder.txtMessageTitle.setText(msg.title);
			holder.txtMDescription.setMaxLines(2);
			if (msg.msg_flow.equals("s")) {
				holder.txtMUserName.setText(msg.msg_from_name);
			} else {
				holder.txtMUserName.setText(msg.msg_from_name);
			}
		} else{
			holder.txtMUserName.setText(msg.msg_from_name);
			holder.txtMessageTitle.setVisibility(View.GONE);
			holder.imvMArrow.setVisibility(View.GONE);
			holder.txtMUserName.setTypeface(null, Typeface.BOLD);
			layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			holder.txtMDescription.setLayoutParams(layoutParams);
			
			if (msg.is_read != null) { 
				if (msg.is_read.equals("0")) {
					holder.llMsgMain.setBackgroundColor(Color.parseColor("#eff3f3"));
				}else{
					holder.llMsgMain.setBackgroundColor(Color.parseColor("#ffffff"));
				}
			}
			
			if (msg.isEditable) {
				if (msg.operationType.equals(Constants.MESSAGE_OPERATION_DELETE)) {
					holder.chkDeleteMessage.setVisibility(View.VISIBLE);
				}else if (msg.operationType.equals(Constants.MESSAGE_OPERATION_MARK_READ)) {
					if (Integer.parseInt(msg.is_read) == 0) {
						holder.chkDeleteMessage.setVisibility(View.VISIBLE);
					}else{
						holder.chkDeleteMessage.setVisibility(View.GONE);
					}
				}else if (msg.operationType.equals(Constants.MESSAGE_OPERATION_MARK_UNREAD)) {
					if (Integer.parseInt(msg.is_read) == 1) {
						holder.chkDeleteMessage.setVisibility(View.VISIBLE);
					}else{
						holder.chkDeleteMessage.setVisibility(View.GONE);
					}
				}
			}else{
				holder.chkDeleteMessage.setVisibility(View.GONE);
			}
		}
		
		String arrString[] = msg.created_at.split(" ");
		holder.txtMDate.setText(Util.setProjectDateWithMonth(arrString[0], true));
		holder.txtMTime.setText(arrString[1]); 
		holder.txtMDescription.setText(msg.body);
		
		if (_messageList.get(position).isSelected) {
			holder.chkDeleteMessage.setChecked(true);
		}else{
			holder.chkDeleteMessage.setChecked(false);
		}
	}
}
