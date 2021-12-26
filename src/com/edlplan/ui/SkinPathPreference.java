package com.edlplan.ui;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.ListPreference;

import java.io.File;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.anddev.andengine.util.Debug;

import ru.nsu.ccfit.zuev.osu.Config;
import ru.nsu.ccfit.zuev.osu.GlobalManager;
import ru.nsu.ccfit.zuev.osu.async.AsyncTaskLoader;
import ru.nsu.ccfit.zuev.osu.async.OsuAsyncCallback;
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
		try {
			/* new AsyncTaskLoader().execute(new OsuAsyncCallback() {
				public void run() {
					File skinMain = new File(Config.getSkinTopPath());
					if(!skinMain.exists() && !skinMain.mkdir()) {
						skinMain = new File(Config.getDefaultCorePath() + "Skin/");
					}
					File[] skinFolders = skinMain.listFiles(file -> file.isDirectory() && !file.getName().startsWith(".")); */
					File skinMain = new File(Config.getSkinTopPath());
					// Map<String, String> skins = new HashMap<String, String>(Config.getSkins());
					HashMap<String, String> skins = Config.getSkins();
					List<String> skinIndex = new ArrayList<String>(skins.keySet());
					CharSequence[] entries = new CharSequence[skins.size() + 1];
					CharSequence[] entryValues = new CharSequence[skins.size() + 1];
					entries[0] = skinMain.getName() + " (Default)";
					entryValues[0] = skinMain.getPath();
					
					for (int i = 1; i < entries.length; i++) {
						entries[i] = skinIndex.get(i - 1); // skinFolders[i - 1].getName();
						entryValues[i] = skins.get(entries[i -1]); // skinFolders[i - 1].getPath();
					}
					Arrays.sort(entries, 1, entries.length);
					Arrays.sort(entryValues, 1, entryValues.length);
					setEntries(entries);
					setEntryValues(entryValues);
				/* }
				 public void onComplete() {}
			}); */
		} catch (Exception e) {
			Debug.e("SkinPathPreference.reloadSkinList: ", e);
			e.printStackTrace();
		}
	}

}