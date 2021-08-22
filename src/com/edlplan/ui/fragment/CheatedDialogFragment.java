package com.edlplan.ui.fragment;

import ru.nsu.ccfit.zuev.osu.GlobalManager;
import ru.nsu.ccfit.zuev.osuplus.R;

public class CheatedDialogFragment extends ConfirmDialogFragment {

    @Override
    public void show() {
        setMessage(R.string.message_cheat_detected);
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        GlobalManager.getMainActivity().finish();
        System.exit(0);
    }

}