package erandoo.app.main.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.adapters.AddCategoryAdapter;
import erandoo.app.adapters.AddSkillsAdapter;
import erandoo.app.adapters.SettingNotificationsAdapter;
import erandoo.app.cmds.Cmd;
import erandoo.app.cmds.CmdFactory;
import erandoo.app.config.Config;
import erandoo.app.custom.AppHeaderView;
import erandoo.app.custom.BaseFragActivity;
import erandoo.app.custom.IListItemClickListener;
import erandoo.app.database.Category;
import erandoo.app.database.DatabaseMgr;
import erandoo.app.database.NotificationSetting;
import erandoo.app.database.Skill;
import erandoo.app.database.TableType;
import erandoo.app.network.NetworkMgr;
import erandoo.app.network.NetworkResponse;
import erandoo.app.tempclasses.NotificationSettingData;
import erandoo.app.utilities.Util;

public class NotificationsSettActivity extends BaseFragActivity implements
		OnClickListener, IListItemClickListener {

	/*
	 * Selected Skill and Category List data from server
	 */
	private ArrayList<Category> selectedCategoryListFromServer;
	private ArrayList<Skill> selectedSkillListFromServer;
	private ArrayList<Category> selectedCategoryList;
	private ArrayList<Skill> selectedSkillList;

	/*
	 * Data for showing skill and category Dailog
	 */

	private ArrayList<Skill> addSkillListFromDB;
	private ArrayList<Category> addCatListFromDB;

	private HashMap<String, Category> catHashMap;
	private HashMap<String, JSONObject> saveNotiSettMap;
	private AddSkillsAdapter addSkillAdapter;
	private AddCategoryAdapter addCategoryAdapter;
	private JSONArray notiSettArray;
	private JSONArray notiSettCatArray;
	private JSONArray notiSettSkillArray;

	private NotificationSettingData notificationData;
	private ArrayList<NotificationSetting> notiListForShowAndSend;
	private int selectedColorCodeValue_txt;
	private int unSelectedColorCodeValue_txt;
	private int selectedColorCodeValue_bg;
	private int unSelectedColorCodeValue_bg;
	private LinearLayout doerLinearLay;
	private ListView settingNotiLv, selectedSkillLV, selectedCatLV;

	private TextView txtPoster, txtDoer, txtSystem;
	private TextView textAddCat, txtAddSkills;
	private SettingNotificationsAdapter SetDataTagListAdapter;
	private GetNotiSettDataAsync getNotiSettingAsync;
	private String forPoster = "p";
	private String forDoer = "t";
	private String forSystem = "s";
	private String whichTagIsClicked = forPoster;
	DatabaseMgr dbMgr;
	private AppHeaderView appHeaderView;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.ed_setting_notifications);
		inIt();

		appHeaderView.setHeaderContent(
				getResources().getDrawable(R.drawable.ic_back), getResources()
						.getString(R.string.Notifications), null,
				getResources().getString(R.string.save), Gravity.CENTER);

		selectedSkillList = new ArrayList<Skill>();
		selectedCategoryList = new ArrayList<Category>();
		dbMgr = DatabaseMgr.getInstance(NotificationsSettActivity.this);
		selectedColorCodeValue_txt = getResources().getColor(R.color.white);
		unSelectedColorCodeValue_txt = getResources().getColor(
				R.color.main_title_color);
		selectedColorCodeValue_bg = getResources().getColor(R.color.red);
		unSelectedColorCodeValue_bg = getResources().getColor(
				R.color.trans_color);
		getNotiSettingAsync = new GetNotiSettDataAsync();
		if (getNotiSettingAsync.getStatus() == AsyncTask.Status.PENDING) {
			getNotiSettingAsync.execute();
		}

	}

	private void inIt() {
		// TODO Auto-generated method stub
		appHeaderView = (AppHeaderView) findViewById(R.id.appHeaderView);
		settingNotiLv = (ListView) findViewById(R.id.settingNotiLv);
		selectedSkillLV = (ListView) findViewById(R.id.selectedSkillLV);
		selectedCatLV = (ListView) findViewById(R.id.selectedCatLV);
		doerLinearLay = (LinearLayout) findViewById(R.id.doerLinearLay);

		txtPoster = (TextView) findViewById(R.id.txtPoster);
		txtDoer = (TextView) findViewById(R.id.txtDoer);
		txtSystem = (TextView) findViewById(R.id.txtSystem);
		doerLinearLay.setVisibility(ViewGroup.GONE);

		settingNotiLv.setVisibility(View.VISIBLE);
		textAddCat = (TextView) findViewById(R.id.textAddCat);
		txtAddSkills = (TextView) findViewById(R.id.txtAddSkills);

		textAddCat.setOnClickListener(this);
		txtAddSkills.setOnClickListener(this);

		txtPoster.setOnClickListener(this);
		txtDoer.setOnClickListener(this);
		txtSystem.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {

		if (v.getId() == appHeaderView.txtHLeft.getId()) {
			finishActivity();
		} else if (v.getId() == R.id.txtPoster) {
			if (!whichTagIsClicked.equals(forPoster)) {
				whichTagIsClicked = forPoster;
				setDataOnTabClick();
			}
		} else if (v.getId() == R.id.txtDoer) {
			if (!whichTagIsClicked.equals(forDoer)) {
				whichTagIsClicked = forDoer;
				setDataOnTabClick();
			}
		} else if (v.getId() == R.id.txtSystem) {
			if (!whichTagIsClicked.equals(forSystem)) {
				whichTagIsClicked = forSystem;
				setDataOnTabClick();
			}
		} else if (v.getId() == appHeaderView.txtHRight2.getId()) {
			if (Util.isDeviceOnline(NotificationsSettActivity.this)) {
				UpadateNotiSettAsync updateAsync;
				updateAsync = new UpadateNotiSettAsync();
				if (updateAsync.getStatus() == AsyncTask.Status.PENDING) {
					updateAsync.execute();
				}
			} else {
				Util.showCenteredToast(NotificationsSettActivity.this,
						getResources().getString(R.string.msg_Connect_internet));
			}
		} else if (v.getId() == R.id.textAddCat) {

			ArrayList<Category> tempList = new ArrayList<Category>(
					addCatListFromDB);
			customDialogForCatAndSKill(
					getResources().getString(R.string.select_category), true,
					null, tempList);

		} else if (v.getId() == R.id.txtAddSkills) {

			ArrayList<Skill> tempList = new ArrayList<Skill>(addSkillListFromDB);
			customDialogForCatAndSKill(
					getResources().getString(R.string.select_skills), true,
					tempList, null);
		}

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finishActivity();
	}

	private void finishActivity() {
		NotificationsSettActivity.this.finish();
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}

	public void setDataOnTabClick() {
		setDataToArrayList();
		if (whichTagIsClicked.equals(forPoster)) {
			doerLinearLay.setVisibility(ViewGroup.GONE);
			txtPoster.setBackgroundColor(selectedColorCodeValue_bg);
			txtDoer.setBackgroundColor(unSelectedColorCodeValue_bg);
			txtSystem.setBackgroundColor(unSelectedColorCodeValue_bg);
			txtPoster.setTextColor(selectedColorCodeValue_txt);
			txtDoer.setTextColor(unSelectedColorCodeValue_txt);
			txtSystem.setTextColor(unSelectedColorCodeValue_txt);
		} else if (whichTagIsClicked.equals(forDoer)) {
			doerLinearLay.setVisibility(ViewGroup.VISIBLE);
			txtPoster.setBackgroundColor(unSelectedColorCodeValue_bg);
			txtDoer.setBackgroundColor(selectedColorCodeValue_bg);
			txtSystem.setBackgroundColor(unSelectedColorCodeValue_bg);
			txtPoster.setTextColor(unSelectedColorCodeValue_txt);
			txtDoer.setTextColor(selectedColorCodeValue_txt);
			txtSystem.setTextColor(unSelectedColorCodeValue_txt);
		} else if (whichTagIsClicked.equals(forSystem)) {
			doerLinearLay.setVisibility(ViewGroup.GONE);
			txtPoster.setBackgroundColor(unSelectedColorCodeValue_bg);
			txtDoer.setBackgroundColor(unSelectedColorCodeValue_bg);
			txtSystem.setBackgroundColor(selectedColorCodeValue_bg);
			txtPoster.setTextColor(unSelectedColorCodeValue_txt);
			txtDoer.setTextColor(unSelectedColorCodeValue_txt);
			txtSystem.setTextColor(selectedColorCodeValue_txt);
		}
		SetDataTagListAdapter = new SettingNotificationsAdapter(
				NotificationsSettActivity.this, notiListForShowAndSend);
		settingNotiLv.setAdapter(SetDataTagListAdapter);
	}

	// ----------------TASK TO Get Notification Settings List
	// --------------------------------
	private class GetNotiSettDataAsync extends
			AsyncTask<String, Void, NetworkResponse> {

		@Override
		protected void onPreExecute() {
			Util.showProDialog(NotificationsSettActivity.this);
		}

		@Override
		protected NetworkResponse doInBackground(String... params) {
			NetworkResponse response = null;
			if (Util.isDeviceOnline(NotificationsSettActivity.this)) {
				selectedCategoryList = new ArrayList<Category>();
				notiListForShowAndSend = new ArrayList<NotificationSetting>();
				Cmd cmd = null;
				cmd = CmdFactory.getNotiSettingsCmd();
				response = NetworkMgr.httpPost(Config.API_URL,
						cmd.getJsonData());
				setDataTonotificationList(response);
			}
			addSkillListFromDB = new ArrayList<Skill>();
			addCatListFromDB = new ArrayList<Category>();
			addSkillListFromDB = dbMgr.getSkillSListFromDB( addSkillListFromDB);
			getCateListFromDB();
			return response;
		}

		@Override
		protected void onPostExecute(NetworkResponse networkResponse) {
			Util.dimissProDialog();

			if (networkResponse != null) {
				if (!networkResponse.isSuccess()) {
					Util.showCenteredToast(NotificationsSettActivity.this,
							networkResponse.getErrMsg());
				} else {
					setDataOnTabClick();
				}

			}

		}

	}

	private void setDataTonotificationList(NetworkResponse response) {
		// TODO Auto-generated method stub
		if (response != null && response.isSuccess()) {
			JSONObject jobject;
			try {
				jobject = response.getJsonObject().getJSONObject("data");
				String notificationDataString = jobject.toString();
				notificationData = (NotificationSettingData) Util
						.getJsonToClassObject(notificationDataString,
								NotificationSettingData.class);
				for (NotificationSetting notiSettObj : notificationData.notification) {
					if (notiSettObj.applicable_for.equals(forDoer)) {

						if (notiSettObj.category.size() != 0) {
							selectedCategoryListFromServer = new ArrayList<Category>(
									notiSettObj.category);
							selectedCategoryList = new ArrayList<Category>(
									selectedCategoryListFromServer);
						}
						if (notiSettObj.skill.size() != 0) {
							selectedSkillListFromServer = new ArrayList<Skill>(
									notiSettObj.skill);
							selectedSkillList = new ArrayList<Skill>(
									selectedSkillListFromServer);
						}
					}
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void setDataToArrayList() {
		saveNotiSettMap = new HashMap<String, JSONObject>();
		notiListForShowAndSend = new ArrayList<NotificationSetting>();
		if (notificationData != null) {
			for (NotificationSetting notiSettObj : notificationData.notification) {
				JSONObject jobje = setDataToJSonObject(notiSettObj);
				saveNotiSettMap.put(notiSettObj.notification_id, jobje);
				if (notiSettObj.applicable_for.equals(forPoster)
						&& whichTagIsClicked.equals(forPoster)) {

					setDataToShowList(notiSettObj);
				} else if (notiSettObj.applicable_for.equals(forDoer)
						&& whichTagIsClicked.equals(forDoer)) {
					if (selectedSkillListFromServer != null) {
						selectedSkillList = new ArrayList<Skill>(
								selectedSkillListFromServer);
					}
					if (selectedCategoryListFromServer != null) {
						selectedCategoryList = new ArrayList<Category>(
								selectedCategoryListFromServer);
					}
					setDataToShowList(notiSettObj);
				} else if (notiSettObj.applicable_for.equals(forSystem)
						&& whichTagIsClicked.equals(forSystem)) {
					setDataToShowList(notiSettObj);
				}
			}
		}

	}

	public void setDataToShowList(NotificationSetting notiSettObj) {
		NotificationSetting notiSettObjTemp = new NotificationSetting();
		notiSettObjTemp.applicable_for = notiSettObj.applicable_for;

		notiSettObjTemp.category = notiSettObj.category;
		notiSettObjTemp.description = notiSettObj.description;
		notiSettObjTemp.send_email = notiSettObj.send_email;
		notiSettObjTemp.notification_id = notiSettObj.notification_id;
		notiSettObjTemp.send_sms = notiSettObj.send_sms;
		notiSettObjTemp.skill = notiSettObj.skill;
		notiListForShowAndSend.add(notiSettObjTemp);
	}

	public void setDataToSkillList(ArrayList<Category> selectedCat,
			ArrayList<Skill> selectedSkills) {

		if (selectedCat != null) {
			addCategoryAdapter = new AddCategoryAdapter(
					NotificationsSettActivity.this, selectedCat, catHashMap,
					false);
			selectedCatLV.setAdapter(addCategoryAdapter);
		}
		if (selectedSkills != null) {
			addSkillAdapter = new AddSkillsAdapter(
					NotificationsSettActivity.this, selectedSkillList, false);
			selectedSkillLV.setAdapter(addSkillAdapter);
		}

	}

	@Override
	public void onItemSelected(Object item, int pos) {
		String from = (String) item;
		NotificationSetting notiObj = (NotificationSetting) SetDataTagListAdapter
				.getItem(pos);
		String checkSmsOrEmail = null;
		if (getResources().getString(R.string.Email).equals(from)) {
			checkSmsOrEmail = notiObj.send_email;
		} else if (getResources().getString(R.string.SMS).equals(from)) {
			checkSmsOrEmail = notiObj.send_sms;
		}
		if (checkSmsOrEmail.equals("1")) {
			checkSmsOrEmail = "0";
		} else {
			checkSmsOrEmail = "1";
		}
		if (getResources().getString(R.string.Email).equals(from)) {
			notiListForShowAndSend.get(pos).send_email = checkSmsOrEmail;
		} else {
			notiListForShowAndSend.get(pos).send_sms = checkSmsOrEmail;
		}
		SetDataTagListAdapter.notifyDataSetChanged();
	}

	// ----------------TASK TO Update Notification setting
	// --------------------------------
	private class UpadateNotiSettAsync extends
			AsyncTask<String, Void, NetworkResponse> {

		@Override
		protected void onPreExecute() {
			Util.showProDialog(NotificationsSettActivity.this);
		}

		@Override
		protected NetworkResponse doInBackground(String... params) {
			NetworkResponse response = null;

			if (Util.isDeviceOnline(NotificationsSettActivity.this)) {
				Cmd cmd = null;
				setDataToJSonArray();
				cmd = CmdFactory.updateNotiSettCmd(notiSettArray,
						notiSettCatArray, notiSettSkillArray);
				response = NetworkMgr.httpPost(Config.API_URL,
						cmd.getJsonData());
				if (response != null && response.isSuccess()) {
					setDataTonotificationList(response);
				}

			}

			return response;
		}

		@Override
		protected void onPostExecute(NetworkResponse networkResponse) {
			Util.dimissProDialog();
			if (networkResponse != null) {
				if (!networkResponse.isSuccess()) {
					Util.showCenteredToast(NotificationsSettActivity.this,
							networkResponse.getErrMsg());
				}

			}

		}

	}

	public void setDataToJSonArray() {
		notiSettArray = new JSONArray();
		notiSettCatArray = new JSONArray();
		notiSettSkillArray = new JSONArray();
		for (int k = 0; k < notiListForShowAndSend.size(); k++) {
			if (saveNotiSettMap
					.containsKey(notiListForShowAndSend.get(k).notification_id)) {
				saveNotiSettMap
						.remove(notiListForShowAndSend.get(k).notification_id);
				JSONObject jobje = setDataToJSonObject(notiListForShowAndSend
						.get(k));
				saveNotiSettMap.put(
						notiListForShowAndSend.get(k).notification_id, jobje);
			}
		}
		for (Entry<String, JSONObject> entry : saveNotiSettMap.entrySet()) {

			notiSettArray.put(saveNotiSettMap.get(entry.getKey()));
		}

		for (int l = 0; l < selectedSkillList.size(); l++) {
			notiSettSkillArray.put(selectedSkillList.get(l).skill_id);
		}

		for (int m = 0; m < selectedCategoryList.size(); m++) {
			notiSettCatArray.put(selectedCategoryList.get(m).category_id);
		}
	}

	private JSONObject setDataToJSonObject(
			NotificationSetting notificationSetting) {
		// TODO Auto-generated method stub
		JSONObject jobje = new JSONObject();
		try {
			jobje.put(NotificationSetting.FLD_NOTIFICATION_ID,
					notificationSetting.notification_id);
			jobje.put(NotificationSetting.FLD_SEND_EMAIL,
					notificationSetting.send_email);
			jobje.put(NotificationSetting.FLD_SEND_SMS,
					notificationSetting.send_sms);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jobje;
	}

	private void customDialogForCatAndSKill(final String forWhat,
			boolean showChkBx, final ArrayList<Skill> addSkillList,
			final ArrayList<Category> addCategorylList) {
		final ArrayList<Object> searchList;
		final CustomViewHolder holder;
		holder = new CustomViewHolder();
		final Dialog customDialog = new Dialog(NotificationsSettActivity.this);
		customDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		customDialog.setContentView(R.layout.ed_simple_list);

		holder.llSListSearchBar = (LinearLayout) customDialog
				.findViewById(R.id.llSListSearchBar);
		holder.txtSimpleListTitle = (TextView) customDialog
				.findViewById(R.id.txtSimpleListTitle);
		holder.txtSListDone = (TextView) customDialog
				.findViewById(R.id.txtSListDone);
		holder.imgBtnSimpleListBack = (ImageButton) customDialog
				.findViewById(R.id.imgBtnSimpleListBack);
		holder.listVSimpleList = (ListView) customDialog
				.findViewById(R.id.listVSimpleList);
		holder.llSListSearchBar = (LinearLayout) customDialog
				.findViewById(R.id.llSListSearchBar);
		holder.eTextSimpleListSearch = (EditText) customDialog
				.findViewById(R.id.eTextSimpleListSearch);
		holder.imgBtnSListClearSearch = (ImageButton) customDialog
				.findViewById(R.id.imgBtnSListClearSearch);
		holder.txtSimpleListTitle.setText(forWhat);
		holder.imgBtnSimpleListBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				customDialog.dismiss();
			}
		});
		if (showChkBx) {
			holder.llSListSearchBar.setVisibility(View.VISIBLE);
			searchList = new ArrayList<Object>();
			if (addSkillList != null) {
				searchList.addAll(addSkillList);
			} else {
				searchList.addAll(addCategorylList);
			}
			holder.eTextSimpleListSearch
					.addTextChangedListener(new TextWatcher() {
						@Override
						public void onTextChanged(CharSequence s, int start,
								int before, int count) {
							// TODO Auto-generated method stub
							int textlength = holder.eTextSimpleListSearch
									.getText().toString().length();
							if (textlength > 0) {
								if (addSkillList != null) {
									addSkillList.clear();
								} else {
									addCategorylList.clear();
								}
								for (int i = 0; i < searchList.size(); i++) {
									if (addSkillList != null) {
										Skill skillObj = (Skill) searchList
												.get(i);
										if (skillObj.skill_desc != null) {
											if (skillObj.skill_desc
													.toString()
													.toLowerCase()
													.startsWith(
															holder.eTextSimpleListSearch
																	.getText()
																	.toString())) {
												addSkillList.add(skillObj);
											}
										}
										setDataToSkillDialog(holder,
												addSkillList, null);
									} else {
										Category cateObj = (Category) searchList
												.get(i);
										if (cateObj.category_name != null) {
											if (cateObj.category_name
													.toString()
													.toLowerCase()
													.startsWith(
															holder.eTextSimpleListSearch
																	.getText()
																	.toString())) {
												addCategorylList.add(cateObj);
											}
										}
										setDataToSkillDialog(holder, null,
												addCategorylList);
									}
								}

							} else {
								if (addSkillList != null) {
									addSkillList.clear();
									for (int m = 0; m < searchList.size(); m++) {
										addSkillList.add((Skill) searchList
												.get(m));
									}

									setDataToSkillDialog(holder, addSkillList,
											null);
								} else {
									addCategorylList.clear();
									for (int m = 0; m < searchList.size(); m++) {
										addCategorylList
												.add((Category) searchList
														.get(m));
									}
									setDataToSkillDialog(holder, null,
											addCategorylList);
								}
							}
						}

						@Override
						public void beforeTextChanged(CharSequence s,
								int start, int count, int after) {
							// TODO Auto-generated method stub

						}

						@Override
						public void afterTextChanged(Editable s) {
							// TODO Auto-generated method stub

						}
					});

		} else {
			holder.llSListSearchBar.setVisibility(View.GONE);
		}

		if (addSkillList != null) {
			setDataToSkillDialog(holder, addSkillList, null);
		} else {
			setDataToSkillDialog(holder, null, addCategorylList);
		}

		holder.txtSListDone.setVisibility(View.VISIBLE);
		customDialog.setCancelable(true);
		holder.txtSListDone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (forWhat.equals(getResources().getString(
						R.string.select_skills))) {
					selectedSkillList.clear();
					for (Skill entry : addSkillList) {
						if(entry.isSelected)
						selectedSkillList.add(entry);
					}
				}

				else {

					selectedCategoryList.clear();

					
					for (Category entry : addCategorylList) {
						if(entry.isChecked)
							selectedCategoryList.add(entry);
					}
				}
				customDialog.dismiss();
			}
		});
		customDialog.show();
	}

	private void setDataToSkillDialog(CustomViewHolder holder,
			ArrayList<Skill> addSkillList, ArrayList<Category> addCategoryList) {
		// TODO Auto-generated method stub
		AddSkillsAdapter addSkillAdapterShow = null;
		catHashMap = new HashMap<String, Category>();
		if (addSkillList != null) {
			for (int j = 0; j < addSkillList.size(); j++) {
				boolean checkOrNot = false;
				for (int k = 0; k < selectedSkillList.size(); k++) {
					if (addSkillList.get(j).skill_id
							.equals(selectedSkillList.get(k).skill_id)) {
						checkOrNot = true;
					}
				}
				addSkillList.get(j).isSelected = checkOrNot;
			}

			if (selectedSkillList.size() == 0) {
				for (Skill skillObj : addSkillList) {
					skillObj.isSelected = false;
				}
			}
			addSkillAdapterShow = new AddSkillsAdapter(
					NotificationsSettActivity.this, addSkillList, true);
			holder.listVSimpleList.setAdapter(addSkillAdapterShow);
		}

		else {
			for (int j = 0; j < addCategoryList.size(); j++) {
				boolean checkOrNot = false;
				for (int k = 0; k < selectedCategoryList.size(); k++) {
					if (addCategoryList.get(j).category_id
							.equals(selectedCategoryList.get(k).category_id)) {
						checkOrNot = true;
						if (!catHashMap
								.containsKey(selectedCategoryList.get(k).category_id)) {
							catHashMap.put(String.valueOf(selectedCategoryList
									.get(k).category_id), selectedCategoryList
									.get(k));
						}
					}
				}

				addCategoryList.get(j).isChecked = checkOrNot;
			}

			if (selectedCategoryList.size() == 0) {
				for (Category CatObj : selectedCategoryList) {
					CatObj.isChecked = false;
				}
			}
			addCategoryAdapter = new AddCategoryAdapter(
					NotificationsSettActivity.this, addCategoryList,
					catHashMap, true);
			holder.listVSimpleList.setAdapter(addCategoryAdapter);
		}
	}

	// public void getSkillSListFromDB(ArrayList<Skill> addSkillListFromDB) {
	// Cursor cursor = null;
	// try {
	// cursor = dbMgr.rawQuery(
	// "SELECT * FROM " + TableType.SkillTable.getTableName(),
	// null);
	// cursor.moveToFirst();
	// if (cursor.getCount() > 0) {
	// while (!cursor.isAfterLast()) {
	// Skill skill = new Skill();
	// skill.skill_id = cursor.getString(cursor
	// .getColumnIndex(Skill.FLD_SKILL_ID));
	// skill.skill_desc = cursor.getString(cursor
	// .getColumnIndex(Skill.FLD_SKILL_DESC));
	// skill.isSelected = false;
	// addSkillListFromDB.add(skill);
	// cursor.moveToNext();
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// if (cursor != null) {
	// cursor.close();
	// cursor = null;
	// }
	// }
	// }

	public void getCateListFromDB() {
		Cursor cursor = null;
		try {
			cursor = dbMgr.rawQuery(
					"SELECT * FROM " + TableType.CategoryTable.getTableName(),
					null);
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				while (!cursor.isAfterLast()) {
					Category catObj = new Category();
					catObj.category_id = (long) cursor.getInt((cursor
							.getColumnIndex(Category.FLD_CATEGORY_ID)));

					catObj.category_name = cursor.getString(cursor
							.getColumnIndex(Category.FLD_CATEGORY_NAME));
					catObj.isChecked = false;
					addCatListFromDB.add(catObj);
					cursor.moveToNext();
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

	public class CustomViewHolder {
		public TextView txtSListDone;
		public TextView txtSimpleListTitle;
		public ListView listVSimpleList;
		public ImageButton imgBtnSimpleListBack;
		LinearLayout llSListSearchBar;
		EditText eTextSimpleListSearch;
		ImageButton imgBtnSListClearSearch;
	}

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}
}
