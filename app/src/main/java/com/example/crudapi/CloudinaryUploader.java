package com.example.crudapi;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public final class CloudinaryUploader {

    public interface UploadListener {
        void onUploaded(String secureUrl);

        void onError(String message);
    }

    private static final OkHttpClient CLIENT = new OkHttpClient();

    private CloudinaryUploader() {
    }

    public static boolean isConfigured(Context context) {
        String cloud = trimToNull(context.getString(R.string.cloudinary_cloud_name));
        String preset = trimToNull(context.getString(R.string.cloudinary_upload_preset));
        return cloud != null && preset != null
                && !cloud.equalsIgnoreCase("YOUR_CLOUD_NAME")
                && !preset.equalsIgnoreCase("YOUR_UPLOAD_PRESET");
    }

    @Nullable
    private static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    public static void uploadImage(Context appContext, File file, UploadListener listener) {
        Context context = appContext.getApplicationContext();
        String cloud = trimToNull(context.getString(R.string.cloudinary_cloud_name));
        String preset = trimToNull(context.getString(R.string.cloudinary_upload_preset));
        if (cloud == null || preset == null) {
            listener.onError(context.getString(R.string.err_cloudinary_config));
            return;
        }

        String url = "https://api.cloudinary.com/v1_1/" + cloud + "/image/upload";
        MediaType jpeg = MediaType.parse("image/jpeg");
        RequestBody fileBody = RequestBody.create(file, jpeg);

        MultipartBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("upload_preset", preset)
                .addFormDataPart("file", file.getName(), fileBody)
                .build();

        Request request = new Request.Builder().url(url).post(body).build();

        CLIENT.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.w("Cloudinary", e);
                listener.onError(context.getString(R.string.err_upload_camera));
            }

            @Override
            public void onResponse(Call call, Response response) {
                try (Response resp = response) {
                    ResponseBody rb = resp.body();
                    String json = rb != null ? rb.string() : "";
                    if (!resp.isSuccessful()) {
                        listener.onError(context.getString(R.string.err_upload_camera));
                        return;
                    }
                    JSONObject o = new JSONObject(json);
                    if (!o.has("secure_url")) {
                        listener.onError(context.getString(R.string.err_upload_camera));
                        return;
                    }
                    listener.onUploaded(o.getString("secure_url"));
                } catch (Exception e) {
                    Log.w("Cloudinary", e);
                    listener.onError(context.getString(R.string.err_upload_camera));
                }
            }
        });
    }
}
