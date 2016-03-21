package com.lob.musicshare.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.lob.musicshare.R;
import com.lob.musicshare.adapter.recyclerview.UserAdapter;
import com.lob.musicshare.adapter.viewpager.ViewPagerAdapter;
import com.lob.musicshare.fragment.ArtistsFragment;
import com.lob.musicshare.fragment.GenresFragment;
import com.lob.musicshare.fragment.PeopleFragment;
import com.lob.musicshare.fragment.ProfileFragment;
import com.lob.musicshare.json.ParseJson;
import com.lob.musicshare.query.Query;
import com.lob.musicshare.user.User;
import com.lob.musicshare.util.AndroidOverViewUtils;
import com.lob.musicshare.util.Constants;
import com.lob.musicshare.util.NotificationUtils;
import com.lob.musicshare.util.ParseValues;
import com.lob.musicshare.util.PhotoSelectUtils;
import com.lob.musicshare.util.PreferencesUtils;
import com.lob.musicshare.util.ui.RoundedCornerLayout;
import com.lob.musicshare.util.ui.StatusBarColorChanger;
import com.lob.musicshare.util.ui.ToolbarColorizeUtils;
import com.lob.musicshare.util.web.UploadProfilePicture;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.soundcloud.android.crop.Crop;

import java.io.File;

public class ContentActivity extends AppCompatActivity {

    private static final int SELECT_PHOTO = 100;
    private static final int[] TAB_ICONS = {
            R.drawable.people, R.drawable.artists, R.drawable.genres, R.drawable.profile
    };
    private static final Fragment[] FRAGMENTS = {
            new PeopleFragment(), new ArtistsFragment(), new GenresFragment(), new ProfileFragment()
    };

    private boolean customToastClicked;

    private int fragmentIndex;

