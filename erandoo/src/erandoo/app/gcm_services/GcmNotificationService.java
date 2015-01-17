package erandoo.app.gcm_services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import erandoo.app.utilities.SyncAppData;

public class GcmNotificationService extends IntentService {
	public GcmNotificationService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
				//sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
				//sendNotification("Deleted messages on server: "+ extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				SyncAppData syncAppData = SyncAppData.getInstance(getApplicationContext(),extras.getString("id"));
				syncAppData.execute();
				//String msg = extras.getString("msg");
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}
	//Bundle[{id=sync_inbox, msg={"task_id":"111","user_id":"819"}, from=543129283963, android.support.content.wakelockid=2, collapse_key=do_not_collapse}]
}
