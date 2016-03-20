package com.lob.musicshare.util;

import android.os.Build;

public class Constants {
    public static final String REGISTER_URL = "http://musicshare.altervista.org/db_handler/register_user.php";
    public static final String LOGIN_URL = "http://musicshare.altervista.org/db_handler/login_user.php";
    public static final String ADD_TO_LISTENED_URL = "http://musicshare.altervista.org/mobile/add_to_listened.php";
    public static final String ADD_TO_FOLLOWING_URL = "http://musicshare.altervista.org/query/add_to_following.php";
    public static final String REMOVE_FROM_FOLLOWING_URL = "http://musicshare.altervista.org/query/remove_from_following.php";
    public static final String CHECK_IF_FOLLOWING = "http://musicshare.altervista.org/query/check_if_already_following.php";
    public static final String GET_FOLLOWING = "http://musicshare.altervista.org/query/get_following.php";
    public static final String SEARCH_PEOPLE = "http://musicshare.altervista.org/query/search_people.php";
    public static final String GET_USER_INFO = "http://musicshare.altervista.org/query/get_user_info.php";
    public static final String GET_N_FOLLOWERS = "http://musicshare.altervista.org/query/get_number_followers.php";
    public static final String GET_TRACKS = "http://musicshare.altervista.org/query/get_tracks.php";
    public static final String EDIT_ARTISTS = "http://musicshare.altervista.org/query/edit_artists.php";
    public static final String EDIT_GENRES = "http://musicshare.altervista.org/query/edit_genres.php";
    public static final String YOUTUBE_SEARCH = "http://musicshare.altervista.org/query/youtube_video_id.php";
    public static final String UPLOAD_PROFILE_PICTURE_PHP_SCRIPT = "http://www.musicshare.altervista.org/mobile/upload_profile_picture.php";
    public static final String SEARCH_BY_ARTISTS_URL = "http://musicshare.altervista.org/query/byartist.php";
    public static final String SEARCH_BY_GENRE_URL = "http://musicshare.altervista.org/query/bygenre.php";
    public static final String ADD_TO_NEW_FOLLOWERS = "http://musicshare.altervista.org/query/add_to_new_follower.php";
    public static final String GET_NEW_FOLLOWERS = "http://musicshare.altervista.org/query/get_new_followers.php";
    public static final String REMOVE_NEW_FOLLOWERS = "http://musicshare.altervista.org/query/remove_new_followers.php";
    public static final String REMOVE_SONG = "http://musicshare.altervista.org/query/remove_song.php";
    public static final String CRASH_REPORT_URL = "http://musicshare.altervista.org/crash_reports/report.php";

    public static final String TYPEFACE_NAME = "fonts/lavanderia_sturdy.otf";

    public static final String PROFILE_PICTURE = "http://musicshare.altervista.org/profile_pictures/";

    public static final String RESULT_SUCCESS = "success";
    public static final String RESULT_ERROR = "error";

    public static final String PROFILE_IMAGE_PATH = "/sdcard/profile_image";

    public static final boolean MARSHMALLOW_OR_EARLIER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
}
