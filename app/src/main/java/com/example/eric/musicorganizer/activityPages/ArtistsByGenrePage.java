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
import com.example.eric.musicorganizer.mediaDataTypes.Artist;
import com.example.eric.musicorganizer.mediaDataTypes.Genre;
import com.example.eric.musicorganizer.musicManager.MusicManager;
import com.example.eric.musicorganizer.mediaDataTypes.Song;
import com.example.eric.musicorganizer.mediaDataTypes.adapters.AllArtistsAdapter;
import com.example.eric.musicorganizer.mediaPlayer.MusicService;
import com.example.eric.musicorganizer.mediaPlayer.PlayerPage;
import com.example.eric.musicorganizer.settings.Settings;
import com.example.eric.musicorganizer.settings.SettingsPageActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArtistsByGenrePage extends AppCompatActivity implements SearchView.OnQueryTextListener, ServiceConnection {

    // Data types:
    private Genre genre;
    private AllArtistsAdapter allArtistAdapter;
    private List<Artist> artistsList;
    private boolean isAlbumArtistDisplay;

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artists_by_genre);
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

    private void initActivityDisplay() {
        String genreName = getIntent().getStringExtra(getResources().getString(R.string.GenreNameKey));
        if (genreName == null) {
            Toast.makeText(this, R.string.no_genres, Toast.LENGTH_LONG).show();
            this.finish();
        }
        this.genre = MusicManager.getInstance().getGenreByName(genreName);
        if (genre == null) {
            this.finish();
        }
        setTitle(genre.getName());
        if (genre.getAllSongs() == null || genre.getAllSongs().isEmpty()) {
            Toast.makeText(this, R.string.general_error, Toast.LENGTH_LONG).show();
            this.finish();
        }
        getArtistsList();
        if (artistsList == null || artistsList.isEmpty()) {
            Toast.makeText(this, R.string.no_albums, Toast.LENGTH_SHORT).show();
            this.finish();
        }
        isAlbumArtistDisplay = Settings.getInstance().isAlbumArtistDisplay();
        if (isAlbumArtistDisplay) {
            artistsList = setAlbumArtistDisplay();
        }
        allArtistAdapter = new AllArtistsAdapter(this, artistsList);
        ListView artistListView = findViewById(R.id.artists_page_list);
        artistListView.setAdapter(allArtistAdapter);
        artistListView.setOnItemClickListener((parent, view, position, id) -> onItemClick(position));
    }

    public void allSongsByGenreDisplay(View view) {
        Intent intent = new Intent(getBaseContext(), SongsByGenrePage.class);
        intent.putExtra(getResources().getString(R.string.GenreNameKey), genre.getName());
        startActivity(intent);
    }

    public void allAlbumsByGenreDisplay(View view) {
        Intent intent = new Intent(getBaseContext(), AlbumsByGenrePage.class);
        intent.putExtra(getResources().getString(R.string.GenreNameKey), genre.getName());
        startActivity(intent);
    }

    private void onItemClick(int position) {
        Intent intent = new Intent(getBaseContext(), ArtistPage.class);
        intent.putExtra(getResources().getString(R.string.ArtistNameKey), allArtistAdapter.getItem(position).getName());
        intent.putExtra(getResources().getString(R.string.GenreNameKey), genre.getName());
        startActivity(intent);
    }

    private void getArtistsList() {
        this.artistsList = genre.getArtists();
        Collections.sort(artistsList, Artist::compareTo);
    }

    private List<Artist> setAlbumArtistDisplay() {
        List<Artist> albumArtistsList = new ArrayList<>();
        for (Artist artist : artistsList) {
            if (artist.isAlbumArtist()) {
                albumArtistsList.add(artist);
            }
        }
        if (albumArtistsList.isEmpty()) {
            Toast.makeText(this, R.string.no_artists, Toast.LENGTH_SHORT).show();
            this.finish();
        }
        return albumArtistsList;
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

    @Override
    protected void onResume() {
        super.onResume();
        if (isAlbumArtistDisplay != Settings.getInstance().isAlbumArtistDisplay()) {
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
