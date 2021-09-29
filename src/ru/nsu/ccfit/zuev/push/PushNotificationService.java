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

    public static int notificationCount = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Debug.i("SaveServiceObject From: " + remoteMessage.getFrom());

        if(remoteMessage.getData().size() > 0) {
            Debug.i("SaveServiceObject Message data payload: " + remoteMessage.getData());
        }

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if(notification != null) {
            String channelId = "ru.nsu.ccfit.zuev.push";
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            String title = notification.getTitle();
            if(title == null) title = "osu!droid";
            String message = notification.getBody();
            if(message == null) message = "error";
            Debug.i("SaveServiceObject" + title + ":" + message);

            NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.notify_inso)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri);

            String imageUrl = notification.getImageUrl().toString();
            Debug.i("SaveServiceObject" + imageUrl);
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

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

            // supported short URL's 
            Pattern pattern = Pattern.compile("(https://(bit\\.ly|waa\\.ai|cutt\\.ly)\\S*)\\b");
            Matcher matcher = pattern.matcher(message);
            Debug.i("SaveServiceObject" + url);
            if(matcher.find()) {
                String url = matcher.group(0);
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                pendingIntent = PendingIntent.getActivity(this, 0, webIntent,
                    PendingIntent.FLAG_ONE_SHOT);
                notificationBuilder.setContentText(message.replace(url, ""));
            }
            notificationBuilder.setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId,
                    "Push notfication channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }
    
            int notificationId = notificationCount++;
            notificationManager.notify(notificationId, notificationBuilder.build());
        }
    }

}