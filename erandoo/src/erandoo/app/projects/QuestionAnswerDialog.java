package erandoo.app.projects;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.custom.CustomDialog;
import erandoo.app.custom.QuestionAnswerView;
import erandoo.app.database.Question;
import erandoo.app.main.CraftABidActivity;
import erandoo.app.utilities.Constants;

public class QuestionAnswerDialog extends CustomDialog implements OnClickListener {
	private View view;
	private ImageView imvQuestionBack;
	private TextView txtDHTitle;
	private TextView txtDHRight;
	private LinearLayout llQuesAnsContainer;
	private QuestionAnswerView answerView;
	private ArrayList<Question> questions = new ArrayList<Question>();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.ed_question, container, false);
		initialize();
		return view;
	}
	
	@SuppressWarnings("unchecked")
	private void initialize() {
		txtDHTitle = (TextView) view.findViewById(R.id.txtDHTitle);
		txtDHRight = (TextView) view.findViewById(R.id.txtDHRight);
		imvQuestionBack = (ImageView) view.findViewById(R.id.imgDBack);
		llQuesAnsContainer = (LinearLayout) view.findViewById(R.id.llQuesAnsContainer);
		txtDHTitle.setText(getResources().getString(R.string.Question));
		
		txtDHRight.setVisibility(View.GONE);
		imvQuestionBack.setOnClickListener(this);

		try{
			if(getArguments().getString(Constants.PARENT_VIEW).toString().equals("proposalDetail")){
				questions = (ArrayList<Question>) getArguments().getSerializable(Constants.SERIALIZABLE_DATA);
				for (Question question : questions) {
					answerView = new QuestionAnswerView(getActivity());
					answerView.setResultText(question, true);
					llQuesAnsContainer.addView(answerView);
				}
			}else if(getArguments().getString(Constants.PARENT_VIEW).toString().equals("craftABid")){
				int index = 0;
				for (Question question : CraftABidActivity.questionList) {
					answerView = new QuestionAnswerView(getActivity());
					answerView.setResultText(question, false);
					answerView.setTag(String.valueOf(index)); 
					llQuesAnsContainer.addView(answerView);
					index ++;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.imgDBack) {
			dismissDialog();
		}
	}
	
	private void dismissDialog() {
		QuestionAnswerDialog.this.dismiss();
	}
}
