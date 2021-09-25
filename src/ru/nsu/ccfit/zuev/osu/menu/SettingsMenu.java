package ru.nsu.ccfit.zuev.osu.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.LayoutInflater;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.edlplan.ui.ActivityOverlay;
import com.edlplan.ui.fragment.WebViewFragment;

import java.io.File;
import java.util.Arrays;

import ru.nsu.ccfit.zuev.osu.Config;
import ru.nsu.ccfit.zuev.osu.GlobalManager;
import ru.nsu.ccfit.zuev.osu.LibraryManager;
import ru.nsu.ccfit.zuev.osu.MainActivity;
import ru.nsu.ccfit.zuev.osu.PropertiesLibrary;
import ru.nsu.ccfit.zuev.osu.ResourceManager;
import ru.nsu.ccfit.zuev.osu.ToastLogger;
import ru.nsu.ccfit.zuev.osu.game.SpritePool;
import ru.nsu.ccfit.zuev.osu.helper.StringTable;
import ru.nsu.ccfit.zuev.osu.online.OnlineInitializer;
import ru.nsu.ccfit.zuev.osu.online.OnlineManager;
import ru.nsu.ccfit.zuev.osuplus.R;

public class SettingsMenu extends PreferenceFragmentCompat {

    private Activity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = GlobalManager.getInstance().getMainActivity();
        // addPreferencesFromResource(R.xml.options);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.options, rootKey);
        reloadSkinList();
        final EditTextPreference skinToppref = (EditTextPreference) findPreference("skinTopPath");

        skinToppref.setOnPreferenceChangeListener((preference, newValue) -> {
            if (newValue.toString().trim().length() == 0) {
                skinToppref.setText(Config.getCorePath() + "Skin/");
                Config.loadConfig(mActivity);
                reloadSkinList();
                return false;
            }

            File file = new File(newValue.toString());
            if (!file.exists()) {
                if (!file.mkdirs()) {
                    ToastLogger.showText(StringTable.get(R.string.message_error_dir_not_found), true);
                    return false;
                }
            }

            skinToppref.setText(newValue.toString());
            Config.loadConfig(mActivity);
            reloadSkinList();
            return false;
        });

        final Preference pref = findPreference("clear");
        pref.setOnPreferenceClickListener(preference -> {
            LibraryManager.getInstance().clearCache(mActivity);
            return true;
        });
        final Preference clearProps = findPreference("clear_properties");
        clearProps.setOnPreferenceClickListener(preference -> {
            PropertiesLibrary.getInstance()
                    .clear(mActivity);
            return true;
        });
        final Preference register = findPreference("registerAcc");
        register.setOnPreferenceClickListener(preference -> {
            OnlineInitializer initializer = new OnlineInitializer(mActivity);
            initializer.createInitDialog();
            return true;
        });

        final Preference update = findPreference("update");
        update.setOnPreferenceClickListener(preference -> {
            new WebViewFragment("https://" + OnlineManager.hostname).show();
            // Beta.checkUpgrade();
            return true;
        });

        final Preference close = findPreference("close");
        close.setOnPreferenceClickListener(preference -> {
            dismiss();
            return true;
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Config.loadConfig(mActivity);
        GlobalManager.getInstance().getMainScene().reloadOnlinePanel();
        GlobalManager.getInstance().getMainScene().loadTimeingPoints(false);
        // GlobalManager.getInstance().getSongService().setIsSettingMenu(false);
        GlobalManager.getInstance().getSongService().setGaming(false);
    }

    public void show() {
        ActivityOverlay.addOverlay(this, this.getClass().getName() + "@" + this.hashCode());
    }

    public void dismiss() {
        ActivityOverlay.dismissOverlay(this);
    }

    /* 
    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    } */

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void reloadSkinList() {
        try {
            final ListPreference skinPathPref = (ListPreference) findPreference("skinPath");
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
            skinPathPref.setEntries(entries);
            skinPathPref.setEntryValues(entryValues);
            skinPathPref.setOnPreferenceChangeListener((preference, newValue) -> {
                SpritePool.getInstance().purge();
                GlobalManager.getInstance().setSkinNow(newValue.toString());
                ResourceManager.getInstance().loadCustomSkin(newValue.toString());
                GlobalManager.getInstance().getEngine().getTextureManager().reloadTextures();
                ToastLogger.showText(StringTable.get(R.string.message_loaded_skin), true);
                return true;
            });
            skinPathPref.setValueIndex(skinPathPref.findIndexOfValue(Config.getSkinPath()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
