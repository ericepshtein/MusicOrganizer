package com.example.eric.musicorganizer.musicManager;

import com.example.eric.musicorganizer.mediaDataTypes.Album;
import com.example.eric.musicorganizer.mediaDataTypes.Artist;
import com.example.eric.musicorganizer.mediaDataTypes.Genre;
import com.example.eric.musicorganizer.mediaDataTypes.Song;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GenreList {

    private List<Genre> genresList;

    public GenreList() {
        this.genresList = new ArrayList<>();
    }

    public boolean addSongToGenre(String genres, Song song, Album album, List<Artist> artists) {
        if (genres != null && song != null && song.getGenre() != null) {
            Genre genre = getGenreByName(genres);
            if (genre == null) {
                genre = new Genre(song.getGenre());
            }
            else {
                genresList.remove(genre);
            }
            genre.addSong(song);
            genre.addAlbum(album);
            genre.addArtists(artists);
            album.addGenre(genre);
            return addGenre(genre);
        }
        return false;
    }

    public boolean addSongToGenres(String[] genresToAdd, Song song, Album album, List<Artist> artists) {
        if (song.getAlbumArtists()[0].equals("Rush")) {
            System.out.println("Rush");
        }
        if (genresToAdd != null && song.getGenre() != null) {
            List<Genre> genres = getGenresByNames(Arrays.asList(genresToAdd));
            if (genres == null || genres.isEmpty() || genres.size() < genresToAdd.length) {
                for (String current : genresToAdd) {
                    if (getGenreByName(current) == null) {
                        genres.add(new Genre(current));
                    }
                }
            }
            else {
                genresList.removeAll(genres);
            }
            if (genres == null) {
                return false;
            }
            boolean status = false;
            for (Genre genre : genres) {
                genre.addSong(song);
                genre.addAlbum(album);
                genre.addArtists(artists);
                if (!genresList.contains(genre)) {
                    status |= addGenre(genre);
                }
            }
            album.addGenres(genres);
            return status;
        }
        return false;
    }

    private List<Genre> getGenresByNames(List<String> genresToFind) {
        if (genresToFind == null || genresToFind.isEmpty()) {
            return null;
        }
        List<Genre> fendedGenres = new ArrayList<>();
        for (Genre current : genresList) {
            for (String genre : genresToFind) {
                if (genre.equalsIgnoreCase(current.getName())) {
                    fendedGenres.add(current);
                }
            }
        }
        return fendedGenres;
    }

    public Genre getGenreByName(String genreName) {
        if (genreName == null || genreName.isEmpty()) {
            return null;
        }
        for (Genre current : genresList) {
            if (current.getName().equals(genreName)) {
                return current;
            }
        }
        return null;
    }

    private boolean addGenre(Genre genre) {
        if (genre != null) {
            return genresList.add(genre);
        }
        return false;
    }

    public List<Genre> getGenresList() {
        return genresList;
    }

    public void sortGenresList() {
        Collections.sort(genresList);
    }

    public void clear() {
        genresList.clear();
    }

    public List<Genre> getAllMatchedGenres(String query) {
        List<Genre> genres = new ArrayList<>();
        for (Genre genre : genresList) {
            if (genre.getName().toLowerCase().contains(query.toLowerCase())) {
                genres.add(genre);
            }
        }
        return genres;
    }
}
