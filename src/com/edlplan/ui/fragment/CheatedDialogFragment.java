package com.edlplan.ui.fragment;

import android.os.PowerManager;

import ru.nsu.ccfit.zuev.osu.GlobalManager;
import ru.nsu.ccfit.zuev.osuplus.R;

public class CheatedDialogFragment extends ConfirmDialogFragment {

    @Override
    public void show() {
        setMessage(R.string.message_cheat_detected);
        onResult = (isAccepted) -> {
            if(GlobalManager.getInstance().getEngine().getScene() != GlobalManager.getInstance().getMainScene().getScene()) {
                GlobalManager.getInstance().getMainScene().exit();
                PowerManager.WakeLock wakeLock = GlobalManager.getInstance().getMainActivity().getWakeLock();
                if (wakeLock != null && wakeLock.isHeld()) {
                    wakeLock.release();
                }
            }
        });
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        GlobalManager.getInstance().getMainActivity().finish();
        System.exit(0);
    }

}