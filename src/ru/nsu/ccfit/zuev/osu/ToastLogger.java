package ru.nsu.ccfit.zuev.osu;

import android.app.Activity;
import android.widget.Toast;

import java.util.ArrayList;

import ru.nsu.ccfit.zuev.osu.helper.StringTable;

public class ToastLogger /*implements Runnable */{
    private static ToastLogger instance = null;
    Activity activity;
    String message = "";
    boolean showlong = false;
    ArrayList<String> debugLog = new ArrayList<String>();
    float percentage;

    private ToastLogger(final Activity activity) {
        this.activity = activity;
    }

    public static void init(final Activity activity) {
        instance = new ToastLogger(activity);
    }

    public static void showText(final String message, final boolean showlong) {
        if (instance == null) {
            return;
        }
        /* instance.message = message;
        instance.showlong = showlong;
        instance.activity.runOnUiThread(instance);*/
        instance.activity.runOnUiThread(new Runnable(){
            public void run(){
                Toast.makeText(instance.activity, message,
                    showlong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void showTextId(final int resID, final boolean showlong) {
        showText(StringTable.get(resID), showlong);
    }

    public static void addToLog(final String str) {
        if (instance == null) {
            return;
            /*
             * if (instance.debugLog.size() >= 20) instance.debugLog.remove(0);
             * instance.debugLog.add(str);
             */
        }
    }

    public static ArrayList<String> getLog() {
        if (instance == null) {
            return null;
        }
        return instance.debugLog;
    }

    public static float getPercentage() {
        if (instance == null) {
            return -1;
        }
        return instance.percentage;
    }

    public static void setPercentage(final float perc) {
        if (instance == null) {
            return;
        }
        instance.percentage = perc;
    }

    /* public void run() {
        Toast.makeText(instance.activity, message,
                showlong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    } */
}
