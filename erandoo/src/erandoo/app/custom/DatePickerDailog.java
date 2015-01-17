package erandoo.app.custom;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.custom.datepicker.ArrayWheelAdapter;
import erandoo.app.custom.datepicker.NumericWheelAdapter;
import erandoo.app.custom.datepicker.OnWheelChangedListener;
import erandoo.app.custom.datepicker.WheelView;
import erandoo.app.utilities.Util;

@SuppressLint("SimpleDateFormat")
public class DatePickerDailog extends Dialog {

	private Context Mcontex;

	private int NoOfYear = 40; 
	String curTime;
	String times[];
	
	
	Button btnSetDate, btnCancel;
	
	Context con;
	Calendar cal;
	DatePickerListner dpl;
	int count=0;
	private boolean showPreDate;
	LinearLayout llDateSliderContainer;
	WheelView month,year,am_pm,day,time;
	
	public DatePickerDailog(boolean showPreDate,Context context, Calendar calendar, final DatePickerListner dtp) {
		super(context);
		this.con = context;
		this.cal = calendar;
		this.dpl = dtp;
		this.showPreDate=showPreDate;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ed_date_picker);

		btnSetDate = (Button) findViewById(R.id.btnSetDate);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		llDateSliderContainer = (LinearLayout) findViewById(R.id.llDateSliderContainer);
		
