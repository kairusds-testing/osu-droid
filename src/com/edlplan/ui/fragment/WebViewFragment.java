package ru.nsu.ccfit.zuev.osu.online;

import android.animation.Animator;
import android.view.View;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebChromeClient;

import androidx.annotation.StringRes;

import com.edlplan.framework.easing.Easing;
import com.edlplan.ui.BaseAnimationListener;
import com.edlplan.ui.EasingHelper;

import ru.nsu.ccfit.zuev.osu.GlobalManager;
import ru.nsu.ccfit.zuev.osuplus.R;

public class WebViewFragment extends BaseFragment {

    public static final String REGISTER_URL = OnlineManager.hostname + "user/?action=register";
    public static final String PROFILE_URL = OnlineManager.hostname + "profile.php?uid=";

    private WebView webview;
    private String url;

    public WebViewFragment() {
        webview = (WebView) findViewById(R.id.webview);
        String packageName = GlobalManager.getInstance().getMainActivity().getPackageName();
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setUserAgentString(packageName);
        webview.setWebChromeClient(new WebChromeClient());
        webview.setOnKeyListener((v, keyCode, event) -> {
            if(event.getAction() != KeyEvent.ACTION_DOWN) return true;
            if(keyCode == KeyEvent.KEYCODE_BACK) {
                if(webview.canGoBack()) {
                    webview.goBack();
                }else {
                    dismiss();
                }
                return true;
            }
            return false;
        });
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_webview;
    }

    @Override
    protected void onLoadView() {
        playOnLoadAnim();
    }

    @Override
    public void dismiss() {
        playOnDismissAnim(super::dismiss);
    }

    public WebViewFragment setURL(String url) {
        this.url = url;
        return this;
    }

    protected void playOnLoadAnim() {
        View body = findViewById(R.id.fullLayout);
        body.setTranslationY(100);
        body.animate().cancel();
        body.animate()
            .translationY(0)
            .setDuration(200)
            .setInterpolator(EasingHelper.asInterpolator(Easing.InOutQuad))
            .start();
        playBackgroundHideInAnim(200);
    }

    protected void playOnDismissAnim(Runnable runnable) {
        View body = findViewById(R.id.fullLayout);
        body.animate().cancel();
        body.animate()
            .translationY(100)
            .setDuration(200)
            .setInterpolator(EasingHelper.asInterpolator(Easing.InOutQuad))
            .setListener(new BaseAnimationListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            })
            .start();
        playBackgroundHideOutAnim(200);
    }

}