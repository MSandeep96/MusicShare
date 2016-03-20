package com.lob.musicshare.util;

public class ParseValues {

    public static final String DOT = "__dot__";
    public static final String AT = "__at__";
    public static final String OPEN_PARENTHESIS = "__opar__";
    public static final String CLOSED_PARENTHESIS = "__cpar__";
    public static final String COMMA = "__and__";
    public static final String TWO_DASHES = "--";
    public static final String APOSTROPHE = "__apostrophe__";
    public static final String OTHER_APOSTROPHE = "̀";
    public static final String BY = "--by--";
    public static final String OTHER_ARTIST_SEPARATOR = "__OTHERARTIST__";
    public static final String OTHER_SONG_SEPARATOR = "__OTHERSONG__";
    public static final String OTHER_USER_SEPARATOR = "__OTHERUSER__";
    public static final String THREE_BLANK_SPACES = "   ";
    public static final String TWO_BLANK_SPACES = "  ";
    public static final String ONE_BLANK_SPACE = " ";
    public static final String SINGLE_DASH = "-";
    public static final String TWO_COMMAS = ",,";

    public static String getParsedPassword(String password) {
        return password.replace(".", DOT)
                .replace("@", AT)
                .replace("(", OPEN_PARENTHESIS)
                .replace(")", CLOSED_PARENTHESIS)
                .replaceAll("^[^a-zA-Z0-9\\s]+|[^a-zA-Z0-9\\s]+$", "");
    }

    public static String getParsedEmail(String email) {
        return email.replace(".", DOT)
                .replace("@", AT)
                .replace("(", OPEN_PARENTHESIS)
                .replace(")", CLOSED_PARENTHESIS)
                .replaceAll("^[^a-zA-Z0-9\\s]+|[^a-zA-Z0-9\\s]+$", "");
    }

    public static String getParsedArtists(String artist) {
        return artist.replace(" ", TWO_DASHES)
                .replace(",", COMMA)
                .replace("'", APOSTROPHE)
                .replace(".", DOT)
                .replaceAll("^[^a-zA-Z0-9\\s]+|[^a-zA-Z0-9\\s]+$", "");
    }

    public static String getParsedGenres(String genres) {
        return genres.replace(" ", TWO_DASHES)
                .replace(",", "__and__")
                .replace("'", APOSTROPHE)
                .replace(".", DOT)
                .replaceAll("^[^a-zA-Z0-9\\s]+|[^a-zA-Z0-9\\s]+$", "");
    }

    public static String getParsedName(String name) {
        return name.replace(" ", TWO_DASHES)
                .replaceAll("^[^a-zA-Z0-9\\s]+|[^a-zA-Z0-9\\s]+$", "");
    }

    public static String getParsedSurname(String surname) {
        return surname.replace(" ", TWO_DASHES)
                .replaceAll("^[^a-zA-Z0-9\\s]+|[^a-zA-Z0-9\\s]+$", "");
    }

    public static String getParsedSongName(String track) {
        return track.replace(" ", TWO_DASHES)
                .replace(",", COMMA)
                .replace("'", APOSTROPHE)
                .replace("(", OPEN_PARENTHESIS)
                .replace(")", CLOSED_PARENTHESIS)
                .replace(".", DOT)
                .replace(" , ", "")
                .replace("-------", "-----")
                .replace("----", "__and__--")
                .replaceAll("^[^a-zA-Z0-9\\s]+|[^a-zA-Z0-9\\s]+$", "");
    }
}
