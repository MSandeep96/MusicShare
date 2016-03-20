package com.lob.musicshare.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lob.musicshare.R;

public class ArtistsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_artists, container, false);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .detach(this)
                .attach(this)
                .commit();
        super.onConfigurationChanged(newConfig);
    }
}
