package ru.nsu.ccfit.zuev.osu.online;

import org.anddev.andengine.util.Debug;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class OnlineFileOperator {
    private static final String CrLf = "\r\n";

    public static void sendFile(String urlstr, String filename) {
        HttpsURLConnection conn = null;
        OutputStream os = null;

        try {
            File file = new File(filename);
            if (!file.exists())
                return;
            long fileLength = file.length();

            URL url = new URL(urlstr);
            conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);

            String message1 = "";
            message1 += "-----------------------------4664151417711" + CrLf;
            message1 += "Content-Disposition: form-data; name=\"uploadedfile\"; filename=\"" +
                    file.getName() + "\"" + CrLf;
            message1 += "Content-Type: application/octet-stream" + CrLf;
            message1 += CrLf;


            String message2 = "";
            message2 += CrLf + "-----------------------------4664151417711--"
                    + CrLf;

            conn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=---------------------------4664151417711");
            conn.setRequestProperty("Content-Length", String.valueOf((message1
                    .length() + message2.length() + fileLength)));
            os = conn.getOutputStream();
            os.write(message1.getBytes());

            InputStream in = new FileInputStream(file);
            byte[] buffer = new byte[(int) fileLength];
            int byteCount = 0;
            while ((byteCount = in.read(buffer)) > 0) {
                os.write(buffer, 0, byteCount);
            }

            os.write(message2.getBytes());
            os.flush();

            os.close();
            in.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            for (String s = reader.readLine(); s != null; s = reader.readLine()) {
                Debug.i(s);
            }

        } catch (Exception e) {
            Debug.e("Cannot upload file: " + e.getMessage(), e);
        }

    }

    public static boolean downloadFile(String urlstr, String filename) {
        Debug.i("Starting download " + urlstr);
        URL url;
        HttpsURLConnection connection;
        BufferedInputStream in;
        File file = new File(filename);
        try {
            url = new URL(urlstr);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36");

            // Cheching for errors
            Debug.i("Connected to " + connection.getURL());

            final String mime = connection.getContentType();
            if (mime == null || mime.startsWith("null")) {
                Debug.e("Server gave bad response!");
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                for (String s = reader.readLine(); s != null; s = reader.readLine()) {
                    Debug.i(s);
                }
                return false;
            }
            long time = connection.getLastModified();
            if (file.exists() && file.lastModified() == time) {
                Debug.i(file.getName() + " already exists");
                return true;
            }
            in = new BufferedInputStream(connection.getInputStream());
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));

            Debug.i("Downloading file...");
            final byte[] buffer = new byte[connection.getContentLength()];
            int len;
            while ((len = in.read(buffer)) >= 0) {
                out.write(buffer, 0, len);
            }
            Debug.i("File downloaded!");

            out.flush();
            out.close();
            in.close();
            file.setLastModified(time);
            return true;

        } catch (final MalformedURLException e) {
            Debug.e("Invalid url " + e.getMessage(), e);
            return false;
        } catch (final IOException e) {
            Debug.e("IOException " + e.getMessage(), e);
            return false;
        } catch (final Exception e) {
            Debug.e("Exception " + e.getMessage(), e);
            return false;
        }
    }
}
