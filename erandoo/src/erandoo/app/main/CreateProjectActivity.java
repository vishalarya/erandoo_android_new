package erandoo.app.main;

import erandoo.app.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import erandoo.app.adapters.HListAdapter;
import erandoo.app.custom.AppHeaderView;
import erandoo.app.custom.BaseFragActivity;
import erandoo.app.custom.CTimePickerDialog;
import erandoo.app.custom.CTimePickerDialog.TimerListner;
import erandoo.app.custom.DatePickerDailog;
import erandoo.app.custom.DatePickerDailog.DatePickerListner;
import erandoo.app.custom.HListView;
import erandoo.app.custom.IListItemClickListener;
import erandoo.app.database.Attachment;
import erandoo.app.database.Category;
import erandoo.app.database.Country;
import erandoo.app.database.DatabaseMgr;
import erandoo.app.database.Project;
import erandoo.app.database.Question;
import erandoo.app.database.Skill;
import erandoo.app.database.TableType;
import erandoo.app.database.TaskDoer;
import erandoo.app.database.Template;
import erandoo.app.database.WorkLocation;
import erandoo.app.mqtt.ErandooMqttMessage;
import erandoo.app.network.NetworkResponse;
import erandoo.app.projects.BidDurationDialog;
import erandoo.app.projects.DoerDetailsDialog;
import erandoo.app.projects.HourListDialog;
import erandoo.app.projects.InstantResultDialog;
import erandoo.app.projects.PCategoryListDialog;
import erandoo.app.projects.PTemplateListDialog;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.Util;
import erandoo.app.utilities.LicenseValidation;

