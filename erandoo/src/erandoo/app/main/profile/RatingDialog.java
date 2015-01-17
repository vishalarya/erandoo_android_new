package erandoo.app.main.profile;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.RatingBar;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.adapters.RatingAdapter;
import erandoo.app.adapters.RatingCategoryAdapter;
import erandoo.app.cmds.Cmd;
import erandoo.app.cmds.CmdFactory;
import erandoo.app.config.Config;
import erandoo.app.custom.CustomDialog;
import erandoo.app.database.Category;
import erandoo.app.database.Profile;
import erandoo.app.network.NetworkMgr;
import erandoo.app.network.NetworkResponse;
import erandoo.app.tempclasses.PRCData;
import erandoo.app.tempclasses.ProfileRatingData;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.Util;

public class RatingDialog extends CustomDialog implements OnClickListener{
	private TextView txtHLeft;
	private TextView txtHTitle;
	
	private View view;
	private RatingBar rtbOverallRating;
	private ListView lvRating;
	private TextView txtSelectCategory;
	
	private RatingAdapter ratingAdapter;
	private ProfileRatingData rating;
	private ProfileRatingData profileRatingData;
	private RatingCategoryAdapter ratingCategoryAdapter;
	private Category category;
	
	//Custom Dialog Components.
	private LinearLayout llSListSearchBar;
	private TextView txtSimpleListTitle;
	private ImageButton imgBtnSimpleListBack;
	private ListView listVSimpleList;
	private Dialog customDialog;
	
	private String user_id;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.ed_user_profile_rating, container, false);
		user_id = getArguments().getString("User_id");
		rating = (ProfileRatingData) getArguments().getSerializable(Constants.SERIALIZABLE_DATA);
		
		if (rating.rating_category.size() > 0 && !rating.rating_category.get(0).category_name.equals(Profile.ALL_CATEGORY)) {
			Category cate = new Category();
			cate.category_name = Profile.ALL_CATEGORY;
			rating.rating_category.add(0, cate);
		}
		
		initialize();
		return view;
	}
	
	private void initialize() {
		txtHLeft = (TextView) view.findViewById(R.id.txtHLeft);
		txtHTitle = (TextView) view.findViewById(R.id.txtHTitle);
		
		lvRating = (ListView) view.findViewById(R.id.lvRating);
		rtbOverallRating = (RatingBar) view.findViewById(R.id.rtbOverallRating);
		txtSelectCategory = (TextView) view.findViewById(R.id.txtSelectCategory);
		
		setHeaderView();
		txtSelectCategory.setOnClickListener(this);
		txtHLeft.setOnClickListener(this);
		
		setRatingData(rating);
		txtSelectCategory.setText(Profile.ALL_CATEGORY);
	}

	
	private void setHeaderView() {
		txtHTitle.setText(getResources().getString(R.string.rating));
		Drawable imgHLeft = getResources().getDrawable(R.drawable.ic_back);
		txtHLeft.setCompoundDrawablesWithIntrinsicBounds(imgHLeft, null, null, null);
	}


	//----------------TASK TO GET DATA ACCORDING TO SELECTED RATING CATEGORY----------------------//
	private class ProfileDataTask extends AsyncTask<String, Void, NetworkResponse> {
		
		@Override
		protected void onPreExecute() {
			Util.showProDialog(getActivity());
		}

		@Override
		protected NetworkResponse doInBackground(String... params) {
			NetworkResponse response = null; 
			if (Util.isDeviceOnline(getActivity())) {
				Cmd cmd = CmdFactory.createGetProfileRatingCmd();
				cmd.addData(Profile.OPERATION, Profile.OPERATION_LIST_CATEGORY);
				cmd.addData(Profile.FLD_CATEGORY_ID, category.category_id);
				cmd.addData(Profile.FLD_RATING_OF_USER_ID,  user_id);
				cmd.addData(Profile.FLD_TRNO, 0);
				response = NetworkMgr.httpPost(Config.API_URL, cmd.getJsonData());
				if (response != null) {
					if (response.isSuccess()) {
						getRatingData(response);
					}
				}
			}
			return response;
		}

		@Override
		protected void onPostExecute(NetworkResponse networkResponse) {
			Util.dimissProDialog();
			if (networkResponse != null) {
				if (!networkResponse.isSuccess()) {
					Util.showCenteredToast(getActivity(), networkResponse.getErrMsg());
				}else{
					setRatingData(profileRatingData);
				}
			}
		}
	}
	
	private void getRatingData(NetworkResponse response){
		PRCData prcData = (PRCData) Util.getJsonToClassObject(response.getJsonObject().toString(), PRCData.class);
		profileRatingData = prcData.data;
	}
	
	private void setRatingData(ProfileRatingData prdata){
		if (prdata.category_rating.size() > 0) {
			ratingAdapter = new RatingAdapter(getActivity(), R.layout.ed_rating_row, prdata.category_rating,false,false);
			lvRating.setAdapter(ratingAdapter);
			rtbOverallRating.setRating(Float.parseFloat(prdata.overall_rating));
		}
		
		if (rating.rating_category.size() > 0) {
			txtSelectCategory.setVisibility(View.VISIBLE);
		}else{
			txtSelectCategory.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.txtHLeft) {
			dismiss();
		}else if (view.getId() == R.id.txtSelectCategory) {
			if(Util.isDeviceOnline(getActivity())){
				customDialog();
			}else{
				Util.showCenteredToast(getActivity(), getResources().getString(R.string.msg_Connect_internet));
			}
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
		
		ratingCategoryAdapter = new RatingCategoryAdapter(getActivity(), R.layout.ed_simple_list_row, rating.rating_category);
		listVSimpleList.setAdapter(ratingCategoryAdapter);
		
		listVSimpleList.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				category = new Category();
				category = (Category) adapter.getItemAtPosition(position);
				if (category.category_name.equals(Profile.ALL_CATEGORY)) {
					setRatingData(rating);
					customDialog.dismiss();
				}else{
					txtSelectCategory.setText(category.category_name);
					customDialog.dismiss();
					new ProfileDataTask().execute();
				}
			}
		});
		
		imgBtnSimpleListBack.setVisibility(View.GONE);
		llSListSearchBar.setVisibility(View.GONE);
		
		customDialog.setCancelable(true);
		customDialog.show();
	}
}
