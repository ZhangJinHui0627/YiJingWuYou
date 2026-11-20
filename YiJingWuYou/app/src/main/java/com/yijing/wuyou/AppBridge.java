package com.yijing.wuyou;

import android.content.Context;
import android.webkit.JavascriptInterface;

/**
 * 颐境无忧 - Java 与 前端 JS 交互原生桥梁
 * 开发者：张锦慧，协助者：许明萱、胡明祖、张睿格、张博垚
 * 核心技术栈：Android SDK 35 · Java 17 · @JavascriptInterface 原生桥梁
 * 版本：内测第二版 (v1.1.0)
 */
public class AppBridge {
    private final Context context;

    public AppBridge(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public String callDeepSeek(String prompt, String customKey) {
        return DeepSeekService.queryDeepSeek(prompt, customKey);
    }

    @JavascriptInterface
    public String getWeatherInfo() {
        return WeatherService.getRealWeather(context);
    }

    @JavascriptInterface
    public boolean checkNetwork() {
        return WeatherService.isNetworkAvailable(context);
    }

    @JavascriptInterface
    public String getTechStack() {
        return "Android 15 (API 35) · Java 17 · DeepSeek-V3 · HTML5/CSS3 牛奶雾玻璃 · GSM Telephony · wttr.in";
    }

    @JavascriptInterface
    public String getAuthor() {
        return "开发者：张锦慧，协助者：许明萱、胡明祖、张睿格、张博垚";
    }

    @JavascriptInterface
    public String getVersion() {
        return "内测第二版 (v1.1.0)";
    }
}
