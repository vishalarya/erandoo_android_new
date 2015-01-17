package erandoo.app.projects;

import erandoo.app.R;
import erandoo.app.adapters.SimpleListAdapter;
import erandoo.app.custom.IListItemClickListener;
import erandoo.app.utilities.Util;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class BidDurationDialog extends Dialog {
	private ListView listVLDialog;
	private Button btnLDialogCancel;
	private static String[] data;
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
		txtLDialogTitle = (TextView)findViewById(R.id.txtLDialogTitle);
		listVLDialog = (ListView)findViewById(R.id.listVLDialog);
		btnLDialogCancel = (Button)findViewById(R.id.btnLDialogCancel);
		
		txtLDialogTitle.setText(context.getResources().getString(R.string.Bid_Duration));  
		listVLDialog.setAdapter(new SimpleListAdapter(context,R.layout.ed_simple_list_row, data)); 
		
		listVLDialog.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				target.onItemSelected(data[position], position); 
				BidDurationDialog.this.dismiss();
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
	public BidDurationDialog(Context context,IListItemClickListener Listener,String selctedDate) {
		super(context);
		this.context = context;
		if (selctedDate == null) {
			createCompletionArray();
		} else {
			createDurationArray(Util.getDateDiff(selctedDate));
		}
		setTarget(Listener);
	}

	private void setTarget(IListItemClickListener target) {
        this.target = target;
    }
	
	private static void createDurationArray(long diff){
		if(diff < 7){
			data = new String[]{"1 Day"};
		}else if(diff < 15){
			data = new String[]{"1 Day","1 Week"};
//		}else if(diff < 30){
//			data = new String[]{"1 Day","1 Week","15 Days"};
		}else{
			data = new String[]{"1 Day","1 Week","15 Days"};
		}
	}
	
	private static void createCompletionArray() {

		data = new String[] { "1 Week", "15 Days", "1 Month", "2 Month",
				"3 Month", "4 Month", "5 Month", "+6 Month" };
	}




	
}
