package com.example.eric.musicorganizer.mediaDataTypes;

import com.example.eric.musicorganizer.musicManager.MusicManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Artist implements Comparable<Artist> {

    private String name, nameToSort;
    private List<Song> songs;
    private List<Album> albums;
    private boolean isAlbumArtist;

    public Artist(String name, boolean isAlbumArtist) {
        this.name = name;
        this.isAlbumArtist = isAlbumArtist;
        this.songs = new ArrayList<>();
        this.albums = new ArrayList<>();
        setNameToSort(name);
    }

    public void setName(String name) {
        this.name = (name != null) ? name : "";
    }

    public boolean isSongExists(Song song) {
        return songs.contains(song);
    }

    public boolean addSong(Song song) {
        if (song == null || songs.contains(song)) {
            return false;
        }
        return songs.add(song);
    }

    public void setAlbumArtist(boolean albumArtist) {
        isAlbumArtist = albumArtist;
    }

    private void setNameToSort(String name) {
        this.nameToSort = (name != null) ? MusicManager.titleToSort(name) : "";
    }

    public boolean isAlbumArtist() {
        return isAlbumArtist;
    }

    public List<Album> getAllAlbums() {
        Collections.sort(albums);
        return albums;
    }

    public List<Song> getAllSongs() {
        Set<Song> songsSet = new HashSet<>(songs);
        songs.clear();
        songs.addAll(songsSet);
        Collections.sort(songs);
        return songs;
    }

    public String getName() {
        return name;
    }

    public String getNameToSort() {
        return nameToSort;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 17 + (name != null ? name.hashCode() : 1);
        hash = hash * 31 + (songs != null ? songs.hashCode() : 1);
        hash = hash * 13 + (albums != null ? albums.hashCode(): 1);
        return hash;
    }

    @Override
    public int compareTo(Artist other) {
        return this.nameToSort.compareTo(other.nameToSort);
    }

    public Song getNextSong(Song song) {
        if (song == null) {
            return null;
        }
        int currentIndex = songs.indexOf(song);
        if (currentIndex < 0 || currentIndex + 1 > songs.size()) {
            return null;
        }
        return songs.get(currentIndex + 1);
    }

    public void addAlbum(Album album) {
        if (album != null && !albums.contains(album)) {
            albums.add(album);
        }
    }
}