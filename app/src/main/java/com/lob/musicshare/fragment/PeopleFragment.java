package com.lob.musicshare.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lob.musicshare.R;

public class PeopleFragment extends Fragment {

    private OnFragmentRootViewCreated onFragmentRootViewCreated;

    public void setCreationListener(OnFragmentRootViewCreated onFragmentRootViewCreated) {
        this.onFragmentRootViewCreated = onFragmentRootViewCreated;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_following, container, false);

        if (onFragmentRootViewCreated != null) {
            onFragmentRootViewCreated.onViewCreated(rootView);
        }

        return rootView;
    }

    public interface OnFragmentRootViewCreated {
        void onViewCreated(View rootView);
    }
}
