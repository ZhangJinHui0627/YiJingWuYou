package com.yijing.wuyou;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * 颐境无忧 - DeepSeek 官方大模型 API 原生直连服务
 * 开发者：张锦慧，协助者：许明萱、胡明祖、张睿格、张博垚
 * 核心技术栈：端侧 HTTPS 直连 · Bearer Auth · Slot-Filling 槽位反问 · 晚辈人设工程
 * 版本：内测第二版 (v1.1.0)
 */
public class DeepSeekService {
    public static final String DEFAULT_API_KEY = "sk-d866aad9d29e49859fde91e22dadf60e";
    private static final String API_URL = "https://api.deepseek.com/chat/completions";

    public static String queryDeepSeek(String userPrompt, String customKey) {
        String apiKey = (customKey != null && !customKey.trim().isEmpty()) ? customKey.trim() : DEFAULT_API_KEY;
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(18000);
            conn.setDoOutput(true);

            String sysPrompt = "你是由张锦慧团队（开发者：张锦慧，协助者：许明萱、胡明祖、张睿格、张博垚）开发的智慧适老语音助手【颐境无忧】。你是陪伴在长辈身边的专属孝顺晚辈智能管家。严禁称呼对方为孩子，必须尊称长辈或您！严禁输出Markdown符号（严禁输出 **、##、* 等原始标记）。请在160字以内作答：第一句定心丸安抚情绪，第二步给出3点马上行动建议，第三步提醒若嘴麻胸闷按右上角SOS呼救。通俗温暖，重点突出。";
            String safeUser = userPrompt.replace((char)34, (char)39).replace((char)10, ' ');
            String safeSys = sysPrompt.replace((char)34, (char)39);

            String q = String.valueOf((char)34);
            String jsonPayload = "{" + q + "model" + q + ":" + q + "deepseek-chat" + q + "," + q + "messages" + q + ":["
                    + "{" + q + "role" + q + ":" + q + "system" + q + "," + q + "content" + q + ":" + q + safeSys + q + "},"
                    + "{" + q + "role" + q + ":" + q + "user" + q + "," + q + "content" + q + ":" + q + safeUser + q + "}"
                    + "]," + q + "temperature" + q + ":0.7," + q + "max_tokens" + q + ":400}";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
            }

            int code = conn.getResponseCode();
            if (code == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();
            } else {
                return "{\"error\":true,\"code\":" + code + ",\"msg\":\"云端通信繁忙，请稍后再试\"}";
            }
        } catch (Exception e) {
            return "{\"error\":true,\"msg\":\"网络无法连接: " + e.getMessage() + "\"}";
        }
    }
}
