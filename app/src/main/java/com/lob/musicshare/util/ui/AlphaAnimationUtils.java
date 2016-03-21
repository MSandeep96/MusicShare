package com.lob.musicshare.util.ui;

import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.lob.musicshare.R;

public class AlphaAnimationUtils {
    public static void alphaSetResource(final boolean repeat, final ImageView imageView, final int imageRes) {
        final Animation animOut = AnimationUtils.loadAnimation(imageView.getContext(), android.R.anim.fade_out);
        final Animation anim_in  = AnimationUtils.loadAnimation(imageView.getContext(), android.R.anim.fade_in);
        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {}

            @Override public void onAnimationRepeat(Animation animation) {}

            @Override public void onAnimationEnd(Animation animation) {
                imageView.setImageResource(imageRes);
                imageView.startAnimation(anim_in);

                if (repeat) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int res = 0;
                            switch (imageRes) {
                                case R.drawable.smiling_image_colored:
                                    res = R.drawable.smiling_image;
                                    break;
                                case R.drawable.confused_emoji_colored:
                                    res = R.drawable.confused_emoji;
                                    break;
                                case R.drawable.what_emoji_colored:
                                    res = R.drawable.what_emoji;
                                    break;
                            }
                            alphaSetResource(false, imageView, res);
                        }
                    }, 1000);
                }
            }
        });
        imageView.startAnimation(animOut);
    }
}
