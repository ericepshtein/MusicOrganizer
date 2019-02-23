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
import com.example.eric.musicorganizer.mediaDataTypes.Album;
import com.example.eric.musicorganizer.mediaDataTypes.Artist;
import com.example.eric.musicorganizer.mediaDataTypes.Genre;
import com.example.eric.musicorganizer.musicManager.MusicManager;
import com.example.eric.musicorganizer.mediaDataTypes.Song;
import com.example.eric.musicorganizer.mediaDataTypes.adapters.AllAlbumsAdapter;
import com.example.eric.musicorganizer.mediaPlayer.MusicService;
import com.example.eric.musicorganizer.mediaPlayer.PlayerPage;
import com.example.eric.musicorganizer.settings.Settings;
import com.example.eric.musicorganizer.settings.SettingsPageActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.eric.musicorganizer.settings.Settings.OrderOptions.ASCENDING;
import static com.example.eric.musicorganizer.settings.Settings.OrderOptions.DESCENDING;
import static com.example.eric.musicorganizer.settings.Settings.SortOptions.TITLE;
import static com.example.eric.musicorganizer.settings.Settings.SortOptions.YEAR;

public class ArtistPage extends AppCompatActivity {

    private Artist artist;
    private Genre genre;
    private List<Album> albums;
    private AllAlbumsAdapter albumAdapter;
    private Settings.OrderOptions albumsOrder;
    private Settings.SortOptions albumsSort;
    private MenuItem currentSongMenuItem;
    private boolean isGenreContext;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_page);
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
        albumsOrder = Settings.getInstance().getArtistAlbumsOrder();
        albumsSort = Settings.getInstance().getArtistAlbumsSort();
    }

    private void initActivityDisplay() {
        String artistName = getIntent().getStringExtra(getResources().getString(R.string.ArtistNameKey));
        if (artistName == null) {
            Toast.makeText(this, R.string.no_albums, Toast.LENGTH_LONG).show();
            this.finish();
        }
        artist = MusicManager.getInstance().getArtistByName(artistName);
        if (artist == null) {
            this.finish();
        }
        setTitle(artist.getName());
        isGenreContext();
        if (artist.getAllSongs() == null || artist.getAllSongs().isEmpty()) {
            Toast.makeText(this, R.string.general_error, Toast.LENGTH_LONG).show();
            this.finish();
        }
        albums = getAlbums();
        if (albums == null || albums.isEmpty()) {
            Toast.makeText(this, R.string.no_albums, Toast.LENGTH_SHORT).show();
            this.finish();
        }
        sortAndOrderAlbumsList();
        ListView albumsListView = findViewById(R.id.albums_by_artist_list);
        albumAdapter = new AllAlbumsAdapter(this, albums);
        albumsListView.setAdapter(albumAdapter);
        albumsListView.setOnItemClickListener((parent, view, position, id) -> onAlbumSelected(position));
    }
    
    private boolean isGenreContext() {
        if (genre != null) {
            return isGenreContext = true;
        }
        String genreName = getIntent().getStringExtra(getResources().getString(R.string.GenreNameKey));
        if (genreName != null && !genreName.isEmpty()) {
            if (genre == null) {
                genre = MusicManager.getInstance().getGenreByName(genreName);
            }
            isGenreContext = genre != null;
        }
        return isGenreContext;
    }

    private List<Album> getAlbums() {
        if (isGenreContext()) {
            List<Album> albums = new ArrayList<>(artist.getAllAlbums());
            List<Album> albumsToRemove = new ArrayList<>();
            if (albums.isEmpty()) {
                return null;
            }
            try {
                for (Album album : albums) {
                    if (!album.containsGenre(genre)) {
                        albumsToRemove.add(album);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            albums.removeAll(albumsToRemove);
            return albums;
        }
        else {
            return artist.getAllAlbums();
        }
    }

    private void onAlbumSelected(int position) {
        Album item = albumAdapter.getItem(position);
        Intent intent = new Intent(getBaseContext(), AlbumPage.class);
        intent.putExtra("ALBUM_TITLE", item.getTitle());
        intent.putExtra("ALBUM_ARTIST", item.getAlbumArtist());
        startActivity(intent);
    }

    private void sortAndOrderAlbumsList() {
        if (albumsSort == TITLE && albumsOrder == ASCENDING) {
            Collections.sort(albums, Album::compareByTitleAscending);
        }
        if (albumsSort == TITLE && albumsOrder == DESCENDING) {
            Collections.sort(albums, Album::compareByTitleDescending);
        }
        if (albumsSort == YEAR && albumsOrder == ASCENDING) {
            Collections.sort(albums, Album::compareByYearAscending);
        }
        if (albumsSort == YEAR && albumsOrder == DESCENDING) {
            Collections.sort(albums, Album::compareByYearDescending);
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
            case R.id.settings_menu_item: startActivity(new Intent(getApplicationContext(), SettingsPageActivity.class));
                break;
            case R.id.current_song_menu_item:
                if (musicBound && musicService != null && musicService.getSource() != null) {
                    startActivity(new Intent(getApplicationContext(), PlayerPage.class));
                }
        }
        return super.onOptionsItemSelected(item);
    }

    public void allSongsByArtistDisplay(View view) {
        Intent intent = new Intent(getBaseContext(), SongsByArtistPage.class);
        intent.putExtra("ARTIST_NAME", artist.getName());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (albumsOrder != Settings.getInstance().getArtistAlbumsOrder() || albumsSort != Settings.getInstance().getArtistAlbumsSort()) {
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
