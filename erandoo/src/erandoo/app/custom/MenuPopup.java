package erandoo.app.custom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import erandoo.app.R;

public class MenuPopup {

	private LayoutInflater layoutInflater;
	private PopupWindow popupWindow;
	private View showPopupView;
	private View popupView;

	private ListView lvItemsList;
	private ArrayAdapter<String> filterAdapter;
	
	private int dp;
	private Context con;
	private String[] itemsList;
	
	private ItemClickListener icl;

	public MenuPopup(Context context, View v, String[] items, ItemClickListener itemClickListener) {
		con = context;
		showPopupView = v;
		itemsList = items;
		icl = itemClickListener;
		filterPopup();
	}

	@SuppressWarnings("deprecation")
	public void filterPopup() {
		layoutInflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		popupView = layoutInflater.inflate(R.layout.ed_popup_menus, null);
		
		lvItemsList = (ListView) popupView.findViewById(R.id.itemsList);

		filterAdapter = new ArrayAdapter<String>(con, android.R.layout.simple_list_item_1, itemsList){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View rowView = super.getView(position, convertView, parent);
		        TextView text = (TextView) rowView.findViewById(android.R.id.text1);
		        text.setTextColor(Color.BLACK);
		        text.setTextSize(14);
		        text.setHeight(45);
		        text.setMinimumHeight(22);
				return rowView;
			}
		};
		lvItemsList.setAdapter(filterAdapter);
		lvItemsList.setOnItemClickListener(itemClick);
		
		popupWindow = new PopupWindow(con);
		popupWindow.setContentView(popupView);
		dp = (int) (con.getResources().getDimension(R.dimen.PopupMenuWidth) / con.getResources().getDisplayMetrics().density);
		popupWindow.setWidth(dp);
		popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
		popupWindow.setFocusable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.showAsDropDown(showPopupView);
	}
	
	String item;
	private OnItemClickListener itemClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int position,long id) {
			item  = adapter.getItemAtPosition(position).toString();
			icl.onClick(item);
			popupWindow.dismiss();
		}
	};
	
	public interface ItemClickListener {
		public void onClick(String str);
	}
}
