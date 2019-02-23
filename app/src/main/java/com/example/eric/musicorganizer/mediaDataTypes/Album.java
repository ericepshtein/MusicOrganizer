package com.example.eric.musicorganizer.mediaDataTypes;

import com.example.eric.musicorganizer.musicManager.MusicManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Album implements Comparable<Album> {

    private final int MINUTE = 1000 * 60;

    private String title, titleToSort;
    private String albumArtist;
    private List<String> artists;
    private List<Song> songs;
    private List<Genre> genres;
    private int year;
    private long duration;

    public Album(String title, String albumArtist, int year) {
        setTitle(title);
        setAlbumArtist(albumArtist);
        setYear(year);
        this.artists = new ArrayList<>();
        this.songs = new ArrayList<>();
        this.genres = new ArrayList<>();
        this.duration = 0;
        setTitleToSort(this.title);
    }

    public void setTitle(String title) {
        this.title = (title != null) ? title.trim() : "";
    }

    private void setAlbumArtist(String albumArtist) {
        this.albumArtist = (albumArtist != null) ? albumArtist.trim() : "";
    }

    public void setYear(int year) {
        this.year = (year > 0) ? year : 0;
    }

    public String getAlbumArtist() {
        return albumArtist;
    }

    private void sortArtistsList() {
        HashSet<String> hashArtists = new HashSet<>(artists);
        artists.clear();
        artists.addAll(hashArtists);
    }

    public List<String> getArtists() {
        sortArtistsList();
        return artists;
    }

    public long getDurationInMinutes() {
        return duration / (MINUTE) + (duration % (MINUTE) == 0 ? 0 : 1);
    }

    public String getTitle() {
        return title;
    }

    public String getTitleToSort() {
        return titleToSort;
    }

    public List<Song> getAllSongs() {
        Set<Song> songsSet = new HashSet<>(songs);
        songs.clear();
        songs.addAll(songsSet);
        Collections.sort(songs, Song::compareTo);
        return songs;
    }

    public boolean addGenre(Genre genre) {
        if (genre != null && !genres.contains(genre)) {
            return genres.add(genre);
        }
        return false;
    }

    public boolean addGenres(List<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return false;
        }
        boolean status = false;
        for (Genre current : genres) {
            if (!this.genres.contains(current)) {
                status |= this.genres.add(current);
            }
        }
        return status;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public Song getSong(int position) {
        return songs.get(position);
    }

    public int getYear() {
        return year;
    }

    private void setTitleToSort(String title) {
        this.titleToSort = (title != null) ? MusicManager.titleToSort(title) : "";
    }

    /**
     * This method add a song to the app_logo songs list, the duration to the app_logo total duration
     * and the artist to the app_logo artists list.
     * @param song that shouldn't be null.
     * @return true if and only if the song and the artist added successfully to the album.
     */
    public boolean addSong(Song song) {
        if (song == null || songs.contains(song)) {
            return false;
        }
        addDuration(song.getDuration());
        for (String artist : song.getArtist()) {
            addArtist(artist);
        }
        return songs.add(song);
    }

    /**
     * This song gets an artist and adds it to the artist list (if it not exists already)
     * @param artist
     * @return true if the artist is already exists or if the artist added successfully.
     */
    boolean addArtist(String artist) {
        return !artists.contains(artist) && artists.add(artist);
    }

    public boolean removeSong(Song song) {
        if (song == null || !songs.contains(song)) {
            return false;
        }
        decDuration(song.getDuration());
        for (String artist : song.getArtist()) {
            removeArtist(MusicManager.getInstance().getArtistByName(artist));
        }
        return songs.remove(song);
    }

    public boolean removeArtist(Artist artist) {
        return artist != null && artists.contains(artist) && artists.remove(artist);
    }

    private void addDuration(long duration) {
        this.duration += (duration > 0) ? duration : 0;
    }

    private void decDuration(long duration) {
        this.duration -= (duration > 0) ? duration : 0;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 17 + (title != null ? title.length() : 1);
        hash = hash * 31 + (albumArtist != null ? albumArtist.hashCode() : 1);
        if (artists != null) {
            for (String artist : artists) {
                hash = hash * 13 + artist.hashCode();
            }
        }
        if (songs != null) {
            for (Song song : songs) {
                hash = hash * 17 + song.hashCode();
            }
        }
        hash = hash * 31 + year;
        return hash;
    }

    @Override
    public int compareTo(Album other) {
        return this.titleToSort.compareTo(other.titleToSort) /* * asc*/;
    }

    public int compareByTitleAscending(Album other) {
        return (titleToSort != null && other != null && other.titleToSort != null) ? this.titleToSort.compareTo(other.titleToSort) : 0;
    }

    public int compareByTitleDescending(Album other) {
        return (titleToSort != null && other != null && other.titleToSort != null) ? this.titleToSort.compareTo(other.titleToSort) * -1 : 0;
    }

    public int compareByYearAscending(Album other) {
        return (other != null) ? Integer.compare(year, other.year) : 0;
    }

    public int compareByYearDescending(Album other) {
        return (other != null) ? Integer.compare(year, other.year) * -1 : 0;
    }

    public boolean isSongExists(Song song) {
        return (song != null) && songs.contains(song);
    }

    public boolean isArtistExists(Artist artist) {
        return artist != null && artists.contains(artist);
    }

    public Song getNextSong(Song song) {
        if (song == null || !isSongExists(song)) {
            return null;
        }
        int currentSongIndex = songs.indexOf(song);
        if (currentSongIndex < 0 || (currentSongIndex + 1) > songs.size()) {
            return null;
        }
        return songs.get(currentSongIndex + 1);
    }

    public boolean containsGenre(Genre genre) {
        if (genres.isEmpty()) {
            for (Song current : songs) {
                if (current.getGenre().equals(genre.getName())) {
                    return true;
                }
            }
            return false;
        }
        return genres.contains(genre);
    }
}
