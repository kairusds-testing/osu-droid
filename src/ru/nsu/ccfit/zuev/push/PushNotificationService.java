package ru.nsu.ccfit.zuev.push;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.anddev.andengine.util.Debug;

import ru.nsu.ccfit.zuev.osu.MainActivity;
import ru.nsu.ccfit.zuev.osuplus.R;
import ru.nsu.ccfit.zuev.osu.helper.MD5Calcuator;
import ru.nsu.ccfit.zuev.osu.online.OnlineFileOperator;

public class PushNotificationService extends FirebaseMessagingService {

    private static final String TAG = "PushNotifs";
    public static int notificationCount = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (BuildConfig.DEBUG) {
            try {
                File d = new File(Environment.getExternalStorageDirectory(), "osu!droid/Log");
                if(!d.exists()) d.mkdirs();
                File f = new File(d, "rawlog.txt");
                if(!f.exists()) f.createNewFile();
                Runtime.getRuntime().exec("logcat -f " + f.getAbsolutePath());
            }catch(IOException e) {}
        }

        Log.i(TAG, "From: " + remoteMessage.getFrom());

        if(remoteMessage.getData().size() > 0) {
            Log.i(TAG, "Message data payload: " + remoteMessage.getData());
        }

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if(notification != null) {
            String channelId = "ru.nsu.ccfit.zuev.push";
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            String title = notification.getTitle();
            if(title == null) title = "osu!droid";
            String message = notification.getBody();
            if(message == null) message = "error";
            Log.i(TAG, "Title:Message = " + title + ":" + message);

            NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.notify_inso)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri);

            String imageUrl = notification.getImageUrl().toString();
            Log.i(TAG, imageUrl);
            if(imageUrl != null) {
                String filePath = getCacheDir().getPath() + "/" + MD5Calcuator.getStringMD5("osuplus" + imageUrl);
                boolean downloaded = OnlineFileOperator.downloadFile(imageUrl, filePath);
                if(downloaded) {
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                    notificationBuilder.setLargeIcon(bitmap)
                        .setStyle(new NotificationCompat.BigPictureStyle()
                            .bigPicture(bitmap)
                            .bigLargeIcon(null));
                }
            }

            Pattern pattern = Pattern.compile("(https://(bit\\.ly|waa\\.ai|cutt\\.ly)\\S*)\\b");
            Matcher matcher = pattern.matcher(message);
            if(matcher.find()) {
                String url = matcher.group(0);
                Log.i(TAG, "Url found: " + url);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);
                notificationBuilder.setContentText(message.replace(url, ""));
                notificationBuilder.setContentIntent(pendingIntent);
            }else {
                Log.i(TAG, "Url not found");
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
                notificationBuilder.setContentIntent(pendingIntent);
            }

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId,
                    "osu!droid Push Notfications",
                    NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription("osu!droid Push Notfications");
                notificationManager.createNotificationChannel(channel);
            }
    
            int notificationId = notificationCount++;
            notificationManager.notify(notificationId, notificationBuilder.build());
        }
    }

}