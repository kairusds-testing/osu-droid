package ru.nsu.ccfit.zuev.osu.menu;

import android.app.Activity;
import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
// import android.preference.PreferenceActivity;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.edlplan.framework.easing.Easing;
import com.edlplan.ui.ActivityOverlay;
import com.edlplan.ui.BaseAnimationListener;
import com.edlplan.ui.SkinPathPreference;
import com.edlplan.ui.fragment.SettingsFragment;
import com.edlplan.ui.fragment.WebViewFragment;
import com.edlplan.ui.EasingHelper;

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

public class SettingsMenu extends SettingsFragment {

    private Activity mActivity;
    private PreferenceScreen mParentScreen;
    private PreferenceScreen parentScreen;
    private boolean isOnNestedScreen = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = GlobalManager.getInstance().getMainActivity();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.options, rootKey);

        SkinPathPreference skinPath = (SkinPathPreference) findPreference("skinPath");
        skinPath.reloadSkinList();

        // screens
        mParentScreen = parentScreen = getPreferenceScreen();

        final PreferenceScreen onlineOption = (PreferenceScreen) findPreference("onlineOption");
        onlineOption.setOnPreferenceClickListener(preference -> {
            setPreferenceScreen(onlineOption);
            return true;
        });

        final PreferenceScreen general = (PreferenceScreen) findPreference("general");
        general.setOnPreferenceClickListener(preference -> {
            setPreferenceScreen(general);
            return true;
        });

        final PreferenceScreen color = (PreferenceScreen) findPreference("color");
        color.setOnPreferenceClickListener(preference -> {
            parentScreen = general;
            setPreferenceScreen(color);
            return true;
        });

        final PreferenceScreen sound = (PreferenceScreen) findPreference("sound");
        sound.setOnPreferenceClickListener(preference -> {
            setPreferenceScreen(sound);
            return true;
        });

        final PreferenceScreen beatmaps = (PreferenceScreen) findPreference("beatmaps");
        beatmaps.setOnPreferenceClickListener(preference -> {
            setPreferenceScreen(beatmaps);
            return true;
        });

        final PreferenceScreen advancedOpts = (PreferenceScreen) findPreference("advancedopts");
        advancedOpts.setOnPreferenceClickListener(preference -> {
            setPreferenceScreen(advancedOpts);
            return true;
        });

        // screens END

        final EditTextPreference onlinePassword = (EditTextPreference) findPreference("onlinePassword");
        onlinePassword.setOnBindEditTextListener(editText -> {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        });

        final EditTextPreference skinToppref = (EditTextPreference) findPreference("skinTopPath");
        skinToppref.setOnPreferenceChangeListener((preference, newValue) -> {
            if (newValue.toString().trim().length() == 0) {
                skinToppref.setText(Config.getCorePath() + "Skin/");
                Config.loadConfig(mActivity);
                skinPath.reloadSkinList();
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
            skinPath.reloadSkinList();
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
            new WebViewFragment().setURL("https://" + OnlineManager.hostname).show();
            // Beta.checkUpgrade();
            return true;
        });
    }

    public void onNavigateToScreen(PreferenceScreen preferenceScreen) {
        if(preferenceScreen.getKey() != null) {
            isOnNestedScreen = true;
            setTitle(preferenceScreen.getTitle().toString());
        }
    }

    private void setTitle(String title) {
       ((TextView) findViewById(R.id.title)).setText(title); 
    }

    @Override
    public void callDismissOnBackPress() {
        navigateBack();
    }

    // only supports 1 child with an optional grandchild
    private void navigateBack() {
        if(parentScreen.getKey() != null) {
            setPreferenceScreen(parentScreen);
            setTitle(parentScreen.getTitle().toString());
            parentScreen = mParentScreen;
            return;
        }

        if(isOnNestedScreen) {
            isOnNestedScreen = false;
            setPreferenceScreen(mParentScreen);
            setTitle("");
        }else {
           dismiss();
        }
    }

    @Override
    protected void playOnLoadAnim() {
        ImageButton backButton = (ImageButton) findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            navigateBack();
        });

        View body = findViewById(R.id.body);
        body.setTranslationY(50);
        body.animate().cancel();
        body.animate()
            .translationY(0)
            .setDuration(200)
            .setInterpolator(EasingHelper.asInterpolator(Easing.InOutQuad))
            .start();
    }

    protected void playOnDismissAnim(Runnable runnable) {
        View body = findViewById(R.id.body);
        body.animate().cancel();
        body.animate()
            .translationY(50)
            .setDuration(200)
            .setInterpolator(EasingHelper.asInterpolator(Easing.InOutQuad))
            .setListener(new BaseAnimationListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            })
            .start();
    }

    @Override
    public void show() {
        if(isCreated()) {
            return;
        }
        super.show();
    }

    @Override
    public void dismiss() {
        playOnDismissAnim(() -> {
            Config.loadConfig(mActivity);
            GlobalManager.getInstance().getMainScene().reloadOnlinePanel();
            GlobalManager.getInstance().getMainScene().loadTimeingPoints(false);
            // GlobalManager.getInstance().getSongService().setIsSettingMenu(false);
            GlobalManager.getInstance().getSongService().setGaming(false);
            SettingsMenu.super.dismiss();
        });
    }

}