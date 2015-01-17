package erandoo.app.projects;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.adapters.SelectedCountryListAdapter;
import erandoo.app.adapters.SelectedQuestionListAdapter;
import erandoo.app.adapters.SelectedSkillListAdapter;
import erandoo.app.custom.CustomDialog;
import erandoo.app.custom.IDataChangedListener;
import erandoo.app.custom.IListItemClickListener;
import erandoo.app.database.Country;
import erandoo.app.database.DatabaseMgr;
import erandoo.app.database.Question;
import erandoo.app.database.Skill;
import erandoo.app.database.WorkLocation;
import erandoo.app.main.AppGlobals;
import erandoo.app.network.NetworkResponse;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.Util;

public class DoerDetailsDialog extends CustomDialog implements OnClickListener{
	private View view;
	
	private ImageView imgDBack;
	private TextView txtDHTitle;
	
	private LinearLayout llVPLocation;
	private TextView txtIPLocation;
	private TextView txtChooseCountry;
	private TextView txtChooseQuestion;
	private TextView txtAddSkills;
	private RadioGroup rdGroupVPLocation;
	public ListView listVDDetailsCountry;
	private ListView listVDDetailsSkills;
	private ListView listVDDetailsQuestion;
	private ArrayList<Country> countries = null;
	private ArrayList<Skill> skills = null;
	private ArrayList<Question> questions;
	private String categotyId = "";
	private String projectType;
	private DatabaseMgr database;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.ed_cp_doer_detail,container, false);
		initialize();
		return view;
	}
	
	private void initialize() {
		database = DatabaseMgr.getInstance(getActivity());
		imgDBack = (ImageView)view.findViewById(R.id.imgDBack);
		txtDHTitle = (TextView)view.findViewById(R.id.txtDHTitle);
	
		
		llVPLocation = (LinearLayout)view.findViewById(R.id.llVPLocation);
		
		txtChooseCountry = (TextView)view.findViewById(R.id.txtChooseCountry);
		txtChooseQuestion = (TextView)view.findViewById(R.id.txtChooseQuestion);
		txtIPLocation = (TextView)view.findViewById(R.id.txtIPLocation);
		
		txtAddSkills = (TextView)view.findViewById(R.id.txtAddSkills);
		rdGroupVPLocation = (RadioGroup)view.findViewById(R.id.rdGroupVPLocation);
		
		listVDDetailsCountry = (ListView)view.findViewById(R.id.listVDDetailsCountry);
		listVDDetailsSkills = (ListView)view.findViewById(R.id.listVDDetailsSkills);
		listVDDetailsQuestion = (ListView)view.findViewById(R.id.listVDDetailsQuestion);
		
		txtDHTitle.setText(this.getResources().getString(R.string.Doer_Details));
		
		imgDBack.setOnClickListener(this);
		txtChooseCountry.setOnClickListener(this);
		txtChooseQuestion.setOnClickListener(this);
 		txtAddSkills.setOnClickListener(this); 
 		txtIPLocation.setOnClickListener(this); 
 		
 		if(projectType.equals(Constants.VIRTUAL)){
 			txtIPLocation.setVisibility(View.GONE); 
 			llVPLocation.setVisibility(View.VISIBLE);
 			if(countries.size() > 0){
 				setVPLocationSelction(R.id.rdCountry);
 			}else{
 				setVPLocationSelction(R.id.rdAnywhere);
 			}
 		}else{
 			txtIPLocation.setVisibility(View.VISIBLE);
 			llVPLocation.setVisibility(View.GONE);
 		}
 		
 		rdGroupVPLocation.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				setVPLocationSelction(checkedId);
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		 if(v.getId() == R.id.txtChooseCountry){
			showCountryListDialog();
		}else if(v.getId() == R.id.txtAddSkills){
			showSkillListDialog();
		}else if(v.getId() == R.id.txtChooseQuestion){
			showQuestionListDialog();
		}else if(v.getId() == R.id.txtIPLocation){
			showWLocationListDialog();
		}else if(v.getId() == R.id.imgDBack){
			this.dismiss();
		}
	}
	
	private void setVPLocationSelction(int rdId){
		if(rdId == R.id.rdCountry){
			txtChooseCountry.setVisibility(View.VISIBLE);
			rdGroupVPLocation.check(R.id.rdCountry);
		}else{
			txtChooseCountry.setVisibility(View.GONE); 
			listVDDetailsCountry.setVisibility(View.GONE);
			countries.clear();
			rdGroupVPLocation.check(R.id.rdAnywhere);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(skills.size() > 0 || questions.size() > 0 ){
			setSelectedSkillListData();
	 		setSelectedQuestionListData();
		}else{
			getCategorySkillAndQuestion(categotyId);
		}
		
		setDefaultWLocation();
	}
	
	private void setDefaultWLocation(){
		if(projectType.equals(Constants.VIRTUAL)){
			setSelectedCountryListData();
		}else{
			if(AppGlobals.defaultWLocation != null){
				if(AppGlobals.defaultWLocation.location_name != null){
					txtIPLocation.setText(AppGlobals.defaultWLocation.location_name);
				}else{
					txtIPLocation.setHint(getActivity().getResources().getString(R.string.Hint_Saved_locations)); 
				}
			}
		}
	}
	
	private void getCategorySkillAndQuestion(final String categoryId) {
		new AsyncTask<Void, Void, NetworkResponse>() {
			@Override
			protected void onPreExecute() {
				Util.showProDialog(view.getContext());
			};

			@Override
			protected NetworkResponse doInBackground(Void... params) {
				NetworkResponse response = null;
				try {
					if (Util.isDeviceOnline(view.getContext())) {
						response = database.syncCategorySkillAndQuestion(categoryId);
					}
					database.getCategoryQuestion(categoryId);
					database.getCategorySkill(categoryId);
					if(projectType.equals(Constants.INPERSON) && 
							AppGlobals.defaultWLocation == null){
						database.getWorkLocationList(); 
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return response;
			}

			@Override
			protected void onPostExecute(NetworkResponse result) {
				super.onPostExecute(result);
				Util.dimissProDialog();
				if (result != null) {
					if (!result.isSuccess()) {
						Util.showCenteredToast(view.getContext(),result.getErrMsg());
					}
				}
				setDefaultWLocation();
			}
		}.execute();
	}
	
	//------------------Selected Country List Handling------------------------
	
	private void showCountryListDialog(){
		CountryListDialog dialog = CountryListDialog.getInstance(countries,new IListItemClickListener() {
			@Override
			public void onItemSelected(Object item, int position) {
				getSelectedCountries(item);
			}
		});
		dialog.show(getFragmentManager(), "country_list"); 
	}
	
	@SuppressWarnings("unchecked")
	private void getSelectedCountries(Object object){
		try{
			countries.clear();
			ArrayList<Country> temp= (ArrayList<Country>)object;
			for (Country country : temp) {
				countries.add(country);
			}
			setSelectedCountryListData();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void setSelectedCountryListData(){
		if(countries.size() > 0){
			SelectedCountryListAdapter sCListAdapter = new SelectedCountryListAdapter(getActivity(), R.layout.ed_ddata_detail_row,countries);
			setVPLocationSelction(R.id.rdCountry);
			sCListAdapter.setNotifyOnChange(true); 
			listVDDetailsCountry.setAdapter(sCListAdapter); 
			listVDDetailsCountry.setVisibility(View.VISIBLE);
			sCListAdapter.setDataChangeListener(new IDataChangedListener() {
				@Override
				public void onDataChanged() {
					setCListLayoutParams();
				}
			});
			setCListLayoutParams();
		}else{
			listVDDetailsCountry.setVisibility(View.GONE);
		}
	}
	
	private void setCListLayoutParams(){
		if(countries.size()<=3){
			listVDDetailsCountry.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT)); 
		}else{
			listVDDetailsCountry.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,165)); 
		}
	}
	
	//------------------Selected Skill List Handling------------------------
	
	private void showSkillListDialog(){
		if(AppGlobals.categorySkills.size() > 0){
			SkillListDialog dialog = SkillListDialog.getInstance(skills,new IListItemClickListener() {
				@Override
				public void onItemSelected(Object item, int position) {
					getSelectedSkills(item);
				}
			});
			dialog.show(getFragmentManager(), "skill_list"); 
		}else{
			Util.showCenteredToast(getActivity(), getResources().getString(R.string.msg_noSkillsFound));   
		}
	}
	
	@SuppressWarnings("unchecked")
	private void getSelectedSkills(Object object){
		try{
			skills.clear();
			ArrayList<Skill> temp= (ArrayList<Skill>)object;
			for (Skill skill : temp) {
				skills.add(skill);
			}
			setSelectedSkillListData();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void setSelectedSkillListData(){
		if(skills.size()>0){
			SelectedSkillListAdapter sSListAdapter = new SelectedSkillListAdapter(getActivity(), R.layout.ed_ddata_detail_row,skills);
			sSListAdapter.setNotifyOnChange(true);
			listVDDetailsSkills.setAdapter(sSListAdapter); 
			listVDDetailsSkills.setVisibility(View.VISIBLE);
			sSListAdapter.setDataChangeListener(new IDataChangedListener() {
				@Override
				public void onDataChanged() {
					setSListLayoutParams();
				}
			});
			setSListLayoutParams();
		}else{
			listVDDetailsSkills.setVisibility(View.GONE);
		}
	}
	
	private void setSListLayoutParams(){
		if(skills.size()<=3){
			listVDDetailsSkills.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT)); 
		}else{
			listVDDetailsSkills.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,165)); 
		}
	}
	
	//---------------------Selection Question Handling--------------------
	
	private void showQuestionListDialog(){
		if (AppGlobals.categoryQuestions.size() > 0) {
			QuestionListDialog dialog = QuestionListDialog.getInstance(questions, new IListItemClickListener() {
				@Override
				public void onItemSelected(Object item, int position) {
					getSelectedQuestion(item);
				}
			});
			dialog.show(getFragmentManager(), "question__list"); 
		} else {

			Util.showCenteredToast(getActivity(),getResources().getString(R.string.msg_noQuestionsFound));
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void getSelectedQuestion(Object item) {
		try{
			questions.clear();
			ArrayList<Question> temp= (ArrayList<Question>)item;
			for (Question question : temp) {
				questions.add(question);
			}
			setSelectedQuestionListData();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	private void setSelectedQuestionListData() {
		if(questions.size()>0){
			SelectedQuestionListAdapter qListAdapter = new SelectedQuestionListAdapter(getActivity(), R.layout.ed_ddata_detail_row,questions);
			qListAdapter.setNotifyOnChange(true);
			listVDDetailsQuestion.setAdapter(qListAdapter); 
			listVDDetailsQuestion.setVisibility(View.VISIBLE);
		}else{
			listVDDetailsQuestion.setVisibility(View.GONE);
		}
	}
	
	private void showWLocationListDialog(){
		WorkLocationListDialog dialog = WorkLocationListDialog.getInstance(new IListItemClickListener() {
			@Override
			public void onItemSelected(Object item, int position) {
				resetDefaultWLocation();
				AppGlobals.workLocations.get(position).is_default_location = "1";
				WorkLocation location = AppGlobals.workLocations.get(position);
				txtIPLocation.setText(location.location_name);
				AppGlobals.defaultWLocation = location;
			}
		});
		dialog.show(getFragmentManager(), "wl_list");
	}
	
	private void resetDefaultWLocation(){
		for (int i = 0; i < AppGlobals.workLocations.size(); i++) {
			AppGlobals.workLocations.get(i).is_default_location = "0";
		}
	}
	
	//------------------------------------
	public static DoerDetailsDialog getInstance(ArrayList<Country> countries,ArrayList<Skill> skills,ArrayList<Question> questions,Long categoryId,String projectType) {
		DoerDetailsDialog fragment;
		fragment = new DoerDetailsDialog();
		fragment.countries = countries;
		fragment.skills = skills;
		fragment.questions = questions;
		fragment.categotyId = String.valueOf(categoryId);
		fragment.projectType = projectType;
		return fragment;
	}
}
