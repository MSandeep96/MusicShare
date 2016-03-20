package com.lob.musicshare.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lob.musicshare.R;
import com.lob.musicshare.activity.ContentActivity;
import com.lob.musicshare.adapter.recyclerview.SongAdapter;
import com.lob.musicshare.json.ParseJson;
import com.lob.musicshare.query.Query;
import com.lob.musicshare.user.User;
import com.lob.musicshare.util.Constants;
import com.lob.musicshare.util.Debug;
import com.lob.musicshare.util.ParseValues;
import com.lob.musicshare.util.PreferencesUtils;
import com.lob.musicshare.util.recyclerview.RecyclerViewItemDecorator;
import com.lob.musicshare.util.web.ServerConnectionUtils;

import java.util.ArrayList;
import java.util.Collections;

public class ProfileFragment extends Fragment {

    private boolean isSongRemovalDismissed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        if (getActivity().getClass().getSimpleName().equals(ContentActivity.class.getSimpleName())) {

            final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

            Query.getInstance(getActivity(), Query.QueryType.GET_USER_INFO)
                    .setHisEmail(ParseValues.getParsedEmail(PreferencesUtils.getEmail(getContext())))
                    .setOnResultListener(new Query.OnResultListener() {
                        @Override
                        public void onResult(String result) {

                            rootView.findViewById(R.id.loading_songs_frame_layout).setVisibility(View.GONE);

                            final User user = ParseJson.generateUsers(result).get(0);
                            String[] parts = user.songName.split(",");

                            if (parts.length <= 1) {
                                rootView.findViewById(R.id.no_songs_frame_layout).setVisibility(View.VISIBLE);

                                ((TextView) rootView.findViewById(R.id.no_music_text_view)).setText(R.string.have_not_listened_yet);
                                return;
                            }

                            ArrayList<String> tracksArrayList = new ArrayList<>();
                            Collections.addAll(tracksArrayList, parts);

                            final SongAdapter adapter = new SongAdapter(getActivity(), tracksArrayList, new SongAdapter.OnErrorInterface() {
                                @Override
                                public void onError(String error) {
                                    Debug.log(error);
                                }
                            });

                            ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper
                                    .SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                                @Override
                                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                                    return false;
                                }

                                @Override
                                public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                                    final int position = viewHolder.getAdapterPosition();
                                    final String titleOfSong = adapter.getTrackAt(position);
                                    final String whatToReplace = ParseValues.getParsedSongName(titleOfSong);

                                    final String message = getString(R.string.you_removed) + ": " + titleOfSong;
                                    showSnackBar(message, 3000, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            isSongRemovalDismissed = true;

                                            adapter.songs.add(position, titleOfSong);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }, whatToReplace);


                                    adapter.songs.remove(viewHolder.getAdapterPosition());
                                    adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                                }
                            };

                            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
                            itemTouchHelper.attachToRecyclerView(recyclerView);

                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.addItemDecoration(new RecyclerViewItemDecorator(getContext()));
                            recyclerView.setItemViewCacheSize(Integer.MAX_VALUE);
                            recyclerView.setAdapter(adapter);
                        }
                    }).startQuery();
        }

        return rootView;
    }

    private void showSnackBar(String message, int duration, View.OnClickListener onClick, final String whatToReplace) {
        final Snackbar snackbar = Snackbar.make(getView(), message, Snackbar.LENGTH_INDEFINITE).setActionTextColor(Color.WHITE);

        snackbar.setAction(R.string.dismiss, onClick);
        snackbar.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isSongRemovalDismissed) {
                    ServerConnectionUtils.getContent(Constants.REMOVE_SONG
                            + "?email=" + ParseValues.getParsedEmail(PreferencesUtils.getEmail(getContext()))
                            + "&track=" + ParseValues.getParsedSongName(whatToReplace));
                }
                isSongRemovalDismissed = false;

                snackbar.dismiss();
            }
        }, duration);
    }
}
