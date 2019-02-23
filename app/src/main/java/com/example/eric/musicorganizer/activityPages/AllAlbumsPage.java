package com.example.eric.musicorganizer.activityPages;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;
import com.example.eric.musicorganizer.R;
import com.example.eric.musicorganizer.mediaDataTypes.Album;
import com.example.eric.musicorganizer.musicManager.MusicManager;
import com.example.eric.musicorganizer.mediaDataTypes.Song;
import com.example.eric.musicorganizer.mediaDataTypes.adapters.AllAlbumsAdapter;
import com.example.eric.musicorganizer.mediaPlayer.MusicService;
import com.example.eric.musicorganizer.mediaPlayer.PlayerPage;
import com.example.eric.musicorganizer.settings.Settings;
import com.example.eric.musicorganizer.settings.Settings.OrderOptions;
import com.example.eric.musicorganizer.settings.Settings.SortOptions;
import com.example.eric.musicorganizer.settings.SettingsPageActivity;

import java.util.Collections;
import java.util.List;

public class AllAlbumsPage extends AppCompatActivity {

    private List<Album> albumsList;
    private AllAlbumsAdapter allAlbumsAdapter;
    private OrderOptions orderOption;
    private SortOptions sortOption;
    private MenuItem currentSongMenuItem;
    private boolean musicBound;
    private Intent playIntent;
    private MusicService musicService;
    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            musicService = binder.getService(); //get service
            musicService.addSongQueueEmptyListener(() -> onSongQueueEmpty());
            musicService.addSongStartedListener((song) -> onSongStarted(song));
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (musicService != null) {
                musicService = null;
            }
            musicBound = false;
        }
    };

    private void onSongQueueEmpty() {
        if (currentSongMenuItem != null) currentSongMenuItem.setVisible(false);
    }

    private void onSongStarted(Song song) {
        if (song != null && currentSongMenuItem != null) {
            currentSongMenuItem.setVisible(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);                                                 // create a Service
        setContentView(R.layout.activity_all_albums_page);                                  // load data from activity_all_songs_pagepage.xml
        initDisplaySettings();
        initActivityDisplay();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private void initDisplaySettings() {
        orderOption = Settings.getInstance().getAllAlbumsOrder();
        sortOption = Settings.getInstance().getAllAlbumsSort();
    }

    private void initActivityDisplay() {
        ListView albumsListView = findViewById(R.id.albums_page_list);
        getAllAlbumsList();                                                                 // get the all_songs_list list sorted
        if (albumsList == null || albumsList.isEmpty()) {
            Toast.makeText(this, R.string.no_albums, Toast.LENGTH_SHORT).show();
            this.finish();
        }
        allAlbumsAdapter = new AllAlbumsAdapter(this, albumsList);  // get view adapter
        albumsListView.setAdapter(allAlbumsAdapter);                                         // it should present the list of songs on the device
        albumsListView.setOnItemClickListener((parent, view, position, id) -> selectAlbumByPosition(position));
    }

    private void selectAlbumByPosition(int position) {
        Album item = allAlbumsAdapter.getItem(position);
        Intent intent = new Intent(getBaseContext(), AlbumPage.class);
        intent.putExtra("ALBUM_TITLE", item.getTitle());
        intent.putExtra("ALBUM_ARTIST", item.getAlbumArtist());
        startActivity(intent);
    }

    private void getAllAlbumsList() {
        MusicManager.getInstance().sortAlbumsList();
        albumsList = MusicManager.getInstance().getAlbumsList();
        sortAndOrderAlbumsList();
    }

    private void sortAndOrderAlbumsList() {
        int order = orderOption == Settings.OrderOptions.ASCENDING ? 1 : -1;
        switch (sortOption) {
            case TITLE: Collections.sort(albumsList, (album1, album2) -> album1.compareTo(album2) * order);
                break;
            case YEAR: Collections.sort(albumsList, (album1, album2) -> Integer.compare(album1.getYear(), album2.getYear()) * order);
                break;
        }
    }

    /** respond to menu item selection */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView)searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        currentSongMenuItem = menu.findItem(R.id.current_song_menu_item);
        currentSongMenuItem.setVisible(musicService != null && musicService.isSongPlayingNow() && musicService.getCurrentSong() != null);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.search_menu_item:
                Toast.makeText(this, "NOT IMPLEMENTED YET!!!", Toast.LENGTH_SHORT).show();
                break;*/
            case R.id.settings_menu_item:
                startActivity(new Intent(getApplicationContext(), SettingsPageActivity.class));
                break;
            case R.id.current_song_menu_item:
                if (musicBound && musicService != null && musicService.getSource() != null) {
                    startActivity(new Intent(getApplicationContext(), PlayerPage.class));
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (orderOption != Settings.getInstance().getAllSongsOrder() || sortOption != Settings.getInstance().getAllSongsSort()) {
            initDisplaySettings();
            initActivityDisplay();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (musicBound && musicService != null && !musicService.isSongPlayingNow() && musicConnection != null) {
            unbindService(musicConnection);
            musicConnection = null;
            musicService = null;
            musicBound = false;
        }
    }
}
