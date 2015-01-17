package erandoo.app.projects;

import java.util.ArrayList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.adapters.TemplateListAdapter;
import erandoo.app.custom.CustomDialog;
import erandoo.app.custom.IListItemClickListener;
import erandoo.app.database.Template;
import erandoo.app.utilities.Util;

public class PTemplateListDialog extends CustomDialog implements OnClickListener, TextWatcher{
	private View view;
	private ImageButton imgBtnSimpleListBack;
	private ListView listVSimpleList;
	private EditText eTextSimpleListSearch;
	private TextView txtSimpleListTitle;
	private ImageButton imgBtnSListClearSearch;
	private IListItemClickListener target;
	private static ArrayList<Template> templateList; 
	private TemplateListAdapter adapter;
	private int textlength = 0;
	private ArrayList<Template> teamList;
	private ArrayList<Template> searchedList = new ArrayList<Template>(0);
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.ed_simple_list,container, false);
		initialize();
		return view;
	}
	
	private void initialize() {
		getDialog().getWindow().getAttributes().windowAnimations = R.style.AppDialogSlideAnimation;
		
		imgBtnSimpleListBack = (ImageButton)view.findViewById(R.id.imgBtnSimpleListBack);
		imgBtnSListClearSearch = (ImageButton)view.findViewById(R.id.imgBtnSListClearSearch);
		listVSimpleList = (ListView)view.findViewById(R.id.listVSimpleList);
		eTextSimpleListSearch = (EditText)view.findViewById(R.id.eTextSimpleListSearch);
		txtSimpleListTitle = (TextView)view.findViewById(R.id.txtSimpleListTitle);
		
		txtSimpleListTitle.setText(view.getContext().getResources().getString(R.string.Project_Template)); 
		
		imgBtnSimpleListBack.setOnClickListener(this);
		imgBtnSListClearSearch.setOnClickListener(this); 
		eTextSimpleListSearch.addTextChangedListener(this); 
		
		setTemplateListData(false);
		
		listVSimpleList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view,int position, long id) {
				Template template = (Template)adapter.getItemAtPosition(position);
				target.onItemSelected(template, templateList.indexOf(template)); 
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
		teamList = templateList; 
		try{
			textlength = eTextSimpleListSearch.getText().toString().length();
			if(textlength>0){
				searchedList.clear();
	            for (int i = 0; i < teamList.size(); i++){
	            	if(teamList.get(i).getTitle()!= null){
	      			   if(teamList.get(i).getTitle().toString().toLowerCase().contains(((CharSequence) eTextSimpleListSearch.getText().toString().toLowerCase().subSequence(0, textlength)))){
	     				   searchedList.add(teamList.get(i));
	          		   }
	            	}
	            }
	            setTemplateListData(true);
	        }else{
	        	setTemplateListData(false);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setTemplateListData(boolean isSearch){
		if(isSearch){
			adapter = new TemplateListAdapter(getActivity(), R.layout.ed_simple_list_row, searchedList);
		}else{
			adapter = new TemplateListAdapter(getActivity(), R.layout.ed_simple_list_row, templateList);
		}
		listVSimpleList.setAdapter(adapter);
	}
	
	private void dismissDialog(){
		Util.hideKeypad(view.getContext(),eTextSimpleListSearch); 
		PTemplateListDialog.this.dismiss();
	}
	
	//--------------------------CUSTOM EVENT HANDLING-----------------------------------------
	public static PTemplateListDialog getInstance(final IListItemClickListener target,ArrayList<Template> listData) {
		PTemplateListDialog fragment;
		templateList = listData; 
        fragment = new PTemplateListDialog();
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