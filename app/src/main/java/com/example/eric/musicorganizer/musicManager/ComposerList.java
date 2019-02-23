package com.example.eric.musicorganizer.musicManager;

import com.example.eric.musicorganizer.mediaDataTypes.Composer;
import com.example.eric.musicorganizer.mediaDataTypes.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComposerList {

    private List<Composer> composerList;

    public ComposerList() {
        composerList = new ArrayList<>();
    }

    public boolean addSongToComposer(Song song) {
        Composer composer = new Composer(song.getComposer());
        if (getComposerByName(song.getComposer()) != null) {
            composer = getComposerByName(song.getComposer());
            composerList.remove(composer);
        }
        if (!composer.isSongExists(song)) {
            composer.addSong(song);
        }
        return addComposer(composer);
    }

    public Composer getComposerByName(String artist) {
        for (Composer current : composerList) {
            if (current.getName().equals(artist)) {
                return current;
            }
        }
        return null;
    }

    private boolean addComposer(Composer composer) {
        if (!composerList.contains(composer))
            return composerList.add(composer);
        return false;
    }

    public List<Composer> getComposersList() {
        return composerList;
    }

    public void sortComposerList() {
        Collections.sort(composerList);
    }

    public void clear() {
        composerList.clear();
    }
}
