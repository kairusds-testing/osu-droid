package ru.nsu.ccfit.zuev.osu;

import android.os.Build;

import com.edlplan.ui.fragment.LoadingFragment;
import com.edlplan.ui.fragment.UpdateDialogFragment;

import com.google.gson.Gson;

import okhttp3.Response;
import okhttp3.Request;

import ru.nsu.ccfit.zuev.osu.async.AsyncTaskLoader;
import ru.nsu.ccfit.zuev.osu.async.OsuAsyncCallback;
import ru.nsu.ccfit.zuev.osu.model.vo.GithubReleaseVO;
import ru.nsu.ccfit.zuev.osu.model.vo.GithubReleaseVO.Asset;
import ru.nsu.ccfit.zuev.osu.online.OnlineManager;
import ru.nsu.ccfit.zuev.osuplus.R;

import java.util.ArrayList;

public class Updater {

    private boolean newUpdate = false;
    private String changelogMsg, downloadUrl;
    private LoadingFragment loadingFragment;

    private static Updater instance = new Updater();

    public static Updater getInstance() {
        return instance;
    }

    private Response httpGet(String url) {
        Request request = new Request.Builder()
            .url(url)
            .build();
        return OnlineManager.client.newCall(request).execute();
    }

    public void checkForUpdates() {
        new AsyncTaskLoader().execute(new OsuAsyncCallback() {
             public void run() {
                 ToastLogger.showTextId(R.string.update_info_checking, true);
                 GlobalManager.getInstance().getMainActivity().runOnUiThread(() -> {
                     if(loadingFragment == null) {
                        loadingFragment = new LoadingFragment();
                        loadingFragment.show();
                    }
                });

                Gson gson = new Gson();
                Response response = httpGet("https://api.github.com/repos/kairusds-testing/osu-droid/releases/latest");
                GithubReleaseVO updateInfo = new Gson().fromJson(response.body().string(), GithubReleaseVO.class);

                ArrayList<Asset> assets = new ArrayList<Asset>(updateInfo.getAssets());
                for(Asset asset : assets) {
                    if(!newUpdate && asset.getName() == "versioncode.txt") {
                        Response versionResponse = httpGet(asset.getBrowser_download_url());

                        if(GlobalManager.getInstance().getMainActivity().getVersionCode() <=
                            Long.valueOf(versionResponse.body().string())) {
                            changelogMsg = updateInfo.getBody();
                            newUpdate = true;
                        }
                    }else if(newUpdate && asset.getName().endsWith(".apk")) {
                        downloadUrl = asset.getBrowser_download_url();
                    }
                }
            }

            public void onComplete() {
                GlobalManager.getInstance().getMainActivity().runOnUiThread(() -> {
                    if(loadingFragment != null) {
                        loadingFragment.dismiss();
                        loadingFragment = null;
                    }

                    if(newUpdate) {
                        new UpdateDialogFragment()
                            .setChangelogMessage(changelogMsg)
                            .setDownloadUrl(downloadUrl)
                            .show();
                    }else {
                        ToastLogger.showTextId(R.string.update_info_latest, true);  
                    }
                });
            }
        });
    }

}