    private ViewPagerAdapter viewPagerAdapter;
    private MaterialSearchView searchView;
    private MenuItem searchItem;
    private View inflatedCustomToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_content);

        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        final FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        floatingActionButton.hide();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appBarLayout.setExpanded(true, true);
                searchView.showSearch(true);
            }
        });

        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.showOverflowMenu();

        ToolbarColorizeUtils.setOverflowButtonColor(this,
                new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP));
        setSupportActionBar(toolbar);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout1, int verticalOffset) {
                if (verticalOffset == 0) {
                    floatingActionButton.hide();
                } else if (fragmentIndex != 3) {
                    floatingActionButton.show();
                }
            }
        });

        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        final ViewPager pager = (ViewPager) findViewById(R.id.view_pager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FRAGMENTS);
        pager.setAdapter(viewPagerAdapter);

        Typeface fontFace = Typeface.createFromAsset(getApplication().getAssets(), Constants.TYPEFACE_NAME);
        ((TextView) findViewById(R.id.title_toolbar)).setTypeface(fontFace);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(pager);
        tabLayout.setTabTextColors(Color.WHITE, Color.WHITE);

        for (int i = 0; i < TAB_ICONS.length; i++) {
            tabLayout.getTabAt(i).setIcon(getResources().getDrawable(TAB_ICONS[i]));
        }

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                fragmentIndex = tab.getPosition();
                pager.setCurrentItem(fragmentIndex);

                handleSearchIcon();

                if (fragmentIndex == 0) {
                    loadPeople();
                } else if (fragmentIndex == 3) {
                    floatingActionButton.hide();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        AndroidOverViewUtils.setHeader(this);

        loadPeople();
        NotificationUtils.setNotificationAlarm(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        searchView = (MaterialSearchView) findViewById(R.id.search_view);

        searchItem = menu.findItem(R.id.action_search);
        searchItem.getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        Drawable back = getResources().getDrawable(R.drawable.ic_action_navigation_arrow_back);
        Drawable close = getResources().getDrawable(R.drawable.ic_action_navigation_close);

        back.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        close.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);

        searchView.setBackIcon(back);
        searchView.setCloseIcon(close);

        searchView.setMenuItem(searchItem);
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                StatusBarColorChanger.setStatusBarColorWithFade(getWindow(),
                        getResources().getColor(R.color.colorPrimaryDark),
                        getResources().getColor(R.color.md_grey_500));

                if (Constants.MARSHMALLOW_OR_EARLIER) {
                    StatusBarColorChanger.setLightStatusBar(getWindow().getDecorView());
                }
            }

            @Override
            public void onSearchViewClosed() {
                StatusBarColorChanger.setStatusBarColorWithFade(getWindow(),
                        getResources().getColor(R.color.md_grey_500),
                        getResources().getColor(R.color.colorPrimaryDark));
                if (Constants.MARSHMALLOW_OR_EARLIER) {
                    StatusBarColorChanger.clearLightStatusBar(getWindow().getDecorView());
                }
            }
        });
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String inputText) {
                loadNewPeople(inputText);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        handleSearchIcon();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_artists:
                Query.getInstance(this, Query.QueryType.GET_USER_INFO)
                        .setShowDialog(true)
                        .setHisEmail(ParseValues.getParsedEmail(PreferencesUtils.getEmail(getApplicationContext())))
                        .setOnResultListener(new Query.OnResultListener() {
                            @Override
                            public void onResult(String result) {
                                final User user = ParseJson.generateUsers(result).get(0);
                                new MaterialDialog.Builder(ContentActivity.this)
                                        .title(R.string.edit_artists)
                                        .inputType(InputType.TYPE_CLASS_TEXT)
                                        .input("", user.artists, new MaterialDialog.InputCallback() {
                                            @Override
                                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                                if (input.toString().equals(user.artists)) {
                                                    Toast.makeText(ContentActivity.this, R.string.done, Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Query.getInstance(ContentActivity.this, Query.QueryType.EDIT_ARTISTS)
                                                            .setArtist(ParseValues.getParsedArtists(input.toString()))
                                                            .setShowDialog(false)
                                                            .setOnResultListener(new Query.OnResultListener() {
                                                                @Override
                                                                public void onResult(String result) {
                                                                    Toast.makeText(ContentActivity.this, R.string.done, Toast.LENGTH_SHORT).show();
                                                                }
                                                            }).startQuery();
                                                }
                                            }
                                        }).show();
                            }
                        }).startQuery();
                break;
            case R.id.action_edit_genres:
                Query.getInstance(this, Query.QueryType.GET_USER_INFO)
                        .setShowDialog(true)
                        .setHisEmail(ParseValues.getParsedEmail(PreferencesUtils.getEmail(getApplicationContext())))
                        .setOnResultListener(new Query.OnResultListener() {
                            @Override
                            public void onResult(String result) {
                                final User user = ParseJson.generateUsers(result).get(0);
                                new MaterialDialog.Builder(ContentActivity.this)
                                        .title(R.string.edit_genres)
                                        .inputType(InputType.TYPE_CLASS_TEXT)
                                        .input("", user.genres, new MaterialDialog.InputCallback() {
                                            @Override
                                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                                if (input.toString().equals(user.genres)) {
                                                    Toast.makeText(ContentActivity.this, R.string.done, Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Query.getInstance(ContentActivity.this, Query.QueryType.EDIT_GENRES)
                                                            .setGenre(ParseValues.getParsedGenres(input.toString()))
                                                            .setShowDialog(false)
                                                            .setOnResultListener(new Query.OnResultListener() {
                                                                @Override
                                                                public void onResult(String result) {
                                                                    Toast.makeText(ContentActivity.this, R.string.done, Toast.LENGTH_SHORT).show();
                                                                }
                                                            }).startQuery();
                                                }
                                            }
                                        }).show();
                            }
                        }).startQuery();
                break;
            case R.id.action_show_profile_picture:
                Intent intent = new Intent(ContentActivity.this, ImageActivity.class);
                intent.putExtra("title", PreferencesUtils.getName(getApplicationContext()));
                intent.putExtra("image-url", Constants.PROFILE_PICTURE
                        + ParseValues.getParsedEmail(PreferencesUtils.getEmail(getApplicationContext())));
                startActivity(intent);
                break;
            case R.id.action_change_profile_picture:
                PhotoSelectUtils.selectPhoto(ContentActivity.this, SELECT_PHOTO);
                break;
            case R.id.action_show_tutorial:
                startActivity(new Intent(ContentActivity.this, IntroductionActivity.class));
                break;
            case R.id.action_settings:
                startActivity(new Intent(ContentActivity.this, SettingsActivity.class));
                break;
            case R.id.action_exit:
                PreferencesUtils.exit(getApplicationContext());
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    Crop.of(selectedImage, Uri.fromFile(new File(Constants.PROFILE_IMAGE_PATH))).
                            asSquare().start(ContentActivity.this);
                }
                break;
            case Crop.REQUEST_CROP:
                if (resultCode == Activity.RESULT_OK) {
                    new UploadProfilePicture(ContentActivity.this,
                            ParseValues.getParsedEmail(PreferencesUtils.getEmail(getApplicationContext())),
                            Crop.getOutput(imageReturnedIntent).getPath());
                }
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        loadPeople();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
    }

    private View getFragmentRootView() {
        return viewPagerAdapter.getItem(fragmentIndex).getView();
    }

    private void loadNewPeople(String inputText) {
        final View fragmentRootView = getFragmentRootView();

        if (fragmentRootView != null) {
            final RecyclerView recyclerView = (RecyclerView) fragmentRootView.findViewById(R.id.recycler_view);
            recyclerView.setVisibility(View.GONE);

            final ProgressWheel progressWheel = (ProgressWheel) fragmentRootView.findViewById(R.id.progress_wheel);
            progressWheel.setVisibility(View.VISIBLE);

            final RelativeLayout tipContainer = (RelativeLayout) fragmentRootView.findViewById(R.id.tip_container);
            tipContainer.setVisibility(View.GONE);

            Query.QueryType queryType = getQueryType();
            Query query = Query.getInstance(ContentActivity.this, queryType)
                    .setOnResultListener(new Query.OnResultListener() {
                        @Override
                        public void onResult(String jsonResult) {
                            progressWheel.setVisibility(View.GONE);
                            if (!jsonResult.equals("no_one")) {
                                if (fragmentIndex == 0) {
                                    if (inflatedCustomToast == null || customToastClicked) {
                                        final LayoutInflater layoutInflater = LayoutInflater.from(ContentActivity.this);
                                        inflatedCustomToast = layoutInflater.inflate(R.layout.custom_toast, (ViewGroup) fragmentRootView);
                                        final RoundedCornerLayout roundedCustomToast = (RoundedCornerLayout)
                                                inflatedCustomToast.findViewById(R.id.custom_toast_rounded_layout);

                                        customToastClicked = false;

                                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                                                roundedCustomToast.getLayoutParams();
                                        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                                        roundedCustomToast.setLayoutParams(layoutParams);

                                        roundedCustomToast.setVisibility(View.INVISIBLE);

                                        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
                                        anim.setDuration(500);
                                        anim.setAnimationListener(new Animation.AnimationListener() {
                                            @Override
                                            public void onAnimationStart(Animation animation) {
                                            }

                                            @Override
                                            public void onAnimationEnd(Animation animation) {
                                                roundedCustomToast.setVisibility(View.VISIBLE);
                                                roundedCustomToast.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        customToastClicked = true;
                                                        getSupportFragmentManager()
                                                                .beginTransaction()
                                                                .detach(viewPagerAdapter.getItem(fragmentIndex))
                                                                .attach(viewPagerAdapter.getItem(fragmentIndex))
                                                                .commit();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onAnimationRepeat(Animation animation) {
                                            }
                                        });
                                        inflatedCustomToast.startAnimation(anim);
                                    }
                                }

                                recyclerView.setVisibility(View.VISIBLE);
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setLayoutManager(new LinearLayoutManager(ContentActivity.this));
                                recyclerView.setItemViewCacheSize(Integer.MAX_VALUE);
                                UserAdapter adapter = new UserAdapter(ContentActivity.this,
                                        ParseJson.generateUsers(jsonResult));
                                recyclerView.setAdapter(adapter);
                            } else {
                                tipContainer.setVisibility(View.VISIBLE);
                            }
                        }
                    });
            if (queryType != null) {
                switch (queryType) {
                    case BY_ARTISTS:
                        query.setArtist(inputText.replace(" ", "--"));
                        break;
                    case BY_GENRES:
                        query.setGenre(inputText.replace(" ", "--"));
                        break;
                    case PEOPLE:
                        query.setPersonToFind(inputText.replace(" ", "--"));
                        break;
                }
                query.startQuery();
            }
        }
    }

    private void loadPeople() {
        // As getView() returns null, use a listener
        ((PeopleFragment) viewPagerAdapter.getItem(0)).setCreationListener(new PeopleFragment.OnFragmentRootViewCreated() {
            @Override
            public void onViewCreated(View rootView) {
                final ProgressWheel progressWheel = (ProgressWheel) rootView.findViewById(R.id.progress_wheel);
                progressWheel.setVisibility(View.VISIBLE);

                final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
                recyclerView.setVisibility(View.GONE);

                final RelativeLayout tipContainer = (RelativeLayout) rootView.findViewById(R.id.tip_container);
                tipContainer.setVisibility(View.GONE);

                Query.getInstance(ContentActivity.this, Query.QueryType.GET_FOLLOWING)
                        .setOnResultListener(new Query.OnResultListener() {
                            @Override
                            public void onResult(String result) {
                                if (!result.equals("")) {
                                    progressWheel.setVisibility(View.GONE);
                                    tipContainer.setVisibility(View.GONE);

                                    String[] parts = result.split(ParseValues.OTHER_USER_SEPARATOR);

                                    recyclerView.setVisibility(View.VISIBLE);
                                    recyclerView.setHasFixedSize(true);
                                    recyclerView.setItemViewCacheSize(Integer.MAX_VALUE);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(ContentActivity.this));
                                    recyclerView.setAdapter(new UserAdapter(ContentActivity.this, parts));
                                } else {
                                    progressWheel.setVisibility(View.GONE);
                                    tipContainer.setVisibility(View.VISIBLE);
                                }
                            }
                        }).startQuery();
            }
        });
    }

    private void handleSearchIcon() {
        if (searchItem != null && searchView != null) {
            searchView.closeSearch();
            searchView.clearFocus();

            searchItem.setVisible(getQueryType() != Query.QueryType.GET_TRACKS);
        }
    }

    private Query.QueryType getQueryType() {
        switch (fragmentIndex) {
            case 0:
                return Query.QueryType.PEOPLE;
            case 1:
                return Query.QueryType.BY_ARTISTS;
            case 2:
                return Query.QueryType.BY_GENRES;
            default:
                return Query.QueryType.GET_TRACKS;
        }
    }
}
