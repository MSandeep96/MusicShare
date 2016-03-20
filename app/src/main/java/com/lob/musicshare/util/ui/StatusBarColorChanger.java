package com.lob.musicshare.util.ui;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.Window;

public class StatusBarColorChanger {
    public static void setStatusBarColorWithFade(final Window window, final int colorFrom, final int colorTo) {
        ValueAnimator colorAnimation = ValueAnimator.ofArgb(colorFrom, colorTo);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                window.setStatusBarColor((Integer) animator.getAnimatedValue());
            }

        });
        colorAnimation.start();
    }

    public static void setLightStatusBar(View view) {
        int flags = view.getSystemUiVisibility();
        flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        view.setSystemUiVisibility(flags);
    }

    public static void clearLightStatusBar(View view) {
        int flags = view.getSystemUiVisibility();
        flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        view.setSystemUiVisibility(flags);
    }
}
