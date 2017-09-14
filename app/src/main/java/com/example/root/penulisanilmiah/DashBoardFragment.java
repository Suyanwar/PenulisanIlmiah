package com.example.root.penulisanilmiah;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.HashMap;
import java.util.Map;

public class DashBoardFragment extends Fragment {
    View v7;
    WebView dashboard;
    public DashBoardFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v7 = inflater.inflate(R.layout.fragment_dashboard, container, false);
        dashboard = (WebView)v7.findViewById(R.id.dashboard_web);
        dashboard.getSettings().setJavaScriptEnabled(true);
        dashboard.getSettings().setAppCacheEnabled(true);
        dashboard.getSettings().setBuiltInZoomControls(true);
        dashboard.getSettings().setPluginState(WebSettings.PluginState.ON);
        dashboard.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        CookieSyncManager.createInstance(getActivity());
        CookieManager cm = CookieManager.getInstance();
        cm.removeSessionCookie();
        String cookieString = "param=value";
        cm.setCookie("app.ubidots.com", cookieString);
        CookieSyncManager.getInstance().sync();
        Map<String, String> header = new HashMap<>();
        header.put("Cookie", cookieString);
        dashboard.loadUrl("https://app.ubidots.com/ubi/insights/#/list", header);
        return v7;
    }

}
