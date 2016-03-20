package com.lob.musicshare.youtube;

public class YouTubeVideo {

    public final String videoId, thumbnailUrl;

    public YouTubeVideo(String videoId) {
        this.videoId = videoId;
        this.thumbnailUrl = "http://img.youtube.com/vi/" + videoId + "/default.jpg";
    }
}
