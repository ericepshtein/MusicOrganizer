package com.example.eric.musicorganizer.mediaDataTypes;

import com.example.eric.musicorganizer.musicManager.MusicManager;

import java.util.Arrays;

public class Song implements Comparable<Song> {

    private int discNumber;
    private int trackNumber;
    private int totalTrackNumber;
    private int year;
    private long duration;
    private long id;
    private String album;
    private String albumArtPath;
    private String albumArtist;
    private String[] artists;
    private String[] albumArtists;
    private String[] genres;
    private String composer;
    private String data;
    private String genre;
    private String title;
    private String titleToSort;

    public Song() {}

    public Song(long id, String title, String album, String albumArtPath, String artist, String albumArtist, String genre, String composer, long duration, int year, int trackNumber, int totalTrackNumber, String data) {
        setId(id);
        setTitle(title);
        setAlbum(album);
        setAlbumArtPath(albumArtPath);
        setArtists(artist);
        setAlbumArtist(albumArtist);
        setComposer(composer);
        setGenre(genre);
        setDuration(duration);
        setYear(year);
        setTrackAndDiscNumber(trackNumber);
        setTotalTrackNumber(totalTrackNumber);
        setData(data);
    }

    public void setArtists(String artistsString) {
        if (artistsString == null) {
            return;
        }
        String[] artists = artistsString.trim().split(";");
        for (int i = 0; i < artists.length; i++) {
            artists[i] = (artists[i] != null) ? artists[i].trim() : "";
        }
        this.artists = artists;
    }

    public void setArtists(String[] artists) {
        if (artists == null || artists.length == 0) {
            return;
        }
        this.artists = new String[artists.length];
        for (int i = 0; i < artists.length; i++) {
            this.artists[i] = (artists[i] != null) ? artists[i].trim() : "";
        }
    }

    public String[] getAlbumArtists() {
        return albumArtists;
    }

    public void setAlbumArtist(String albumArtistString) {
        if (albumArtistString == null || albumArtistString.isEmpty()) {
            albumArtist = "";
        }
        else {
            if (albumArtistString.contains(";")) {
                albumArtists = albumArtistString.split(";");
                albumArtist = getArtistsString(albumArtists).trim();
            }
            else {
                albumArtist = albumArtistString.trim();
                albumArtists = new String[1];
                albumArtists[0] = this.albumArtist;
            }
        }
    }

    public void setAlbumArtist(String[] albumArtists) {
        if (albumArtists == null || albumArtists.length == 0) {
            return;
        }
        this.albumArtists = new String[albumArtists.length];
        for (int i = 0; i < albumArtists.length; i++) {
           this.albumArtists[i] = (albumArtists[i] != null) ? albumArtists[i].trim() : "";
        }
    }

    public void setTrackAndDiscNumber(int discAndTrackNumber) {
        int DISC_OFFSET = 1000;
        if (discAndTrackNumber < DISC_OFFSET) {
            setDiscNumber(0);
            setTrackNumber(discAndTrackNumber);
        }
        else {
            setDiscNumber(discAndTrackNumber / DISC_OFFSET);
            setTrackNumber(discAndTrackNumber % DISC_OFFSET);
        }
    }

    public void setTotalTrackNumber(int totalTrackNumber) {
        this.totalTrackNumber = totalTrackNumber;
    }

    public void setComposer(String composer) {
        this.composer = (composer != null) ? composer.trim() : "";
    }

    public void setGenre(String genreString) {
        if (genreString == null || genreString.isEmpty()) {
            genre = "";
        }
        else {
            if (genreString.contains(";")) {
                genres = genreString.split(";");
                genre = getGenresString(genres).trim();
            }
            else {
                genre = genreString.trim();
                genres = new String[1];
                genres[0] = this.genre;
            }
        }
        //this.genre = (genre != null) ? genre.trim() : "";
    }

    public void setTitle(String title) {
        this.title = (title != null) ? title.trim() : "";
        if (!this.title.isEmpty()) {
            setTitleToSort(this.title);
        }
    }

    private void setTitleToSort(String title) {
        this.titleToSort = (title != null) ? MusicManager.titleToSort(title) : "";
    }

    public void setAlbum(String album) {
        this.album = (album != null) ? album.trim() : "";
    }

