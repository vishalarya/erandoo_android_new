package erandoo.app.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import erandoo.app.R;

public class AppHeaderView extends RelativeLayout {
	public TextView txtHLeft;
	private TextView txtHTitle;
	public TextView txtHRight1;
	public TextView txtHRight2;
	public View vRightLine;

	public AppHeaderView(Context context) {
		super(context);
		init(context);
	}

	public AppHeaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public AppHeaderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		if(!isInEditMode()){
			super.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)); 
			LayoutInflater.from(context).inflate(R.layout.ed_app_header_view, this,true);
		//	inflate(getContext(), R.layout.ed_app_header_view, this);
			this.txtHLeft = (TextView) findViewById(R.id.txtHLeft);
			this.txtHTitle = (TextView) findViewById(R.id.txtHTitle);
			this.txtHRight1 = (TextView) findViewById(R.id.txtHRight1);
			this.txtHRight2 = (TextView) findViewById(R.id.txtHRight2);
			this.vRightLine = (View) findViewById(R.id.vRightLine);

			txtHLeft.setOnClickListener((OnClickListener) context);
			txtHRight1.setOnClickListener((OnClickListener) context);
			txtHRight2.setOnClickListener((OnClickListener) context);
		}
	}

	public void setHeaderContent(Object icHLeft, String title,
			Object icHRight1, Object icHRight2, int titleGravity) {
		RelativeLayout.LayoutParams rParams;
		LinearLayout.LayoutParams lParams;
		if (title != null) {
			txtHTitle.setText(title);
			if (titleGravity != -1) {
				txtHTitle.setGravity(titleGravity);
			}
		}

		if (icHLeft != null) {
			txtHLeft.setVisibility(View.VISIBLE);
			if (icHLeft instanceof String) {
				rParams = (RelativeLayout.LayoutParams) txtHLeft
						.getLayoutParams();
				rParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				rParams.setMargins(10, 0, 0, 0);
				txtHLeft.setLayoutParams(rParams);

				txtHLeft.setText((CharSequence) icHLeft);
				txtHLeft.setBackgroundResource(R.drawable.ed_btn_white_border_selector);
			} else if (icHLeft instanceof Drawable) {

				txtHLeft.setCompoundDrawablesWithIntrinsicBounds(
						(Drawable) icHLeft, null, null, null);
			}
		}else{
			txtHLeft.setVisibility(View.GONE);
		}
		if (icHRight1 != null) {
			if (icHRight1 instanceof String) {

				lParams = (LinearLayout.LayoutParams) txtHRight1.getLayoutParams();
				lParams.setMargins(0, 0, 10, 0);
				txtHRight1.setLayoutParams(lParams);

				txtHRight1.setText((CharSequence) icHRight1);
				txtHRight1
						.setBackgroundResource(R.drawable.ed_btn_white_border_selector);

			} else if (icHRight1 instanceof Drawable) {
				txtHRight1.setCompoundDrawablesWithIntrinsicBounds(
						(Drawable) icHRight1, null, null, null);
			}
		}

		if (icHRight2 != null) {
			if (icHRight2 instanceof String) {
				lParams = (LinearLayout.LayoutParams) txtHRight2
						.getLayoutParams();
				lParams.setMargins(0, 0, 10, 0);
				txtHRight2.setLayoutParams(lParams);

				txtHRight2.setText((CharSequence) icHRight2);
				txtHRight2
						.setBackgroundResource(R.drawable.ed_btn_white_border_selector);
			} else if (icHRight2 instanceof Drawable) {
				txtHRight2.setCompoundDrawablesWithIntrinsicBounds(
						(Drawable) icHRight2, null, null, null);
			}
		}

		if ((icHRight1 != null) && (icHRight2 != null)) {
			vRightLine.setVisibility(View.VISIBLE);
		}

		else {
			vRightLine.setVisibility(View.GONE);
		}
	}

}
