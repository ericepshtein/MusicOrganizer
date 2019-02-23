package com.example.eric.musicorganizer.activityPages;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eric.musicorganizer.R;
import com.example.eric.musicorganizer.mediaDataTypes.Album;
import com.example.eric.musicorganizer.mediaDataTypes.Genre;
import com.example.eric.musicorganizer.musicManager.MusicManager;
import com.example.eric.musicorganizer.mediaDataTypes.Song;
import com.example.eric.musicorganizer.mediaDataTypes.adapters.AlbumAdapter;
import com.example.eric.musicorganizer.mediaPlayer.MusicService;
import com.example.eric.musicorganizer.mediaPlayer.PlayerPage;
import com.example.eric.musicorganizer.settings.SettingsPageActivity;

import java.util.Collections;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP;

/***************************************** Album Page: **********************************************
 *                                                                                                  *
 * This activity displays and controls the UI of the album page, include:                           *
 * - Display the app_logo artwork                                                                   *
 * - Display the app_logo artist/s, include redirection to each of one the different artist.        *
 * - Display the app_logo title.                                                                    *
 * - Display the amount of the songs in the album and the length of the album in minutes.           *
 * - Display the app_logo genre/s, include redirection to each of one the different genres.         *
 *                                                                                                  *
 * This activity also displays all the songs in the album with their:                               *
 * - Song's track number.                                                                           *
 * - Song's name.                                                                                   *
 * - Song's album artist.                                                                           *
 * - Song's length in minutes and seconds.                                                          *
 *                                                                                                  *
 * This activity also includes the ability of:                                                      *
 * - Search songs, album, artists and genres in the library.                                        *
 * - Redirect to the settings page.                                                                 *
 * - Redirect to the player if there is any song playing currently.                                 *
 * - Return to the previous activity.                                                               *
 *                                                                                                  *
 * @author eric.epshtein                                                                            *
 ***************************************************************************************************/
public class AlbumPage extends AppCompatActivity implements SearchView.OnQueryTextListener, ServiceConnection {

