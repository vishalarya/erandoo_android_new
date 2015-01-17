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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import erandoo.app.R;
import erandoo.app.adapters.QuestionListAdapter;
import erandoo.app.custom.CustomDialog;
import erandoo.app.custom.IListItemClickListener;
import erandoo.app.database.Question;
import erandoo.app.main.AppGlobals;
import erandoo.app.utilities.Util;

public class QuestionListDialog extends CustomDialog implements
		OnClickListener, TextWatcher {
	private View view;
	private ImageButton imgBtnSimpleListBack;
	private ListView listVSimpleList;
	private EditText eTextSimpleListSearch;
	private TextView txtSimpleListTitle;
	private ImageButton imgBtnSListClearSearch;
	private TextView txtSListDone;
	private QuestionListAdapter adapter;
	private ArrayList<Question> selectedQuestions;
	private IListItemClickListener target;
	private int textlength = 0;
	private ArrayList<Question> teamList;
	private ArrayList<Question> searchedList = new ArrayList<Question>(0);
	private boolean isSearch = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.ed_simple_list, container, false);
		initialize();
		return view;
	}

	private void initialize() {
		getDialog().getWindow().getAttributes().windowAnimations = R.style.AppDialogFadeAnimation;
		listVSimpleList = (ListView) view.findViewById(R.id.listVSimpleList);
		imgBtnSimpleListBack = (ImageButton) view
				.findViewById(R.id.imgBtnSimpleListBack);
		eTextSimpleListSearch = (EditText) view
				.findViewById(R.id.eTextSimpleListSearch);
		txtSimpleListTitle = (TextView) view
				.findViewById(R.id.txtSimpleListTitle);
		txtSListDone = (TextView) view.findViewById(R.id.txtSListDone);
		imgBtnSListClearSearch = (ImageButton) view
				.findViewById(R.id.imgBtnSListClearSearch);

		txtSimpleListTitle.setText(view.getContext().getResources()
				.getString(R.string.Questions));
		txtSListDone.setVisibility(View.VISIBLE);

		imgBtnSimpleListBack.setOnClickListener(this);
		imgBtnSListClearSearch.setOnClickListener(this);
		txtSListDone.setOnClickListener(this);
		eTextSimpleListSearch.addTextChangedListener(this);

		listVSimpleList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (isSearch) {
					if (searchedList.get(position).isSelected) {
						searchedList.get(position).isSelected = false;
					} else {
						searchedList.get(position).isSelected = true;
					}
				} else {
					if (AppGlobals.categoryQuestions.get(position).isSelected) {
						AppGlobals.categoryQuestions.get(position).isSelected = false;
					} else {
						AppGlobals.categoryQuestions.get(position).isSelected = true;
					}
				}
				adapter.notifyDataSetChanged();
			}
		});

		if (AppGlobals.categoryQuestions.size() > 0) {
			setQuestionListData();
		} else {

			Util.showCenteredToast(getActivity(),getResources().getString(R.string.msg_noQuestionsFound));
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.imgBtnSimpleListBack) {
			dismissDialog();
		} else if (v.getId() == R.id.imgBtnSListClearSearch) {
			eTextSimpleListSearch.setText("");
		} else if (v.getId() == R.id.txtSListDone) {
			target.onItemSelected(getSelectedQuestions(), 0);
			dismissDialog();
		}
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		teamList = AppGlobals.categoryQuestions;
		try {
			textlength = eTextSimpleListSearch.getText().toString().length();
			if (textlength > 0) {
				searchedList.clear();
				for (int i = 0; i < teamList.size(); i++) {
					if (teamList.get(i).question_desc != null) {
						if (teamList.get(i).question_desc
								.toString()
								.toLowerCase()
								.contains(
										((CharSequence) eTextSimpleListSearch
												.getText().toString()
												.toLowerCase()
												.subSequence(0, textlength)))) {
							searchedList.add(teamList.get(i));
						}
					}
				}
				isSearch = true;
			} else {
				isSearch = false;
			}
			setQuestionListData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setQuestionListData() {
		clearSelection();
		if (isSearch) {
			adapter = new QuestionListAdapter(getActivity(),
					R.layout.ed_simple_list_row, searchedList,
					selectedQuestions);
		} else {
			adapter = new QuestionListAdapter(getActivity(),
					R.layout.ed_simple_list_row, AppGlobals.categoryQuestions,
					selectedQuestions);
		}
		adapter.setNotifyOnChange(true);
		listVSimpleList.setAdapter(adapter);
	}

	private ArrayList<Question> getSelectedQuestions() {
		ArrayList<Question> arrayList = new ArrayList<Question>(0);
		if (isSearch) {
			for (Question question : searchedList) {
				if (question.isSelected) {
					arrayList.add(question);
				}
			}
		} else {
			for (Question question : AppGlobals.categoryQuestions) {
				if (question.isSelected) {
					arrayList.add(question);
				}
			}
		}

		return arrayList;
	}

	private void clearSelection() {
		if (AppGlobals.categoryQuestions.size() > 0 || searchedList.size() > 0) {
			if (isSearch) {
				for (int i = 0; i < searchedList.size(); i++) {
					searchedList.get(i).isSelected = false;
				}
			} else {
				for (int i = 0; i < AppGlobals.categoryQuestions.size(); i++) {
					AppGlobals.categoryQuestions.get(i).isSelected = false;
				}
			}
		}
	}

	private void dismissDialog() {
		Util.hideKeypad(view.getContext(), eTextSimpleListSearch);
		QuestionListDialog.this.dismiss();
	}

	// --------------------------CUSTOM EVENT
	public static QuestionListDialog getInstance(
			ArrayList<Question> selectedQuestions,
			final IListItemClickListener target) {
		QuestionListDialog fragment;
		fragment = new QuestionListDialog();
		fragment.selectedQuestions = selectedQuestions;
		fragment.setTarget(target);
		return fragment;
	}

	public void setTarget(IListItemClickListener target) {
		this.target = target;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void afterTextChanged(Editable s) {
	}
}