package com.lob.musicshare.util.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;

public class CircularRevealUtils {

    public static void enterReveal(final View view) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Animator animator = null;

                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int cx = view.getMeasuredWidth() / 2;
                int cy = view.getMeasuredHeight() / 2;

                try {
                    final int finalRadius = Math.max(view.getWidth(), view.getHeight()) / 2 + 125;
                    animator = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);

                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                        }
                    });
                } catch (Exception ignored) {
                }

                view.setVisibility(View.VISIBLE);
                if (animator != null) {
                    animator.start();
                }
            }
        });
    }
}
