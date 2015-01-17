package erandoo.app.main;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import erandoo.app.R;
import erandoo.app.custom.AppHeaderView;
import erandoo.app.custom.BaseFragActivity;
import erandoo.app.projects.FragInviteDoers;
import erandoo.app.utilities.Util;

public class InviteDoersActivity extends BaseFragActivity implements OnClickListener{

	private Button tabBtnInDoerFeatured;
	private Button tabBtnInDoerPrevious;
	private Button tabBtnInDoerFavorite;
	private Button tabBtnInDoerSearch;
	
	public static final String TAB_FEATURED = "account_type";
	public static final String TAB_PREVIOUS = "previously_worked";
	public static final String TAB_FAVORITE= "bookmark_subtype";
	public static final String TAB_SEARCH = "search";
	
	private Button currSelectTab;
	private FragInviteDoers fragInviteDoers;

	private AppHeaderView appHeaderView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ed_cp_invite_doers); 
		
		initialize();
	}
	
	private void initialize() {
		
		tabBtnInDoerFeatured = (Button)findViewById(R.id.tabBtnInDoerFeatured);
		tabBtnInDoerPrevious = (Button)findViewById(R.id.tabBtnInDoerPrevious);
		tabBtnInDoerFavorite = (Button)findViewById(R.id.tabBtnInDoerFavorite);
		tabBtnInDoerSearch = (Button)findViewById(R.id.tabBtnInDoerSearch);
		currSelectTab = tabBtnInDoerSearch;
		
		setHeaderView();
		
		tabBtnInDoerFeatured.setOnClickListener(this);
		tabBtnInDoerPrevious.setOnClickListener(this);
		tabBtnInDoerFavorite.setOnClickListener(this);
		tabBtnInDoerSearch.setOnClickListener(this);
		
		setTabView(CreateProjectActivity.invitedDoersType);
	}
	
	private void setHeaderView() {
		appHeaderView = (AppHeaderView) findViewById(R.id.appHeaderView);
		appHeaderView.setHeaderContent(getResources().getDrawable(R.drawable.ic_back), getResources()
						.getString(R.string.Invite_Doers),null, null, Gravity.LEFT);
	}

	@Override
	public void onClick(View v) {
		int vId = v.getId();
		if(vId == R.id.tabBtnInDoerFeatured){
			AppGlobals.invitedDoers.clear();
			setTabView(TAB_FEATURED);
		}else if(vId == R.id.tabBtnInDoerPrevious){
			AppGlobals.invitedDoers.clear();
			setTabView(TAB_PREVIOUS);
		}else if(vId == R.id.tabBtnInDoerFavorite){
			AppGlobals.invitedDoers.clear();
			setTabView(TAB_FAVORITE);
		}else if(vId == R.id.tabBtnInDoerSearch){
			AppGlobals.invitedDoers.clear();
			setTabView(TAB_SEARCH);
		}else if(vId == appHeaderView.txtHLeft.getId()){
			finishActivity();
		}
		
	}
	
	private void setTabView(String viewType){
		CreateProjectActivity.invitedDoersType = viewType;
		Util.unloadFragment(this, fragInviteDoers);
		fragInviteDoers = new FragInviteDoers();
		Util.loadFragment(this, R.id.flFragContainer, fragInviteDoers, false); 
		if(viewType == TAB_FEATURED){
			changeTabColor(tabBtnInDoerFeatured);
			setTabSelector(tabBtnInDoerFeatured,false);
			setTabSelector(tabBtnInDoerPrevious,true);
			setTabSelector(tabBtnInDoerFavorite,true);
			setTabSelector(tabBtnInDoerSearch,true);
		}else if(viewType == TAB_PREVIOUS){
			changeTabColor(tabBtnInDoerPrevious);
			setTabSelector(tabBtnInDoerFeatured,true);
			setTabSelector(tabBtnInDoerPrevious,false);
			setTabSelector(tabBtnInDoerFavorite,true);
			setTabSelector(tabBtnInDoerSearch,true);
		}else if(viewType == TAB_FAVORITE){
			changeTabColor(tabBtnInDoerFavorite);
			setTabSelector(tabBtnInDoerFeatured,true);
			setTabSelector(tabBtnInDoerPrevious,true);
			setTabSelector(tabBtnInDoerFavorite,false);
			setTabSelector(tabBtnInDoerSearch,true);
		}else if(viewType == TAB_SEARCH){
			changeTabColor(tabBtnInDoerSearch);
			setTabSelector(tabBtnInDoerFeatured,true);
			setTabSelector(tabBtnInDoerPrevious,true);
			setTabSelector(tabBtnInDoerFavorite,true);
			setTabSelector(tabBtnInDoerSearch,false);
		}
	}
	
	private void setTabSelector(Button tab,boolean isEnable){
		tab.setEnabled(isEnable);
	}
	
	private void changeTabColor(Button tab) {
		currSelectTab.setSelected(false);
		tab.setSelected(true);
		currSelectTab = tab;
	}
	
	@Override
	public void onBackPressed() {
		finishActivity();
	}
	
	private void finishActivity() {
//		/*if(isClearSelection){
//			if(fragInviteDoers != null){
//				fragInviteDoers.clearSelectedUserIds();
//			}
//		}*/
		InviteDoersActivity.this.finish();
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}
}
