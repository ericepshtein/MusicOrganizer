package com.example.eric.musicorganizer.mediaDataTypes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.eric.musicorganizer.R;
import com.example.eric.musicorganizer.mediaDataTypes.Artist;

import java.util.List;

public class AllArtistsAdapter extends BaseAdapter {

    private List<Artist> artists;
    private LayoutInflater artistInf;

    public AllArtistsAdapter(Context context, List<Artist> artists) {
        this.artists = artists;
        artistInf = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return artists.size();
    }

    @Override
    public Artist getItem(int position) {
        return artists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout songLayout = (LinearLayout) artistInf.inflate(R.layout.all_artists_list, parent, false); //get title and artist views
        TextView artistView = songLayout.findViewById(R.id.artist_name);                         //get all_songs_list using position

        Artist currArtist = artists.get(position);  //get title and artist strings
        artistView.setText(currArtist.getName());   //set position as tag
        songLayout.setTag(position);
        return songLayout;
    }
}
