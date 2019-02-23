package com.example.eric.musicorganizer.activityPages;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.eric.musicorganizer.R;
import com.example.eric.musicorganizer.musicManager.MusicManager;
import com.example.eric.musicorganizer.mediaDataTypes.Song;
import com.example.eric.musicorganizer.mediaPlayer.MusicService;
import com.example.eric.musicorganizer.mediaPlayer.PlayerPage;
import com.example.eric.musicorganizer.settings.Settings;
import com.example.eric.musicorganizer.settings.SettingsPageActivity;

public class MainActivity extends AppCompatActivity {

    private MusicService musicService;
    private Intent playIntent;
    private boolean musicBound = false;
    private MenuItem currentSongMenuItem;
    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            musicService = binder.getService();                                                     //get service
            musicService.addSongQueueEmptyListener(() -> onSongQueueEmpty());
            musicService.addSongStartedListener((song) -> onSongStarted(song));
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService = null;
            musicBound = false;
        }
    };

    private void onSongQueueEmpty() {
        findViewById(R.id.current_song).setVisibility(View.GONE);
        if (currentSongMenuItem != null) {
            currentSongMenuItem.setVisible(false);
        }
        if (findViewById(R.id.current_song_menu_item) != null) {
            findViewById(R.id.current_song_menu_item).setVisibility(View.GONE);
        }
    }

    private void onSongStarted(Song song) {
        findViewById(R.id.current_song).setVisibility(View.VISIBLE);
        if (song != null && currentSongMenuItem != null) {
            currentSongMenuItem.setVisible(true);
        }
        if (findViewById(R.id.current_song_menu_item) != null) {
            findViewById(R.id.current_song_menu_item).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);                     // Load the settings of main_activity.xml
        //runtimePermissionsFromAndroid();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            MusicManager.getInstance().fillMusicDataFromStorage(getContentResolver());
        }
        Settings.getInstance().initSettingsFromMemory(this);
        findViewById(R.id.current_song).setVisibility(View.GONE);
        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);       // activates the search dialog when the user starts typing on the keyboard—the keystrokes are inserted into the search dialog.
    }

    /**
     * When the connection to the bound Service instance is made, we pass the song list.
     * We will also be able to interact with the Service instance in order to control playback later.
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    protected void onNewIntent(Intent intent) {
        Toast.makeText(this, "MainActivity::onNewIntent", Toast.LENGTH_SHORT).show();
        if (musicBound && musicService != null && musicService.isSongPlayingNow()) {
            startActivity(new Intent(getApplicationContext(), PlayerPage.class));
        }
    }

    /** respond to menu item selection */
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Set the settings of the search service
        getMenuInflater().inflate(R.menu.main, menu);
        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
        }
        // Set the visibility of the Player link on the menu
        currentSongMenuItem = menu.findItem(R.id.current_song_menu_item);
        if (currentSongMenuItem != null) {
            currentSongMenuItem.setVisible(musicService != null && musicService.getCurrentSong() != null);
        }
        return super.onCreateOptionsMenu(menu);
    }*/

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
            case R.id.settings_menu_item: switchPage(SettingsPageActivity.class);
                break;
            case R.id.current_song_menu_item:
                if (musicBound && musicService != null && musicService.isSongPlayingNow()) {
                    startActivity(new Intent(getApplicationContext(), PlayerPage.class));
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(musicConnection);
    }

    /** Switch between the pages by the selected button. */
    public void onOptionsPageSelected(View view) {
        switch (view.getId()) {
            case R.id.song_page_btn: switchPage(SongsPage.class);
                break;
            case R.id.albums_page_btn: switchPage(AllAlbumsPage.class);
                break;
            case R.id.artists_page_btn: switchPage(AllArtistsPage.class);
                break;
            /*case R.id.composers_page_btn: switchPage(ComposersPage.class);
                break;*/
            case R.id.genres_page_btn: switchPage(AllGenresPage.class);
                break;
            case R.id.settings_page_btn: switchPage(SettingsPageActivity.class);
                break;
            case R.id.current_song: switchPage(PlayerPage.class);
                break;
        }
    }

    // Switch to the selected page
    private void switchPage(Class className) {
        startActivity(new Intent(getApplicationContext(), className));
    }

    /*private void runtimePermissionsFromAndroid() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },1);
            }
        }
        /*if (!isExternalStorageReadable()) {}
        if (!isExternalStorageWritable()) {}
    }*/

    /*public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }*/

    /* Checks if external storage is available to at least read */
    /*public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }*/
}
