package co.oysterbay.stylup;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;

public class StylupGcmListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);
    }
}
