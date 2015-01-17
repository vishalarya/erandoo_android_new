package erandoo.app.main.registration;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import erandoo.app.R;
import erandoo.app.utilities.Constants;

public class RegProfileImageDialog extends DialogFragment implements OnClickListener {
	
	private Dialog dialog;
	private TextView txtUseCamera;
	private TextView txtUseExistPhoto;
	private TextView txtUploadPhotoCancel;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		dialog = new Dialog(getActivity());
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.ed_reg_upload_img_dialog);
		init();
		Window window = dialog.getWindow();
		window.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.ed_bg_edit_text));
		window.getAttributes().windowAnimations = R.style.AppDialogPullAnimation;
		window.setGravity(Gravity.BOTTOM);
		window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		dialog.setCancelable(true);
		return dialog;
	}

	private void init() {
		txtUseCamera = (TextView) dialog.findViewById(R.id.txtUseCamera);
		txtUseExistPhoto = (TextView) dialog.findViewById(R.id.txtUseExistPhoto);
		txtUploadPhotoCancel = (TextView) dialog.findViewById(R.id.txtUploadPhotoCancel);
		
		txtUseCamera.setOnClickListener(this);
		txtUseExistPhoto.setOnClickListener(this);
		txtUploadPhotoCancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.txtUseCamera) {
			goToImageView(true);
			dismiss();
		} else if(v.getId() == R.id.txtUseExistPhoto) {
			goToImageView(false);
			dismiss();
		} else if(v.getId() == R.id.txtUploadPhotoCancel) {
			dismiss();
		}
	}
	
	private void goToImageView(boolean fromCamera){
		Intent intent = new Intent();
		try {
			if(fromCamera){
				intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
			}else{
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_PICK);
			}
			getActivity().startActivityForResult(intent, Constants.PICK_PROFILE_IMAGE);
		} catch (Exception e) {
		// Do nothing for now
		}
	}
}
