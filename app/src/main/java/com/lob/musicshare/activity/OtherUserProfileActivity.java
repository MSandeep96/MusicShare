package com.lob.musicshare.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lob.musicshare.R;
import com.lob.musicshare.adapter.recyclerview.SongAdapter;
import com.lob.musicshare.json.ParseJson;
import com.lob.musicshare.query.Query;
import com.lob.musicshare.user.User;
import com.lob.musicshare.util.AndroidOverViewUtils;
import com.lob.musicshare.util.BitmapUtils;
import com.lob.musicshare.util.Constants;
import com.lob.musicshare.util.Debug;
import com.lob.musicshare.util.ParseValues;
import com.lob.musicshare.util.PreferencesUtils;
import com.lob.musicshare.util.picasso.CircleTransform;
import com.lob.musicshare.util.recyclerview.RecyclerViewItemDecorator;
import com.lob.musicshare.util.ui.CircularRevealUtils;
import com.lob.musicshare.util.web.ServerConnectionUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import it.gmariotti.recyclerview.adapter.SlideInLeftAnimatorAdapter;

public class OtherUserProfileActivity extends AppCompatActivity {

    private static final int[] DIALOG_TEXTVIEW_IDS = new int[]{
            R.id.other_info_artists,
            R.id.other_info_followers,
            R.id.other_info_genres
    };

