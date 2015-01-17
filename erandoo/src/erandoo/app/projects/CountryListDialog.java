package erandoo.app.projects;

import java.util.ArrayList;

import erandoo.app.R;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import erandoo.app.adapters.CountryListAdapter;
import erandoo.app.custom.CustomDialog;
import erandoo.app.custom.IListItemClickListener;
import erandoo.app.database.Country;
import erandoo.app.main.AppGlobals;
import erandoo.app.utilities.Util;

public class CountryListDialog extends CustomDialog implements OnClickListener, TextWatcher{
	private View view;
	private ImageButton imgBtnSimpleListBack;
	private ListView listVSimpleList;
	private EditText eTextSimpleListSearch;
	private TextView txtSimpleListTitle;
	private ImageButton imgBtnSListClearSearch;
	private TextView txtSListDone;
	private CountryListAdapter adapter;
	private ArrayList<Country> selectedCountries;
	private IListItemClickListener target;
	private int textlength = 0;
	private ArrayList<Country> teamList;
	private ArrayList<Country> searchedList = new ArrayList<Country>(0);
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

		txtSimpleListTitle.setText(view.getContext().getResources().getString(R.string.Countries));  
		txtSListDone.setVisibility(View.VISIBLE);
		
		imgBtnSimpleListBack.setOnClickListener(this);
		imgBtnSListClearSearch.setOnClickListener(this);
		txtSListDone.setOnClickListener(this);
		eTextSimpleListSearch.addTextChangedListener(this);
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
					if(AppGlobals.countries.get(position).isSelected){
						AppGlobals.countries.get(position).isSelected = false; 
					}else{
						AppGlobals.countries.get(position).isSelected = true; 
					}
				}
				adapter.notifyDataSetChanged();
			}
		});

		if(AppGlobals.countries.size() > 0){
			setCountryListData();
		}else{
			Util.showCenteredToast(getActivity(), getResources().getString(R.string.msg_Data_not_available));  
		}
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.imgBtnSimpleListBack) {
			dismissDialog();
		}else if (v.getId() == R.id.imgBtnSListClearSearch) {
			eTextSimpleListSearch.setText("");
		}else if(v.getId() == R.id.txtSListDone){
			target.onItemSelected(getSelectedCountries(),0);
			dismissDialog();
		}
	}
	
	private ArrayList<Country> getSelectedCountries(){
		ArrayList<Country> arrayList = new ArrayList<Country>(0);
		if(isSearch){
			for (Country country: searchedList) {
				if(country.isSelected){
					arrayList.add(country);
				}
			}
		}else{
			for (Country country: AppGlobals.countries) {
				if(country.isSelected){
					arrayList.add(country);
				}
			}
		}
		return arrayList;
	}

	private void dismissDialog() {
		Util.hideKeypad(view.getContext(), eTextSimpleListSearch);
		CountryListDialog.this.dismiss();
	}
	
	private void clearSelection(){
		if(AppGlobals.countries.size()>0 || searchedList.size() >0){
			if(isSearch){
				for (int i = 0; i < searchedList.size(); i++) {
					searchedList.get(i).isSelected = false; 
				}
			}else{
				for (int i = 0; i < AppGlobals.countries.size(); i++) {
					AppGlobals.countries.get(i).isSelected = false; 
				}
			}
		}
	}

	// --------------------------CUSTOM EVENT
	public static CountryListDialog getInstance(ArrayList<Country> selectedCountries,final IListItemClickListener target) {
		CountryListDialog fragment;
		fragment = new CountryListDialog();
		fragment.selectedCountries = selectedCountries;
		fragment.setTarget(target);
		return fragment;
	}

	public void setTarget(IListItemClickListener target) {
		this.target = target;
	}
	
	
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		teamList = AppGlobals.countries; 
		try{
			textlength = eTextSimpleListSearch.getText().toString().length();
			if(textlength>0){
				searchedList.clear();
	            for (int i = 0; i < teamList.size(); i++){
	            	if(teamList.get(i).country_name!= null){
	      			   if(teamList.get(i).country_name.toString().toLowerCase().contains(((CharSequence) eTextSimpleListSearch.getText().toString().toLowerCase().subSequence(0, textlength)))){
	     				   searchedList.add(teamList.get(i));
	          		   }
	            	}
	            }
	            isSearch = true;
	        }else{
				isSearch = false;
			}
			setCountryListData();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setCountryListData() {
		clearSelection();
		if(isSearch){
			adapter = new CountryListAdapter(getActivity(),R.layout.ed_simple_list_row, searchedList,selectedCountries);
		}else{
			adapter = new CountryListAdapter(getActivity(),R.layout.ed_simple_list_row, AppGlobals.countries,selectedCountries);
		}
		adapter.setNotifyOnChange(true); 
		listVSimpleList.setAdapter(adapter);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,int after) {}

	@Override
	public void afterTextChanged(Editable s) {}
}
