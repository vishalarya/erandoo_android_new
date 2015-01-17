package erandoo.app.projects;

import erandoo.app.R;
import erandoo.app.adapters.SimpleListAdapter;
import erandoo.app.custom.IListItemClickListener;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class HourListDialog extends Dialog {
	private ListView listVLDialog;
	private Button btnLDialogCancel;
	private String[] data;
	private Context context;
	private TextView txtLDialogTitle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ed_list_dialog); 
		initialize();
	}

	private void initialize() {
		data = context.getResources().getStringArray(R.array.InstantHoursList);
		txtLDialogTitle = (TextView)findViewById(R.id.txtLDialogTitle);
		listVLDialog = (ListView)findViewById(R.id.listVLDialog);
		btnLDialogCancel = (Button)findViewById(R.id.btnLDialogCancel);
		
		txtLDialogTitle.setText(context.getResources().getString(R.string.Hint_Select_Hours));
		
		listVLDialog.setAdapter(new SimpleListAdapter(context,R.layout.ed_simple_list_row, data));  
		
		listVLDialog.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				target.onItemSelected(data[position], position); 
				HourListDialog.this.dismiss();
			}
		});
		
		btnLDialogCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}

    //-----------------------------------------------------------
	private IListItemClickListener target;
	public HourListDialog(Context context,IListItemClickListener Listener) {
		super(context);
		this.context = context;
		setTarget(Listener); 
	}

	private void setTarget(IListItemClickListener target) {
        this.target = target;
    }
}