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
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.eric.musicorganizer.R;
import com.example.eric.musicorganizer.mediaDataTypes.Genre;
import com.example.eric.musicorganizer.musicManager.MusicManager;
import com.example.eric.musicorganizer.mediaDataTypes.Song;
import com.example.eric.musicorganizer.mediaDataTypes.adapters.SongAdapter;
import com.example.eric.musicorganizer.mediaPlayer.MusicService;
import com.example.eric.musicorganizer.mediaPlayer.PlayerPage;
import com.example.eric.musicorganizer.settings.Settings;
import com.example.eric.musicorganizer.settings.SettingsPageActivity;

import java.util.Collections;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP;
import static com.example.eric.musicorganizer.settings.Settings.OrderOptions.ASCENDING;
import static com.example.eric.musicorganizer.settings.Settings.OrderOptions.DESCENDING;
import static com.example.eric.musicorganizer.settings.Settings.SortOptions.TITLE;
import static com.example.eric.musicorganizer.settings.Settings.SortOptions.YEAR;

public class SongsByGenrePage extends AppCompatActivity implements SearchView.OnQueryTextListener, ServiceConnection {

    private Genre genre;
    private List<Song> songList;
    private ListView songsListView;
    private SongAdapter songAdapter;
    private Settings.OrderOptions songsOrder;
    private Settings.SortOptions songsSort;

    // Service binding and search options:
    private MenuItem currentSongMenuItem;
    private boolean musicBound;
    private Intent playIntent;
    private MusicService musicService;
    private ServiceConnection musicConnection = this;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
        musicService = binder.getService();
        musicService.addSongQueueEmptyListener(this::onSongQueueEmpty);
        musicService.addSongStartedListener(this::onSongStarted);
        musicBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        if (musicService != null) {
            musicService = null;
        }
        musicBound = false;
    }

    public void onSongQueueEmpty() {
        if (currentSongMenuItem != null) {
            currentSongMenuItem.setVisible(false);
        }
    }

    public void onSongStarted(Song song) {
        if (song != null && currentSongMenuItem != null) {
            currentSongMenuItem.setVisible(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);                             // create a Service
        setContentView(R.layout.activity_all_songs_page);

        initDisplaySettings();
        initActivityDisplay();
    }

    private void initDisplaySettings() {
        songsOrder = Settings.getInstance().getGenresSongsOrder();
        songsSort = Settings.getInstance().getGenresSongsSort();
    }

    private void initActivityDisplay() {
        String genreName = getIntent().getStringExtra(getResources().getString(R.string.GenreNameKey));
        if (genreName == null) {
            onDestroy();
        }
        genre = MusicManager.getInstance().getGenreByName(genreName);
        if (genre == null) {
            onDestroy();
        }
        setTitle(genre.getName());
        songsListView = findViewById(R.id.song_page_list);
        getSongList();
        if (songList == null || songList.isEmpty()) {
            Toast.makeText(this, R.string.no_songs, Toast.LENGTH_SHORT).show();
            onDestroy();
        }
        songAdapter = new SongAdapter(this, songList);              // get view adapter
        songsListView.setAdapter(songAdapter);                              // it should present the list of songs on the device
        songsListView.setOnItemClickListener((parent, view, position, id) -> onSongSelected(position));
    }

    private void getSongList() {
        songList = genre.getAllSongs();
        orderAndSortSongsList();
    }

    private void orderAndSortSongsList() {
        if (songsSort == TITLE && songsOrder == ASCENDING) {
            Collections.sort(songList, Song::compareSongsByTitleAscending);
        }
        if (songsSort == TITLE && songsOrder == DESCENDING) {
            Collections.sort(songList, Song::compareSongsByTitleDescending);
        }
        if (songsSort == YEAR && songsOrder == ASCENDING) {
            Collections.sort(songList, Song::compareSongsByYearAscending);
        }
        if (songsSort == YEAR && songsOrder == DESCENDING) {
            Collections.sort(songList, Song::compareSongsByYearDescending);
        }
    }

    private void onSongSelected(int position) {
        /*
        Song item = songAdapter.getItem(position);
        Intent intent = new Intent(getBaseContext(), PlayerPage.class);
        intent.putExtra("SONG_ID", item.getId());
        intent.putExtra("CONTEXT", "ALL_GENRES");
        startActivity(intent);
        */
        musicService.playSelectedSong(songAdapter.getItem(position), genre, MusicService.Source.ARTISTS);
        startActivity(new Intent(getApplicationContext(), PlayerPage.class).setFlags(FLAG_ACTIVITY_PREVIOUS_IS_TOP));
    }

    /** respond to menu item selection */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView)searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        currentSongMenuItem = menu.findItem(R.id.current_song_menu_item);
        currentSongMenuItem.setVisible(musicService != null && musicService.isSongPlayingNow() && musicService.getCurrentSong() != null);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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

    public void songPicked(View view) {
        try {
            musicService.playSelectedSong(songAdapter.getItem(songsListView.getPositionForView(view)), genre, MusicService.Source.GENRES);
            startActivity(new Intent(getApplicationContext(), PlayerPage.class).setFlags(FLAG_ACTIVITY_PREVIOUS_IS_TOP));
        } catch (Exception e) {
            e.printStackTrace();
            if (musicService == null) {
                playIntent = new Intent(this, MusicService.class);
                bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            }
            initActivityDisplay();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

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
