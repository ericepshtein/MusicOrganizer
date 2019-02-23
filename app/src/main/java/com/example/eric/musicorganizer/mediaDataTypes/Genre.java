package com.example.eric.musicorganizer.mediaDataTypes;

import com.example.eric.musicorganizer.musicManager.MusicManager;

import java.util.ArrayList;
import java.util.List;

public class Genre implements Comparable<Genre> {

    private String name, nameToSort;
    private List<Song> songs;
    private List<Album> albums;
    private List<Artist> artists;

    public Genre(String name) {
        this.name = name;
        this.songs = new ArrayList<>();
        this.albums = new ArrayList<>();
        this.artists = new ArrayList<>();
        this.nameToSort = MusicManager.titleToSort(name);
    }

    public boolean addSong(Song song) {
        if (song != null && !songs.contains(song)) {
            return songs.add(song);
        }
        return false;
    }

    public boolean addAlbum(Album album) {
        if (album != null && !albums.contains(album)) {
            return albums.add(album);
        }
        return false;
    }

    public boolean addArtist(Artist artist) {
        if (artist != null && !artists.contains(artist)) {
            return this.artists.add(artist);
        }
        return false;
    }

    public boolean addArtists(List<Artist> artists) {
        if (artists == null || artists.isEmpty()) {
            return false;
        }
        boolean isAllArtistsAdded = false;

        for (Artist artist : artists) {
            isAllArtistsAdded |= addArtist(artist);
        }
        return isAllArtistsAdded;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSongExists(Song song) {
        return songs.contains(song);
    }

    public boolean isAlbumExists(Album album) {
        return albums.contains(album);
    }

    public boolean isArtistExists(Artist artist) {
        return artists.contains(artist);
    }

    public static String getGenresString(List<Genre> genres) {
        if (genres.size() > 2) {
            StringBuilder artists = new StringBuilder();
            for (int i = 0; i < genres.size(); i++) {
                if (i == genres.size() - 2) {
                    artists.append(genres.get(i).getName()).append(" & ").append(genres.get(i + 1).getName());
                    break;
                }
                else {
                    artists.append(genres.get(i).getName()).append(", ");
                }
            }
            return artists.toString();
        }
        else if (genres.size() == 2) {
            return genres.get(0).getName() + " & " + genres.get(1).getName();
        }
        else if (genres.size() == 1) {
            return genres.get(0).getName();
        }
        else {
            return "";
        }
    }
    @Override
    public int compareTo(Genre other) {
        return nameToSort.compareTo(other.nameToSort);
    }

    public List<Song> getAllSongs() {
        return songs;
    }
}
