package erandoo.app.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.custom.IListItemClickListener;
import erandoo.app.database.NotificationSetting;

public class SettingNotificationsAdapter extends
		ArrayAdapter<NotificationSetting> {
	IListItemClickListener iListItemClick;
	ArrayList<NotificationSetting> settingsNameArrayList;
	Context context;
	LayoutInflater inflater;

	public SettingNotificationsAdapter(Context context,
			ArrayList<NotificationSetting> settingsNameArrayList) {
		super(context, R.layout.ed_setting_notification_row,
				settingsNameArrayList);
		this.context = context;

		this.settingsNameArrayList = settingsNameArrayList;

		inflater = LayoutInflater.from(this.context);
		this.iListItemClick = (IListItemClickListener) context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		try {
			if (convertView == null) {
				convertView = inflater.inflate(
						R.layout.ed_setting_notification_row, parent, false);
				holder = new ViewHolder();
				
				holder.chkBxEmail = (CheckBox) convertView
						.findViewById(R.id.chkBxEmail);
				holder.chkBxSMS = (CheckBox) convertView
						.findViewById(R.id.chkBxSMS);
				holder.txtSettingDescr=(TextView)convertView.findViewById(R.id.settingNoti_txt);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.txtSettingDescr.setText(settingsNameArrayList.get(position).description);
			if (settingsNameArrayList.get(position).send_email.equals("1")) {
				holder.chkBxEmail.setChecked(true);
			} else {
				holder.chkBxEmail.setChecked(false);
			}

			if (settingsNameArrayList.get(position).send_sms.equals("1")) {
				holder.chkBxSMS.setChecked(true);
			} else {
				holder.chkBxSMS.setChecked(false);
			}

			
			holder.chkBxEmail.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					iListItemClick.onItemSelected(context.getResources()
							.getString(R.string.Email), (Integer) v.getTag());
				}
			});

			holder.chkBxSMS.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					iListItemClick.onItemSelected(context.getResources()
							.getString(R.string.SMS), (Integer) v.getTag());
				}
			});

			holder.chkBxSMS.setTag(position);
			holder.chkBxEmail.setTag(position);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;
	}

	@Override
	public NotificationSetting getItem(int position) {
		// TODO Auto-generated method stub
		return super.getItem(position);
	}

	public class ViewHolder {
		private TextView txtSettingDescr;
		private CheckBox chkBxEmail, chkBxSMS;

	}
}
