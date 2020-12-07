package com.edlplan.ui.fragment;

import android.animation.Animator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.edlplan.framework.easing.Easing;
import com.edlplan.framework.utils.Lazy;
import com.edlplan.ui.BaseAnimationListener;
import com.edlplan.ui.EasingHelper;
import com.edlplan.ui.TriangleEffectView;

import ru.nsu.ccfit.zuev.osuplus.R;

public class BuildTypeNoticeFragment extends BaseFragment {

    public static final Lazy<BuildTypeNoticeFragment> single = Lazy.create(BuildTypeNoticeFragment::new);

    public BuildTypeNoticeFragment() {
        setDismissOnBackgroundClick(true);
        setDismissOnBackPress(true);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_build_type_notice;
    }

    @Override
    protected void onLoadView() {
        ((TriangleEffectView) findViewById(R.id.bg_triangles)).setXDistribution(
                ()-> (float) (2f/(1 + Math.exp((Math.random() * 2 - 1) * 10)) - 1)
        );
        playOnLoadAnim();
    }

    @Override
    public void dismiss() {
        playOnDismissAnim(super::dismiss);
    }

    protected void playOnLoadAnim() {
        View body = findViewById(R.id.frg_body);
        body.setAlpha(0);
        body.animate().cancel();
        body.animate()
                .alpha(1)
                .setDuration(1000)
                .start();
        /*View icon = findViewById(R.id.warning_icon);
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.warning_rotate);
        anim.setInterpolator(EasingHelper.asInterpolator(Easing.InOutQuad));
        icon.startAnimation(anim);*/
        body.postDelayed(this::dismiss, 6000);
    }

    protected void playOnDismissAnim(Runnable runnable) {
        View body = findViewById(R.id.frg_body);
        body.animate().cancel();
        body.animate()
                .scaleX(2)
                .scaleY(2)
                .setDuration(1000)
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
        playBackgroundHideOutAnim(1000);
    }
}