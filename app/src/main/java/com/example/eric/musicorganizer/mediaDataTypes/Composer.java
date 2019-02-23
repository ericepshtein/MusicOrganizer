package com.example.eric.musicorganizer.mediaDataTypes;

import com.example.eric.musicorganizer.musicManager.MusicManager;

import java.util.ArrayList;
import java.util.List;

public class Composer implements Comparable<Composer> {

    private String name, nameToSort;
    private List<Song> songs;

    public Composer(String name) {
        this.name = name;
        this.songs = new ArrayList<>();
        setNameToSort(name);
    }

    public boolean isSongExists(Song song) {
        return songs.contains(song);
    }

    public boolean addSong(Song song) {
        return songs.add(song);
    }

    public String getName() {
        return name;
    }

    private void setNameToSort(String name) {
        this.nameToSort = MusicManager.titleToSort(name);
    }

    @Override
    public int compareTo(Composer other) {
        return this.nameToSort.compareTo(other.nameToSort);
    }

    public List<Song> getAllSongs() {
        return songs;
    }
}
