package com.edlplan.ui.fragment;

import android.animation.Animator;

import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.StringRes;

import com.edlplan.framework.easing.Easing;
import com.edlplan.ui.BaseAnimationListener;
import com.edlplan.ui.EasingHelper;

import ru.nsu.ccfit.zuev.osu.online.OnlineManager;
import ru.nsu.ccfit.zuev.osuplus.R;

public class WebViewFragment extends BaseFragment {

    public static final String REGISTER_URL = OnlineManager.hostname + "user/?action=register";
    public static final String PROFILE_URL = OnlineManager.hostname + "profile.php?uid=";

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
        loadingFragment = new LoadingFragment();
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setUserAgentString("osudroid");
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if(loadingFragment.isCreated()) {
                    loadingFragment.dismiss();
                }
            }

            @Override
            public void onPageStarted(WebView view,  String url, Bitmap favicon) {
                if(!loadingFragment.isCreated()) {
                    loadingFragment.show();
                }
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
            if(loadingFragment.isCreated()) {
                loadingFragment.dismiss();
            }
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