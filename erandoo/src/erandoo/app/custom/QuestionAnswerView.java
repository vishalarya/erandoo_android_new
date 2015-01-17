package erandoo.app.custom;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.database.Question;
import erandoo.app.main.CraftABidActivity;

public class QuestionAnswerView extends LinearLayout{

	private TextView txtQRQuestion;
	private EditText etQRAnswer;
	private TextView txtQRPDAnswer;
	boolean isProjectDetails = false;
	
	public QuestionAnswerView(Context context) {
		super(context);
		super.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		LayoutInflater.from(context).inflate(R.layout.ed_question_row,this, true);
	}
	
	public void setResultText(final Question data, boolean isProjectDetails){
		txtQRQuestion = (TextView) findViewById(R.id.txtQRQuestion);
		txtQRPDAnswer = (TextView)findViewById(R.id.txtQRPDAnswer);
		etQRAnswer = (EditText) findViewById(R.id.etQRAnswer);
		
		txtQRQuestion.setText("Q." + data.question_desc);	

		etQRAnswer.setText(data.reply_desc);

		if (isProjectDetails) { 
			txtQRPDAnswer.setVisibility(View.VISIBLE);
			etQRAnswer.setVisibility(View.GONE);
			etQRAnswer.setFocusable(false);
			txtQRPDAnswer.setText("Ans. " + data.reply_desc);
		}else{
			txtQRPDAnswer.setVisibility(View.GONE);
			etQRAnswer.setVisibility(View.VISIBLE);
			etQRAnswer.setFocusable(true);
		}
		
		etQRAnswer.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence cStr, int arg1, int arg2, int arg3) {
				try{
					int index  = Integer.parseInt(getTag().toString()); 
					CraftABidActivity.questionList.get(index).reply_desc = cStr.toString();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
			
			@Override
			public void afterTextChanged(Editable arg0) {}
		});
	}
}
