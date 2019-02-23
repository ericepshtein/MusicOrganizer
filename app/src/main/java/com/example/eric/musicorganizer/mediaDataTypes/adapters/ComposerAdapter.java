package com.example.eric.musicorganizer.mediaDataTypes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.eric.musicorganizer.R;
import com.example.eric.musicorganizer.mediaDataTypes.Composer;

import java.util.List;

public class ComposerAdapter extends BaseAdapter {

    private List<Composer> composers;
    private LayoutInflater artistInf;

    public ComposerAdapter(Context context, List<Composer> artists) {
        this.composers = artists;
        artistInf = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return composers.size();
    }

    @Override
    public Composer getItem(int position) {
        return composers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout composerLayout = (LinearLayout)artistInf.inflate(R.layout.all_composers_list, parent, false); //get title and artist views
        TextView composerView = (TextView)composerLayout.findViewById(R.id.composers_name);                         //get all_songs_list using position

        Composer currentComposer = composers.get(position);     //get title and artist strings
        composerView.setText(currentComposer.getName());        //set position as tag
        composerLayout.setTag(position);
        return composerLayout;
    }
}
