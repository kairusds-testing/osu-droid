package com.edlplan.ui.fragment;

import android.annotation.TargetApi;
import android.animation.Animator;

import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

import androidx.annotation.StringRes;

import com.edlplan.framework.easing.Easing;
import com.edlplan.ui.BaseAnimationListener;
import com.edlplan.ui.EasingHelper;

import ru.nsu.ccfit.zuev.osu.online.OnlineManager;
import ru.nsu.ccfit.zuev.osuplus.R;

public class WebViewFragment extends BaseFragment {

    public static final String REGISTER_URL = "https://" + OnlineManager.hostname + "user/?action=register";
    public static final String PROFILE_URL = "https://" + OnlineManager.hostname + "profile.php?uid=";

    private WebView webview;
    private String url;

    private LoadingFragment loadingFragment;

    public WebViewFragment(String url) {
        this.url = url;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_webview;
    }

    @Override
    protected void onLoadView() {
        webview = (WebView) findViewById(R.id.web);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUserAgentString("osudroid");
        webSettings.setSupportMultipleWindows(true);

        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(loadingFragment == null && newProgress < 100) {
                    loadingFragment = new LoadingFragment();
                    loadingFragment.show();
                }else if(loadingFragment != null && newProgress == 100) {
                    loadingFragment.dismiss();
                    loadingFragment = null;
                }
            }
        });

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }

            @Override
            @TargetApi(Build.VERSION_CODES.N)
            public boolean shouldOverrideUrlLoading(WebView view,  WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return false;
            }
        });
        webview.loadUrl(url);
        playOnLoadAnim();
    }

    @Override
    public void dismiss() {
        playOnDismissAnim(super::dismiss);
    }

    @Override
    public void callDismissOnBackPress() {
        if(webview.canGoBack()) {
            webview.goBack();
        }else {
            dismiss();
        }
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