package com.lob.musicshare.util.web;

import com.lob.musicshare.util.Debug;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ServerConnectionUtils {
    public static String getContent(String url) {
        String content = null;
        try {
            Debug.log("Connecting\nto -> " + url);

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = new OkHttpClient().newCall(request).execute();
            content = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }
}
