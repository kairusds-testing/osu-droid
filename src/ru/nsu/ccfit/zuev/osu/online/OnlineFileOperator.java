package ru.nsu.ccfit.zuev.osu.online;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Response;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;

import org.anddev.andengine.util.Debug;

import java.io.File;
import java.io.IOException;

public class OnlineFileOperator {
    private static final String CrLf = "\r\n";

    public static void sendFile(String urlstr, String filename) {
        try {
            File file = new File(filename);
            if (!file.exists())
                return;

            MediaType mime = MediaType.parse("application/octet-stream");
            RequestBody fileBody = RequestBody.create(mime, file);
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("uploadedfile", file.getName(), fileBody)
                .build();
            Request request = new Request.Builder().url(urlstr)
                .post(requestBody).build();
            Response response = OnlineManager.client.newCall(request).execute();
            String responseStr = response.body().string();
            Debug.i("sendFile request " + responseStr);
        } catch (final IOException e) {
            Debug.e("sendFile " + e.getMessage(), e);
        } catch (final Exception e) {
            Debug.e("sendFile " + e.getMessage(), e);
        }

    }

    public static boolean downloadFile(String urlstr, String filename) {
        Debug.i("Starting download " + urlstr);
        File file = new File(filename);
        try {
            if(file.exists()) {
                Debug.i(file.getName() + " already exists");
                return true;
            }
            // Cheching for errors
            Debug.i("Connected to " + urlstr);

            Request request = new Request.Builder()
                .url(urlstr)
                .build();
            Response response = OnlineManager.client.newCall(request).execute();
            BufferedSink sink = Okio.buffer(Okio.sink(file));
            sink.writeAll(response.body().source());
            response.close();
            sink.close();
            return true;
        } catch (final IOException e) {
            Debug.e("downloadFile " + e.getMessage(), e);
            return false;
        } catch (final Exception e) {
            Debug.e("downloadFile " + e.getMessage(), e);
            return false;
        }
    }
}
