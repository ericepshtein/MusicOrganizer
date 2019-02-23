package com.example.eric.musicorganizer.mediaDataTypes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.eric.musicorganizer.R;
import com.example.eric.musicorganizer.mediaDataTypes.Album;
import com.example.eric.musicorganizer.musicManager.MusicManager;
import com.example.eric.musicorganizer.mediaDataTypes.Song;
import com.example.eric.musicorganizer.mediaPlayer.MusicService;

import java.util.List;

public class AlbumAdapter extends BaseAdapter {

    private Song currentSong;
    private List<Song> songs;
    private LayoutInflater albumInf;
    private MusicService musicService;

    public AlbumAdapter(Context context, Album album, MusicService musicService) {
        this.songs = album.getAllSongs();
        this.albumInf = LayoutInflater.from(context);
        this.musicService = musicService;
    }

    public AlbumAdapter(Context context, Album album, MusicService musicService, Song song) {
        this.songs = album.getAllSongs();
        this.albumInf = LayoutInflater.from(context);
        this.musicService = musicService;
        this.currentSong = currentSong;
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Song getItem(int position) {
        if (position < 0 || position >= songs.size()) {
            return null;
        }
        return songs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return songs.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout albumSongsLayout = (LinearLayout)albumInf.inflate(R.layout.album_songs_list, parent, false);

        TextView songNumberView = albumSongsLayout.findViewById(R.id.album_song_number);
        TextView songTitleView = albumSongsLayout.findViewById(R.id.album_song_title);
        TextView songAlbumArtistView = albumSongsLayout.findViewById(R.id.album_song_album_artist);
        TextView songDurationView = albumSongsLayout.findViewById(R.id.album_song_duration);

        Song current = getItem(position);
        if (current == null) {
            return null;
        }
        String songNumber = Integer.toString(current.getTrackNumber());
        if (musicService != null && musicService.getSource().equals(MusicService.Source.ALBUMS) && currentSong != null && currentSong.equals(current)) {
            songNumberView.setText("|||");
        }
        else {
            songNumberView.setText(current.getTrackNumber() > 0 ? songNumber : "");
        }
        songTitleView.setText(current.getTitle());
        songAlbumArtistView.setText(current.getArtistsString(current.getArtist()));                 //set position as tag
        songDurationView.setText(MusicManager.durationToString(current.getDuration()));
        albumSongsLayout.setTag(position);

        return albumSongsLayout;
    }
}
