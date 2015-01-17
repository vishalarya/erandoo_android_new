package erandoo.app.projects;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.adapters.IRGridViewAdapter;
import erandoo.app.custom.CircularImageView;
import erandoo.app.main.CreateProjectActivity;
import erandoo.app.mqtt.ErandooMqttMessage;
import erandoo.app.utilities.Util;

public class InstantResultDialog extends Dialog {
	private final String VIEW_INV_DEFAULT = "inv_default";
	private final String VIEW_INV_SENT = "inv_sent";
	private final String VIEW_INV_ACCEPTED = "inv_accepted";
	private final String VIEW_INV_NOT_ACCEPTED = "inv_notaccepted";

	private LinearLayout llIResultDefaultView;
	private LinearLayout llIResultInvAccpted;
	private RelativeLayout llIResultInvNotAccpted;

	private TextView txtIRdialogTitle;
	private TextView txtIRdialogSubTitle;
	private TextView txtIResultMessage;

	private Button btnIResultMyProject;
	private Button btnIResultRevise;

	private CircularImageView cimgVIResultUserImage;
	private TextView txtIResultUserName;
	private GridView gViewIRInvitedUsers;

	private ErandooMqttMessage eMqttMessage = null;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ed_cp_iresult);
		initialize();
	}

	private void initialize() {
		llIResultDefaultView = (LinearLayout) findViewById(R.id.llIResultDefaultView);
		llIResultInvAccpted = (LinearLayout) findViewById(R.id.llIResultInvAccpted);
		llIResultInvNotAccpted = (RelativeLayout) findViewById(R.id.llIResultInvNotAccpted);

		txtIRdialogTitle = (TextView) findViewById(R.id.txtIRdialogTitle);
		txtIRdialogSubTitle = (TextView) findViewById(R.id.txtIRdialogSubTitle);
		txtIResultMessage = (TextView) findViewById(R.id.txtIResultMessage);

		btnIResultMyProject = (Button) findViewById(R.id.btnIResultMyProject);
		btnIResultRevise = (Button) findViewById(R.id.btnIResultRevise);

		cimgVIResultUserImage = (CircularImageView) findViewById(R.id.cimgVIResultUserImage);
		txtIResultUserName = (TextView) findViewById(R.id.txtIResultUserName);

		gViewIRInvitedUsers = (GridView) findViewById(R.id.gViewIRInvitedUsers);

		String view = VIEW_INV_DEFAULT;
		if (eMqttMessage != null) {
			view = eMqttMessage.cmd;
		}
		setDataForView(view);
		setBtnClickHandlers();
	}
	
	private void setDataForView(String view) {
		if (view.equals(VIEW_INV_DEFAULT) || view.equals(VIEW_INV_SENT)) {
			llIResultDefaultView.setVisibility(View.VISIBLE);
			llIResultInvAccpted.setVisibility(View.GONE);
			llIResultInvNotAccpted.setVisibility(View.GONE);
			gViewIRInvitedUsers.setVisibility(View.GONE);

			txtIRdialogTitle.setText("Posting instant task");
			txtIRdialogSubTitle.setText("Please wait. We are posting your instant task.");
			txtIResultMessage.setText("We are searching for the best person for your instant task.");

			if (view.equals(VIEW_INV_SENT)) {
				gViewIRInvitedUsers.setVisibility(View.VISIBLE);
				txtIResultMessage.setVisibility(View.GONE);
				txtIRdialogTitle.setText("Your instant task successfully posted");
				txtIRdialogSubTitle.setText("Please wait. We are waiting for response.");
				setInvitedUsersData();
			}

		} else if (view.equals(VIEW_INV_ACCEPTED)) {
			llIResultDefaultView.setVisibility(View.GONE);
			llIResultInvNotAccpted.setVisibility(View.GONE);
			llIResultInvAccpted.setVisibility(View.VISIBLE);

			txtIRdialogTitle.setText("SUCCESS ! your task has been accepted by");
			txtIRdialogSubTitle.setVisibility(View.GONE);
			if (eMqttMessage != null) {
				Util.loadImage(cimgVIResultUserImage, eMqttMessage.pic, R.drawable.ic_launcher); 
				txtIResultUserName.setText(eMqttMessage.nm); 
			}
		} else if (view.equals(VIEW_INV_NOT_ACCEPTED)) {
			llIResultInvNotAccpted.setVisibility(View.VISIBLE);
			llIResultDefaultView.setVisibility(View.GONE);
			llIResultInvAccpted.setVisibility(View.GONE);

			txtIRdialogTitle.setText("So far, No doers have accepted your task. Would you like to revise your task details? ");
			txtIRdialogSubTitle.setVisibility(View.GONE);
		}
	}

	private void setInvitedUsersData() {
		if (eMqttMessage != null) {
			if (eMqttMessage.doers_info.size() > 0) {
				IRGridViewAdapter adapter = new IRGridViewAdapter(context,R.layout.ed_cp_iresult_grid_row,eMqttMessage.doers_info);
				gViewIRInvitedUsers.setAdapter(adapter);
			}
		}
	}
	
	private void setBtnClickHandlers(){
		btnIResultMyProject.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((CreateProjectActivity)context).finish();
				dismiss();
			}
		});
		
		btnIResultRevise.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}

	public InstantResultDialog(Context context, ErandooMqttMessage eMqttMessage) {
		super(context);
		this.context = context;
		this.eMqttMessage = eMqttMessage;
	}
}
