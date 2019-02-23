package com.example.eric.musicorganizer.search;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eric.musicorganizer.R;
import com.example.eric.musicorganizer.activityPages.AlbumPage;
import com.example.eric.musicorganizer.activityPages.ArtistPage;
import com.example.eric.musicorganizer.activityPages.ArtistsByGenrePage;
import com.example.eric.musicorganizer.mediaDataTypes.Album;
import com.example.eric.musicorganizer.mediaDataTypes.Artist;
import com.example.eric.musicorganizer.mediaDataTypes.Genre;
import com.example.eric.musicorganizer.musicManager.MusicManager;
import com.example.eric.musicorganizer.mediaDataTypes.Song;
import com.example.eric.musicorganizer.mediaDataTypes.adapters.AllAlbumsAdapter;
import com.example.eric.musicorganizer.mediaDataTypes.adapters.AllArtistsAdapter;
import com.example.eric.musicorganizer.mediaDataTypes.adapters.GenreAdapter;
import com.example.eric.musicorganizer.mediaDataTypes.adapters.SongAdapter;

import java.text.Normalizer;
import java.util.Collections;
import java.util.List;

public class SearchableActivity extends AppCompatActivity {

    private List<Song> songsList;
    private List<Album> albumsList;
    private List<Artist> artistsList;
    //private List<Composer> composersList;
    private List<Genre> genresList;

    private ListView songsListView;
    private ListView albumsListView;
    private ListView artistsListView;
    //private ListView composersListView;
    private ListView genresListView;

    private TextView songsTitle;
    private TextView albumTitle;
    private TextView artistTitle;
    private TextView genreTitle;

