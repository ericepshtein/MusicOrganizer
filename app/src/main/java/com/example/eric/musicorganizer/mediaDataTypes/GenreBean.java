package com.example.eric.musicorganizer.mediaDataTypes;

public class GenreBean implements java.io.Serializable {

    private long audioId;
    private String genreName;

    public long getAudioId() {
        return audioId;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setAudioId(long audioId) {
        this.audioId = audioId;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }
}
