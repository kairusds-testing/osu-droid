package ru.nsu.ccfit.zuev.osu.online;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebChromeClient;
import android.webkit.JavascriptInterface;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import ru.nsu.ccfit.zuev.osu.GlobalManager;
import ru.nsu.ccfit.zuev.osu.MainActivity;
import ru.nsu.ccfit.zuev.osuplus.R;

public class WebViewActivity extends AppCompatActivity {

    public static final String EXTRA_INFO = "ru.nsu.ccfit.zuev.osuplus.WebViewActivityExtra";
    public static final String EXTRA_URL = "ru.nsu.ccfit.zuev.osuplus.WebViewActivityURL";
    public static final String JAVASCRIPT_INTERFACE_NAME = "Android";

    public static final String ENDPOINT = "https://acivev.com/";
    public static final String LOGIN_URL = ENDPOINT + "user/?action=login";
    public static final String REGISTER_URL = ENDPOINT + "user/?action=register";
    public static final String PROFILE_URL = ENDPOINT + "profile.php?uid=";

    private WebView webview;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_activity);

        mActivity = GlobalManager.getInstance().getMainActivity();

        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setUserAgentString("osudroid");
        webview.setWebChromeClient(new WebChromeClient());

        switch(getIntent().getStringExtra(EXTRA_URL)) {
            case LOGIN_URL:
                webview.loadUrl(LOGIN_URL);
                break;

            case REGISTER_URL:
                webview.addJavascriptInterface(new RegisterTypeInterface(),
                    JAVASCRIPT_INTERFACE_NAME);
                webview.loadUrl("https://www.whatismybrowser.com/detect/what-is-my-user-agent");
                break;

            case PROFILE_URL:
                String extraInfo = getIntent().getStringExtra(EXTRA_INFO);
                if(extraInfo == null) {
                    closeActivity();
                    break;
                }
                webview.loadUrl(PROFILE_URL + EXTRA_INFO);
                break;

            default:
                closeActivity();
        }
    }

    private void closeActivity() {
        mActivity.startActivity(new Intent(mActivity, MainActivity.class));
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(webview.canGoBack()) {
                webview.goBack();
            }else {
                closeActivity();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private class RegisterTypeInterface {
        @JavascriptInterface
        public void showSnackbar(String message) {
            Snackbar.make(findViewById(android.R.id.content), message, 1500).show();
        }
    }

    private class LoginTypeInterface {
    }

}