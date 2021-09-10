package ru.nsu.ccfit.zuev.osu.online;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.JavascriptInterface;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import ru.nsu.ccfit.zuev.osu.MainActivity;
import ru.nsu.ccfit.zuev.osuplus.R;

public class WebViewActivity extends AppCompatActivity {

    public static final String EXTRA_TYPE = "ru.nsu.ccfit.zuev.osuplus.WebViewActivityType";
    public static final int TYPE_REGISTER = 0;
    public static final int TYPE_LOGIN = 1;
    public static final int TYPE_VIEW_PROFILE = 2;
    public static final String JAVASCRIPT_INTERFACE_NAME = "Android";

    public static final String LOGIN_URL = OnlineManager.host + "user/?action=login";
    public static final String REGISTER_URL = OnlineManager.host + "user/?action=register";
    public static final String PROFILE_URL = OnlineManager.host + "profile.php?uid=%d";

    private WebView webview;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_activity);

        mActivity = GlobalManager.getInstance().getMainActivity();

        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClientImpl());

        switch(getIntent().getIntExtra(EXTRA_TYPE, -1)) {
            case TYPE_REGISTER:
                webview.addJavascriptInterface(new RegisterTypeInterface(),
                    JAVASCRIPT_INTERFACE_NAME);
                webview.loadUrl(REGISTER_URL);
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

	private class ViewProfileTypeInterface {
	}

}