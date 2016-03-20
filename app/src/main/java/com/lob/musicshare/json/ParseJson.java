package com.lob.musicshare.json;

import com.lob.musicshare.user.User;
import com.lob.musicshare.util.Constants;
import com.lob.musicshare.util.ParseValues;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ParseJson {

    public static ArrayList<User> generateUsers(String result) {
        ArrayList<User> userArrayList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String email = jsonObject.getString("email");
                String profileImageUrl = !jsonObject.getString("profile_picture").equals("")
                        ? Constants.PROFILE_PICTURE + email
                        : "no";

                String name = jsonObject.getString("firstname").replace(ParseValues.TWO_DASHES, " ");
                String surname = jsonObject.getString("surname").replace(ParseValues.TWO_DASHES, " ");

                String userName = name + " " + surname
                        .replace(ParseValues.TWO_DASHES + " ", " ")
                        .replace(ParseValues.DOT, ".")
                        .replace(ParseValues.THREE_BLANK_SPACES, " ");

                String songName = jsonObject.getString("tracks")
                        .replace(ParseValues.BY, " by ")
                        .replace(ParseValues.TWO_DASHES, " ")
                        .replace(ParseValues.OTHER_SONG_SEPARATOR, ", ")
                        .replace(ParseValues.TWO_BLANK_SPACES, " ")
                        .replace(ParseValues.OPEN_PARENTHESIS, "(")
                        .replace(ParseValues.CLOSED_PARENTHESIS, ")")
                        .replace(ParseValues.APOSTROPHE, "'")
                        .replace(ParseValues.COMMA, " ")
                        .replace(ParseValues.DOT, ".")
                        .replace(ParseValues.SINGLE_DASH, " - ")
                        .replace(ParseValues.TWO_DASHES, " ")
                        .replace(ParseValues.TWO_COMMAS, ", ")
                        .replace("__dot", ".")
                        .replace("__cpar", ")")
                        .replaceAll("^[^a-zA-Z0-9\\s]+|[^a-zA-Z0-9\\s]+$", "");

                String artists = jsonObject.getString("artists")
                        .replace(ParseValues.OTHER_ARTIST_SEPARATOR, ", ")
                        .replace(ParseValues.TWO_DASHES, " ")
                        .replace(ParseValues.COMMA, ", ")
                        .replace(ParseValues.TWO_BLANK_SPACES, " ")
                        .replace(ParseValues.APOSTROPHE, "'")
                        .replace(ParseValues.DOT, ".")
                        .replace(" , ", "")
                        .replace(",,", ", ")
                        .replace("__dot", ".")
                        .replace("__cpar", ")")
                        .replaceAll("^[^a-zA-Z0-9\\s]+|[^a-zA-Z0-9\\s]+$", "");

                String genres = jsonObject.getString("genres")
                        .replace(ParseValues.COMMA, ", ")
                        .replace(",,", ",")
                        .replace(ParseValues.TWO_DASHES, " ")
                        .replace(ParseValues.DOT, ".")
                        .replace(ParseValues.TWO_BLANK_SPACES, " ")
                        .replace(ParseValues.APOSTROPHE, "'")
                        .replace("__dot", ".")
                        .replace("__cpar", ")")
                        .replaceAll("^[^a-zA-Z0-9\\s]+|[^a-zA-Z0-9\\s]+$", "");

                int numberFollowers = Integer.valueOf(jsonObject.getString("n_followers"));
                int numberFollowing = Integer.valueOf(jsonObject.getString("n_following"));

                userArrayList.add(new User(email, profileImageUrl, name, surname,
                        userName, artists, genres, songName, numberFollowers, numberFollowing));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return userArrayList;
    }
}
