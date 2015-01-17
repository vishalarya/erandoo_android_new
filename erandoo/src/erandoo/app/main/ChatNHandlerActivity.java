package erandoo.app.main;

import erandoo.app.mqtt.ErandooMqttMessage;
import erandoo.app.utilities.Constants;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ChatNHandlerActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = new Intent(this, ChatViewActivity.class);
		intent.putExtra(Constants.SERIALIZABLE_DATA, (ErandooMqttMessage) getIntent().getSerializableExtra(Constants.SERIALIZABLE_DATA));
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		ChatNHandlerActivity.this.finish();
	}
}
