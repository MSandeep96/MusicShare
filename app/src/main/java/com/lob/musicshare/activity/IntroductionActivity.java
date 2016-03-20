package com.lob.musicshare.activity;

import android.graphics.Color;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.lob.musicshare.R;
import com.lob.musicshare.util.SpotifyUtils;

public class IntroductionActivity extends AppIntro {

    private final int[][] fragmentValues = {
            new int[]{
                    R.string.people,
                    R.string.by_artists,
                    R.string.by_genres,
                    R.string.my_profile
            },
            new int[]{
                    R.string.introduction_people_title,
                    R.string.introduction_artists_title,
                    R.string.introduction_genres_title,
                    R.string.introduction_profile_title
            },
            new int[]{
                    R.drawable.following_tutorial,
                    R.drawable.artists_tutorial,
                    R.drawable.genres_tutorial,
                    R.drawable.my_profile_tutorial
            },
            new int[]{
                    R.color.bg_introduction,
                    R.color.bg_introduction,
                    R.color.bg_introduction,
                    R.color.bg_introduction
            }
    };

    @Override
    public void init(Bundle savedInstanceState) {
        getWindow().setStatusBarColor(Color.parseColor("#696969"));

        for (int i = 0; i < fragmentValues.length; i++) {
            addSlide(AppIntroFragment.newInstance(getResources().getString(fragmentValues[0][i]),
                    getResources().getString(fragmentValues[1][i]), fragmentValues[2][i], getResources().getColor(fragmentValues[3][i])));
        }

        if (SpotifyUtils.isSpotifyInstalled(getApplicationContext())) {
            addSlide(AppIntroFragment.newInstance(getResources().getString(R.string.spotify_support),
                    getResources().getString(R.string.spotify_support_description),
                    R.drawable.spotify, R.color.bg_introduction));
        }

        showSkipButton(false);
        setProgressButtonEnabled(true);
        setSeparatorColor(Color.TRANSPARENT);
    }

    @Override
    public void onSkipPressed() {
    }

    @Override
    public void onDonePressed() {
        finish();
    }

    @Override
    public void onSlideChanged() {
    }

    @Override
    public void onNextPressed() {
    }

}