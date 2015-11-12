package co.oysterbay.stylup;


import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

public class StylupRegistrationService extends IntentService {
    private static final String TAG = "StylUp/" + StylupRegistrationService.class.getName();

    public StylupRegistrationService() {
        super(TAG);
    }

    public StylupRegistrationService(String name) {
        super(name);
    }

    private static String gcmToken;
    public static String getGcmToken() {
        return gcmToken;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        synchronized (TAG) {
            try {
                InstanceID instanceID = InstanceID.getInstance(this);
                gcmToken = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
