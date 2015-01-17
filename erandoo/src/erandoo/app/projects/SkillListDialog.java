package erandoo.app.projects;

import java.util.ArrayList;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.adapters.SkillListAdapter;
import erandoo.app.custom.CustomDialog;
import erandoo.app.custom.IListItemClickListener;
import erandoo.app.database.Skill;
import erandoo.app.main.AppGlobals;
import erandoo.app.utilities.Util;

public class SkillListDialog extends CustomDialog implements OnClickListener, TextWatcher{
	private View view;
	private ImageButton imgBtnSimpleListBack;
	private ListView listVSimpleList;
	private EditText eTextSimpleListSearch;
	private TextView txtSimpleListTitle;
	private ImageButton imgBtnSListClearSearch;
	private TextView txtSListDone;
	private SkillListAdapter adapter;
	private IListItemClickListener target;
	private ArrayList<Skill> selectedSkills;
	private int textlength = 0;
	private ArrayList<Skill> teamList;
	private ArrayList<Skill> searchedList = new ArrayList<Skill>(0);
	private boolean isSearch = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.ed_simple_list, container, false);
		initialize();
		return view;
	}

	private void initialize() {
		getDialog().getWindow().getAttributes().windowAnimations = R.style.AppDialogFadeAnimation;
		listVSimpleList = (ListView) view.findViewById(R.id.listVSimpleList);
		imgBtnSimpleListBack = (ImageButton) view.findViewById(R.id.imgBtnSimpleListBack);
		eTextSimpleListSearch = (EditText) view.findViewById(R.id.eTextSimpleListSearch);
		txtSimpleListTitle = (TextView) view.findViewById(R.id.txtSimpleListTitle);
		txtSListDone = (TextView) view.findViewById(R.id.txtSListDone);
		imgBtnSListClearSearch = (ImageButton) view.findViewById(R.id.imgBtnSListClearSearch);
		
		txtSimpleListTitle.setText(view.getContext().getResources().getString(R.string.Skills));  
		txtSListDone.setVisibility(View.VISIBLE);
		
		imgBtnSimpleListBack.setOnClickListener(this);
		imgBtnSListClearSearch.setOnClickListener(this);
		txtSListDone.setOnClickListener(this);
		
		listVSimpleList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				if(isSearch){
					if(searchedList.get(position).isSelected){
						searchedList.get(position).isSelected = false; 
					}else{
						searchedList.get(position).isSelected = true; 
					}
				}else{
					if(AppGlobals.categorySkills.get(position).isSelected){
						AppGlobals.categorySkills.get(position).isSelected = false; 
					}else{
						AppGlobals.categorySkills.get(position).isSelected = true; 
					}
				}
				adapter.notifyDataSetChanged();
			}
		});

		if(AppGlobals.categorySkills.size() > 0){
			setSkillListData();
		}else{
			Util.showCenteredToast(getActivity(), getResources().getString(R.string.msg_noSkillsFound));   
		}
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.imgBtnSimpleListBack) {
			dismissDialog();
		}else if (v.getId() == R.id.imgBtnSListClearSearch) {
			eTextSimpleListSearch.setText("");
		}else if(v.getId() == R.id.txtSListDone){
			target.onItemSelected(getSelectedSkills(),0);
			dismissDialog();
		}
	}
	
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		teamList = AppGlobals.categorySkills; 
		try{
			textlength = eTextSimpleListSearch.getText().toString().length();
			if(textlength>0){
				searchedList.clear();
	            for (int i = 0; i < teamList.size(); i++){
	            	if(teamList.get(i).skill_desc!= null){
	      			   if(teamList.get(i).skill_desc.toString().toLowerCase().contains(((CharSequence) eTextSimpleListSearch.getText().toString().toLowerCase().subSequence(0, textlength)))){
	     				   searchedList.add(teamList.get(i));
	          		   }
	            	}
	            }
	            isSearch = true;
	        }else{
				isSearch = false;
			}
			setSkillListData();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setSkillListData() {
		clearSelection();
		if(isSearch){
			adapter = new SkillListAdapter(getActivity(),R.layout.ed_simple_list_row, searchedList,selectedSkills);
		}else{
			adapter = new SkillListAdapter(getActivity(),R.layout.ed_simple_list_row, AppGlobals.categorySkills,selectedSkills);
		}
		adapter.setNotifyOnChange(true); 
		listVSimpleList.setAdapter(adapter);
	}
	
	private ArrayList<Skill> getSelectedSkills(){
		ArrayList<Skill> arrayList = new ArrayList<Skill>(0);
		if(isSearch){
			for (Skill skill: searchedList) {
				if(skill.isSelected){
					arrayList.add(skill);
				}
			}
		}else{
			for (Skill skill: AppGlobals.categorySkills) {
				if(skill.isSelected){
					arrayList.add(skill);
				}
			}
		}
		return arrayList;
	}
	
	private void clearSelection(){
		if(AppGlobals.categorySkills.size()>0 || searchedList.size() >0){
			if(isSearch){
				for (int i = 0; i < searchedList.size(); i++) {
					searchedList.get(i).isSelected = false; 
				}
			}else{
				for (int i = 0; i < AppGlobals.categorySkills.size(); i++) {
					AppGlobals.categorySkills.get(i).isSelected = false; 
				}
			}
		}
	}
	
	private void dismissDialog() {
		Util.hideKeypad(view.getContext(), eTextSimpleListSearch);
		SkillListDialog.this.dismiss();
	}
	
	// --------------------------CUSTOM EVENT
	
	public static SkillListDialog getInstance(ArrayList<Skill> selectedSkills, final IListItemClickListener target) {
		SkillListDialog fragment;
		fragment = new SkillListDialog();
		fragment.selectedSkills = selectedSkills;
		fragment.setTarget(target);
		return fragment;
	}

	public void setTarget(IListItemClickListener target) {
		this.target = target;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,int after) {}

	@Override
	public void afterTextChanged(Editable s) {}
}
