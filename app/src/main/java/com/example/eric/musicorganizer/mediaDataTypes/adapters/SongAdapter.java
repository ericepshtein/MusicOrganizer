package com.example.eric.musicorganizer.mediaDataTypes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.eric.musicorganizer.R;
import com.example.eric.musicorganizer.musicManager.MusicManager;
import com.example.eric.musicorganizer.mediaDataTypes.Song;

import java.util.List;

public class SongAdapter extends BaseAdapter {

    private List<Song> songs;
    private LayoutInflater songInflater;

    public SongAdapter(Context context, List<Song> songs){
        this.songs = songs;
        songInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Song getItem(int position) {
        return songs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return songs.get(position).getId();
    }

    //map to all_songs_list layout
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout songLayout = (LinearLayout)songInflater.inflate(R.layout.all_songs_list, parent, false);   //get title and artist views

        TextView songView = songLayout.findViewById(R.id.song_title);                                        // get songs_title using position
        TextView artistView = songLayout.findViewById(R.id.song_artist);                                     // get song_artist using position
        TextView durationView = songLayout.findViewById(R.id.song_duration);                                 // get song_duration using position

        Song currentSong = songs.get(position);                                                                        // get current song by position
        songView.setText(currentSong.getTitle());
        artistView.setText(String.format("%s  -  %s", currentSong.getAlbum(), currentSong.getAlbumArtistString()));
        durationView.setText(MusicManager.durationToString(currentSong.getDuration()));
        songLayout.setTag(position);                                                                                    //set position as tag
        return songLayout;
    }
}
