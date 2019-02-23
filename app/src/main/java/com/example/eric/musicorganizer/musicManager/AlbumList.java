package com.example.eric.musicorganizer.musicManager;

import com.example.eric.musicorganizer.mediaDataTypes.Album;
import com.example.eric.musicorganizer.mediaDataTypes.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlbumList {

    private List<Album> albumList;

    public AlbumList() {
        albumList = new ArrayList<>();
    }

    public Album addSongToAlbum(Song song) {
        Album album = getAlbumByNameAndArtist(song.getAlbum(), song.getAlbumArtistString());
        if (album == null) {
            album = new Album(song.getAlbum(), song.getAlbumArtistString(), song.getYear());
        }
        else {
            albumList.remove(album);
        }
        album.addSong(song);
        albumList.add(album);
        return album;

        /*Album album = new Album(song.getId(), song.getAlbum(), song.getAlbumArtistString(), song.getYear());
        if (getAlbumByTitleAndArtist(album.getTitle(), album.getAlbumArtistString()) != null) {
            album = getAlbumByTitleAndArtist(album.getTitle(), album.getAlbumArtistString());
            albumList.remove(album);
        }
        if (!album.isSongExists(song)) {
            album.addSong(song);
        }
        return addAlbum(album);*/
    }

    public Album getAlbumByNameAndArtist(String albumName, String albumArtist) {
        for (Album album : albumList) {
            if (album.getTitle().equals(albumName) && album.getAlbumArtist().equals(albumArtist)) {
                return album;
            }
        }
        return null;
    }

    /*private Album getAlbumByTitleAndArtist(String title, Artist artist) {
        for (Album current : albumList) {
            if (current.getTitle().equals(title) && current.getAlbumArtistString().equals(artist)) {
                return current;
            }
        }
        return null;
    }*/

    /*private boolean addAlbum(Album album) {
        if (!albumList.contains(album))
            return albumList.add(album);
        return false;
    }*/

    public List<Album> getAlbumList() {
        return albumList;
    }

    public void sortAlbumList() {
        Set<Album> albumSet = new HashSet<>(albumList);
        albumList.clear();
        albumList.addAll(albumSet);
        Collections.sort(albumList);
    }

    public Song getAllNextSongsInAlbum(Song song) {
        for (Album currentAlbum : albumList) {
            if (currentAlbum.isSongExists(song)) {
                return currentAlbum.getNextSong(song);
            }
        }
        return null;
    }

    public Album getAlbumById(long id) {
        for (Album current : albumList) {
            if (current.hashCode() == id) {
                return current;
            }
        }
        return null;
    }

    public Album getAlbumBySong(Song song) {
        for (Album album : albumList) {
            if (album.getAllSongs().contains(song) && album.getTitle().equals(song.getAlbum())) {
                return album;
            }
        }
        return null;
    }

    public void clear() {
        albumList.clear();
    }

    public List<Album> getAllMatchedAlbums(String query) {
        if (query == null || albumList == null || query.isEmpty()) {
            return null;
        }
        List<Album> matches = new ArrayList<>();
        for (Album current : albumList) {
            if (current.getTitle().toLowerCase().contains(query.toLowerCase())  || current.getTitleToSort().contains(query.toLowerCase())) {
                matches.add(current);
            }
        }
        return matches;
    }
}
