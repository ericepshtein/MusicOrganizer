package com.example.eric.musicorganizer.mediaDataTypes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.eric.musicorganizer.R;
import com.example.eric.musicorganizer.mediaDataTypes.Genre;

import java.util.List;

public class GenreAdapter extends BaseAdapter {

    private List<Genre> genres;
    private LayoutInflater genreInf;

    public GenreAdapter(Context context, List<Genre> artists) {
        this.genres = artists;
        genreInf = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return genres.size();
    }

    @Override
    public Genre getItem(int position) {
        return genres.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout genreLayout = (LinearLayout)genreInf.inflate(R.layout.all_genres_list, parent, false); //get title and artist views
        TextView artistView = (TextView)genreLayout.findViewById(R.id.genre_name);                         //get all_songs_list using position

        Genre currentGenre = genres.get(position);  //get title and artist strings
        artistView.setText(currentGenre.getName());   //set position as tag
        genreLayout.setTag(position);
        return genreLayout;
    }
}
