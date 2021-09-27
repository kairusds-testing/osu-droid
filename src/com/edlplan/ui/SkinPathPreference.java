package com.edlplan.ui;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;

import androidx.preference.ListPreference;

import java.io.File;
import java.util.Arrays;

import ru.nsu.ccfit.zuev.osu.Config;
import ru.nsu.ccfit.zuev.osu.GlobalManager;
import ru.nsu.ccfit.zuev.osu.MainActivity;
import ru.nsu.ccfit.zuev.osu.ResourceManager;
import ru.nsu.ccfit.zuev.osu.ToastLogger;
import ru.nsu.ccfit.zuev.osu.async.AsyncTaskLoader;
import ru.nsu.ccfit.zuev.osu.async.OsuAsyncCallback;
import ru.nsu.ccfit.zuev.osu.game.SpritePool;
import ru.nsu.ccfit.zuev.osuplus.R;

public class SkinPathPreference extends ListPreference {
    
    public SkinPathPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SkinPathPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
    }
 
    public SkinPathPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SkinPathPreference(Context context) {
        super(context);
    }

    public void reloadSkinList() {
        new AsyncTaskLoader().execute(new OsuAsyncCallback() {
            public void run() {
                try {
                    File skinMain = new File(Config.getSkinTopPath());
                    if (!skinMain.exists()) skinMain.mkdir();
                    File[] skinFolders = skinMain.listFiles(file -> file.isDirectory() && !file.getName().startsWith("."));
                    CharSequence[] entries = new CharSequence[skinFolders.length + 1];
                    CharSequence[] entryValues = new CharSequence[skinFolders.length + 1];
                    entries[0] = skinMain.getName() + " (Default)";
                    entryValues[0] = skinMain.getPath();
                    for (int i = 1; i < entries.length; i++) {
                        entries[i] = skinFolders[i - 1].getName();
                        entryValues[i] = skinFolders[i - 1].getPath();
                    }
                    Arrays.sort(entries, 1, entries.length);
                    Arrays.sort(entryValues, 1, entryValues.length);
                    setEntries(entries);
                    setEntryValues(entryValues);
                    setOnPreferenceChangeListener((preference, newValue) -> {
                        /* SpritePool.getInstance().purge();
                        GlobalManager.getInstance().setSkinNow(newValue.toString());
                        ResourceManager.getInstance().loadCustomSkin(newValue.toString());
                        GlobalManager.getInstance().getEngine().getTextureManager().reloadTextures(); */
                        MainActivity activity = GlobalManager.getInstance().getMainActivity();
                        Intent intent = new Intent(activity, MainActivity.class);
                        activity.finish();
                        activity.overridePendingTransition(0, 0);
                        activity.startActivity(intent);
                        activity.overridePendingTransition(0, 0);
                        ToastLogger.showTextId(R.string.message_loaded_skin, true);
                        return true;
                    });
                    setValueIndex(findIndexOfValue(Config.getSkinPath()));
        
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            public void onComplete() {}
        });
    }

}