    private MusicService musicService;
    private AlbumAdapter albumAdapter;
    private ListView songsListView;
    private Album album;
    private List<Song> songsList;
    private boolean musicBound;
    private MenuItem currentSongMenuItem;
    private Intent playIntent;
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
        if (currentSongMenuItem != null) {
            currentSongMenuItem.setVisible(false);
        }
    }

    private void onSongStarted(Song song) {
        if (song != null && currentSongMenuItem != null) {
            currentSongMenuItem.setVisible(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);                             // create a Service
        setContentView(R.layout.activity_album_page);                   // load data from activity_all_songs_pagepage.xml
        initActivityDisplay();
    }

    private void initActivityDisplay() {
        String albumTitle = getIntent().getStringExtra(getResources().getString(R.string.AlbumTitleKey));
        String albumArtist = getIntent().getStringExtra(getResources().getString(R.string.AlbumArtistKey));
        if (albumArtist == null || albumTitle == null) {
            Toast.makeText(this, R.string.no_albums, Toast.LENGTH_SHORT).show();
            this.finish();
        }

        this.album = MusicManager.getInstance().getAlbumByNameAndArtist(albumTitle, albumArtist);
        if (album == null) {
            Toast.makeText(this, R.string.no_albums, Toast.LENGTH_SHORT).show();
            this.finish();
        }

        songsListView = findViewById(R.id.songs_in_album_list);
        getSongList();                                                  // get the all_songs_list list sorted
        if (songsList.isEmpty()) {
            Toast.makeText(this, R.string.no_albums, Toast.LENGTH_SHORT).show();
            this.finish();
        }

        setAlbumView();
        albumAdapter = new AlbumAdapter(this, album, musicService);
        songsListView.setAdapter(albumAdapter);                              // it should present the list of songs on the device
        setTitle(album.getTitle());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private void setAlbumView() {
        TextView albumArtistView = findViewById(R.id.album_artist_title);
        TextView albumTitleView = findViewById(R.id.album_page_title);
        TextView yearView = findViewById(R.id.album_year_title);
        TextView durationView = findViewById(R.id.album_song_counter_and_length_title);
        TextView genres = findViewById(R.id.album_genres_list);

        albumArtistView.setText(album.getAlbumArtist());
        albumTitleView.setText(album.getTitle());
        yearView.setText(getReleasedFormat(album.getYear()));
        durationView.setText(getTrackNumberAndDurationFormat(album.getAllSongs().size(), album.getDurationInMinutes()));
        genres.setText(Genre.getGenresString(album.getGenres()));

        setAlbumArt();
    }

    private String getReleasedFormat(int year) {
        return "Released " + year;
    }

    private String getTrackNumberAndDurationFormat(int numOfSongs, long duration) {
        return numOfSongs + (numOfSongs == 1 ? " Song, " : " Songs, ") + duration + (duration == 1 ? " Min." : " Mins.");
    }

    private void setAlbumArt() {
        ImageView albumArtView = findViewById(R.id.album_art);
        try {
            Drawable albumArt = Drawable.createFromPath(album.getSong(0).getAlbumArtPath());
            if (albumArt == null || albumArt.getMinimumWidth() <= 0 || albumArt.getMinimumHeight() <= 0) {
                albumArt = ContextCompat.getDrawable(this, R.drawable.image);
            }
            albumArtView.setImageDrawable(albumArt);
        } catch (Exception e) {
            albumArtView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.image));
        }
    }

    public void getSongList() {
        songsList = album.getAllSongs();
    }

    private String[] allArtistArray(List<String> artists) {
        if (artists == null) {
            return null;
        }
        String[] allArtists = new String[artists.size()];
        allArtists = artists.toArray(allArtists);
        return allArtists;
    }

    private String[] allGenresArray(List<Genre> genres) {
        if (genres == null) {
            return null;
        }
        String[] allGenres = new String[genres.size()];
        for (int i = 0; i < allGenres.length; i++) {
            allGenres[i] = genres.get(i).getName();
        }
        return allGenres;
    }

    public void displayAllGenres(View view) {
        Collections.sort(album.getGenres());
        String[] genres = allGenresArray(album.getGenres());

        if ((genres != null ? genres.length : 0) != 0) {
            if (genres.length == 1) {
                redirectTo(ArtistsByGenrePage.class, getResources().getString(R.string.GenreNameKey), genres[0]);
            }
            else {
                alertDialogBuild(getResources().getString(R.string.AllAlbumGenresTitle), genres, ArtistsByGenrePage.class, getResources().getString(R.string.GenreNameKey));
            }
        }
    }

    public void displayAllArtists(View view) {
        Collections.sort(album.getArtists());
        String[] artists = allArtistArray(album.getArtists());

        if ((artists != null ? artists.length : 0) != 0) {
            if (artists.length == 1) {
                redirectTo(ArtistPage.class, getResources().getString(R.string.ArtistNameKey), artists[0]);
            }
            else {
                alertDialogBuild(getResources().getString(R.string.AllAlbumArtistsTitle), artists,  ArtistPage.class, getResources().getString(R.string.ArtistNameKey));
            }
        }
    }

    private void alertDialogBuild(String title, String[] items, Class destClass, String extraKey) {
        ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.SeveralItemsSelectionAlertDialog);
        AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        builder.setTitle(title);
        builder.setItems(items, (dialog, which) -> redirectTo(destClass, extraKey, items[which]));
        builder.setCancelable(true);
        builder.show();
    }

    private void redirectTo(Class dest, String extraKey, String value) {
        Intent intent = new Intent(getBaseContext(), dest);
        intent.putExtra(extraKey, value);
        startActivity(intent);
    }

    public void songPicked(View view) {
        try {
            musicService.playSelectedSong(albumAdapter.getItem(songsListView.getPositionForView(view)), album, MusicService.Source.ALBUMS);
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
    protected void onDestroy() {
        if (musicBound && musicService != null /*&& !musicService.isSongPlayingNow()*/ && musicConnection != null) {
            unbindService(musicConnection);
            musicService = null;
            musicBound = false;
        }
        super.onDestroy();
    }
}