public class CreateProjectActivity extends BaseFragActivity implements
		OnClickListener, TextWatcher {
	/** --------------INSTANT PROJECT RELATED-------------------- */
	private TextView txtCPICompletionHours;
	private TextView txtCPICompletionInfo;
	private EditText eTextCPPrice;

	/** --------------VIRTUAL AND INPERSION PROJECT RELATED------- */
	private LinearLayout llCPChooseTemplate;
	private LinearLayout llCPHourPerWeek;
	private LinearLayout llCPBidDuration;
	private EditText eTextCPTitle;
	//private EditText eTextCPMinPrice;
	//private EditText eTextCPMaxPrice;
	private EditText eTextCPExpenses;
	private EditText eTextCPWorkHours;
	private EditText eTextCPDescription;

	private TextView txtCPSwitchBudget;
	private TextView txtCPSelectCompletionDate;
	private TextView txtCPSelectCompletionTime;
	private TextView txtCPSelectBidDuation;
	private TextView txtCPCost;
	private TextView txtCPChooseTemplate;
	private TextView txtCPCategoryName;

	private RadioButton rdCPPublic;

	private CheckBox chkCPIsPremium;
	private CheckBox chkCPIsHighlighted;
	private CheckBox chkCPIsSealProposal;
	/** -------------COMMON ---------------------- */
	private LinearLayout llCPConatiner1;
	private LinearLayout llCPConatiner2;
	private LinearLayout llCPCategory;
	private LinearLayout llCPSubCategory;
	private ScrollView mainScrollCProject;

	private TextView txtCPDoerDetail;
	private TextView txtCPInviteDoer;
	private Button btnCPPost;
	private HListView hListVCPCategory;
	private HListView hListVCPSubCategory;
	private Button btnCPCategory;
	private Button btnCPSubCategory;

	private ArrayList<Category> categories = new ArrayList<Category>(0);
	private ArrayList<Category> subCategories = new ArrayList<Category>(0);
	private ArrayList<Template> templates = new ArrayList<Template>(0);

	private ArrayList<Country> selectedCountries = new ArrayList<Country>(0);
	private ArrayList<Skill> selectedSkills = new ArrayList<Skill>(0);
	private ArrayList<Question> selectedQuestions = new ArrayList<Question>(0);
	public static ArrayList<TaskDoer> selectedDoers = new ArrayList<TaskDoer>(0);

	private HListAdapter categoryHListAdapter;
	private HListAdapter subCategoryHListAdapter;

	private String projectType = "";
	private String categoryType = "";
	private String viewMode = "";
	
	private DatabaseMgr database;
	private static Project project;
	private DatePickerDailog dpDailog;
	private CTimePickerDialog tpDialog;
	private AppHeaderView appHeaderView;
	
	private final String DATA_TYPE_CATEGORY = "category";
	private final String DATA_TYPE_SUB_CATEGORY = "sub-category";
	private final String DATA_TYPE_TEMPLATE = "template";

	public static String invitedDoersType = "search";
	public static boolean invitedDoerSelectionType = false;
	public static int invitedDoerInRange = 100;
//	private static Long mobileRecId = null;
	private LocalBroadcastManager lBroadcastManager;
	private InstantResultDialog iResultDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ed_cp_main);
		projectType = getIntent().getStringExtra(Constants.PROJECT_TYPE);
		viewMode = getIntent().getStringExtra(Constants.VIEW_MODE);
		initialize();
	}

	/** -------------FUNCTION TO INITIALIZE ALL UI VIEWS------------------ */
	private void initialize() {
		if(viewMode.equals(Constants.VIEW_MODE_EDIT)){ 
			project = (Project)getIntent().getSerializableExtra(Constants.SERIALIZABLE_DATA);
		}else{
			project = new Project();
		}
		
		lBroadcastManager = LocalBroadcastManager.getInstance(this);
		/** -----------------COMMON CONTROLS---------------------- */
		llCPConatiner1 = (LinearLayout) findViewById(R.id.llCPConatiner1);
		llCPConatiner2 = (LinearLayout) findViewById(R.id.llCPConatiner2);
		llCPCategory = (LinearLayout) findViewById(R.id.llCPCategory);
		llCPSubCategory = (LinearLayout) findViewById(R.id.llCPSubCategory);

		mainScrollCProject = (ScrollView) findViewById(R.id.mainScrollCProject);

		hListVCPCategory = (HListView) findViewById(R.id.hListVCPCategory);
		hListVCPSubCategory = (HListView) findViewById(R.id.hListVCPSubCategory);

		btnCPCategory = (Button) findViewById(R.id.btnCPCategory);
		btnCPSubCategory = (Button) findViewById(R.id.btnCPSubCategory);


		txtCPDoerDetail = (TextView) findViewById(R.id.txtCPDoerDetail);
		txtCPInviteDoer = (TextView) findViewById(R.id.txtCPInviteDoer);
		txtCPCategoryName = (TextView) findViewById(R.id.txtCPCategoryName);
		btnCPPost = (Button) findViewById(R.id.btnCPPost);

		/** -----------------INSTANT CONTROLS---------------------- */
		txtCPICompletionHours = (TextView) llCPConatiner2.findViewById(R.id.txtCPICompletionHours);
		txtCPICompletionInfo = (TextView) llCPConatiner2.findViewById(R.id.txtCPICompletionInfo);

		/**
		 * -----------------VIRTUAL AND IN-PERSON CONTROLS----------------------
		 */
		llCPChooseTemplate = (LinearLayout) findViewById(R.id.llCPChooseTemplate);
		llCPHourPerWeek = (LinearLayout) findViewById(R.id.llCPHourPerWeek);
		llCPBidDuration = (LinearLayout) findViewById(R.id.llCPBidDuration);

		eTextCPTitle = (EditText) llCPConatiner1.findViewById(R.id.eTextCPTitle);
		//eTextCPMinPrice = (EditText) llCPConatiner1.findViewById(R.id.eTextCPMinPrice);
		//eTextCPMaxPrice = (EditText) llCPConatiner1.findViewById(R.id.eTextCPMaxPrice);
		eTextCPWorkHours = (EditText) llCPConatiner1.findViewById(R.id.eTextCPWorkHours);

		txtCPChooseTemplate = (TextView) llCPConatiner1.findViewById(R.id.txtCPChooseTemplate);
		txtCPSelectCompletionDate = (TextView) llCPConatiner1.findViewById(R.id.txtCPSelectCompletionDate);
		txtCPSelectCompletionTime = (TextView) llCPConatiner1.findViewById(R.id.txtCPSelectCompletionTime);
		txtCPSelectBidDuation = (TextView) llCPConatiner1.findViewById(R.id.txtCPSelectBidDuation);
		txtCPSwitchBudget = (TextView) llCPConatiner1.findViewById(R.id.txtCPSwitchBudget);
		

		rdCPPublic = (RadioButton) llCPConatiner1.findViewById(R.id.rdCPPublic);

		chkCPIsPremium = (CheckBox) llCPConatiner1.findViewById(R.id.chkCPIsPremium);
		chkCPIsHighlighted = (CheckBox) llCPConatiner1.findViewById(R.id.chkCPIsHighlighted);
		chkCPIsSealProposal = (CheckBox) llCPConatiner1.findViewById(R.id.chkCPIsSealProposal);
		setHeaderView();
		
		if(viewMode.equals(Constants.VIEW_MODE_EDIT)){ 
			llCPSubCategory.setVisibility(View.GONE);
			llCPCategory.setVisibility(View.GONE);
			txtCPCategoryName.setVisibility(View.VISIBLE); 
		}else{
			llCPSubCategory.setVisibility(View.GONE);
			txtCPCategoryName.setVisibility(View.GONE); 
		}

		if (projectType.equals(Constants.INSTANT)) {
			eTextCPExpenses = (EditText) llCPConatiner2.findViewById(R.id.eTextCPIExpenses);
			txtCPCost = (TextView) llCPConatiner2.findViewById(R.id.txtCPICost);
			eTextCPDescription = (EditText) llCPConatiner2.findViewById(R.id.eTextCPIDescription);
			eTextCPPrice = (EditText) llCPConatiner2.findViewById(R.id.eTextCPIPrice);
			
			btnCPPost.setText(getResources().getString(R.string.Hire));  
			
			llCPConatiner1.setVisibility(View.GONE);
			llCPConatiner2.setVisibility(View.VISIBLE);
			txtCPDoerDetail.setVisibility(View.GONE);
			categoryType = Category.FLD_IS_INSTANT;
			
			regisLocalBroadcastManager();

		} else {
			//txtCPCost = (TextView) llCPConatiner1.findViewById(R.id.txtCPEstimatedCost);
			eTextCPDescription = (EditText) llCPConatiner1.findViewById(R.id.eTextCPDescription);
			eTextCPExpenses = (EditText) llCPConatiner1.findViewById(R.id.eTextCPExpenses);
			eTextCPPrice = (EditText) llCPConatiner1.findViewById(R.id.eTextCPPrice);
			
			llCPConatiner1.setVisibility(View.VISIBLE);
			llCPConatiner2.setVisibility(View.GONE);
			llCPChooseTemplate.setVisibility(View.GONE);
			if(viewMode.equals(Constants.VIEW_MODE_EDIT)){ 
				txtCPSwitchBudget.setTag(project.payment_mode);
			}else{
				txtCPSwitchBudget.setTag(Constants.PAYMENT_MODE_HOURLY);
			}
			setPaymentModeForVirtualAndInPerson();
			if (projectType.equals(Constants.VIRTUAL)) {
				txtCPSelectCompletionTime.setVisibility(View.GONE);
				llCPBidDuration.setVisibility(View.VISIBLE);
				categoryType = Category.FLD_IS_VIRTUAL;
			} else {
				txtCPSelectCompletionTime.setVisibility(View.VISIBLE);
				llCPBidDuration.setVisibility(View.GONE);
				categoryType = Category.FLD_IS_INPERSON;
			}
		}

		btnCPCategory.setOnClickListener(this);
		btnCPSubCategory.setOnClickListener(this);
		btnCPPost.setOnClickListener(this);
		txtCPChooseTemplate.setOnClickListener(this);
		txtCPDoerDetail.setOnClickListener(this);
		txtCPInviteDoer.setOnClickListener(this);
		txtCPSelectCompletionDate.setOnClickListener(this);
		txtCPSelectBidDuation.setOnClickListener(this);
		txtCPSwitchBudget.setOnClickListener(this);
		txtCPSelectCompletionTime.setOnClickListener(this);
		txtCPICompletionHours.setOnClickListener(this);

		/*eTextCPMinPrice.addTextChangedListener(this);
		eTextCPMaxPrice.addTextChangedListener(this);*/
		eTextCPExpenses.addTextChangedListener(this);
		eTextCPWorkHours.addTextChangedListener(this);
		eTextCPPrice.addTextChangedListener(this);

		database = DatabaseMgr.getInstance(this);
		setTouchListenerOnScrollView();
		loadProjectData(DATA_TYPE_CATEGORY, null);
	}
	
	private void setHeaderView() {
		appHeaderView = (AppHeaderView) findViewById(R.id.appHeaderView);
		String title = null; 
		if(projectType.equals(Constants.VIRTUAL)){
			title = getResources().getString(R.string.New_Virtual_Project);
		}else if(projectType.equals(Constants.INPERSON)){
			title = getResources().getString(R.string.New_In_Person_Project);
		}else if(projectType.equals(Constants.INSTANT)){
			title = getResources().getString(R.string.New_Instant_Task);
		}
		appHeaderView.setHeaderContent(getResources().getDrawable(R.drawable.ic_menu),title,null,null, Gravity.LEFT);
	}
	
	/**
	 * -------------A COMMON CLICK LISTENER FUNCTION TO HANDLE ALL CLICK-ABLE CONTROL'S CLICK---------
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.txtHLeft) {
			finishActivity();
		} else if (v.getId() == R.id.btnCPCategory) {
			showCategoryList();
		} else if (v.getId() == R.id.btnCPSubCategory) {
			showSubCategoryList();
		} else if (v.getId() == R.id.txtCPChooseTemplate) {
			showTemplateList();
		} else if (v.getId() == R.id.txtCPDoerDetail) {
			goToDoerDetails();
		} else if (v.getId() == R.id.txtCPInviteDoer) {
			goToDoerListDialogToInviteDoers();
		} else if (v.getId() == R.id.btnCPPost) {
			postAProject();
		} else if (v.getId() == R.id.txtCPSelectCompletionDate) {
			showDatePicker();
		} else if (v.getId() == R.id.txtCPSelectCompletionTime) {
			showTimePickerDialog();
		} else if (v.getId() == R.id.txtCPSelectBidDuation) {
			showBidDurationDialog();
		} else if (v.getId() == R.id.txtCPSwitchBudget) {
			setPaymentModeForVirtualAndInPerson();
		} else if (v.getId() == R.id.txtCPICompletionHours) {
			showHourListDialog();
		}
	}

	/**
	 * -------------TEXT WATCHER FUNCTION TO DISPLAY DYNAMICALLY UPDATED PROJECT COST---------
	 */
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		calculateProjectCost();
	}

	/**
	 * -------------COMMON FUNCTION TO RUN ASYNC TASK TO LOAD CATEGORY,SUB-CATEGORY AND TEMPLATE DATA--------
	 */
	private void loadProjectData(String dataType, String param) {
		new LoadProjectDataTask().execute(new String[] { dataType, param });
	}

	/**
	 * -------------ASYNC TASK TO LOAD CATEGORY,SUB-CATEGORY AND TEMPLATE DATA------
	 */
	private class LoadProjectDataTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String dataType = params[0];
			if (dataType.equals(DATA_TYPE_CATEGORY)) {
				getCategoryListData();
			} else if (dataType.equals(DATA_TYPE_SUB_CATEGORY)) {
				String cId = params[1];
				getSubCategoryListData(cId);
			} else if (dataType.equals(DATA_TYPE_TEMPLATE)) {
				String cId = params[1];
				getTemplateListData(cId);
			}
			return dataType;
		}

		@Override
		protected void onPostExecute(String dataType) {
			super.onPostExecute(dataType);
			if (dataType.equals(DATA_TYPE_CATEGORY)) {
				setCategoryHListData();
			} else if (dataType.equals(DATA_TYPE_SUB_CATEGORY)) {
				setTemplatesVisibility();
				setSubCategoryHListData();
			} else if (dataType.equals(DATA_TYPE_TEMPLATE)) {
				setTemplatesVisibility();
			}
		}
	}

	/** -------------DISPLAY CATEGORY LIST FOR ALL TYPE OF PROJECTS--------- */
	private void setCategoryHListData() {
		if(viewMode.equals(Constants.VIEW_MODE_EDIT)){ 
			setCategoryHeaderForEditView();
			new LoadEditViewDataTask().execute();
		}else{
			categoryHListAdapter = new HListAdapter(this, R.layout.ed_hlist_item,categories);
			categoryHListAdapter.setNotifyOnChange(true);

			hListVCPCategory.setAdapter(categoryHListAdapter);
			
			hListVCPCategory.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
					categoryHListAdapter.notifyDataSetChanged();
					categoryHListAdapter.setSelectedPosition(position);
					if (projectType.equals(Constants.INSTANT)) {
						setProjectCategoryValue(categories.get(position));
					} else {
						loadProjectData(DATA_TYPE_SUB_CATEGORY, String.valueOf(categories.get(position).category_id));
					}
				}
			});
		}
	}
	
	private void setCategoryHeaderForEditView(){
		if(project.category != null){
			for (Category category : categories) {
				if(category.category_id.equals(project.category.parent_id)){
					txtCPCategoryName.setText(category.category_name+"/"+project.category.category_name);  
					break;
				}
			}
			loadProjectData(DATA_TYPE_TEMPLATE, String.valueOf(project.category.category_id));
		}
	}
	/** -------------DISPLAY SUB-CATEGORY LIST FOR VIRTUAL AND IN-PERSON---- */
	private void setSubCategoryHListData() {
		int subCategoriesSize = subCategories.size();
		if (subCategoriesSize > 0) {
			llCPSubCategory.setVisibility(View.VISIBLE);
			// do not + button if there are <= 3 choices.
			if (subCategoriesSize > 3) {
				btnCPSubCategory.setVisibility(View.VISIBLE);
			} else {
				btnCPSubCategory.setVisibility(View.GONE);
			}

			subCategoryHListAdapter = new HListAdapter(this,R.layout.ed_hlist_item, subCategories);
			subCategoryHListAdapter.setNotifyOnChange(true);
			hListVCPSubCategory.setAdapter(subCategoryHListAdapter);
			
			hListVCPSubCategory.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent,View view, int position, long id) {
					selectedSkills.clear();
					selectedQuestions.clear();
					subCategoryHListAdapter.notifyDataSetChanged();
					subCategoryHListAdapter.setSelectedPosition(position);
					setProjectCategoryValue(subCategories.get(position));
					loadProjectData(DATA_TYPE_TEMPLATE, String.valueOf(subCategories.get(position).category_id));
				}
			});
		}
	}
	
	private void setProjectCategoryValue(Category category){
		project.category = category; 
		project.category_id = Long.valueOf(category.category_id);
	}

	/** -------------CATEGORY LIST DAILOG FOR ALL TYPE OF PROJECTS---------- */
	private void showCategoryList() {
		PCategoryListDialog dialog = PCategoryListDialog.getInstance(
				new IListItemClickListener() {
					@Override
					public void onItemSelected(Object item, int position) {
						Category tempCat = categories.get(position);
						categories.set(position, categories.get(0));
						categories.set(0, tempCat);
						categoryHListAdapter.notifyDataSetChanged();
						categoryHListAdapter.setSelectedPosition(0);
						if (projectType.equals(Constants.INSTANT)) {
							setProjectCategoryValue((Category) item);
						} else {
							loadProjectData(DATA_TYPE_SUB_CATEGORY, String.valueOf(((Category) item).category_id));
						}
					}
				}, categories);
		dialog.show(getSupportFragmentManager(), "cat_list");
	}

	/** -------------SUB-CATEGORY LIST DAILOG FOR VIRTUAL AND IN-PERSON----- */
	private void showSubCategoryList() {
		PCategoryListDialog dialog = PCategoryListDialog.getInstance(
				new IListItemClickListener() {
					@Override
					public void onItemSelected(Object item, int position) {
						Category tempCat = subCategories.get(position);
						subCategories.set(position, subCategories.get(0));
						subCategories.set(0, tempCat);
						subCategoryHListAdapter.notifyDataSetChanged();
						subCategoryHListAdapter.setSelectedPosition(0);
						setProjectCategoryValue((Category) item);
						loadProjectData(DATA_TYPE_TEMPLATE, String.valueOf(subCategories.get(0).category_id));
					}
				}, subCategories);
		dialog.show(getSupportFragmentManager(), "cat_Sub_list");
	}

	/** -------------TEMPLATE LIST DAILOG FOR VIRTUAL AND IN-PERSON--------- */
	private void showTemplateList() {
		PTemplateListDialog dialog = PTemplateListDialog.getInstance(
				new IListItemClickListener() {
					@Override
					public void onItemSelected(Object item, int position) {
						txtCPChooseTemplate.setText(((Template) item).getTitle());
						eTextCPTitle.setText(((Template) item).getTitle());
						eTextCPDescription.setText(((Template) item).getDesc());
					}
				}, templates);
		dialog.show(getSupportFragmentManager(), "template_list");
	}

	/** -------------DATE PICKER DAILOG FOR VIRTUAL AND IN-PERSON----------- */
	private void showDatePicker() {
		dpDailog = new DatePickerDailog(false,this, Calendar.getInstance(),
				new DatePickerListner() {
					@Override
					public void OnDoneButton(Dialog datedialog, Calendar c,String str) {
						txtCPSelectCompletionDate.setText(new SimpleDateFormat("MMM dd, yyyy").format(c.getTime()));
						project.end_date = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
						dpDailog.dismiss();
					}

					@Override
					public void OnCancelButton(Dialog datedialog) {
						dpDailog.dismiss();
					}
				});
		dpDailog.show();
	}

	/** -------------BID DURATION DAILOG FOR VIRTUAL------------------------ */
	private void showBidDurationDialog() {
		if (txtCPSelectCompletionDate.getText().toString().length() == 0) {
			Util.showCenteredToast(this,getResources().getString(R.string.msg_Project_Completion_Date_cannot_be_blank));
		} else {
			BidDurationDialog dialog = new BidDurationDialog(
					CreateProjectActivity.this, new IListItemClickListener() {
						@Override
						public void onItemSelected(Object item, int position) {
							txtCPSelectBidDuation.setText(item.toString());
							project.bid_duration = item.toString().toLowerCase(); // PROJECT
						}
					},txtCPSelectCompletionDate.getText().toString());
			dialog.show();
		}
	}

	/** -------------HOURS LIST DAILOG FOR INSTANT-------------------------- */
	private void showHourListDialog() {
		HourListDialog dialog = new HourListDialog(CreateProjectActivity.this,
				new IListItemClickListener() {
					@Override
					public void onItemSelected(Object item, int position) {
						txtCPICompletionHours.setText(item.toString());
						txtCPICompletionInfo.setVisibility(View.VISIBLE);
						txtCPICompletionInfo.setText(getCompletionInfo((position + 1), "EEEE dd, yyyy  hh:mm aa"));
						project.work_hrs = String.valueOf((position + 1));
						project.end_time = getCompletionInfo((position + 1),"yyyy-MM-dd HH:mm");
					}
				});
		dialog.show();
	}

	/**
	 * -------------FUNCTION TO GET COMPLETION DATA INFO FOR INSTANT
	 * PROJECT----------
	 */
	private String getCompletionInfo(int hours, String formatStr) {
		SimpleDateFormat format = new SimpleDateFormat(formatStr);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, hours);
		Date cTime = cal.getTime();
		return format.format(cTime);
	}

	/** -------------TIME PICKER DAILOG FOR IN-PERSON----------------------- */
	private void showTimePickerDialog() {
		tpDialog = new CTimePickerDialog(this, new TimerListner() {
			@Override
			public void OnDoneButton(Dialog timedialog, String str) {
				txtCPSelectCompletionTime.setText(str);
				tpDialog.dismiss();
			}

			@Override
			public void OnCancelButton(Dialog timedialog) {
				tpDialog.dismiss();
			}
		});
		tpDialog.show();
	}

	/** -------------FUNCTION TO SET TEMPLATE BUTTON VISIBILITY ------------ */
	private void setTemplatesVisibility() {
		txtCPChooseTemplate.setText("");
		if (templates.size() > 0) {
			llCPChooseTemplate.setVisibility(View.VISIBLE);
		} else {
			llCPChooseTemplate.setVisibility(View.GONE);
		}
	}

	/** -------------FUNCTION TO SET PAYMENT MODE FOR VIRTUAL AND IN-PERSON- */
	private void setPaymentModeForVirtualAndInPerson() {
		if (txtCPSwitchBudget.getTag().toString().equals(Constants.PAYMENT_MODE_HOURLY)) {
			txtCPSwitchBudget.setTag(Constants.PAYMENT_MODE_FIXED);
			txtCPSwitchBudget.setText(getResources().getString(R.string.Switch_to_Fixed));
			llCPHourPerWeek.setVisibility(View.VISIBLE);
			project.payment_mode = Constants.PAYMENT_MODE_HOURLY;// PROJECT
			eTextCPPrice.setVisibility(View.GONE); 
		} else {
			txtCPSwitchBudget.setTag(Constants.PAYMENT_MODE_HOURLY);
			txtCPSwitchBudget.setText(getResources().getString(R.string.Switch_to_Hourly));
			llCPHourPerWeek.setVisibility(View.GONE);
			project.payment_mode = Constants.PAYMENT_MODE_FIXED;// PROJECT
			eTextCPWorkHours.setText("");
			eTextCPPrice.setVisibility(View.VISIBLE); 
		}
	}

	/**
	 * ------------FATCH CATEGORY DATA FROM DATA-BASE FOR ALL TYPE OF PROJECTS--------
	 */
	private void getCategoryListData() {
		categories.clear();
		if(!viewMode.equals(Constants.VIEW_MODE_EDIT)){ 
			project.category = new Category();
			project.category_id = null;
		}
		Cursor cursor = null;
		String subCCount = categoryType.equals(Category.FLD_IS_INSTANT) ? " >= 0 ": " > 0 ";
		String parentId = categoryType.equals(Category.FLD_IS_INSTANT) ? "" : " AND (parent_id IS NULL OR parent_id = 0)";
		try {
			cursor = database.rawQuery("SELECT category_id, category_name, category_image, "
					+ "subcategory_cnt FROM category WHERE "+categoryType+" = 1 AND category_status = 1 "
					+ "AND subcategory_cnt"+subCCount
					+ parentId, null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Category category = new Category(); 
				category.category_id = cursor.getLong(cursor.getColumnIndex(Category.FLD_CATEGORY_ID));
				category.category_name = cursor.getString(cursor.getColumnIndex(Category.FLD_CATEGORY_NAME));
				category.category_image = cursor.getString(cursor.getColumnIndex(Category.FLD_CATEGORY_IMAGE));
				category.subcategory_cnt = cursor.getString(cursor.getColumnIndex(Category.FLD_SUB_CATEGORY_CNT));
				categories.add(category);
				cursor.moveToNext();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}

	/**
	 * ------------FATCH SUB-CATEGORY DATA FROM DATA-BASE FOR VIRTUAL AND IN-PERSON---
	 */
	private void getSubCategoryListData(String pCategoryId) {
		subCategories.clear();
		templates.clear();
		project.category = new Category();
		project.category_id = null;
		Cursor cursor = null;
		try {
			cursor = database.queryTable(TableType.CategoryTable, new String[] {
					Category.FLD_CATEGORY_ID, Category.FLD_CATEGORY_NAME,
					Category.FLD_CATEGORY_IMAGE,Category.FLD_PARENT_ID }, Category.FLD_PARENT_ID
					+ "=? AND " + Category.FLD_CATEGORY_STATUS + "=?",
					new String[] { pCategoryId, "1" }, null, null, null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Category category = new Category();
				category.category_id = cursor.getLong(cursor.getColumnIndex(Category.FLD_CATEGORY_ID));
				category.parent_id = cursor.getLong(cursor.getColumnIndex(Category.FLD_PARENT_ID));
				category.category_name = cursor.getString(cursor.getColumnIndex(Category.FLD_CATEGORY_NAME));
				category.category_image = cursor.getString(cursor.getColumnIndex(Category.FLD_CATEGORY_IMAGE));
				subCategories.add(category);
				cursor.moveToNext();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}

	/**
	 * ------------FATCH TEMPLATE DATA FROM DATA-BASE FOR VIRTUAL AND IN-PERSON-------
	 */
	private void getTemplateListData(String categoryId) {
		templates.clear();
		Cursor cursor = null;
		try {
			cursor = database.queryTable(TableType.CategoryTable,new String[] { Category.FLD_TASK_TEMPLATE },
					Category.FLD_CATEGORY_ID + "=? AND "+ Category.FLD_CATEGORY_STATUS + "=?",
					new String[] { categoryId, "1" }, null, null, null);
			cursor.moveToFirst();
			String jsonStr = cursor.getString(cursor.getColumnIndex(Category.FLD_TASK_TEMPLATE));
			if (jsonStr != null && !jsonStr.equals("")) {
				JSONArray jsonArray = new JSONArray(jsonStr);
				for (int i = 0; i < jsonArray.length(); i++) {
					Template template = new Template();
					template.setTitle(jsonArray.getJSONObject(i).getString("title"));
					template.setDesc(jsonArray.getJSONObject(i).getString("desc"));
					templates.add(template);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}

	/**
	 * -------------DOER LIST DIALOG TO INVITE DOERS FOR ALL TYPE OF PROJECTS----------
	 */
	private void goToDoerListDialogToInviteDoers() {
		//if (isValidate()) {
			Intent intent = new Intent();
			if (projectType.equals(Constants.INSTANT)) {
				intent.setClass(CreateProjectActivity.this, InviteIDoerActivity.class);
			} else {
				intent.setClass(CreateProjectActivity.this, InviteDoersActivity.class);
			}
			startActivity(intent);
		//}
	}
	
	/** -------------DOER DETAILS DIALOG FOR VIRTUAL AND IN-PERSON----------- */
	private void goToDoerDetails() {
		if (isValidate()) {
			DoerDetailsDialog dialog = DoerDetailsDialog.getInstance(
					selectedCountries, selectedSkills, selectedQuestions,
					project.category.category_id, projectType);
			dialog.show(getSupportFragmentManager(), "doer_details");
		}
	}

	/** -------------FUNCTION TO POST A PROJECT ----------------------------- */
	private void postAProject() {
		project.task_kind = projectType;
		project.description = eTextCPDescription.getText().toString().trim();
		project.cash_required = eTextCPExpenses.getText().toString().trim();
		project.delivery_status = Constants.DELIVERY_STATUS_PENDING;
		project.trno = Long.valueOf(database.getMaxTrno(TableType.TaskTable, null, null));
        String price = eTextCPPrice.getText().toString().trim();
		if (projectType.equals(Constants.INSTANT)) {
			project.title = getInstantProjectTitle(project.description);
			project.min_price = project.max_price = price.equals("") ? "0" : price ;
			
			project.is_public = "0";
			project.payment_mode = Constants.PAYMENT_MODE_FIXED;
			
			project.tasker_in_range = String.valueOf(invitedDoerInRange);
			
			if (invitedDoerSelectionType) {
				project.selection_type = Constants.DOER_SELECTION_TYPE_AUTO;
			} else {
				project.selection_type = Constants.DOER_SELECTION_TYPE_MANUAL;
			}
			
			setProjectWLocation();
		} else {
			project.title = eTextCPTitle.getText().toString().trim();
			if(project.payment_mode.equals(Constants.PAYMENT_MODE_HOURLY)){ 
				project.min_price = project.max_price = "0";
			}else{
				project.min_price = project.max_price = price.equals("") ? "0" : price ;
			}
			project.work_hrs = eTextCPWorkHours.getText().toString().trim().equals("0") ? "1" : eTextCPWorkHours.getText().toString().trim();
			
			project.seal_all_proposal = chkCPIsSealProposal.isChecked() ? "1" : "0";
			project.is_highlighted = chkCPIsHighlighted.isChecked() ? "1" : "0";
			project.is_premium = chkCPIsPremium.isChecked() ? "1" : "0";
			project.is_public = rdCPPublic.isChecked() ? "1" : "0";
			
			if (projectType.equals(Constants.INPERSON)) {
				project.end_time = Util.getTimeIn24Format(txtCPSelectCompletionTime.getText().toString());
				setProjectWLocation();
			} else {
				if (selectedCountries.size() > 0) {
					project.is_location_region = Constants.LOCATION_REGION_COUNTRY;
				}
			}
		}

	//	Util.showCenteredToast(this, "Selected Dour Count: "+selectedDoers.size()); 
		if (isValidate()) {
			boolean isOnline = Util.isDeviceOnline(CreateProjectActivity.this);
			if(projectType.equals(Constants.INSTANT)){
				if (selectedDoers.size() > 0) {
					if(isOnline){ 
						new PorjectPostTask().execute();
					}else{
						Util.showCenteredToast(this,getResources().getString(R.string.msg_Connect_internet));
					}
				} else {
					Util.showCenteredToast(this,getResources().getString(R.string.msg_Please_invite_atleast_one_doer));
				}
			}else{
				if(!rdCPPublic.isChecked() && selectedDoers.size() == 0){
					Util.showCenteredToast(this,getResources().getString(R.string.msg_Please_invite_atleast_one_doer));
				}else{
					if(!isOnline)
						Util.showCenteredToast(this,getResources().getString(R.string.msg_Project_saved_in_draft));
					
					new PorjectPostTask().execute();
				}
			}
		}
	}

	/**
	 * --------------FUNCTION TO SET WORK LOCATION FOR IN-PERSON AND INSTANT PTOJECT---------
	 */
	private void setProjectWLocation() {
		if (AppGlobals.defaultWLocation != null) {
			if (AppGlobals.defaultWLocation.work_location_id == null) {
				project.is_location_region = Constants.LOCATION_REGION_OTHER;
			} else {
				project.is_location_region = Constants.LOCATION_REGION_SAVED;
			}
			project.workLocation = AppGlobals.defaultWLocation;
		} else {
			AppGlobals.defaultWLocation = new WorkLocation();
			AppGlobals.defaultWLocation.latitude = AppGlobals.latitude;
			AppGlobals.defaultWLocation.longitude = AppGlobals.longitude;
			project.workLocation = AppGlobals.defaultWLocation;
		}
	}

	/**
	 * -------------FUNCTION TO GET TITLE STRING FOR INSTANT PROJECT------------------------
	 */
	private String getInstantProjectTitle(String desc) {
		if (desc.length() >= 60) {
			return desc.substring(0, 60);
		}
		return desc;
	}

	/** -------------ASYNC TASK TO POST A PROJECT ---------------------------- */
	private class PorjectPostTask extends AsyncTask<Void, Void, NetworkResponse> {
		@Override
		protected void onPreExecute() {
			if(projectType.equals(Constants.INSTANT)){ 
				showInstantResultDialog(null);
			}else{
				Util.showProDialog(CreateProjectActivity.this);
			}
		}

		@Override
		protected NetworkResponse doInBackground(Void... params) {
			project.tasklocation = new ArrayList<WorkLocation>(0);
			project.attachments = new ArrayList<Attachment>(0); 
			project.multicatquestion = selectedQuestions;
			project.multiskills = selectedSkills;
			project.multilocations = selectedCountries;
			project.invitedtaskers = selectedDoers;
			if(!projectType.equals(Constants.VIRTUAL)){
				project.tasklocation.add(project.workLocation);	
			}
			return database.createTask(project);
		}

		@Override
		protected void onPostExecute(NetworkResponse networkResponse) {
			Util.dimissProDialog();
			if (networkResponse != null) {
				if (networkResponse.isSuccess()) {
					Util.showCenteredToast(CreateProjectActivity.this,getResources().getString(R.string.msg_Project_Post_Success));
					if(!projectType.equals(Constants.INSTANT)){
						finishActivity();
					}
				} else {
					dismissInstantResultDialog();
					Util.showCenteredToast(CreateProjectActivity.this,networkResponse.getErrMsg());
				}
			}
		}
	}
	
	/** -------------INSTANT TASK RESPONSE HANDLING SECTION--------------------*/
	private void regisLocalBroadcastManager() {
		lBroadcastManager.unregisterReceiver(mBroadcastReceiver);
		lBroadcastManager.registerReceiver(mBroadcastReceiver,
				new IntentFilter(Constants.LOCAL_BROADCAST_FOR_IRESULT));
	}
	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			ErandooMqttMessage eMqttMessage = (ErandooMqttMessage) intent.getSerializableExtra(Constants.SERIALIZABLE_DATA);
			if(eMqttMessage != null){
				project.task_id = eMqttMessage.task_id;
				Long recId = Util.getMobileRecId(eMqttMessage.mobile_rec_id);
				if(project._Id.equals(recId)){ 
					showInstantResultDialog(eMqttMessage);
				}
			}
		}
	};
	
	private void showInstantResultDialog(ErandooMqttMessage eMqttMessage){
		dismissInstantResultDialog();
		iResultDialog = new InstantResultDialog(CreateProjectActivity.this,eMqttMessage);
		iResultDialog.setCancelable(false); 
		iResultDialog.show();
	}
	
	private void dismissInstantResultDialog(){
		if(iResultDialog != null){
			iResultDialog.dismiss();
			iResultDialog = null;
		}
	}
	
	private class LoadEditViewDataTask extends AsyncTask<Void, Void, Void>{
		@Override
		protected void onPreExecute() {
			Util.showProDialog(CreateProjectActivity.this);
			Util.progressDialog.setMessage("Loading project data..."); 
		}
		@Override
		protected Void doInBackground(Void... params) {
			try{
				if(project._Id != null && !project._Id.equals(Constants.INVALID_ID)){ 
			    	selectedSkills = database.getTaskSkill(String.valueOf(project._Id));
			    	selectedQuestions = database.getQuestion(project._Id);
			    	selectedDoers = database.getDoerList(project._Id);
			    	if(projectType.equals(Constants.VIRTUAL)){ 
			    		selectedCountries = database.getTaskLocation(String.valueOf(project._Id));
			    	}
			    }
			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			Util.dimissProDialog();
			setEditViewData();
		}
	}
	
	private void setEditViewData(){
		try{
			if(viewMode.equals(Constants.VIEW_MODE_EDIT)){ 
				eTextCPTitle.setText(project.title);
				eTextCPDescription.setText(project.description);
				eTextCPPrice.setText(project.price);
				eTextCPWorkHours.setText(project.work_hrs);
				eTextCPExpenses.setText(project.cash_required); 
				
				boolean  isSeal = project.seal_all_proposal.equals("1") ? true : false;
				chkCPIsSealProposal.setChecked(isSeal); 
				chkCPIsSealProposal.setEnabled(!isSeal);
				
				boolean  isHighlighted = project.is_highlighted.equals("1") ? true : false;
				chkCPIsHighlighted.setChecked(isHighlighted); 
				chkCPIsHighlighted.setEnabled(!isHighlighted);
				
				boolean  isPremium = project.is_premium.equals("1") ? true : false;
				chkCPIsPremium.setChecked(isPremium); 
				chkCPIsPremium.setEnabled(!isPremium);
		
				rdCPPublic.setChecked(project.is_public.equals("1") ? true : false); 
		        
				txtCPSelectBidDuation.setText(project.bid_duration); 
				txtCPSelectCompletionDate.setText(Util.setProjectDateWithMonth(project.end_date, true)); 
		
				if (projectType.equals(Constants.INPERSON)) {
					txtCPSelectCompletionTime.setText(project.end_time); 
					AppGlobals.defaultWLocation = project.workLocation;
				} else {
					if (selectedCountries.size() > 0) {
						project.is_location_region = Constants.LOCATION_REGION_COUNTRY;
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/** -------------FUNCTION TO VALIDATE DATA FIELDS ------------------------ */
	private boolean isValidate() {
		if (projectType.equals(Constants.INSTANT)) {
			if (categories.size() > 0 && project.category.category_id == null) {
				Util.showCenteredToast(this,
						getResources()
								.getString(R.string.msg_select_a_category));
			} else if (txtCPICompletionHours.getText().toString().trim()
					.length() == 0) {
				Util.showCenteredToast(
						this,
						getResources()
								.getString(
										R.string.msg_Project_Completion_Hours_cannot_be_blank));
			} else if (eTextCPDescription.getText().toString().trim().length() == 0) {
				if(projectType.equals(Constants.INSTANT)) {
					Util.showCenteredToast(
							this,
							getResources()
									.getString(
											R.string.msg_Project_notes_cannot_be_blank));
				}
				else {
					Util.showCenteredToast(
							this,
							getResources()
									.getString(
											R.string.msg_Project_description_cannot_be_blank));
				}
			} else if (eTextCPDescription.getText().toString().trim().length() < 10) {
				Util.showCenteredToast(
						this,
						getResources().getString(
								R.string.msg_Project_description_is_too_short));
			} else if (eTextCPPrice.getText().toString().trim().length() == 0) {
				Util.showCenteredToast(
						this,
						getResources().getString(
								R.string.msg_price_cannot_be_blank));
			} else {
				return true;
			}
		} else {
			if (subCategories.size() == 0 && viewMode.equals(Constants.VIEW_MODE_CREATE)) { 
				Util.showCenteredToast(this, getResources().getString(R.string.msg_select_a_category));
				
			} else if (viewMode.equals(Constants.VIEW_MODE_CREATE) && subCategories.size() > 0 
					&& project.category.category_id == null) {
				Util.showCenteredToast(this, getResources().getString(R.string.msg_select_a_sub_category));
				
			} else if (eTextCPTitle.getText().toString().trim().length() == 0) {
				Util.showCenteredToast(this,getResources().getString(R.string.msg_Project_title_cannot_be_blank));
				
			} else if (eTextCPTitle.getText().toString().matches(".*[a-zA-Z]+.*") == false) {
				Util.showCenteredToast(this,"Project title must contains atleast a alphabet");
				
			} else if (eTextCPTitle.getText().toString().length() < 10) {
				Util.showCenteredToast(
						this,
						getResources().getString(
								R.string.msg_Project_title_is_too_short));
			} else if (eTextCPDescription.getText().toString().trim().length() == 0) {
				Util.showCenteredToast(
						this,
						getResources()
								.getString(
										R.string.msg_Project_description_cannot_be_blank));
			} else if (eTextCPDescription.getText().toString().trim().length() < 10) {
				Util.showCenteredToast(
						this,
						getResources().getString(
								R.string.msg_Project_description_is_too_short));
			} else if (txtCPSelectCompletionDate.getText().toString().trim()
					.length() == 0) {
				Util.showCenteredToast(
						this,
						getResources()
								.getString(
										R.string.msg_Project_Completion_Date_cannot_be_blank));
			} else if (txtCPSelectCompletionTime.getText().toString().trim()
					.length() == 0
					&& projectType.equals(Constants.INPERSON)) {
				Util.showCenteredToast(
						this,
						getResources()
								.getString(
										R.string.msg_Project_Completion_Time_cannot_be_blank));
			} else if (txtCPSelectBidDuation.getText().toString().trim()
					.length() == 0
					&& projectType.equals(Constants.VIRTUAL)) {
				Util.showCenteredToast(
						this,
						getResources().getString(
								R.string.msg_Bid_duration_cannot_be_blank));
			} /*else if (eTextCPMinPrice.getText().toString().trim().length() == 0) {
				Util.showCenteredToast(
						this,
						getResources().getString(
								R.string.msg_Min_price_cannot_be_blank));
			} else if (eTextCPMaxPrice.getText().toString().trim().length() == 0) {
				Util.showCenteredToast(
						this,
						getResources().getString(
								R.string.msg_Max_price_cannot_be_blank));
			} else if (Integer.parseInt(eTextCPMaxPrice.getText().toString()) < Integer
					.parseInt(eTextCPMinPrice.getText().toString())) {
				Util.showCenteredToast(
						this,
						getResources()
								.getString(
										R.string.msg_Max_price_must_be_greater_than_Min_price));
			}*/else if (txtCPSwitchBudget.getText().toString()
					.equalsIgnoreCase("Switch to Fixed")
					&& eTextCPWorkHours.getText().toString().trim().length() > 0
					&& Integer.parseInt(eTextCPWorkHours.getText().toString()
							.trim()) > 168) {
				Util.showCenteredToast(this,
						getResources().getString(R.string.msg_Hours_per_week));
			}else {
				return true;
			}
		}
		return false;
	}

	/** -------------FUNCTION TO CALCULATE COST FOR ALL TYPE OF PROJECTS------ */
	private void calculateProjectCost() {
		String estimatedCost = LicenseValidation.calculateProjectCost(projectType, 
										eTextCPExpenses.getText().toString().trim(),
										eTextCPPrice.getText().toString().trim(),
										eTextCPPrice.getText().toString().trim(),//minimum price
										eTextCPPrice.getText().toString().trim(), // maximum price
										eTextCPWorkHours.getText().toString().trim());
		if(projectType.equals(Constants.INSTANT)){ 
			txtCPCost.setText(estimatedCost);
		}
		project.price = estimatedCost;
	}

	private void setTouchListenerOnScrollView() {
		eTextCPDescription.setMovementMethod(new ScrollingMovementMethod());
		mainScrollCProject.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				findViewById(R.id.mainScrollCProject).getParent()
						.requestDisallowInterceptTouchEvent(false);
				return false;
			}
		});

		eTextCPDescription.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent arg1) {
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});
	}

	public static ArrayList<TaskDoer> updateInvitedDoerList(TaskDoer doer) {
		if(doer != null){
			if (selectedDoers.size() > 0) {
				for (TaskDoer doer2 : selectedDoers) {
					if (doer.user_id.equals(doer2.user_id)) {
						if (!doer.isSelected) {
							selectedDoers.remove(doer2);
							break;
						}
					} else {
						if (doer.isSelected) {
							selectedDoers.add(doer);
							break;
						} 
					}
				}
			} else {
				selectedDoers.add(doer);
			}
		}
		return selectedDoers;
	}
	
	public static void updateInstantDoerSelection(boolean selectionType,int taskerRange){
		invitedDoerInRange = taskerRange;
		invitedDoerSelectionType = selectionType;
	}
	
	public static void clearSelectedDoers(){
		selectedDoers.clear();
	}
	
	public static void updateMobileRecId(Long recId){
		project._Id = recId;
	}

	@Override
	public void onBackPressed() {
		finishActivity();
	}

	private void finishActivity() {
		CreateProjectActivity.this.finish();
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}
	
	@Override
	protected void onDestroy() {
		lBroadcastManager.unregisterReceiver(mBroadcastReceiver);
		AppGlobals.invitedDoers.clear();
		categories.clear();
		subCategories.clear();
		templates.clear();
		selectedCountries.clear();
		selectedSkills.clear();
		selectedQuestions.clear();
		selectedDoers.clear();
		selectedDoers.clear();
		projectType = "";
		categoryType = "";
		invitedDoersType = "search";
		invitedDoerSelectionType = false;
		invitedDoerInRange = 100;
		project.category = null;
		project = null;
		super.onDestroy();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void afterTextChanged(Editable s) {
	}
}
