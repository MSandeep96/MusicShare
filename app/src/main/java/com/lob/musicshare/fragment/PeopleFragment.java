package com.lob.musicshare.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lob.musicshare.R;
import com.lob.musicshare.util.ui.AlphaAnimationUtils;

public class PeopleFragment extends Fragment {

    private OnFragmentRootViewCreated onFragmentRootViewCreated;

    public void setCreationListener(OnFragmentRootViewCreated onFragmentRootViewCreated) {
        this.onFragmentRootViewCreated = onFragmentRootViewCreated;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_following, container, false);

        final ImageView emoji = (ImageView) rootView.findViewById(R.id.no_image_view);
        emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlphaAnimationUtils.alphaSetResource(true, emoji, R.drawable.smiling_image_colored);
            }
        });

        if (onFragmentRootViewCreated != null) {
            onFragmentRootViewCreated.onViewCreated(rootView);
        }

        return rootView;
    }

    public interface OnFragmentRootViewCreated {
        void onViewCreated(View rootView);
    }
}
