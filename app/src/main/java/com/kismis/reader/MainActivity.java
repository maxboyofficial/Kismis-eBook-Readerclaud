package com.kismis.reader;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    WebView w;
    String insets = "";

    class Bridge {
        @JavascriptInterface
        public void bar(final boolean dark) {
            runOnUiThread(() -> {
                int f = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
                if (!dark) f |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
                w.setSystemUiVisibility(f);
                w.setBackgroundColor(dark ? 0xFF09090B : 0xFFF2F2F7);
            });
        }
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= 29) getWindow().setNavigationBarContrastEnforced(false);
        w = new WebView(this);
        setContentView(w);
        WebSettings s = w.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        s.setAllowFileAccessFromFileURLs(true);
        s.setAllowUniversalAccessFromFileURLs(true);
        w.setOverScrollMode(View.OVER_SCROLL_NEVER);
        w.addJavascriptInterface(new Bridge(), "A");
        w.setOnApplyWindowInsetsListener((v, i) -> {
            float d = getResources().getDisplayMetrics().density;
            insets = "var s=document.documentElement.style;s.setProperty('--st','" + (i.getSystemWindowInsetTop() / d)
                    + "px');s.setProperty('--sb','" + (i.getSystemWindowInsetBottom() / d) + "px');";
            w.evaluateJavascript(insets, null);
            return i;
        });
        w.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView v, String url) {
                v.evaluateJavascript(insets, null);
            }
        });
        w.loadUrl("file:///android_asset/index.html");
    }

    @Override
    public void onBackPressed() {
        w.evaluateJavascript("back()", r -> {
            if (!"true".equals(r)) finish();
        });
    }
}
