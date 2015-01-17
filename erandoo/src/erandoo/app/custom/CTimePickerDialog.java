package erandoo.app.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import erandoo.app.R;

public class CTimePickerDialog extends Dialog implements android.view.View.OnClickListener  {

	private TimerListner tpl;
	
	private Button btnTPSetDate;
	private Button btnTPCancel;
	private ImageButton imgBtnHourUp;
	private ImageButton imgBtnHourDown;
	private ImageButton imgBtnMinuteUp;
	private ImageButton imgBtnMinuteDown;
	private ImageButton imgBtnTimeUp;
	private ImageButton imgBtnTimeDown;
	private TextView txtHour;
	private TextView txtMinute;
	private TextView txtAMPM;
	
	public CTimePickerDialog(Context context, final TimerListner timeListner) {
		super(context);
		this.tpl = timeListner;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ed_time_picker);

		initialize();
	}
	
	private void initialize() {
		btnTPSetDate = (Button) findViewById(R.id.btnTPSetDate);
		btnTPCancel = (Button) findViewById(R.id.btnTPCancel);
		imgBtnHourUp = (ImageButton) findViewById(R.id.imgBtnHourUp);
		imgBtnHourDown = (ImageButton) findViewById(R.id.imgBtnHourDown);
		imgBtnMinuteUp = (ImageButton) findViewById(R.id.imgBtnMinuteUp);
		imgBtnMinuteDown = (ImageButton) findViewById(R.id.imgBtnMinuteDown);
		imgBtnTimeUp = (ImageButton) findViewById(R.id.imgBtnTimeUp);
		imgBtnTimeDown = (ImageButton) findViewById(R.id.imgBtnTimeDown);
		txtHour = (TextView) findViewById(R.id.txtHour);
		txtMinute = (TextView) findViewById(R.id.txtMinute);
		txtAMPM = (TextView) findViewById(R.id.txtAMPM);
		
		txtHour.setText("12");
		txtMinute.setText("00");
		txtAMPM.setText("AM");
		
		btnTPSetDate.setOnClickListener(this);
		btnTPCancel.setOnClickListener(this);
		imgBtnHourUp.setOnClickListener(this);
		imgBtnHourDown.setOnClickListener(this);
		imgBtnMinuteUp.setOnClickListener(this);
		imgBtnMinuteDown.setOnClickListener(this);
		imgBtnTimeUp.setOnClickListener(this);
		imgBtnTimeDown.setOnClickListener(this);
	}
	

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.btnTPSetDate) {
			String selectedTime = txtHour.getText().toString()+":"+txtMinute.getText().toString()+" "+txtAMPM.getText().toString();
			tpl.OnDoneButton(CTimePickerDialog.this,selectedTime);
			dismiss();
		}else if (view.getId() == R.id.btnTPCancel) {
			dismiss();
		}else if (view.getId() == R.id.imgBtnHourUp) {
			int hour = Integer.parseInt(txtHour.getText().toString());
			if (hour == 12) {
				hour = 1;
			}else{
				hour = hour+1;
			}
			txtHour.setText(""+setPreZero(hour));
		}else if (view.getId() == R.id.imgBtnHourDown) {
			int hour = Integer.parseInt(txtHour.getText().toString());
			if (hour == 1) {
				hour = 12;
			}else{
				hour = hour-1;
			}
			txtHour.setText(""+setPreZero(hour));
		}else if (view.getId() == R.id.imgBtnMinuteUp) {
			int minute = Integer.parseInt(txtMinute.getText().toString());
			if (minute == 45) {
				minute = 00;
			}else{
				minute = minute+15;
			}
			txtMinute.setText(""+setPreZero(minute));
		}else if (view.getId() == R.id.imgBtnMinuteDown) {
			int minute = Integer.parseInt(txtMinute.getText().toString());
			if (minute == 00) {
				minute = 45;
			}else{
				minute = minute-15;
			}
			txtMinute.setText(""+setPreZero(minute));
		}else if (view.getId() == R.id.imgBtnTimeUp || view.getId() == R.id.imgBtnTimeDown) {
			String time = txtAMPM.getText().toString();
			if (time.equals("AM")) {
				time = "PM";
			}else{
				time = "AM";
			}
			txtAMPM.setText(""+time);
		}
	}
	
	private String setPreZero(int value){
		String val = null;
		if (String.valueOf(value).length() == 1) {
			val = "0"+value;
		}else{
			val = String.valueOf(value);
		}
		return val;
	}
	
	public interface TimerListner {
		public void OnDoneButton(Dialog timedialog,String str);
		public void OnCancelButton(Dialog timedialog);
	}
}
