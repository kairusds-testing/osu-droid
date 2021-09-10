package ru.nsu.ccfit.zuev.osu.online;

import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;

public class WebViewClientImpl extends WebViewClient {

    // >= api 24
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        if(request.getUrl().toString().startsWith(OnlineManager.host)) return false;
        return true;
    }

    // < api 24
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if(url.startsWith(OnlineManager.host)) return false;
        return true;
    }

}