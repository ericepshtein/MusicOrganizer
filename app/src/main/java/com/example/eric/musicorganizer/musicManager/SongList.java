package com.example.eric.musicorganizer.musicManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.eric.musicorganizer.R;
import com.example.eric.musicorganizer.mediaDataTypes.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SongList extends BaseAdapter {

    private List<Song> allSongs;

    public SongList() {
        allSongs = new ArrayList<>();
    }

    public void clear() {
        allSongs.clear();
    }

    public boolean addSong(Song song) {
        if (song == null) {
            return false;
        }
        if (!isSongExist(song))
            return allSongs.add(song);
        return false;
    }

    public boolean deleteSong(Song song) {
        if (song == null) {
            return false;
        }
        return allSongs.remove(song);
    }

    public boolean deleteSong(long id) {
        return deleteSong(getItemId((int)id));
    }

    public List<Song> getAllSongs() {
        return allSongs;
    }

    public boolean isSongExist(Song newSong) {
        if (newSong == null) {
            return false;
        }
        for (Song currentSong : allSongs) {
            try {
                if (currentSong.equals(newSong)) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public int getCount() {
        return allSongs.size();
    }

    @Override
    public Song getItem(int position) {
        return allSongs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return allSongs.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater songInf = LayoutInflater.from(parent.getContext());
        LinearLayout songLayout = (LinearLayout)songInf.inflate(R.layout.activity_all_songs_page, parent, false); //get title and artist views
        // get the TextView data
        TextView songView = (TextView)songLayout.findViewById(R.id.song_title);         // get songs title position
        TextView artistView = (TextView)songLayout.findViewById(R.id.song_artist);      // get songs artist position
        TextView durationView = (TextView)songLayout.findViewById(R.id.song_duration);  // get songs duration position
        // set the data of the songs
        Song currentSong = allSongs.get(position);                                  // get title and artist strings
        songView.setText(currentSong.getTitle());                                   // set songs title
        artistView.setText(currentSong.getAlbumArtistString());                                // set position as tag
        durationView.setText(Long.toString(currentSong.getDuration()));             // set songs duration
        songLayout.setTag(position);                                                // set songs position
        return songLayout;
    }

    public void sortSongList() {
        Collections.sort(allSongs);
    }

    public Song getSongById(long id) {
        for (Song current : allSongs) {
            if (current.getId() == id) {
                return current;
            }
        }
        return null;
    }

    public Song getAllNextSong(Song song) {
        if (song == null) {
            return null;
        }
        int currentSongPosition = allSongs.indexOf(song);
        if (currentSongPosition < 0 || (currentSongPosition + 1) > allSongs.size()) {
            return allSongs.get(currentSongPosition + 1);
        }
        else {
            return null;
        }
    }

    public List<Song> getAllMatchedSongs(String query) {
        if (query == null || allSongs == null || query.isEmpty()) {
            return null;
        }
        List<Song> matches = new ArrayList<>();
        for (Song current : allSongs) {
            if (current.getTitle().toLowerCase().contains(query.toLowerCase()) || current.getTitleToSort().contains(query.toLowerCase())) {
                matches.add(current);
            }
        }
        return matches;
    }
}
