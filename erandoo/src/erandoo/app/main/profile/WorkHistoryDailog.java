package erandoo.app.main.profile;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.adapters.ProfileWorkHistoryAdapter;
import erandoo.app.custom.CustomDialog;
import erandoo.app.database.WorkHistory;
import erandoo.app.utilities.Constants;

public class WorkHistoryDailog extends CustomDialog implements OnClickListener{
	private ImageView imgDBack;
	private TextView txtDHTitle;
	private TextView txtDHRight;
	
	private View view;
	private ListView lvUPWHList;
	
	private ArrayList<WorkHistory> history;
	private ProfileWorkHistoryAdapter adapter;
	
	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.ed_user_profile_work_history, container, false);
		history = (ArrayList<WorkHistory>) getArguments().getSerializable(Constants.SERIALIZABLE_DATA);
		initialize();
		return view;
	}

	private void initialize() {
		imgDBack = (ImageView) view.findViewById(R.id.imgDBack);
		txtDHTitle = (TextView) view.findViewById(R.id.txtDHTitle);
		txtDHRight = (TextView) view.findViewById(R.id.txtDHRight);
		
		lvUPWHList = (ListView) view.findViewById(R.id.lvUPWHList);
		
		txtDHRight.setVisibility(View.GONE);
		txtDHTitle.setText(getResources().getString(R.string.work_history));
		imgDBack.setOnClickListener(this);
		
		setWHData();
		
		lvUPWHList.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				/*WorkHistory  history = (WorkHistory) adapter.getItemAtPosition(position);
				Util.showCenteredToast(getActivity(), history.title);*/
			} 
		});
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.imgDBack) {
			dismiss();
		}
	}
	
	private void setWHData(){
		adapter = new ProfileWorkHistoryAdapter(getActivity(), R.layout.ed_profile_work_history_row, history);
		lvUPWHList.setAdapter(adapter);
	}
}
