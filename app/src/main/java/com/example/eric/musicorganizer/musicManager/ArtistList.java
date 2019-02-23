package com.example.eric.musicorganizer.musicManager;

import com.example.eric.musicorganizer.mediaDataTypes.Album;
import com.example.eric.musicorganizer.mediaDataTypes.Artist;
import com.example.eric.musicorganizer.mediaDataTypes.Song;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ArtistList {

    private List<Artist> artistList;

    public ArtistList() {
        this.artistList = new ArrayList<>();
    }

    public Artist getArtistByName(String name) {
        for (Artist current : artistList) {
            if (current.getName().equals(name)) {
                return current;
            }
        }
        Artist artist = new Artist(name, false);
        artistList.add(artist);
        return artist;
    }

    public void addArtist(String artistName, Album album, Song song) {
        if (artistName == null || song == null) {
            return;
        }
        boolean isAlbumArtist = song.getAlbumArtistString().equals(artistName);
        Artist artist = getArtistByName(artistName);
        if (artist == null) {
            List<String> allArtists = Arrays.asList(song.getArtist());
            artist = new Artist(artistName, allArtists.contains(song.getAlbumArtistString()));
        }
        else {
            artistList.remove(artist);
        }
        artist.addSong(song);
        artist.addAlbum(album);
        artist.setAlbumArtist(isAlbumArtist);
        artistList.add(artist);
    }

    private Artist getArtist(String name) {
        for (Artist artist : artistList) {
            if (artist.getName().equals(name)) {
                return artist;
            }
        }
        return null;
    }

    public boolean addArtist(Artist artist) {
        if (!artistList.contains(artist))
            return artistList.add(artist);
        return false;
    }

    public List<Artist> getArtistList() {
        sortArtists();
        return artistList;
    }

    private void sortArtists() {
        for (int i = 0; i < artistList.size(); i++) {
            for (int j = i; j < artistList.size(); j++) {
                if (artistList.get(i).compareTo(artistList.get(j)) > 0) {
                    Artist artist = artistList.get(i);
                    artistList.set(i, artistList.get(j));
                    artistList.set(j, artist);
                }
            }
        }
    }

    public Song getNextSong(Song song) {
        for (Artist current : artistList) {
            if (current.getName().equals(song.getArtist())) {
                return current.getNextSong(song);
            }
        }
        return null;
    }

    public Artist getArtistById(int id) {
        for (Artist current : artistList) {
            if (current.hashCode() == id) {
                return current;
            }
        }
        return null;
    }

    public void addSongToArtists(String artistName, Song song) {
        this.addSongToArtists(artistName, null, song);
    }

    public Artist addSongToArtists(String artistName, Album album, Song song) {
        boolean isAlbumArtist = song != null && song.getAlbumArtists() != null && artistName != null && Arrays.asList(song.getAlbumArtists()).contains(artistName);
        Artist artist = getArtistByName(artistName);
        if (artist == null) {
            artist = new Artist(artistName, Arrays.asList(song.getArtist()).contains(song.getAlbumArtistString()));
        }
        else {
            isAlbumArtist = isAlbumArtist || artist.isAlbumArtist();
            artistList.remove(artist);
        }
        artist.addSong(song);
        artist.setAlbumArtist(isAlbumArtist);
        if (album != null) {
            artist.addAlbum(album);
        }
        artistList.add(artist);
        return artist;
    }

    public void clear() {
        artistList.clear();
    }

    public void sort() {
        Collections.sort(artistList);
    }

    public List<Artist> getAllMatchedArtists(String query) {
        if (query == null || artistList == null || query.isEmpty()) {
            return null;
        }
        List<Artist> matches = new ArrayList<>();
        for (Artist current : artistList) {
            if (current.getName().toLowerCase().contains(query.toLowerCase())  || current.getNameToSort().contains(query.toLowerCase())) {
                matches.add(current);
            }
        }
        return matches;
    }
}
