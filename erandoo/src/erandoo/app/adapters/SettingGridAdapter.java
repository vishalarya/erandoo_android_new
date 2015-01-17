package erandoo.app.adapters;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import erandoo.app.R;

public class SettingGridAdapter extends ArrayAdapter<JSONObject> {

	OnClickListener settingClickListener;
	ArrayList<JSONObject> settingsNameArrayList;
	Context context;
	LayoutInflater inflater;

	public SettingGridAdapter(Context context,
			ArrayList<JSONObject> settingsNameArrayList,
			OnClickListener settingClickListener) {
		super(context, R.layout.ed_settings_activity_grid,
				settingsNameArrayList);
		this.context = context;
		this.settingsNameArrayList = settingsNameArrayList;
		this.settingClickListener = settingClickListener;
		inflater = LayoutInflater.from(this.context);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		try {
			if (convertView == null) {
				convertView = inflater.inflate(
						R.layout.ed_settings_activity_grid, parent, false);
				holder = new ViewHolder();
				holder.txtSettingName = (TextView) convertView
						.findViewById(R.id.txtSettingName);
				holder.imvSetting = (ImageView) convertView
						.findViewById(R.id.imvSetting);
				holder.setting_main_lay = (LinearLayout) convertView
						.findViewById(R.id.setting_main_lay);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.setting_main_lay.setOnClickListener(settingClickListener);
			String settingName = settingsNameArrayList.get(position).getString(
					"setting_name");
			holder.setting_main_lay.setTag(settingName);
			holder.txtSettingName.setText(settingName);

			int imageDrawable = context.getResources()
					.getIdentifier(
							settingsNameArrayList.get(position).getString(
									"image_name"), "drawable",
							context.getPackageName());
			Drawable imageDraw = context.getResources().getDrawable(
					imageDrawable);

			holder.imvSetting.setImageDrawable(imageDraw);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;
	}

	public class ViewHolder {
		TextView txtSettingName;
		ImageView imvSetting;
		LinearLayout setting_main_lay;
	}
}
