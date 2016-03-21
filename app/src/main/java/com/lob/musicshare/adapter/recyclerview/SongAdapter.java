package com.lob.musicshare.adapter.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anthonycr.grant.PermissionsManager;
import com.lob.musicshare.R;
import com.lob.musicshare.query.Query;
import com.lob.musicshare.util.ParseValues;
import com.lob.musicshare.util.PreferencesUtils;
import com.lob.musicshare.util.SpotifyUtils;
import com.lob.musicshare.util.picasso.CircleTransform;
import com.lob.musicshare.util.web.ServerConnectionUtils;
import com.lob.musicshare.youtube.YouTubeVideo;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.jar.Manifest;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    public final ArrayList<String> songs;
    private final Activity activity;
    private final OnErrorInterface onErrorInterface;

    public SongAdapter(Activity activity, ArrayList<String> songs, OnErrorInterface onError) {
        this.activity = activity;
        this.onErrorInterface = onError;

        Collections.reverse(songs);
        this.songs = songs;
    }

    public String getTrackAt(int index) {
        return songs.get(index);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return new ViewHolder(inflater.inflate(R.layout.song_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final View rootView = holder.rootView;
        final ImageView songThumbnail = holder.songThumbnail;
        final TextView userNameTextView = holder.userNameTextView;
        final TextView trackTextView = holder.trackTextView;

        String fullTrack = getTrackAt(position);

        if (fullTrack != null) {
            try {
                if (Character.isWhitespace(fullTrack.charAt(0))) {
                    fullTrack = fullTrack.replaceFirst("^ *", "");
                }
            } catch (StringIndexOutOfBoundsException ignored) {
            }

            final String[] songParts = fullTrack.split(" by ");
            userNameTextView.setText(songParts[0]);
            String track = getTrackAt(position);
            if (track.replace(" by ", "").equals("") || track.replace(" by ", "").equals(" ")) {
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) rootView.getLayoutParams();
                params.height = 0;
                rootView.setLayoutParams(params);
            } else {
                try {
                    trackTextView.setText(songParts[1]);

                    final String search = ParseValues.getParsedSongName(getTrackAt(position))
                            .replace(ParseValues.BY, "")
                            .replace(ParseValues.TWO_DASHES, "")
                            .replace(ParseValues.OPEN_PARENTHESIS, "")
                            .replace(ParseValues.CLOSED_PARENTHESIS, "")
                            .replace(ParseValues.OTHER_APOSTROPHE, "")
                            .replace("&", "")
                            .replace("__opar", "")
                            .replace("__cpar", "")
                            .replace("AlbumVersion", "")
                            .replace("album version", "")
                            .replace("__dot__", "")
                            .replace("[", "")
                            .replace("]", "")
                            .replace("-", "")
                            .replace(ParseValues.COMMA, "")
                            .replace(ParseValues.ONE_BLANK_SPACE, "")
                            .replace(ParseValues.APOSTROPHE, "")
                            .replace("c" + ParseValues.APOSTROPHE, "c'")
                            .replaceAll("^[^a-zA-Z0-9\\s]+|[^a-zA-Z0-9\\s]+$", "");

                    Query.getInstance(activity, Query.QueryType.YOUTUBE_SEARCH)
                            .setYoutubeSearch(search)
                            .setShowDialog(false)
                            .setOnResultListener(new Query.OnResultListener() {
                                @Override
                                public void onResult(String result) {

                                    if (result != null && !result.equals("")) {
                                        final YouTubeVideo youTubeVideo = new YouTubeVideo(result);
                                        Picasso.with(activity)
                                                .load(youTubeVideo.thumbnailUrl)
                                                .transform(new CircleTransform())
                                                .fit()
                                                .into(songThumbnail);

                                        rootView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                if (SpotifyUtils.isSpotifyInstalled(activity) && PreferencesUtils.useSpotify(activity)) {
                                                    SpotifyUtils.startSpotifySearch(activity, songParts[0] + " " + songParts[1]);
                                                } else {
                                                    Intent youTube = new Intent(Intent.ACTION_VIEW,
                                                            Uri.parse("http://www.youtube.com/watch?v=" + youTubeVideo.videoId));
                                                    activity.startActivity(youTube);
                                                }
                                            }
                                        });
                                    } else {
                                        Picasso.with(activity)
                                                .load(R.drawable.no_video)
                                                .transform(new CircleTransform())
                                                .fit()
                                                .into(songThumbnail);

                                        rootView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                if (SpotifyUtils.isSpotifyInstalled(activity) && PreferencesUtils.useSpotify(activity)) {
                                                    SpotifyUtils.startSpotifySearch(activity, songParts[0] + " " + songParts[1]);
                                                } else {
                                                    Toast.makeText(activity, R.string.video_not_available, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            }).startQuery();
                } catch (ArrayIndexOutOfBoundsException exception) {
                    onErrorInterface.onError(exception.getLocalizedMessage());
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public interface OnErrorInterface {
        void onError(String error);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View rootView;
        public ImageView songThumbnail;
        public TextView userNameTextView;
        public TextView trackTextView;

        public ViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            this.songThumbnail = (ImageView) rootView.findViewById(R.id.songs_list_thumbnail);
            this.userNameTextView = (TextView) rootView.findViewById(R.id.songs_list_username);
            this.trackTextView = (TextView) rootView.findViewById(R.id.songs_list_track);
        }
    }
}