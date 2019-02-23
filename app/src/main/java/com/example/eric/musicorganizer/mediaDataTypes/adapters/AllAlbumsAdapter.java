package com.example.eric.musicorganizer.mediaDataTypes.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.eric.musicorganizer.R;
import com.example.eric.musicorganizer.mediaDataTypes.Album;

import java.util.List;

public class AllAlbumsAdapter extends BaseAdapter {

    private List<Album> albums;
    private LayoutInflater albumInfo;
    private Context context;

    public AllAlbumsAdapter(Context context, List<Album> albums){
        this.albums = albums;
        this.context = context;
        this.albumInfo = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return albums.size();
    }

    @Override
    public Album getItem(int position) {
        return albums.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    //map to all_songs_list layout
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout albumLayout = (LinearLayout)albumInfo.inflate(R.layout.all_albums_list, parent, false);
        TextView songView = albumLayout.findViewById(R.id.album_title);
        TextView artistView = albumLayout.findViewById(R.id.album_artist);
        TextView yearView = albumLayout.findViewById(R.id.album_year);

        Album currentAlbum = albums.get(position);                      // get the current album
        songView.setText(currentAlbum.getTitle());                      // set the app_logo title view
        artistView.setText(currentAlbum.getAlbumArtist());              // set the app_logo artist view
        yearView.setText(getYearString(currentAlbum.getYear()));        // ste the app_logo year view
        setAlbumArt(currentAlbum, albumLayout);
        return albumLayout;
    }

    private CharSequence getYearString(int year) {
        if (year != 0) {
            return Integer.toString(year);
        }
        return "";
    }

    private void setAlbumArt(Album album, LinearLayout albumLayout) {
        ImageView albumArtView = (ImageView)albumLayout.findViewById(R.id.album_art);
        Drawable albumArt = Drawable.createFromPath(album.getSong(0).getAlbumArtPath());
        if (albumArt != null && albumArt.getMinimumWidth() > 0 && albumArt.getMinimumHeight() > 0) {
            albumArtView.setImageDrawable(albumArt);
        }
    }
}
