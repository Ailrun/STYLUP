package co.oysterbay.stylup;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.CookieSyncManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;


public class MainActivity extends Activity {
    private static final String TAG = "StylUp/" + MainActivity.class.getName();

    public WebView web_view;
    public ImageView title;
    public ImageView title_logo;
    public ImageView loading_error;
    public ImageView restart_button;
    public TimerTask title_timer_task;
    public Timer title_timer;
    public boolean flag_connected = false;
    public boolean first_on = true;
    private GregorianCalendar setLastFinishTry = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().startSync();

        initView();

        if (isConnected()) {
            flag_connected = true;
        } else {
            Log.e("app", "APP WIFI ERROR");
        }

        Log.e("app", "test>>..>>>,>,>.>..>");
    }

    private void initView() {
        web_view = (WebView) findViewById(R.id.web_view);
        title = (ImageView) findViewById(R.id.title);
        title_logo = (ImageView) findViewById(R.id.title_logo);
        loading_error = (ImageView) findViewById(R.id.loading_error);
        restart_button = (ImageView) findViewById(R.id.restart_button);


        web_view.setWebChromeClient(new WebChromeClient());
        web_view.setWebViewClient(new WebViewClient() {
        });
        WebSettings settings = web_view.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDefaultTextEncodingName("utf-8");

        settings.setAllowFileAccess(true);
        web_view.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        web_view.setBackgroundColor(Color.WHITE);
        web_view.clearCache(true);
        web_view.setInitialScale(50);
        web_view.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        web_view.getSettings().setLoadWithOverviewMode(true);
        web_view.getSettings().setUseWideViewPort(true);


        web_view.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {

                super.onReceivedTitle(view, title);

                loading_error.setVisibility(View.GONE);
                restart_button.setVisibility(View.GONE);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                if (message.equals("EXIT")) {
                    if ((setLastFinishTry == null) ||
                            ((new GregorianCalendar().getTimeInMillis()
                                    - setLastFinishTry.getTimeInMillis()) > 3500)) {
                        Toast.makeText(MainActivity.this, getString(R.string.app_exit_announce), Toast.LENGTH_SHORT).show();
                        setLastFinishTry = new GregorianCalendar();
                    } else {
                        finish();
                    }
                    result.confirm();
                    return true;
                } else {
                    return super.onJsAlert(view, url, message, result);
                }
            }
        });

        web_view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                Log.d(TAG, "keyCode is : " + keyCode + "\nEvent is : " + event.toString());
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        web_view.loadUrl("javascript:goto_prev_page()");
                    }
                    /// startActivityForResult(new Intent(MainActivity.this, CancelComfirmActivity.class), CancelCode);
                    return true;
                }
                return false;
            }
        });
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else {
            return true;
        }
    }

    private void connectionCheck() {
        title_timer_task = new TimerTask() {
            public void run() {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            title.setVisibility(View.GONE);
                            title_logo.setVisibility(View.GONE);
                        }
                    });
                } catch (Exception x) {
                    Log.e(TAG, "Timer exception.");

                }
            }
        };

        title_timer = new Timer();
        title_timer.schedule(title_timer_task, 3000);


        if (isConnected()) {
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loading_error.setVisibility(View.GONE);
                        restart_button.setVisibility(View.GONE);

                    }
                });
            } catch (Exception x) {
                Log.e(TAG, "Timer exception...>.." + ".....>>>..>..>>.>>>>>>>...");


            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CookieSyncManager.getInstance().stopSync();

        int gpsError = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        switch (gpsError) {
            case ConnectionResult.SUCCESS :
                break;
            case ConnectionResult.SERVICE_MISSING :
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED :
            case ConnectionResult.SERVICE_DISABLED :
            case ConnectionResult.SERVICE_INVALID :
            default :
                Toast.makeText(this, getString(R.string.google_play_service_fail), Toast.LENGTH_SHORT).show();
                GooglePlayServicesUtil.getErrorDialog(ConnectionResult.SERVICE_MISSING, this, 1).show();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        CookieSyncManager.getInstance().stopSync();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    //final int CancelCode = 1;

    /**
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "oAR");
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CancelCode) {
            if (resultCode == CancelComfirmActivity.QUIT) {
                finish();
            }
        }
    }*/

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (first_on) {
            Window window = getWindow();
            Rect windowRect = new Rect();
            window.getDecorView().getWindowVisibleDisplayFrame(windowRect);

            Log.d(TAG, windowRect.toString());
            Log.d(TAG, "" + windowRect.top);
            Log.d(TAG, "" + windowRect.bottom);
            Log.d(TAG, "" + windowRect.height());
            Log.d(TAG, "" + windowRect.width());

            DisplayMetrics outMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(outMetrics);

            Log.d(TAG, "" + outMetrics.density);

            if (web_view != null) {
                web_view.loadUrl(Constants.server + "index.php/?"
                        + "height=" + windowRect.height() / outMetrics.density
                        + "&width=" + windowRect.width() / outMetrics.density
                        + "&time=" + GregorianCalendar.getInstance().getTimeInMillis());
            }

            connectionCheck();
            first_on = false;
        }
    }

    /// use this with handler and WebView.addJavascriptInterface function.
    /**
    private class FinisherInterface {
        private boolean flag;

        public void finishApp() {

        }
    }*/
}














