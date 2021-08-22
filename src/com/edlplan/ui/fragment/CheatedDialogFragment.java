package com.edlplan.ui.fragment;

import ru.nsu.ccfit.zuev.osu.GlobalManager;
import ru.nsu.ccfit.zuev.osuplus.R;

public class CheatedDialogFragment extends ConfirmDialogFragment {

    public void init() {
        setMessage(R.string.message_cheat_detected).showForResult(isAccepted -> exitGame());
    }

    private void exitGame() {
        if(GlobalManager.getInstance().getEngine().getScene() != GlobalManager.getInstance().getGameScene().getScene()) {
            GameSceneGlobalManager.getInstance().getGameScene().quit();
        }
        GlobalManager.getInstance().getEngine().setScene(GlobalManager.getInstance().getMainScene().getScene());
        GlobalManager.getInstance().getMainScene().exit();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        exitGame();
    }

}