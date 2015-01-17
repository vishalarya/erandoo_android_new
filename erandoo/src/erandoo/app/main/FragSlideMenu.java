package erandoo.app.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.config.Config;
import erandoo.app.custom.IVCheckListener;
import erandoo.app.main.settings.SettingsActivity;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.LicenseValidation;
import erandoo.app.utilities.Util;

public class FragSlideMenu extends Fragment implements OnClickListener {
	private View view;
    private TextView txtNewInstantProject;
	private TextView txtNewVirtualProject;
	private TextView txtNewInpersonProject;
	private TextView txtMyProject;
	private TextView txtSearchProjects;
	private TextView txtFindMembers;
	private TextView txtMyProfile;
	private TextView txtSettings;
	private TextView txtInstantAvailability;
	private TextView txtLogout;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.ed_slide_menu, container, false);
		initialize();
		return view;
	}

	private void initialize() {
		txtNewInstantProject = (TextView) view.findViewById(R.id.txtNewInstantProject);
		txtNewVirtualProject = (TextView) view.findViewById(R.id.txtNewVirtualProject);
		txtNewInpersonProject = (TextView) view.findViewById(R.id.txtNewInpersonProject);
		txtMyProject = (TextView) view.findViewById(R.id.txtMyProject);
		txtSearchProjects = (TextView) view.findViewById(R.id.txtSearchProjects);
		txtFindMembers = (TextView) view.findViewById(R.id.txtFindMembers);
		txtMyProfile = (TextView) view.findViewById(R.id.txtMyProfile);
		txtSettings = (TextView) view.findViewById(R.id.txtSettings);
		txtInstantAvailability = (TextView) view.findViewById(R.id.txtInstantAvailability);
		txtLogout = (TextView) view.findViewById(R.id.txtLogout);
			
		txtNewInstantProject.setOnClickListener(this);
		txtNewVirtualProject.setOnClickListener(this);
		txtNewInpersonProject.setOnClickListener(this);
		txtMyProject.setOnClickListener(this);
		txtSearchProjects.setOnClickListener(this);
		txtFindMembers.setOnClickListener(this);
		txtMyProfile.setOnClickListener(this);
		txtSettings.setOnClickListener(this);
		txtInstantAvailability.setOnClickListener(this);
		txtLogout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int vId = v.getId();
		if(vId == R.id.txtNewInstantProject){
			checkLicenseValidation(Constants.VIEW_I_CREATION);
			
		}else if(vId == R.id.txtNewVirtualProject){
			checkLicenseValidation(Constants.VIEW_VP_CREATION);
			
		}else if(vId == R.id.txtNewInpersonProject){
			checkLicenseValidation(Constants.VIEW_IP_CREATION);
			
		}else if(vId == R.id.txtMyProject){
			goToNewWindow(Constants.VIEW_MY_PROJECTS);
			
		}else if(vId == R.id.txtSearchProjects){
			goToNewWindow(Constants.VIEW_SEARCH_PROJECTS);
			
		}else if(vId == R.id.txtFindMembers){
			goToNewWindow(Constants.VIEW_SEARCH_MEMBERS);
			
		}else if(vId == R.id.txtMyProfile){
			if (Util.isDeviceOnline(getActivity())) {
				goToNewWindow(Constants.VIEW_MY_PROFILE);
			}else{
				Util.showCenteredToast(getActivity(), getResources().getString(R.string.msg_Connect_internet));
			}
			
		}else if(vId == R.id.txtSettings){
			goToNewWindow(Constants.VIEW_SETTINGS);
			
		}else if(vId == R.id.txtInstantAvailability){
			
		}else if(vId == R.id.txtLogout){
			logout();
		}
	}
	
	private void goToNewWindow(String type) {
		Intent intent = new Intent();

		if (type.equals(Constants.VIEW_VP_CREATION)) {
			intent.putExtra(Constants.PROJECT_TYPE, Constants.VIRTUAL);
			intent.putExtra(Constants.VIEW_MODE, Constants.VIEW_MODE_CREATE);
			intent.setClass(view.getContext(), CreateProjectActivity.class);

		} else if (type.equals(Constants.VIEW_IP_CREATION)) {
			intent.putExtra(Constants.PROJECT_TYPE, Constants.INPERSON);
			intent.putExtra(Constants.VIEW_MODE, Constants.VIEW_MODE_CREATE);
			intent.setClass(view.getContext(), CreateProjectActivity.class);

		} else if (type.equals(Constants.VIEW_I_CREATION)) {
			intent.putExtra(Constants.PROJECT_TYPE, Constants.INSTANT);
			intent.putExtra(Constants.VIEW_MODE, Constants.VIEW_MODE_CREATE);
			intent.setClass(view.getContext(), CreateProjectActivity.class);

		} else if (type.equals(Constants.VIEW_SEARCH_PROJECTS)) {
			intent.setClass(view.getContext(), SearchProjectActivity.class);
			
		} else if (type.equals(Constants.VIEW_MY_PROJECTS)) {
			intent.setClass(view.getContext(), MyProjectActivity.class);
			
		} else if (type.equals(Constants.VIEW_SEARCH_MEMBERS)) {
			intent.setClass(view.getContext(), SearchDoerActivity.class);
			
		} else if (type.equals(Constants.VIEW_SETTINGS)) {
			intent.setClass(view.getContext(), SettingsActivity.class);
			
		} else if (type.equals(Constants.VIEW_MY_PROFILE)){
			intent.setClass(view.getContext(), ProfileActivity.class);
		}
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
	}
	
	private void logout(){
		try{
			Config.setUserDetails(true);
			Util.startActivity(getActivity(), SignInActivity.class);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void checkLicenseValidation(final String projectType){
		LicenseValidation validation = LicenseValidation.getInstance(getActivity(), LicenseValidation.VALIDATION_ON_PROJECT_POST, null,new IVCheckListener() {
			@Override
			public void onValidationChecked(String isPercent,String planValue) {
				goToNewWindow(projectType);
			}
		});
		validation.execute();
	}
}
