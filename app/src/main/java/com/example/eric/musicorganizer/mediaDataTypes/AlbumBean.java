package com.example.eric.musicorganizer.mediaDataTypes;

public class AlbumBean implements java.io.Serializable {

    private int id;
    private int numberOfSongs;
    private String albumArtPath;
    private String albumArtist;

    public String getAlbumArtist() {
        return albumArtist;
    }

    public void setAlbumArtist(final String albumArtist) {
        this.albumArtist = albumArtist;
    }

    public String getAlbumArtPath() {
        return albumArtPath;
    }

    public void setAlbumArtPath(final String albumArtPath) {
        this.albumArtPath = albumArtPath;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public int getNumberOfSongs() {
        return numberOfSongs;
    }

    public void setNumberOfSongs(final int numberOfSongs) {
        this.numberOfSongs = numberOfSongs;
    }
}
