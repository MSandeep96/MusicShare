package com.lob.musicshare.query;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.lob.musicshare.util.Constants;
import com.lob.musicshare.util.Debug;
import com.lob.musicshare.util.ParseValues;
import com.lob.musicshare.util.PreferencesUtils;
import com.lob.musicshare.util.web.ServerConnectionUtils;

import dmax.dialog.SpotsDialog;

public class Query {

    private final QueryType whatToFind;
    private final Activity activity;

    private OnResultListener onResultListener;

    private String artist, genre, myEmail, personToFind, hisEmail, youTubeQuery;

    private boolean visibleDialog;

    public Query(Activity activity, QueryType queryType) {
        this.whatToFind = queryType;
        this.activity = activity;
        this.myEmail = ParseValues.getParsedEmail(PreferencesUtils.getEmail(activity));
    }

    public static Query getInstance(Activity activity, QueryType queryType) {
        return new Query(activity, queryType);
    }

    public Query setArtist(String artist) {
        this.artist = artist;
        return this;
    }

    public Query setGenre(String genre) {
        this.genre = genre;
        return this;
    }

    public Query setPersonToFind(String personToFind) {
        this.personToFind = personToFind;
        return this;
    }

    public Query setOnResultListener(OnResultListener onResultListener) {
        this.onResultListener = onResultListener;
        return this;
    }

    public void startQuery() {
        switch (whatToFind) {
            case BY_ARTISTS:
                if (artist == null) {
                    throw new NullPointerException("Artist is null");
                }

                new Search(Constants.SEARCH_BY_ARTISTS_URL
                        + "?artist=" + artist).execute("");
                break;
            case GET_FOLLOWING:
                if (myEmail == null) {
                    throw new NullPointerException("MyEmail is null!");
                }

                new Search(Constants.GET_FOLLOWING
                        + "?email=" + myEmail).execute("");
                break;
            case BY_GENRES:
                if (genre == null) {
                    throw new NullPointerException("Genre is null");
                }

                new Search(Constants.SEARCH_BY_GENRE_URL
                        + "?genre=" + genre).execute("");
                break;
            case GET_TRACKS:
                if (myEmail == null) {
                    throw new NullPointerException("MyEmail is null!");
                }

                new Search(Constants.GET_TRACKS
                        + "?email=" + myEmail).execute("");
                break;
            case ADD_TO_FOLLOWING:
                if (myEmail == null) {
                    throw new NullPointerException("MyEmail is null");
                }

                if (hisEmail == null) {
                    throw new NullPointerException("HisEmail is null");
                }

                new Search(Constants.ADD_TO_FOLLOWING_URL
                        + "?myemail=" + myEmail
                        + "&hisemail=" + hisEmail).execute("");
                break;
            case ADD_TO_NEW_FOLLOWERS:
                if (hisEmail == null) {
                    throw new NullPointerException("HisEmail is null");
                }

                new Search(Constants.ADD_TO_NEW_FOLLOWERS
                        + "?myname=" + ParseValues.getParsedName(PreferencesUtils.getName(activity))
                        + "&hisemail=" + hisEmail);
                break;
            case REMOVE_FROM_FOLLOWING:
                if (hisEmail == null) {
                    throw new NullPointerException("HisEmail is null");
                }

                new Search(Constants.REMOVE_FROM_FOLLOWING_URL
                        + "?myemail=" + myEmail
                        + "&hisemail=" + hisEmail).execute("");
                break;
            case CHECK_IF_FOLLOWING:
                if (myEmail == null) {
                    throw new NullPointerException("MyEmail is null");
                }

                if (hisEmail == null) {
                    throw new NullPointerException("HisEmail is null");
                }

                new Search(Constants.CHECK_IF_FOLLOWING
                        + "?myemail=" + myEmail
                        + "&hisemail=" + hisEmail).execute("");
                break;
            case PEOPLE:
                if (personToFind == null) {
                    throw new NullPointerException("PersonToFind is null");
                }

                new Search(Constants.SEARCH_PEOPLE
                        + "?name=" + personToFind).execute("");
                break;
            case GET_USER_INFO:
                if (hisEmail == null) {
                    throw new NullPointerException("HisEmail is null");
                }

                new Search(Constants.GET_USER_INFO
                        + "?email=" + hisEmail).execute("");
                break;
            case GET_N_FOLLOWERS:
                if (hisEmail == null) {
                    throw new NullPointerException("HisEmail is null");
                }

                new Search(Constants.GET_N_FOLLOWERS
                        + "?hisemail" + hisEmail).execute("");
                break;
            case EDIT_ARTISTS:
                if (artist == null) {
                    throw new NullPointerException("Artist is null");
                }

                if (myEmail == null) {
                    throw new NullPointerException("Email is null");
                }

                new Search(Constants.EDIT_ARTISTS
                        + "?artists=" + artist
                        + "&email=" + myEmail).execute("");
                break;
            case EDIT_GENRES:
                if (genre == null) {
                    throw new NullPointerException("Genre is null");
                }

                if (myEmail == null) {
                    throw new NullPointerException("Email is null");
                }

                new Search(Constants.EDIT_GENRES
                        + "?genres=" + genre
                        + "&email=" + myEmail).execute("");
                break;
            case YOUTUBE_SEARCH:
                if (youTubeQuery == null) {
                    throw new NullPointerException("Query is null");
                }

                new Search(Constants.YOUTUBE_SEARCH
                        + "?query=" + youTubeQuery).execute("");
        }
    }

    public Query setYouTubeSearch(String query) {
        this.youTubeQuery = query;
        return this;
    }

    public Query setShowDialog(boolean visibleDialog) {
        this.visibleDialog = visibleDialog;
        return this;
    }

    public Query setHisEmail(String hisEmail) {
        this.hisEmail = hisEmail;
        return this;
    }

    public enum QueryType {
        BY_ARTISTS, BY_GENRES, GET_TRACKS, ADD_TO_FOLLOWING, ADD_TO_NEW_FOLLOWERS,
        REMOVE_FROM_FOLLOWING, CHECK_IF_FOLLOWING, PEOPLE, GET_USER_INFO, GET_FOLLOWING,
        GET_N_FOLLOWERS, EDIT_ARTISTS, EDIT_GENRES, YOUTUBE_SEARCH
    }

    public interface OnResultListener {
        void onResult(String result);
    }

    private class Search extends AsyncTask<String, String, String> {

        private String result, url;

        private AlertDialog progressDialog;

        Search(@NonNull String url) {
            String password = ParseValues.getParsedPassword(PreferencesUtils.getPassword(activity));
            this.url = url + "&pwd=" + password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Debug.log("Search AsyncTask -> onPreExecute");
            if (visibleDialog) {
                progressDialog = new SpotsDialog(activity);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            Debug.log("Search AsyncTask -> doInBackground");
            result = ServerConnectionUtils.getContent(url);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Debug.log("Search AsyncTask -> onPostExecute");
            if (visibleDialog) {
                progressDialog.cancel();
            }

            if (onResultListener != null) {
                onResultListener.onResult(result);
            }
        }
    }
}