    private SongAdapter songAdapter;
    private AllAlbumsAdapter allAlbumsAdapter;
    private AllArtistsAdapter allArtistAdapter;
    private GenreAdapter allGenresAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results);

        songsTitle = findViewById(R.id.songs_results_title);
        albumTitle = findViewById(R.id.albums_results_title);
        artistTitle = findViewById(R.id.artists_results_title);
        genreTitle = findViewById(R.id.genre_results_title);

        songsListView = findViewById(R.id.songs_results_list);
        albumsListView = findViewById(R.id.albums_results_list);
        artistsListView = findViewById(R.id.artists_results_list);
        genresListView = findViewById(R.id.genre_results_list);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchForResults(query);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchForResults(query);
        }
    }

    /** Make the search for each kind of result.
     * @param query => String to be find.
     */
    private void searchForResults(String query) {
        songsTitle = findViewById(R.id.songs_results_title);
        albumTitle = findViewById(R.id.albums_results_title);
        artistTitle = findViewById(R.id.artists_results_title);
        genreTitle = findViewById(R.id.genre_results_title);
        TextView noResults = findViewById(R.id.no_search_result);
        boolean isAnyResult = false;

        String normalized = Normalizer.normalize(query, Normalizer.Form.NFD);
        query = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        setTitle(String.format("Filter music for: \"%s\"", query));
        songsList = MusicManager.getInstance().getAllMatchedSongs(query);
        albumsList = MusicManager.getInstance().getAllMatchedAlbums(query);
        artistsList = MusicManager.getInstance().getAllMatchedArtists(query);
        genresList = MusicManager.getInstance().getAllMatchedGenres(query);
        if (songsList != null && !songsList.isEmpty()) {
            isAnyResult = true;
            if (songsTitle != null) {
                songsTitle.setText(editTitleWithResults(songsTitle.getText(), songsList.size()));
            }
            displaySongResults();
        }
        else {
            if (songsTitle != null) {
                songsTitle.setVisibility(View.GONE);    // hide songs title in case of no result.
            }
        }
        if (albumsList != null && !albumsList.isEmpty()) {
            isAnyResult = true;
            if (albumTitle != null) {
                albumTitle.setText(editTitleWithResults(albumTitle.getText(), albumsList.size()));
            }
            displayAlbumResults();
        }
        else {
            if (albumTitle != null) {
                albumTitle.setVisibility(View.GONE);    // hide album title in case of no result.
            }
        }
        if (artistsList != null && !artistsList.isEmpty()) {
            isAnyResult = true;
            if (artistTitle != null) {
                artistTitle.setText(editTitleWithResults(artistTitle.getText(), artistsList.size()));
            }
            displayArtistResults();
        }
        else {
            if (artistTitle != null) {
                artistTitle.setVisibility(View.GONE);    // hide artist title in case of no result.
            }
        }

        if (genresListView != null && !genresList.isEmpty()) {
            isAnyResult = true;
            if (genreTitle != null) {
                genreTitle.setText(editTitleWithResults(genreTitle.getText(), genresList.size()));
            }
            displayGenreResults();
        }
        else {
            if (genreTitle != null) {
                genreTitle.setVisibility(View.GONE);    // hide artist title in case of no result.
            }
        }
        if (noResults != null) {
            noResults.setVisibility((isAnyResult) ? View.GONE : View.VISIBLE);
        }
    }

    private String editTitleWithResults(CharSequence title, int numOfResults) {
        return title + " (" + numOfResults + ")";
    }

    private void displaySongResults() {
        songsListView = findViewById(R.id.songs_results_list);         // get the all_songs_list list sorted
        if (songsListView == null || songsList.isEmpty()) {
            Toast.makeText(this, R.string.no_songs, Toast.LENGTH_SHORT).show();
            finishActivity(-1);
        }
        Collections.sort(songsList, Song::compareSongsByTitleAscending);
        songAdapter = new SongAdapter(this, songsList);          // get view adapter
        songsListView.setAdapter(songAdapter);                          // it should present the list of songs on the device
        songsListView.setOnItemClickListener((parent, view, position, id) -> selectSongByPosition(position));
        songsListView.setVisibility(View.GONE);
    }

    private void displayAlbumResults() {
        albumsListView = findViewById(R.id.albums_results_list);
        if (albumsList == null || albumsList.isEmpty()) {
            Toast.makeText(this, R.string.no_albums, Toast.LENGTH_SHORT).show();
            finishActivity(-1);
        }
        Collections.sort(songsList, Song::compareSongsByTitleAscending);
        allAlbumsAdapter = new AllAlbumsAdapter(this, albumsList);  // get view adapter
        albumsListView.setAdapter(allAlbumsAdapter);                                         // it should present the list of songs on the device
        albumsListView.setOnItemClickListener((parent, view, position, id) -> selectAlbumByPosition(position));
        albumsListView.setVisibility(View.GONE);
    }

    private void displayArtistResults() {
        artistsListView = findViewById(R.id.artists_results_list);
        if (artistsList == null || artistsList.isEmpty()) {
            Toast.makeText(this, R.string.no_artists, Toast.LENGTH_SHORT).show();
            this.finish();
        }
        Collections.sort(artistsList);
        allArtistAdapter = new AllArtistsAdapter(this, artistsList);                        // get view adapter
        artistsListView.setAdapter(allArtistAdapter);                                               // it should present the list of songs on the device
        artistsListView.setOnItemClickListener((parent, view, position, id) -> selectArtistByPosition(position));
        artistsListView.setVisibility(View.GONE);
    }

    private void displayGenreResults() {
        genresListView = findViewById(R.id.genre_results_list);
        if (genresList == null || genresList.isEmpty()) {
            Toast.makeText(this, R.string.no_genres, Toast.LENGTH_SHORT).show();
            this.finish();
        }
        Collections.sort(genresList);
        allGenresAdapter = new GenreAdapter(this, genresList);
        genresListView.setAdapter(allGenresAdapter);
        genresListView.setOnItemClickListener(((parent, view, position, id) -> selectGenreByPosition(position)));
        genresListView.setVisibility(View.GONE);
    }

    private void selectGenreByPosition(int position) {
        Intent intent = new Intent(getBaseContext(), ArtistsByGenrePage.class);
        intent.putExtra(getResources().getString(R.string.GenreNameKey), allGenresAdapter.getItem(position).getName());
        startActivity(intent);
    }

    private void selectSongByPosition(int position) {
        // TODO - Play the selected song
    }

    private void selectAlbumByPosition(int position) {
        Album item = allAlbumsAdapter.getItem(position);
        Intent intent = new Intent(getBaseContext(), AlbumPage.class);
        intent.putExtra(getResources().getString(R.string.AlbumTitleKey), item.getTitle());
        intent.putExtra(getResources().getString(R.string.AlbumArtistKey), item.getAlbumArtist());
        startActivity(intent);
    }

    private void selectArtistByPosition(int position) {
        Intent intent = new Intent(getBaseContext(), ArtistPage.class);
        intent.putExtra(getResources().getString(R.string.ArtistNameKey), allArtistAdapter.getItem(position).getName());
        startActivity(intent);
    }

    public void songResultsSelected(View view) {
        if (songsList != null && songsListView != null && !songsList.isEmpty()) {
            if (songsListView.getVisibility() == View.GONE) {
                songsListView.setVisibility(View.VISIBLE);
            }
            else {
                songsListView.setVisibility(View.GONE);
            }
            if (albumsListView != null) {
                albumsListView.setVisibility(View.GONE);
            }
            if (artistsListView != null) {
                artistsListView.setVisibility(View.GONE);
            }
            if (genresListView != null) {
                genresListView.setVisibility(View.GONE);
            }
        }
    }

    public void albumResultsSelected(View view) {
        if (albumsList != null && albumsListView != null && !albumsList.isEmpty()) {
            if (songsListView != null) {
                songsListView.setVisibility(View.GONE);
            }
            if (albumsListView.getVisibility() == View.GONE) {
                albumsListView.setVisibility(View.VISIBLE);
            }
            else {
                albumsListView.setVisibility(View.GONE);
            }
            if (artistsListView != null) {
                artistsListView.setVisibility(View.GONE);
            }
            if (genresListView != null) {
                genresListView.setVisibility(View.GONE);
            }
        }
    }

    public void artistResultsSelected(View view) {
        if (artistsList != null && artistsListView != null && !artistsList.isEmpty()) {
            if (songsListView != null) {
                songsListView.setVisibility(View.GONE);
            }
            if (albumsListView != null) {
                albumsListView.setVisibility(View.GONE);
            }
            if (artistsListView.getVisibility() == View.GONE) {
                artistsListView.setVisibility(View.VISIBLE);
            }
            else {
                artistsListView.setVisibility(View.GONE);
            }
            if (genresListView != null) {
                genresListView.setVisibility(View.GONE);
            }
        }
    }

    public void genreResultsSelected(View view) {
        if (genresList != null && genresListView != null && !genresList.isEmpty()) {
            if (songsListView != null) {
                songsListView.setVisibility(View.GONE);
            }
            if (albumsListView != null) {
                albumsListView.setVisibility(View.GONE);
            }
            if (artistsListView != null) {
                artistsListView.setVisibility(View.GONE);
            }
            if (genresListView.getVisibility() == View.GONE) {
                genresListView.setVisibility(View.VISIBLE);
            }
            else {
                genresListView.setVisibility(View.GONE);
            }
        }
    }
}
