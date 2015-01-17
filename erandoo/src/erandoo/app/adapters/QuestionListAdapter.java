package erandoo.app.adapters;

import java.util.ArrayList;
import java.util.HashSet;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.database.Question;

public class QuestionListAdapter extends ArrayAdapter<Question> {
	private ArrayList<Question> _data;
	private Context _context;
	private int _resource;
	private HashSet<Long> selectedQuestionIds = new HashSet<Long>();
	public QuestionListAdapter(Context context, int resource, ArrayList<Question> data,ArrayList<Question> selectedQuestions) {
		super(context, resource, data);
		_data = data;
		_context = context;
		_resource = resource;
		if(selectedQuestions != null){
			for (Question categoryQuestion: selectedQuestions) {
				selectedQuestionIds.add(categoryQuestion.question_id);
			}
		}
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder holder;
		if (view == null) {
			LayoutInflater mLayoutInflater = ((Activity) _context).getLayoutInflater();
			view = mLayoutInflater.inflate(_resource, parent, false);
			holder = new ViewHolder();
			holder.txtSimpleRow = (TextView) view.findViewById(R.id.txtSimpleRow);
			holder.imgVSListRow = (ImageView)view.findViewById(R.id.imgVSListRow);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		Question question = _data.get(position);
		boolean alreadySelected = selectedQuestionIds.contains(question.question_id);
		
		if(question.isSelected || alreadySelected){
			if(alreadySelected){
				question.isSelected = true;
				selectedQuestionIds.remove(question.question_id);
			}
			holder.imgVSListRow.setImageResource(R.drawable.ic_list_check);
		}else{
			holder.imgVSListRow.setImageResource(R.drawable.ic_list_uncheck);
		}
		
		holder.txtSimpleRow.setText(question.question_desc);
		holder.imgVSListRow.setVisibility(View.VISIBLE); 

		if (position % 2 == 0) {
			view.setBackgroundColor(_context.getResources().getColor(R.color.text_Selection_color));
		} else {
			view.setBackgroundColor(_context.getResources().getColor(R.color.trans_color));
		}
		return view;
	}

	static class ViewHolder {
		TextView txtSimpleRow;
		ImageView imgVSListRow;
	}
}
