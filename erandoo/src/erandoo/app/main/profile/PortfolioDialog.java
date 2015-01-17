package erandoo.app.main.profile;

import java.util.ArrayList;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.adapters.RatingCategoryAdapter;
import erandoo.app.adapters.ViewPagerAdapter;
import erandoo.app.custom.CustomDialog;
import erandoo.app.database.Category;
import erandoo.app.database.Portfolio;
import erandoo.app.database.Profile;
import erandoo.app.database.Project;
import erandoo.app.utilities.Constants;

public class PortfolioDialog extends CustomDialog implements OnClickListener{
	private TextView txtHLeft;
	private TextView txtHTitle;
	
	private View view;
	private TextView txtPoSelectCategory;
	
	//Custom Dialog Components.
	private LinearLayout llSListSearchBar;
	private TextView txtSimpleListTitle;
	private ImageButton imgBtnSimpleListBack;
	private ListView listVSimpleList;
	private Dialog customDialog;
	
	private RatingCategoryAdapter ratingCategoryAdapter;
	private Category category;
	private Portfolio portfolio;
	
	//lokesh
	private ViewPager viewPager;
	private TextView txtChangeImage;
	private ViewPagerAdapter adapter;
	
	private ArrayList<Project> arrayList = new ArrayList<Project>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.ed_user_profile_portfolio, container, false);
		portfolio = (Portfolio) getArguments().getSerializable(Constants.SERIALIZABLE_DATA);
		
		if (!portfolio.category.get(0).category_name.equals(Profile.ALL_CATEGORY)) {
			Category cate = new Category();
			cate.category_name = Profile.ALL_CATEGORY;
			portfolio.category.add(0, cate);
		}
		
		initialize();
		return view;
	}

	private void initialize() {
		txtHLeft = (TextView) view.findViewById(R.id.txtHLeft);
		txtHTitle = (TextView) view.findViewById(R.id.txtHTitle);
		
		txtPoSelectCategory = (TextView) view.findViewById(R.id.txtPoSelectCategory);
		
		viewPager = (ViewPager) view.findViewById(R.id.viewPager);
		txtChangeImage = (TextView) view.findViewById(R.id.txtChangeImage);
		
		txtPoSelectCategory.setOnClickListener(this);
		txtHLeft.setOnClickListener(this);
		
		txtPoSelectCategory.setText(Profile.ALL_CATEGORY);
		
		setHeaderView();
		
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {}
			
			@Override
			public void onPageScrolled(int position, float arg1, int arg2) {
				try {
					if (arrayList.size() > 0) {
						txtChangeImage.setText(arrayList.get(position).title);
					}else{
						txtChangeImage.setText(portfolio.project.get(position).title);
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {}
		});
		
		if (arrayList.size() == 0) {
			adapter = new ViewPagerAdapter(getActivity(),  portfolio.project);
			viewPager.setAdapter(adapter);
		}
	}

	private void setHeaderView() {
		// TODO Auto-generated method stub
		txtHTitle.setText(getResources().getString(R.string.portfolio));
		Drawable imgHLeft = getResources().getDrawable(R.drawable.ic_back);
		txtHLeft.setCompoundDrawablesWithIntrinsicBounds(imgHLeft, null, null, null);
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.txtHLeft) {
			dismiss();
			
		}else if(view.getId() == R.id.txtPoSelectCategory){
			customDialog();
			
		}
	}
	
	private void customDialog(){
		customDialog = new Dialog(getActivity());
		customDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		customDialog.setContentView(R.layout.ed_simple_list);
		
		llSListSearchBar = (LinearLayout) customDialog.findViewById(R.id.llSListSearchBar);
		txtSimpleListTitle = (TextView) customDialog.findViewById(R.id.txtSimpleListTitle);
		imgBtnSimpleListBack = (ImageButton) customDialog.findViewById(R.id.imgBtnSimpleListBack);
		listVSimpleList = (ListView) customDialog.findViewById(R.id.listVSimpleList);
		
		txtSimpleListTitle.setText(getResources().getString(R.string.select_category));
		ratingCategoryAdapter = new RatingCategoryAdapter(getActivity(), R.layout.ed_simple_list_row, portfolio.category);
		listVSimpleList.setAdapter(ratingCategoryAdapter);
		
		listVSimpleList.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				category = new Category();
				category = (Category) adapter.getItemAtPosition(position);
				txtPoSelectCategory.setText(""+category.category_name);
				slideProjectImages(category.category_id);
				customDialog.dismiss();
				
			}
		});
		
		imgBtnSimpleListBack.setVisibility(View.GONE);
		llSListSearchBar.setVisibility(View.GONE);
		
		customDialog.setCancelable(true);
		customDialog.show();
	}
	
	private void slideProjectImages(Long category_id){
		if (category.category_name.equals(Profile.ALL_CATEGORY)) {
			arrayList.clear();
			adapter = new ViewPagerAdapter(getActivity(),  portfolio.project);
		}else{
			arrayList = new ArrayList<Project>();
			for (int i = 0; i < portfolio.project.size(); i++) {
				if (portfolio.project.get(i).category_id.equals(category_id)) {
					arrayList.add(portfolio.project.get(i));
				}
			}
			adapter = new ViewPagerAdapter(getActivity(),  arrayList);
		}
		viewPager.setAdapter(adapter);
	}
}
