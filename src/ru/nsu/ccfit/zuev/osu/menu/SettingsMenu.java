package ru.nsu.ccfit.zuev.osu.menu;

import android.animation.Animator;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Build;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.AnimRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SeekBarPreference;

import com.edlplan.framework.easing.Easing;
import com.edlplan.ui.ActivityOverlay;
import com.edlplan.ui.BaseAnimationListener;
import com.edlplan.ui.SkinPathPreference;
import com.edlplan.ui.fragment.SettingsFragment;
import com.edlplan.ui.fragment.UpdateDialogFragment;
import com.edlplan.ui.EasingHelper;

import com.google.gson.Gson;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

import okhttp3.Response;
import okhttp3.Request;

import org.anddev.andengine.util.Debug;

import ru.nsu.ccfit.zuev.osu.Config;
import ru.nsu.ccfit.zuev.osu.GlobalManager;
import ru.nsu.ccfit.zuev.osu.LibraryManager;
import ru.nsu.ccfit.zuev.osu.MainActivity;
import ru.nsu.ccfit.zuev.osu.PropertiesLibrary;
import ru.nsu.ccfit.zuev.osu.ResourceManager;
import ru.nsu.ccfit.zuev.osu.ToastLogger;
import ru.nsu.ccfit.zuev.osu.async.AsyncTaskLoader;
import ru.nsu.ccfit.zuev.osu.async.OsuAsyncCallback;
import ru.nsu.ccfit.zuev.osu.game.SpritePool;
import ru.nsu.ccfit.zuev.osu.helper.StringTable;
import ru.nsu.ccfit.zuev.osu.online.OnlineInitializer;
import ru.nsu.ccfit.zuev.osu.online.OnlineManager;
import ru.nsu.ccfit.zuev.osuplus.R;

public class SettingsMenu extends SettingsFragment {

