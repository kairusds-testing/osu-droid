package ru.nsu.ccfit.zuev.osu;

import android.content.Intent;
import android.os.Bundle;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.nsu.ccfit.zuev.osuplus.R;

public class PushNotificationActivity extends AppCompatActivity {

    public static final String EXTRA_MSG = "ru.nsu.ccfit.zuev.osuplus.PushNotificationActivityMSG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);
        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent) {
        String message = intent.getStringExtra(EXTRA_MSG);
        if(message != null) {
            Pattern pattern = Pattern.compile("(https://(bit\\.ly|waa\\.ai|cutt\\.ly)\\S*)\\b");
            Matcher matcher = pattern.matcher(message);

            if(matcher.find()) {
                String url = matcher.group(0);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }else {
                startActivity(new Intent(this, MainActivity.class));
            }

            overridePendingTransition(R.anim.fast_activity_swap, R.anim.fast_activity_swap);
            finish();
        }
    }

}