		btnSetDate.setOnClickListener(SetDateListener);
		btnCancel.setOnClickListener(cancelButtonClickListener);
		
		
		callMe();

		
	}
	
	public void callMe(){
		llDateSliderContainer.removeAllViews();
		Mcontex = con;
		
		LinearLayout lytdate = new LinearLayout(Mcontex);

		Button btnset = new Button(Mcontex);
		Button btncancel = new Button(Mcontex);

		btnset.setText("Set");
		btncancel.setText("Cancel");

		month = new WheelView(Mcontex);
		year = new WheelView(Mcontex);
		day = new WheelView(Mcontex);
		time = new WheelView(Mcontex);
		am_pm = new WheelView(Mcontex);
		
		lytdate.addView(year, new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,android.view.ViewGroup.LayoutParams.MATCH_PARENT, 1.15f));
		lytdate.addView(month, new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,android.view.ViewGroup.LayoutParams.MATCH_PARENT, 1.15f));
		lytdate.addView(day, new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,android.view.ViewGroup.LayoutParams.MATCH_PARENT, 1.25f));
		/*lytdate.addView(time, new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,280, 1.12f));
		lytdate.addView(am_pm, new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,280, 1.21f));*/

		llDateSliderContainer.addView(lytdate);
		
		OnWheelChangedListener listener = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				updateDays(year, month, day,time,am_pm);
			}
		};

		// day
		updateDays(year, month, day, time, am_pm);
		day.setCurrentItem(cal.get(Calendar.DAY_OF_MONTH) - 1);
		
		// month
		int curMonth = cal.get(Calendar.MONTH);
		String months[] = new String[] { "Jan", "Feb", "Mar","Apr", "May", "Jun", "Jul", "Aug", "Sep","Oct", "Nov", "Dec" };
		month.setViewAdapter(new DateArrayAdapter(con, months, curMonth));
		month.setCurrentItem(curMonth);
		month.addChangingListener(listener);

		// year
		Calendar cal = Calendar.getInstance();
		int curYear = cal.get(Calendar.YEAR);
		int Year = cal.get(Calendar.YEAR);
		year.setViewAdapter(new DateNumericAdapter(con, Year - NoOfYear,Year + NoOfYear, NoOfYear));
		year.setCurrentItem(curYear-(Year-NoOfYear));
		year.addChangingListener(listener);

		//time hours:minutes
		DateFormat dateFormat = new SimpleDateFormat("hh:mm");
		Date date = new Date();
		curTime = dateFormat.format(date);

		times = new String[720];
		int count=0;
		for (int i = 1; i < 13; i++) {
			String t = "";
			for (int j = 0; j < 60; j++) {
				t = String.valueOf(i);
				if(t.trim().length()==1){
					t = "0"+String.valueOf(i);
				}else{
					t=String.valueOf(i);
				}
				if(String.valueOf(j).trim().length()==1){
					t = t + ":0" + String.valueOf(j);
				}else{
					t = t + ":" + String.valueOf(j);
				}
				times[count] = t;
				t="";
				count++;
			}
		}
		int index=0;
		for (int i = 0; i < times.length; i++) {
			if(times[i].equals(curTime)){
				index = i;
				break;
			}
		}
		time.setViewAdapter(new DateArrayAdapter(con, times, curTime));
		time.setCurrentItem(index);
		time.addChangingListener(listener);

		//am_pm
		Calendar c = Calendar.getInstance();
		int timeFormat = c.get(Calendar.AM_PM);
		String AM_PM[] = new String[] { "AM", "PM",};
		am_pm.setViewAdapter(new DateArrayAdapter(con, AM_PM, timeFormat));
		am_pm.setCurrentItem(timeFormat);
		am_pm.addChangingListener(listener);

	}


	private android.view.View.OnClickListener SetDateListener = new android.view.View.OnClickListener() {
		public void onClick(View v) {
			try {
				//current date time
				Calendar cal = Calendar.getInstance();

				//user date time
				String str="";
				Calendar c = updateDays(year, month, day,time,am_pm);

				//formatter
				SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy, hh:mm a");

				//getting string of date time
				String date = formatter.format(cal.getTime());
				String userDate = formatter.format(c.getTime());

				//converting into date
				Date current = formatter.parse(date);
				Date user = formatter.parse(userDate);
				
				if(current.compareTo(user) > 0&&(!showPreDate)){
					Util.showCenteredToast(getContext(), getContext().getResources().getString(R.string.msg_Completion_date_validation)); 
				}else{
					Calendar c1 = updateDays(year, month, day,time,am_pm);
					dpl.OnDoneButton(DatePickerDailog.this, c1,str);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	};

	private android.view.View.OnClickListener cancelButtonClickListener = new android.view.View.OnClickListener() {
		public void onClick(View v) {
			btnCancel.setBackgroundResource(R.color.red);
			btnCancel.setTextColor(Color.WHITE);
			dismiss();
		}
	};
	
	public Calendar updateDays(WheelView year, WheelView month, WheelView day,WheelView time, WheelView am_pm) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR,calendar.get(Calendar.YEAR)+ (year.getCurrentItem() - NoOfYear));
		calendar.set(Calendar.MONTH, month.getCurrentItem());

		int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		day.setViewAdapter(new DateNumericAdapter(Mcontex, 1, maxDays, calendar.get(Calendar.DAY_OF_MONTH) - 1));
		int curDay = Math.min(maxDays, day.getCurrentItem() + 1);
		day.setCurrentItem(curDay - 1, true);
		try {
			String str = "";
			for (int i = 0; i < times.length; i++) {
				if (i == time.getCurrentItem()) {
					str = times[i];
					break;
				}
			}
			String s[] = str.split(":");
			String hours = s[0];
			String minutes = s[1];
			if(hours.equals("12")){
				calendar.set(Calendar.DAY_OF_MONTH, curDay);
				calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hours));
				calendar.set(Calendar.MINUTE, Integer.parseInt(minutes));
				calendar.set(Calendar.AM_PM, am_pm.getCurrentItem());
			}else{
				calendar.set(Calendar.DAY_OF_MONTH, curDay);
				calendar.set(Calendar.HOUR, Integer.parseInt(hours));
				calendar.set(Calendar.MINUTE, Integer.parseInt(minutes));
				calendar.set(Calendar.AM_PM, am_pm.getCurrentItem());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return calendar;
	}

	private class DateNumericAdapter extends NumericWheelAdapter {
		int currentItem = 0;
		int currentValue = 0;

		public DateNumericAdapter(Context context, int minValue, int maxValue,int current) {
			super(context, minValue, maxValue);
			this.currentValue = current;
			setTextSize(20);
		}

		@Override
		protected void configureTextView(TextView view) {
			super.configureTextView(view);
			if (currentItem == currentValue) {
				view.setTextColor(getContext().getResources().getColor(R.color.blue_dark)); 
			}
			view.setTypeface(null, Typeface.BOLD);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			currentItem = index;
			return super.getItem(index, cachedView, parent);
		}
	}

	private class DateArrayAdapter extends ArrayWheelAdapter<String> {
		int currentItem = 0;
		int currentValue = 0;
		@SuppressWarnings("unused")
		String current = "";

		public DateArrayAdapter(Context context, String[] items, int current) {
			super(context, items);
			this.currentValue = current;
			setTextSize(20);
		}

		public DateArrayAdapter(Context context, String[] times, String curTime) {
			// TODO Auto-generated constructor stub
			super(context, times);
			this.current = curTime;
			for (int i = 0; i < times.length; i++) {
				if(times[i].equals(curTime)){
					currentValue = i;
					break;
				}
			}
			setTextSize(20);
		}

		@Override
		protected void configureTextView(TextView view) {
			super.configureTextView(view);
			if (currentItem == currentValue) {
				view.setTextColor(getContext().getResources().getColor(R.color.blue_dark));
			}
			view.setTypeface(null, Typeface.BOLD);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			currentItem = index;
			return super.getItem(index, cachedView, parent);
		}
	}

	public interface DatePickerListner {
		public void OnDoneButton(Dialog datedialog, Calendar c,String str);
		public void OnCancelButton(Dialog datedialog);
	}
}
