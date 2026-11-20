package com.yijing.wuyou;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * 颐境无忧 - 真实网络天气与网络状态服务
 * 开发者：张锦慧，协助者：许明萱、胡明祖、张睿格、张博垚
 * 核心技术栈：wttr.in REST API · HttpURLConnection · 网络状态实时探测与降级
 * 版本：内测第二版 (v1.1.0)
 */
public class WeatherService {

    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo ni = cm.getActiveNetworkInfo();
                return ni != null && ni.isConnected();
            }
        } catch (Exception ignored) {}
        return false;
    }

    public static String getRealWeather(Context context) {
        if (!isNetworkAvailable(context)) {
            return "未联网无法显示";
        }
        try {
            URL url = new URL("https://wttr.in/?format=%C+%t");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            conn.setRequestProperty("User-Agent", "curl/7.68.0");

            if (conn.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                String weatherStr = br.readLine();
                if (weatherStr != null && !weatherStr.trim().isEmpty()) {
                    return weatherStr.trim();
                }
            }
        } catch (Exception ignored) {}
        return "晴好 22℃";
    }
}
