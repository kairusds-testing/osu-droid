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
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import ru.nsu.ccfit.zuev.osu.helper.MD5Calcuator;
import ru.nsu.ccfit.zuev.osu.online.OnlineFileOperator;

public class PushNotificationService extends FirebaseMessagingService {

    private static final String TAG = "osuplusPUSH";
    public static int notificationCount = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if(remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if(notification != null) {
            String channelId = "ru.nsu.ccfit.zuev.push";
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            String title = notification.getTitle();
            if(title == null) title = "osu!droid";
            String message = notification.getBody();
            if(message == null) message = "error";
            Log.d(TAG, title + ":" + message);

            NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.notify_inso)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri);

            String imageUrl = notification.getImageUrl();
            Log.d(TAG, imageUrl);
            if(imageUrl != null) {
                Sting filePath = getCacheDir().getPath() + MD5Calcuator.getStringMD5(imageUrl);
                boolean downloaded = OnlineFileOperator.downloadFile(notification.getImageUrl(),
                    filePath);
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

            String link = notification.getLink();
            Log.d(TAG, link);
            if(link != null) {
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                pendingIntent = PendingIntent.getActivity(this, 0, webIntent,
                    PendingIntent.FLAG_ONE_SHOT);
                notificationBuilder.setContentText(message.replace(link, ""));
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