    private PreferenceScreen mParentScreen, parentScreen;
    private boolean isOnNestedScreen = false;
    private Context context;
    private HashMap<String, Object> updateInfo;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.options, rootKey);

        SkinPathPreference skinPath = (SkinPathPreference) findPreference("skinPath");
        // skinPath.reloadSkinList();

        // screens
        mParentScreen = parentScreen = getPreferenceScreen();

        ((PreferenceScreen) findPreference("onlineOption")).setOnPreferenceClickListener(preference -> {
            setPreferenceScreen((PreferenceScreen) preference);
            return true;
        });

        ((PreferenceScreen) findPreference("general")).setOnPreferenceClickListener(preference -> {
            setPreferenceScreen((PreferenceScreen) preference);
            return true;
        });

        ((PreferenceScreen) findPreference("color")).setOnPreferenceClickListener(preference -> {
            parentScreen = (PreferenceScreen) findPreference("general");
            setPreferenceScreen((PreferenceScreen) preference);
            return true;
        });

        ((PreferenceScreen) findPreference("sound")).setOnPreferenceClickListener(preference -> {
            setPreferenceScreen((PreferenceScreen) preference);
            return true;
        });

        ((PreferenceScreen) findPreference("beatmaps")).setOnPreferenceClickListener(preference -> {
            setPreferenceScreen((PreferenceScreen) preference);
            return true;
        });

        ((PreferenceScreen) findPreference("advancedopts")).setOnPreferenceClickListener(preference -> {
            setPreferenceScreen((PreferenceScreen) preference);
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
                Config.loadConfig(context);
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
            Config.loadConfig(context);
            skinPath.reloadSkinList();
            return false;
        });

        final Preference pref = findPreference("clear");
        pref.setOnPreferenceClickListener(preference -> {
            LibraryManager.getInstance().clearCache();
            return true;
        });
        final Preference clearProps = findPreference("clear_properties");
        clearProps.setOnPreferenceClickListener(preference -> {
            PropertiesLibrary.getInstance()
                    .clear(context);
            return true;
        });
        final Preference register = findPreference("registerAcc");
        register.setOnPreferenceClickListener(preference -> {
            OnlineInitializer initializer = new OnlineInitializer(getActivity());
            initializer.createInitDialog();
            return true;
        });

        final Preference update = findPreference("update");
        update.setOnPreferenceClickListener(preference -> {
            /* new AsyncTaskLoader().execute(new OsuAsyncCallback() {
                 public void run() {
                    Gson gson = new Gson();
                    Request request = new Request.Builder()
                        .url("https://api.github.com/repos/kairusds-testing/osu-droid/releases/latest")
                        .build();
                    Response response = OnlineManager.client.newCall(request).execute();
                    updateInfo = new Gson().fromJson(response.body().string(), HashMap.class);
                }
                public void onComplete() {
                    
                }
            }); */
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
                String versionCode = String.valueOf(packageInfo.versionCode);
                String longVersionCode = "";

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    longVersionCode = String.valueOf(packageInfo.getLongVersionCode());
                }

                ToastLogger.showText("versionCode: " + versionCode + ", getLongVersionCode(): " + longVersionCode + ", productFlavor: " + packageInfo.versionName, true);
            } catch (PackageManager.NameNotFoundException e) {
                Debug.e("PackageManager: " + e.getMessage(), e);
            }
            new UpdateDialogFragment()
                .setChangelogMessage("#Testing\r\n`one two tree four`")
                .setDownloadUrl("https://google.com")
                .show();
            return true;
        });
    }

    public void onNavigateToScreen(PreferenceScreen preferenceScreen) {
        if(preferenceScreen.getKey() != null) {
            if(!isOnNestedScreen) {
                isOnNestedScreen = true;
                animateBackButton(R.drawable.back_black);
            }
            setTitle(preferenceScreen.getTitle().toString());
            for(int v : new int[]{android.R.id.list_container, R.id.title}) {
                animateView(v, R.anim.slide_in_right);
            }
        }
    }

    private void animateBackButton(@DrawableRes int newDrawable) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.rotate_360);
        animation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                ImageButton backButton = (ImageButton) findViewById(R.id.back_button);
                backButton.setImageDrawable(context.getResources().getDrawable(newDrawable));
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });
        ((ImageButton) findViewById(R.id.back_button)).startAnimation(animation);
    }

    private void animateView(@IdRes int viewId, @AnimRes int anim) {
        findViewById(viewId).startAnimation(AnimationUtils.loadAnimation(context, anim));
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
        for(int v : new int[]{android.R.id.list_container, R.id.title}) {
            animateView(v, R.anim.slide_in_left);
        }

        if(parentScreen.getKey() != null) {
            setPreferenceScreen(parentScreen);
            setTitle(parentScreen.getTitle().toString());
            parentScreen = mParentScreen;
            return;
        }

        if(isOnNestedScreen) {
            isOnNestedScreen = false;
            animateBackButton(R.drawable.close_black);
            setPreferenceScreen(mParentScreen);
            setTitle(StringTable.get(R.string.menu_settings_title));
        }else {
           dismiss();
        }
    }

    @Override
    protected void onLoadView() {
        ((ImageButton) findViewById(R.id.back_button)).setOnClickListener(v -> {
            navigateBack();
        });
    }

    protected void playOnLoadAnim() {
        View body = findViewById(R.id.body);
        body.setTranslationX(100);
        body.setTranslationY(0);
        body.animate().cancel();
        body.animate()
            .translationX(0)
            .setDuration(300)
            .setInterpolator(EasingHelper.asInterpolator(Easing.InOutQuad))
            .start();
        playBackgroundHideInAnim(200);
    }

    protected void playOnDismissAnim(Runnable runnable) {
        View body = findViewById(R.id.body);
        body.animate().cancel();
        body.animate()
            .translationX(100)
            .setDuration(300)
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
        playBackgroundHideOutAnim(200);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        playOnDismissAnim(() -> {
            Config.loadConfig(context);
            GlobalManager.getInstance().getMainScene().reloadOnlinePanel();
            GlobalManager.getInstance().getMainScene().loadTimeingPoints(false);
            float bgmvolume = (float) ((SeekBarPreference) findPreference("bgmvolume")).getValue();
            GlobalManager.getInstance().getSongService().setVolume(bgmvolume);
            GlobalManager.getInstance().getSongService().setGaming(false);
            SettingsMenu.super.dismiss();
        });
    }

}