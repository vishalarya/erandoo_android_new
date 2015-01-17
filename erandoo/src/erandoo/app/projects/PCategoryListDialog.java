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
import erandoo.app.adapters.CategoryListAdapter;
import erandoo.app.custom.CustomDialog;
import erandoo.app.custom.IListItemClickListener;
import erandoo.app.database.Category;
import erandoo.app.utilities.Util;

public class PCategoryListDialog extends CustomDialog implements OnClickListener, TextWatcher{
	private View view;
	private ImageButton imgBtnSimpleListBack;
	private ListView listVSimpleList;
	private EditText eTextSimpleListSearch;
	private TextView txtSimpleListTitle;
	private ImageButton imgBtnSListClearSearch;
	private IListItemClickListener target;
	private static ArrayList<Category> categoryList; 
	private CategoryListAdapter adapter;
	private int textlength = 0;
	private ArrayList<Category> teamList;
	private ArrayList<Category> searchedList = new ArrayList<Category>(0);
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.ed_simple_list,container, false);
		initialize();
		return view;
	}
	
	private void initialize() {
		getDialog().getWindow().getAttributes().windowAnimations = R.style.AppDialogSlideAnimation;
		
		listVSimpleList = (ListView)view.findViewById(R.id.listVSimpleList);
		imgBtnSimpleListBack = (ImageButton)view.findViewById(R.id.imgBtnSimpleListBack);
		eTextSimpleListSearch = (EditText)view.findViewById(R.id.eTextSimpleListSearch);
		txtSimpleListTitle = (TextView)view.findViewById(R.id.txtSimpleListTitle);
		imgBtnSListClearSearch = (ImageButton)view.findViewById(R.id.imgBtnSListClearSearch);
		
		txtSimpleListTitle.setText(view.getContext().getResources().getString(R.string.Project_Category)); 
		
		imgBtnSimpleListBack.setOnClickListener(this);
		imgBtnSListClearSearch.setOnClickListener(this);
		eTextSimpleListSearch.addTextChangedListener(this); 
		
		setCategoryListData(false);
		
		listVSimpleList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view,int position, long id) {
				Category category = (Category)adapter.getItemAtPosition(position);
				target.onItemSelected(category, categoryList.indexOf(category)); 
				dismissDialog();
			}
		});
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.imgBtnSimpleListBack){
			dismissDialog();
		}else if(v.getId() == R.id.imgBtnSListClearSearch){
			eTextSimpleListSearch.setText(""); 
		}
	}
	
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		teamList = categoryList; 
		try{
			textlength = eTextSimpleListSearch.getText().toString().length();
			if(textlength>0){
				searchedList.clear();
	            for (int i = 0; i < teamList.size(); i++){
	            	if(teamList.get(i).category_name!= null){
	      			   if(teamList.get(i).category_name.toString().toLowerCase().contains(((CharSequence) eTextSimpleListSearch.getText().toString().toLowerCase().subSequence(0, textlength)))){
	     				   searchedList.add(teamList.get(i));
	          		   }
	            	}
	            }
	            setCategoryListData(true);
	        }else{
	        	setCategoryListData(false);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setCategoryListData(boolean isSearch){
		if(isSearch){
			adapter = new CategoryListAdapter(getActivity(), R.layout.ed_pcategory_row, searchedList);
		}else{
			adapter = new CategoryListAdapter(getActivity(), R.layout.ed_pcategory_row, categoryList);
		}
		listVSimpleList.setAdapter(adapter);
	}
	
	private void dismissDialog(){
		Util.hideKeypad(view.getContext(), eTextSimpleListSearch);
		PCategoryListDialog.this.dismiss();
	}
	
	//--------------------------CUSTOM EVENT HANDLING-----------------------------------------
	public static PCategoryListDialog getInstance(final IListItemClickListener target,ArrayList<Category> listData) {
		PCategoryListDialog fragment;
		categoryList = listData; 
        fragment = new PCategoryListDialog();
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
