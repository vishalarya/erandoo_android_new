package erandoo.app.custom;

import erandoo.app.R;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;

public class CustomDialog extends DialogFragment {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = new Dialog(getActivity(),R.style.AppDialogTheme);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().getAttributes().windowAnimations = R.style.AppDialogPullAnimation;
		WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
		layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
		dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT); 
		dialog.setCancelable(true);
		return dialog;
	}
}
