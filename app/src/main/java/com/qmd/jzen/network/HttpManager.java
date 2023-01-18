package com.qmd.jzen.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.orhanobut.logger.Logger;
import com.qmd.jzen.entity.Cookie;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by junzi on 2017/12/25.
 */

public class HttpManager {

    private String url;
    OkHttpClient client;

    public HttpManager(String url) {
        this.url = url;
        client = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS).build();
    }

    /**
     * 获取目标url的文本数据
     *
     */
    public String getTextData() {
        try {
            Request request = new Request.Builder().url(url)
                    .header("Referer", "https://y.qq.com/portal/profile.html")
                    .header("user-agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36")
                    .header("cookie", Cookie.getCookie())
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (Exception ex) {
            Logger.e("异常:" + ex.toString());
        }

        return "";
    }

    /**
     * 获取目标url的图片数据
     *
     * @return
     */
    public Bitmap getImageData() {
        Bitmap bitmap = null;
        try {
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                InputStream stream = response.body().byteStream();
                bitmap = BitmapFactory.decodeStream(stream);
                stream.close();
            }

        } catch (Exception ex) {
            Logger.e("异常:" + ex.getMessage());
        }

        return bitmap;
    }

    // 判断地址是否成功
    public boolean isSuccess() {
        try {
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            return response.isSuccessful();

        } catch (Exception e) {
            Logger.e("异常:" + e.getMessage());
            return false;
        }
    }

    // 只是提交数据
    public void postData(String json) {
        //请求body
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Headers headers = new Headers.Builder().add("Content-Type", "application/json").build();
        //请求组合创建
        Request request = new Request.Builder().url(url).post(body).headers(headers).build();
        //发起请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException ex) {
                Logger.e(ex.getMessage());
                Logger.e("请求失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Logger.e(response.body().string());
            }
        });
    }

    // 附带返回数据
    public String postDataWithResult(String data) {
        try {
            //请求body
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), data);
            Headers headers = new Headers.Builder().add("Content-Type", "application/json")
                    .add("Referer", "https://y.qq.com/portal/profile.html")
                    .add("user-agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36")
                    .add("cookie", Cookie.getCookie())
                    .build();
            //请求组合创建
            Request request = new Request.Builder().url(url).post(body).headers(headers).build();
            //发起请求
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (Exception ex) {
            Logger.e("异常:" + ex.getMessage());
        }
        return "";
    }


}
