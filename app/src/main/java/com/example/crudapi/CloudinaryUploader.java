package com.example.crudapi;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.crudapi.BuildConfig;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.TreeMap;

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

    private static final String UNSIGNED_PRESET_PLACEHOLDER = "YOUR_UPLOAD_PRESET";
    private static final OkHttpClient CLIENT = new OkHttpClient();

    public interface UploadListener {
        void onUploaded(String secureUrl);

        void onError(String message);
    }

    private CloudinaryUploader() {
    }

    @Nullable
    private static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static String cloudName(Context context) {
        return trimToNull(context.getString(R.string.cloudinary_cloud_name));
    }

    private static boolean hasUnsignedPreset(Context context) {
        String preset = trimToNull(context.getString(R.string.cloudinary_upload_preset));
        return preset != null && !preset.equalsIgnoreCase(UNSIGNED_PRESET_PLACEHOLDER);
    }

    private static boolean hasSignedCredentials() {
        return !TextUtils.isEmpty(trimToNull(BuildConfig.CLOUDINARY_API_KEY))
                && !TextUtils.isEmpty(trimToNull(BuildConfig.CLOUDINARY_API_SECRET));
    }

    public static boolean isConfigured(Context context) {
        Context app = context.getApplicationContext();
        return cloudName(app) != null && (hasUnsignedPreset(app) || hasSignedCredentials());
    }

    /** Upload foto mahasiswa (folder Cloudinary sesuai string resource mahasiswa). */
    public static void uploadMahasiswaPhoto(Context ctx, File file, UploadListener listener) {
        String folder = ctx.getString(R.string.cloudinary_upload_folder_mahasiswa);
        uploadImage(ctx, file, listener, trimToNull(folder));
    }

    /** Upload foto dosen (folder Cloudinary sesuai string resource dosen). */
    public static void uploadDosenPhoto(Context ctx, File file, UploadListener listener) {
        String folder = ctx.getString(R.string.cloudinary_upload_folder_dosen);
        uploadImage(ctx, file, listener, trimToNull(folder));
    }

    /**
     * @param folder nama folder Cloudinary untuk upload bertanda; diabaikan jika pakai preset UNSIGNED saja.
     */
    private static void uploadImage(Context appContext, File file, UploadListener listener, @Nullable String folder) {
        Context context = appContext.getApplicationContext();
        String cloud = cloudName(context);
        if (cloud == null) {
            listener.onError(context.getString(R.string.err_cloudinary_config));
            return;
        }

        String url = "https://api.cloudinary.com/v1_1/" + cloud + "/image/upload";
        MediaType jpeg = MediaType.parse("image/jpeg");
        RequestBody fileBody = RequestBody.create(file, jpeg);

        if (hasUnsignedPreset(context)) {
            String preset = trimToNull(context.getString(R.string.cloudinary_upload_preset));
            MultipartBody.Builder multipart = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("upload_preset", preset != null ? preset : "")
                    .addFormDataPart("file", file.getName(), fileBody);
            MultipartBody body = multipart.build();
            enqueuePost(context, listener, url, body);
            return;
        }

        if (hasSignedCredentials()) {
            MultipartBody body = buildSignedUploadBody(file.getName(), fileBody, folder);
            if (body == null) {
                listener.onError(context.getString(R.string.err_cloudinary_config));
                return;
            }
            enqueuePost(context, listener, url, body);
            return;
        }

        listener.onError(context.getString(R.string.err_cloudinary_config));
    }

    @Nullable
    private static MultipartBody buildSignedUploadBody(String fileName, RequestBody fileBody, @Nullable String folder) {
        String apiKey = trimToNull(BuildConfig.CLOUDINARY_API_KEY);
        String apiSecret = trimToNull(BuildConfig.CLOUDINARY_API_SECRET);
        if (apiKey == null || apiSecret == null) return null;

        String timestamp = Long.toString(System.currentTimeMillis() / 1000L);
        TreeMap<String, String> signParams = new TreeMap<>();
        signParams.put("timestamp", timestamp);

        if (!TextUtils.isEmpty(folder)) {
            signParams.put("folder", folder);
        }

        StringBuilder sb = new StringBuilder();
        for (TreeMap.Entry<String, String> e : signParams.entrySet()) {
            if (sb.length() > 0) sb.append('&');
            sb.append(e.getKey()).append('=').append(e.getValue());
        }

        String signature;
        try {
            signature = sha1Hex(sb.toString() + apiSecret);
        } catch (NoSuchAlgorithmException ex) {
            Log.w("Cloudinary", ex);
            return null;
        }

        MultipartBody.Builder multipart = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("timestamp", timestamp)
                .addFormDataPart("signature", signature)
                .addFormDataPart("api_key", apiKey)
                .addFormDataPart("file", fileName, fileBody);

        if (!TextUtils.isEmpty(folder)) {
            multipart.addFormDataPart("folder", folder);
        }

        return multipart.build();
    }

    private static String sha1Hex(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder hex = new StringBuilder(digest.length * 2);
        for (byte b : digest) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }

    private static void enqueuePost(Context context, UploadListener listener, String url, MultipartBody body) {
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
                        Log.w("Cloudinary", "HTTP " + resp.code() + " body: " + json);
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