    public void setId(String id) {
        if (id != null && !id.isEmpty()) {
            try {
                this.id = Long.parseLong(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setYear(int year) {
        this.year = (year > 0) ? year : 0;
    }

    public void setData(String data) {
        this.data = (data != null) ? data : "";
    }

    public void setDuration(long duration) {
        this.duration = (duration > 0) ? duration : 0;
    }

    public void setDiscNumber(int discNumber) {
        this.discNumber = (discNumber > 0) ? discNumber : 0;
    }

    public void setTrackNumber(int trackNumber) {
        this.trackNumber = (trackNumber > 0) ? trackNumber : 0;
    }

    public String getAlbum() {
        return album;
    }

    public String getAlbumArtPath() {
        return albumArtPath;
    }

    public String getAlbumArtistString() {
        return albumArtist;
    }

    public long getId() {
        return id;
    }

    public String[] getArtist() {
        return artists;
    }

    public String getGenresString(String ... genres) {
        return getArtistsString(genres);
    }

    public String getArtistsString(String[] artist) {
        if (artist.length > 2) {
            StringBuilder artists = new StringBuilder();
            for (int i = 0; i < artist.length; i++) {
                if (i == artist.length - 2) {
                    artists.append(artist[i]).append(" & ").append(artist[i + 1]);
                    break;
                }
                else {
                    artists.append(artist[i]).append(", ");
                }
            }
            return artists.toString();
        }
        else if (artist.length == 2) {
            return artist[0] + " & " + artist[1];
        }
        else if (artist.length == 1) {
            return artist[0];
        }
        else {
            return "";
        }
    }

    public String getComposer() {
        return composer;
    }

    public String getData() {
        return data;
    }

    public int getDiscNumber() {
        return discNumber;
    }

    public long getDuration() {
        return duration;
    }

    public String getDurationString() {
        return MusicManager.durationToString(duration);
    }

    public String getGenre() {
        return genre;
    }

    public String[] getGenres() {
        return genres;
    }

    public String getTitle() {
        return title;
    }

    public String getTitleToSort() {
        return titleToSort;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public int getTotalTrackNumber() {
        return totalTrackNumber;
    }

    public int getYear() {
        return year;
    }

    public void setAlbumArtPath(String albumArtPath) {
        this.albumArtPath = (albumArtPath == null) ? "" : albumArtPath;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 17 + this.title.hashCode();
        hash = hash * 31 + this.titleToSort.hashCode();
        hash = hash * 13 + this.album.hashCode();
        hash = hash * 17 + this.albumArtist.hashCode();
        hash = hash * 31 + Arrays.hashCode(this.artists);
        hash = hash * 17 + this.composer.hashCode();
        hash = hash * 31 + this.genre.hashCode();
        hash = hash * 13 + (int)this.id;
        hash = hash * 17 + (int)this.duration;
        hash = hash * 31 + this.year;
        hash = hash * 13 + this.trackNumber;
        hash = hash * 31 + this.discNumber;
        hash = hash * 13 + this.albumArtPath.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !obj.getClass().getName().equals(this.getClass().getName())) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        Song other = (Song) obj;
        return  other.title != null && this.title.equals(other.title) &&
                other.titleToSort != null && this.titleToSort.equals(other.titleToSort) &&
                other.album != null && this.album.equals(other.album) &&
                other.albumArtist != null && this.albumArtist.equals(other.albumArtist) &&
                Arrays.equals(this.artists, other.artists) &&
                other.composer != null && this.composer.equals(other.composer) &&
                other.genre != null && this.genre.equals(other.genre) &&
                this.id == other.id &&
                this.duration == other.duration &&
                this.year == other.year &&
                this.trackNumber == other.trackNumber &&
                this.discNumber == other.discNumber &&
                other.albumArtPath != null && this.albumArtPath.equals(other.albumArtPath);
    }

    @Override
    public int compareTo(Song other) {
        return Integer.compare((this.discNumber * 1000 + this.trackNumber), (other.discNumber * 1000 + other.trackNumber));
        //return this.titleToSort.compareTo(other.titleToSort);
    }

    public int compareSongsByTitleAscending(Song other) {
        return (titleToSort != null && other != null && other.titleToSort != null) ? this.titleToSort.compareTo(other.titleToSort) : 0;
    }

    public int compareSongsByTitleDescending(Song other) {
        return (titleToSort != null && other != null && other.titleToSort != null) ? this.titleToSort.compareTo(other.titleToSort) * -1 : 0;
    }

    public int compareSongsByYearAscending(Song other) {
        return (other != null) ? Integer.compare(this.getYear(), other.getYear()) : 0;
    }

    public int compareSongsByYearDescending(Song other) {
        return (other != null) ? Integer.compare(this.getYear(), other.getYear()) * -1 : 0;
    }

    private String getArtistsString() {
        StringBuilder allArtists = new StringBuilder("\"Artists\": [");
        for (int i = 0; i < artists.length; i++) {
            allArtists.append("\"").append(artists[i]).append("\"");
            if (artists.length - i > 1) {
                allArtists.append(", ");
            }
        }
        return allArtists.append("], ").toString();
    }

    @Override
    public String toString() {
        //getArtistsString(artists);
        return "{ \"Title\": \"" + title + "\", " +
                "\"titleToSort\": \"" + titleToSort + "\", " +
                "\"Album\": \"" + album + "\", " +
                "\"albumArtist\": \"" + albumArtist + "\", " +
                getArtistsString() +
                "\"composer\": \"" + composer + "\", " +
                "\"genre\": \"" + genre + "\", " +
                "\"id\": \"" + id + "\", " +
                "\"duration\": \"" + duration + "\", " +
                "\"year\": \"" + year + "\", " +
                "\"trackNumber\": \"" + trackNumber + "\", " +
                "\"totalTrackNumber\" : \"" + totalTrackNumber + "\", " +
                "\"discNumber\": \"" + discNumber + "\", " +
                "\"albumArtPath\": \"" + albumArtPath + "\", " +
                "\"data\": \"" + data + "\" }";
    }
}