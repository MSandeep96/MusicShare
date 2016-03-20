package com.lob.musicshare.user;

public class User {

    public final String email, profileImageUrl, name, surname, userName, artists, genres, songName;
    public final int numberFollowers, numberFollowing;

    public User(String email, String profileImageUrl, String name, String surname, String userName,
                String artists, String genres, String songName, int numberFollowers, int numberFollowing) {
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.name = name;
        this.surname = surname;
        this.userName = userName;
        this.artists = artists;
        this.genres = genres;
        this.songName = songName;
        this.numberFollowers = numberFollowers;
        this.numberFollowing = numberFollowing;
    }
}