    private boolean mustAddToFollowing = true;
    private String title, imageUrl, otherInfo, email, artists, nFollower, genres;
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private TextView nameTextView, otherInfoTextView;
    private View headerBackground;

    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_other_user);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        AndroidOverViewUtils.setHeader(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OtherUserProfileActivity.this.supportFinishAfterTransition();
            }
        });

        title = getIntent().getExtras().getString("name-surname");
        imageUrl = getIntent().getExtras().getString("profile-image-url");
        email = getIntent().getExtras().getString("email");
        otherInfo = getIntent().getExtras().getString("other-info");

        headerBackground = findViewById(R.id.header_background);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_follow);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        nameTextView = (TextView) findViewById(R.id.name_other_user_text_view);
        otherInfoTextView = (TextView) findViewById(R.id.artists_other_users_text_view);

        floatingActionButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(OtherUserProfileActivity.this, R.string.add_or_remove, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        nameTextView.setText(title);
        otherInfoTextView.setText(Html.fromHtml(otherInfo));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        Query.getInstance(OtherUserProfileActivity.this, Query.QueryType.GET_USER_INFO)
                .setHisEmail(ParseValues.getParsedEmail(email))
                .setShowDialog(false)
                .setOnResultListener(new Query.OnResultListener() {
                    @Override
                    public void onResult(String result) {
                        User user = ParseJson.generateUsers(result).get(0);

                        artists = user.artists;
                        nFollower = String.valueOf(user.numberFollowers);
                        genres = user.genres;

                        String[] parts = user.songName.split(",");
                        ArrayList<String> tracksArrayList = new ArrayList<>();
                        Collections.addAll(tracksArrayList, parts);

                        OtherUserProfileActivity.this.findViewById(R.id.loading_songs_frame_layout)
                                .setVisibility(View.GONE);

                        if (parts.length != 1) {
                            recyclerView.addItemDecoration(new RecyclerViewItemDecorator(OtherUserProfileActivity.this.getApplicationContext()));
                            recyclerView.setItemViewCacheSize(Integer.MAX_VALUE);
                            final SongAdapter adapter = new SongAdapter(OtherUserProfileActivity.this, tracksArrayList, new SongAdapter.OnErrorInterface() {
                                @Override
                                public void onError(String error) {
                                    Debug.log(error);
                                }
                            });
                            recyclerView.setAdapter(new SlideInLeftAnimatorAdapter(adapter, recyclerView));
                        } else {
                            OtherUserProfileActivity.this.findViewById(R.id.no_songs_frame_layout).setVisibility(View.VISIBLE);

                            String text = title + " " + OtherUserProfileActivity.this.getString(R.string.has_not_listened_yet);
                            ((TextView) OtherUserProfileActivity.this.findViewById(R.id.no_music_text_view)).setText(text);
                        }
                    }
                }).startQuery();

        loadBitmap(imageUrl);
        checkIfAlreadyFollowing();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        loadBitmap(imageUrl);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_other_user, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_profile_picture:
                startImageActivity();
                break;
            case R.id.action_other_info:
                showOtherInfoDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startImageActivity() {
        Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("image-url", imageUrl);
        startActivity(intent);
    }

    private String addSIfPluralFollower(String value) {
        return Integer.valueOf(value) > 1 ? "s" : "";
    }

    private String addSIfPluralUsingComma(String value) {
        return value.split(",").length >= 1
                && Locale.getDefault().getLanguage().startsWith("en") ? "s" : "";
    }

    private void showOtherInfoDialog() {
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.other_info_dialog, null);

        for (int id : DIALOG_TEXTVIEW_IDS) {
            TextView textView = (TextView) dialogView.findViewById(id);
            String text = null;
            if (id == DIALOG_TEXTVIEW_IDS[0]) {
                text = textView.getText() + addSIfPluralUsingComma(artists) + ": " + artists;
            } else if (id == DIALOG_TEXTVIEW_IDS[1]) {
                text = textView.getText() + addSIfPluralFollower(nFollower) + ": " + nFollower;
            } else if (id == DIALOG_TEXTVIEW_IDS[2]) {
                text = textView.getText() + addSIfPluralUsingComma(genres) + ": " + genres;
            }
            textView.setText(text);
        }

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getString(R.string.other_info_about) + " " + title);
        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (alertDialog != null) alertDialog.dismiss();
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void checkIfAlreadyFollowing() {
        Query.getInstance(OtherUserProfileActivity.this, Query.QueryType.CHECK_IF_FOLLOWING)
                .setHisEmail(email)
                .setShowDialog(false)
                .setOnResultListener(new Query.OnResultListener() {
                    @Override
                    public void onResult(String result) {
                        floatingActionButton.show();
                        if (!result.equals("not_following")) {
                            // Already following
                            mustAddToFollowing = false;
                            OtherUserProfileActivity.this.setFab(R.drawable.close_white, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    fabClick();
                                }
                            });
                        } else {
                            // Not following
                            mustAddToFollowing = true;
                            OtherUserProfileActivity.this.setFab(R.drawable.tick_white, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    fabClick();
                                }
                            });
                        }
                    }
                }).startQuery();
    }

    private void fabClick() {
        if (mustAddToFollowing) {
            addToFollowing();
            setFab(R.drawable.close_white, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OtherUserProfileActivity.this.fabClick();
                }
            });
        } else {
            removeFromFollowing();
            setFab(R.drawable.tick_white, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OtherUserProfileActivity.this.fabClick();
                }
            });
        }
        mustAddToFollowing = !mustAddToFollowing;
    }

    private void setFab(int imageRes, View.OnClickListener onClickListener) {
        floatingActionButton.setImageDrawable(getResources().getDrawable(imageRes));
        floatingActionButton.setOnClickListener(onClickListener);
    }

    private void removeFromFollowing() {
        Query.getInstance(OtherUserProfileActivity.this, Query.QueryType.REMOVE_FROM_FOLLOWING)
                .setHisEmail(email)
                .setOnResultListener(new Query.OnResultListener() {
                    @Override
                    public void onResult(String result) {
                        Toast.makeText(OtherUserProfileActivity.this, title + " " + OtherUserProfileActivity.this.getString(R.string.removed),
                                Toast.LENGTH_SHORT).show();
                    }
                }).startQuery();
    }

    private void addToFollowing() {
        Query.getInstance(OtherUserProfileActivity.this, Query.QueryType.ADD_TO_FOLLOWING)
                .setHisEmail(email)
                .setOnResultListener(new Query.OnResultListener() {
                    @Override
                    public void onResult(String result) {
                        ServerConnectionUtils.getContent(Constants.ADD_TO_NEW_FOLLOWERS
                                + "?myname=" + ParseValues.getParsedName(PreferencesUtils
                                .getName(OtherUserProfileActivity.this.getApplicationContext()))
                                + "&hisemail=" + email);
                    }
                }).startQuery();
        Toast.makeText(OtherUserProfileActivity.this, title + " " + getString(R.string.added), Toast.LENGTH_SHORT).show();
    }

    private void loadBitmap(final String url) {
        final ImageView imageProfile = (ImageView) findViewById(R.id.image_profile);

        if (url.equals("no")) {
            Picasso.with(this)
                    .load(R.drawable.default_image_profile)
                    .transform(new CircleTransform())
                    .into(imageProfile, new Callback() {
                        @Override
                        public void onSuccess() {
                            CircularRevealUtils.enterReveal(headerBackground);

                            int color = getResources().getColor(R.color.colorPrimary);
                            int darkColor = getResources().getColor(R.color.colorPrimaryDark);

                            headerBackground.setBackgroundColor(color);

                            collapsingToolbarLayout.setContentScrimColor(color);
                            collapsingToolbarLayout.setStatusBarScrimColor(color);
                            floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(darkColor));

                            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
                        }

                        @Override
                        public void onError() {
                        }
                    });
        } else {
            Picasso.with(this)
                    .load(url)
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                            imageProfile.setImageBitmap(BitmapUtils.getCircleBitmap(bitmap));
                            imageProfile.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startImageActivity();
                                }
                            });

                            final Bitmap blurred = BitmapUtils.blurRenderScript(getApplicationContext(), bitmap);

                            headerBackground.setBackground(new BitmapDrawable(getResources(), blurred));
                            CircularRevealUtils.enterReveal(headerBackground);

                            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {
                                    int primaryDark = getResources().getColor(R.color.colorPrimaryDark);
                                    int primary = getResources().getColor(R.color.colorPrimary);

                                    int darkMuted = palette.getDarkMutedColor(primaryDark);
                                    int darkVibrant = palette.getDarkVibrantColor(primaryDark);
                                    int muted = palette.getMutedColor(primary);

                                    collapsingToolbarLayout.setContentScrimColor(muted);
                                    collapsingToolbarLayout.setStatusBarScrimColor(darkMuted);
                                    floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(darkVibrant));

                                    getWindow().setStatusBarColor(darkVibrant);
                                }
                            });
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                        }
                    });
        }
    }
}