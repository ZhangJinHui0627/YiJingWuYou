package com.yijing.wuyou;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.PermissionRequest;
import android.content.pm.PackageManager;
import android.Manifest;
import android.graphics.Color;

/**
 * 颐境无忧 - 主入口 Activity
 * 消除异形屏顶部黑边、沉浸式状态栏、侧滑返回支持
 * 开发者：张锦慧，协助者：许明萱、胡明祖、张睿格、张博垚
 * 核心技术栈：Android 15 (API 35) · Java 17 · Window Cutout SHORT_EDGES 穿透 · 沉浸式透明状态栏
 * 版本：内测第二版 (v1.1.0)
 */
public class MainActivity extends Activity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. 彻底消灭顶部黑边：沉浸式透明状态栏并延展至刘海/挖孔区域
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);

        // 设置状态栏文字图标为深色，配合浅色牛奶雾玻璃背景
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = window.getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        // 允许页面延伸到异形屏/打孔屏顶部短边（彻底消除黑色横条）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            window.setAttributes(lp);
        }

        // 2. 申请必要运行时权限
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET}, 101);
        }

        // 3. 构建 WebView
        webView = new WebView(this);
        webView.setBackgroundColor(Color.TRANSPARENT);
        setContentView(webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setMediaPlaybackRequiresUserGesture(false);

        // 注入原生 Java 桥梁
        webView.addJavascriptInterface(new AppBridge(this), "NativeBridge");

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                request.grant(request.getResources());
            }
        });

        webView.loadUrl("file:///android_asset/index.html");
    }

    // 4. 接管物理返回手势与侧滑返回上一步
    @Override
    public void onBackPressed() {
        if (webView != null) {
            webView.evaluateJavascript("if(typeof handleNativeBack === 'function'){ handleNativeBack(); } else { false; }", value -> {
                if ("false".equals(value) || "null".equals(value)) {
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        MainActivity.super.onBackPressed();
                    }
                }
            });
        } else {
            super.onBackPressed();
        }
    }
}
