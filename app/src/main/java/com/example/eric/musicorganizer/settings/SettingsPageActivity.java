package com.example.eric.musicorganizer.settings;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.eric.musicorganizer.R;
import com.example.eric.musicorganizer.settings.Settings.OrderOptions;
import com.example.eric.musicorganizer.settings.Settings.SortOptions;

/** The activity displaces and control the user settings values, include changing, saving and revert to factory settings. */
public class SettingsPageActivity extends AppCompatActivity {

    // Settings changed by the user:
    private boolean isResumeMusicPlayingTemp;
    private boolean isAlbumArtistDisplayTemp;
    private OrderOptions allSongsOrderTemp;
    private OrderOptions allAlbumsOrderTemp;
    private SortOptions allSongsSortTemp;
    private SortOptions allAlbumsSortTemp;

    // Artists Display Settings:
    private OrderOptions artistSongsOrderTemp;
    private SortOptions artistSongsSortTemp;
    private SortOptions artistAlbumsSortTemp;
    private OrderOptions artistAlbumsOrderTemp;

    // Composers Display Settings:
    private OrderOptions composersAlbumsOrderTemp;
    private OrderOptions composersSongsOrderTemp;
    private SortOptions composersAlbumsSortTemp;
    private SortOptions composersSongsSortTemp;

    // Genres Display Settings:
    private OrderOptions genresAlbumsOrderTemp;
    private OrderOptions genresSongsOrderTemp;
    private SortOptions genresAlbumsSortTemp;
    private SortOptions genresSongsSortTemp;

    private String[] sortOptions;
    private String[] orderOptions;
    private boolean isChangesOccurred;  // True if at least one of the settings options is changed by the user.

    /**
     * Load the visual settings from the xml file and set the local visual and logical variables.
     * @param savedInstanceState => Not used
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);

        isChangesOccurred = false;
        sortOptions = getResources().getStringArray(R.array.sortSettingsItems);
        orderOptions = getResources().getStringArray(R.array.orderSettingsItems);
        initSettings();
        initViews();
    }

    /** Loads the settings of the logical variables from the settings class, this variables used to
     * save the changes that the users does on the settings window. */
    private void initSettings() {
        // Display Settings:
        isResumeMusicPlayingTemp = Settings.getInstance().isResumeMusicPlaying();
        isAlbumArtistDisplayTemp = Settings.getInstance().isAlbumArtistDisplay();

        // Songs Display Settings:
        allSongsOrderTemp = Settings.getInstance().getAllSongsOrder();
        allSongsSortTemp = Settings.getInstance().getAllSongsSort();

        // Albums Display Settings:
        allAlbumsOrderTemp = Settings.getInstance().getAllAlbumsOrder();
        allAlbumsSortTemp = Settings.getInstance().getAllAlbumsSort();

        // Artists Display Settings:
        artistSongsOrderTemp = Settings.getInstance().getArtistSongsOrder();
        artistSongsSortTemp = Settings.getInstance().getArtistSongsSort();
        artistAlbumsSortTemp = Settings.getInstance().getArtistAlbumsSort();
        artistAlbumsOrderTemp = Settings.getInstance().getArtistAlbumsOrder();

        // Composers Display Settings:
        composersAlbumsOrderTemp = Settings.getInstance().getComposersAlbumsOrder();
        composersSongsOrderTemp = Settings.getInstance().getComposersSongsOrder();
        composersAlbumsSortTemp = Settings.getInstance().getComposersAlbumsSort();
        composersSongsSortTemp = Settings.getInstance().getComposersSongsSort();

        // Genres Display Settings:
        genresAlbumsOrderTemp = Settings.getInstance().getGenresAlbumsOrder();
        genresSongsOrderTemp = Settings.getInstance().getGenresSongsOrder();
        genresAlbumsSortTemp = Settings.getInstance().getGenresAlbumsSort();
        genresSongsSortTemp = Settings.getInstance().getGenresSongsSort();

    }

    /** Init and activate all the visual elements, represents the settings options. */
    private void initViews() {
        // Display Settings:
        initResumeMusicPlaying();
        initAlbumArtistDisplayView();

        // Songs Display Views:
        initAllSongsOrder();
        initAllSongsSort();

        // Albums Display Views:
        initAllAlbumsOrder();
        initAllAlbumsSort();

        // Artists Display Views:
        initArtistAlbumsOrder();
        initArtistSongsOrder();
        initAllArtistAlbumsSort();
        initAllArtistSongsSort();

        // Composers Display Settings:
        //initComposersAlbumsOrder();
        //initComposersSongsOrder();
        //initComposersAlbumsSort();
        //initComposersSongsSort();

        // Genres Display Settings:
        initGenreAlbumsOrder();
        initGenreSongsOrder();
        initGenreAlbumsSort();
        initGenreSongsSort();
    }

    private void initResumeMusicPlaying() {
        Switch isResumeMusicPlayingView = findViewById(R.id.resumeMusicPlaying);
        isResumeMusicPlayingView.setChecked(isResumeMusicPlayingTemp);
        isResumeMusicPlayingView.setOnCheckedChangeListener(((buttonView, isChecked) -> onResumeMusicPlayingChanged(isChecked)));
    }

    private void onResumeMusicPlayingChanged(boolean isChecked) {
        isResumeMusicPlayingTemp = isChecked;
        if (isResumeMusicPlayingTemp != Settings.getInstance().isResumeMusicPlaying()) {
            isChangesOccurred = true;
        }
    }

    /** Init and activate the album artist display settings switch. */
    private void initAlbumArtistDisplayView() {
        // Visual elements:
        Switch isAlbumArtistDisplayView = findViewById(R.id.groupByAlbumArtist);
        isAlbumArtistDisplayView.setChecked(isAlbumArtistDisplayTemp);
        isAlbumArtistDisplayView.setOnCheckedChangeListener((buttonView, isChecked) -> onAlbumArtistDisplayChange(isChecked));
    }

    /** Set the change, if occurred on the album artist display logical variable
     * @param isChecked => represent the current state of the visual element that represents the album artist display visual element.
     */
    private void onAlbumArtistDisplayChange(boolean isChecked) {
        isAlbumArtistDisplayTemp = isChecked;
        if (isAlbumArtistDisplayTemp != Settings.getInstance().isAlbumArtistDisplay()) {
            isChangesOccurred = true;
        }
    }

    /** Init and activate the all songs order visual element. */
    private void initAllSongsOrder() {
        Spinner allSongsOrder = findViewById(R.id.songsOrder);
        allSongsOrder.setSelection(getOrderOptionsIndex(allSongsOrderTemp));
        allSongsOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                allSongsOrderTemp = getOrderOptionFromString(orderOptions[position]);
                if (allSongsOrderTemp != Settings.getInstance().getAllSongsOrder()) {
                    isChangesOccurred = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Not used
            }
        });
    }

    /** Init and activate the all songs sort visual element. */
    private void initAllSongsSort() {
        Spinner allSongsSort = findViewById(R.id.songsSort);
        allSongsSort.setSelection(getSortOptionsIndex(allSongsSortTemp));
        allSongsSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                allSongsSortTemp = getSortOptionFromString(sortOptions[position]);
                if (allSongsSortTemp != Settings.getInstance().getAllSongsSort()) {
                    isChangesOccurred = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Not used
            }
        });
    }

    /** Init and activate the all app_logo order visual element. */
    private void initAllAlbumsOrder() {
        Spinner allAlbumsOrder = findViewById(R.id.allAlbumsOrder);
        allAlbumsOrder.setSelection(getOrderOptionsIndex(allAlbumsOrderTemp));
        allAlbumsOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                allAlbumsOrderTemp = getOrderOptionFromString(orderOptions[position]);
                if (allAlbumsOrderTemp != Settings.getInstance().getAllAlbumsOrder()) {
                    isChangesOccurred = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Not used
            }
        });
    }

    /** Init and activate the all app_logo sort visual element. */
    private void initAllAlbumsSort() {
        Spinner allAlbumsSort = findViewById(R.id.allAlbumsSort);
        allAlbumsSort.setSelection(getSortOptionsIndex(allAlbumsSortTemp));
        allAlbumsSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                allAlbumsSortTemp = getSortOptionFromString(sortOptions[position]);
                if (allAlbumsSortTemp != Settings.getInstance().getAllAlbumsSort()) {
                    isChangesOccurred = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Not used
            }
        });
    }

    /** Init and activate the artist app_logo order visual element. */
    private void initArtistAlbumsOrder() {
        Spinner artistAlbumsOrder = findViewById(R.id.artistAlbumsOrder);
        artistAlbumsOrder.setSelection(getOrderOptionsIndex(artistAlbumsOrderTemp));
        artistAlbumsOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                artistAlbumsOrderTemp = getOrderOptionFromString(orderOptions[position]);
                if (artistAlbumsOrderTemp != Settings.getInstance().getArtistAlbumsOrder()) {
                    isChangesOccurred = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Not used
            }
        });
    }

    /** Init and activate the artist songs order visual element. */
    private void initArtistSongsOrder() {
        Spinner artistSongsOrder = findViewById(R.id.artistSongsOrder);
        artistSongsOrder.setSelection(getOrderOptionsIndex(artistSongsOrderTemp));
        artistSongsOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                artistSongsOrderTemp = getOrderOptionFromString(orderOptions[position]);
                if (artistSongsOrderTemp != Settings.getInstance().getArtistSongsOrder()) {
                    isChangesOccurred = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Not used
            }
        });
    }

    /** Init and activate the artist app_logo sort visual element. */
    private void initAllArtistAlbumsSort() {
        Spinner artistAlbumsSort = findViewById(R.id.artistAlbumsSort);
        artistAlbumsSort.setSelection(getSortOptionsIndex(artistAlbumsSortTemp));
        artistAlbumsSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                artistAlbumsSortTemp = getSortOptionFromString(sortOptions[position]);
                if (artistAlbumsSortTemp != Settings.getInstance().getArtistAlbumsSort()) {
                    isChangesOccurred = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Not used
            }
        });
    }

    /** Init and activate the artist songs sort visual element. */
    private void initAllArtistSongsSort() {
        Spinner artistSongsSort = findViewById(R.id.artistSongsSort);
        artistSongsSort.setSelection(getSortOptionsIndex(artistSongsSortTemp));
        artistSongsSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                artistSongsSortTemp = getSortOptionFromString(sortOptions[position]);
                if (artistSongsSortTemp != Settings.getInstance().getArtistSongsSort()) {
                    isChangesOccurred = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Not used
            }
        });
    }

    /** Init and activate the composers app_logo order visual element. */
    /*private void initComposersAlbumsOrder() {
        Spinner composersAlbumsOrder = findViewById(R.id.composersAlbumsOrder);
        composersAlbumsOrder.setSelection(getOrderOptionsIndex(composersAlbumsOrderTemp));
        composersAlbumsOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                composersAlbumsOrderTemp = getOrderOptionFromString(orderOptions[position]);
                if (composersAlbumsOrderTemp != Settings.getInstance().getComposersAlbumsOrder()) {
                    isChangesOccurred = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Not used
            }
        });
    }*/

    /** Init and activate the composers songs order visual element. */
    /*private void initComposersSongsOrder() {
        Spinner composersSongsOrder = findViewById(R.id.composersSongsOrder);
        composersSongsOrder.setSelection(getOrderOptionsIndex(composersSongsOrderTemp));
        composersSongsOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                composersSongsOrderTemp = getOrderOptionFromString(orderOptions[position]);
                if (composersSongsOrderTemp != Settings.getInstance().getComposersSongsOrder()) {
                    isChangesOccurred = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Not used
            }
        });
    }*/

    /** Init and activate the composers app_logo sort visual element. */
    /*private void initComposersAlbumsSort() {
        Spinner composersAlbumsSort = findViewById(R.id.composersAlbumsSort);
        composersAlbumsSort.setSelection(getSortOptionsIndex(composersAlbumsSortTemp));
        composersAlbumsSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                composersAlbumsSortTemp = getSortOptionFromString(sortOptions[position]);
                if (composersAlbumsSortTemp != Settings.getInstance().getComposersAlbumsSort()) {
                    isChangesOccurred = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Not used
            }
        });
    }*/

    /** Init and activate the composers songs sort visual element. */
    /*private void initComposersSongsSort() {
        Spinner composersSongsSort = findViewById(R.id.composersAlbumsSort);
        composersSongsSort.setSelection(getSortOptionsIndex(composersSongsSortTemp));
        composersSongsSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                composersSongsSortTemp = getSortOptionFromString(sortOptions[position]);
                if (composersSongsSortTemp != Settings.getInstance().getComposersSongsSort()) {
                    isChangesOccurred = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Not used
            }
        });
    }*/

    /** Init and activate the genres app_logo order visual element. */
    private void initGenreAlbumsOrder() {
        Spinner genreAlbumsOrder = findViewById(R.id.genreAlbumsOrder);
        genreAlbumsOrder.setSelection(getOrderOptionsIndex(genresAlbumsOrderTemp));
        genreAlbumsOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                genresAlbumsOrderTemp = getOrderOptionFromString(orderOptions[position]);
                if (genresAlbumsOrderTemp != Settings.getInstance().getGenresAlbumsOrder()) {
                    isChangesOccurred = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Not used
            }
        });
    }

    /** Init and activate the genres songs order visual element. */
    private void initGenreSongsOrder() {
        Spinner genreSongsOrder = findViewById(R.id.genreSongsOrder);
        genreSongsOrder.setSelection(getOrderOptionsIndex(genresSongsOrderTemp));
        genreSongsOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                genresSongsOrderTemp = getOrderOptionFromString(orderOptions[position]);
                if (genresSongsOrderTemp != Settings.getInstance().getGenresSongsOrder()) {
                    isChangesOccurred = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Not used
            }
        });
    }

    /** Init and activate the genres app_logo sort visual element. */
    private void initGenreAlbumsSort() {
        Spinner genreAlbumsSort = findViewById(R.id.genreAlbumsSort);
        genreAlbumsSort.setSelection(getSortOptionsIndex(genresAlbumsSortTemp));
        genreAlbumsSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                genresAlbumsSortTemp = getSortOptionFromString(sortOptions[position]);
                if (genresAlbumsSortTemp != Settings.getInstance().getGenresAlbumsSort()) {
                    isChangesOccurred = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Not used
            }
        });
    }

    /** Init and activate the genre songs sort visual element. */
    private void initGenreSongsSort() {
        Spinner genreSongsSort = findViewById(R.id.genreSongsSort);
        genreSongsSort.setSelection(getSortOptionsIndex(genresSongsSortTemp));
        genreSongsSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                genresSongsSortTemp = getSortOptionFromString(sortOptions[position]);
                if (genresSongsSortTemp != Settings.getInstance().getGenresSongsSort()) {
                    isChangesOccurred = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Not used
            }
        });
    }

    /** Find the index of the current order option on the orderOptions array.
     * @param orderOption => The current orderOption.
     * @return => The index of the orderOption on the orderOptions array. If not exists, return 0.
     */
    private int getOrderOptionsIndex(OrderOptions orderOption) {
        for (int i = 0; i < orderOptions.length; i++) {
            if (orderOptions[i].toLowerCase().equals(orderOption.toString().toLowerCase())) {
                return i;
            }
        }
        return 0;
    }

    /** Find the index of the current sort option on the sortOptions array.
     * @param sortOption => The current orderOption.
     * @return => The index of the sortOption on the sortOptions array. If not exists, return 0.
     */
    private int getSortOptionsIndex(SortOptions sortOption) {
        for (int i = 0; i < sortOptions.length; i++) {
            if (sortOptions[i].toLowerCase().equals(sortOption.toString().toLowerCase())) {
                return i;
            }
        }
        return 0;
    }

    /** Returns the orderOption represented by the given string
     * @param orderOption => represents a order option
     * @return => OrderOption represented by the given string, or null if not exists
     */
    private OrderOptions getOrderOptionFromString(String orderOption) {
        if (orderOption == null || orderOption.isEmpty()) {
            return null;
        }
        else if (orderOption.toLowerCase().equals(OrderOptions.ASCENDING.name().toLowerCase())) {
            return OrderOptions.ASCENDING;
        }
        else if (orderOption.toLowerCase().equals(OrderOptions.DESCENDING.name().toLowerCase())) {
            return OrderOptions.DESCENDING;
        }
        else {
            return null;
        }
    }

    /** Returns the SortOptions represented by the given string
     * @param sortOption => represents a sort option
     * @return => SortOptions represented by the given string, or null if not exists
     */
    private SortOptions getSortOptionFromString(final String sortOption) {
        if (sortOption == null || sortOption.isEmpty()) {
            return null;
        }
        else if (sortOption.toLowerCase().equals(SortOptions.TITLE.name().toLowerCase())) {
            return SortOptions.TITLE;
        }
        else if (sortOption.toLowerCase().equals(SortOptions.YEAR.name().toLowerCase())) {
            return SortOptions.YEAR;
        }
        else {
            return null;
        }
    }

    /** Ask the use to save the changed settings and leave the settings activity */
    @Override
    public void onBackPressed() {
        if (isAnyChangeHappen()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Settings")
                    .setMessage("Would you like to save your changes?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> leaveAndSaveChanges())
                    .setNegativeButton("No", (dialog, id) -> SettingsPageActivity.this.finish());
            AlertDialog alert = builder.create();
            alert.show();
        }
        else {
            SettingsPageActivity.this.finish();
        }
    }

    /** Activate saving the changes in the memory and update the settings in the settings class. At the end,
     * this method will finish the current activity and the user will be returned to the activity he comes from. */
    private void leaveAndSaveChanges() {
        setChangesOnSettings();
        Settings.getInstance().setSettingsInMemory();
        Toast.makeText(this, "Your change has been saved!", Toast.LENGTH_SHORT).show();
        SettingsPageActivity.this.finish();
    }

    /** Save the changes in the memory for all the settings that has been changed. */
    public void setChangesOnSettings() {
        if (isResumeMusicPlayingTemp != Settings.getInstance().isResumeMusicPlaying()) {
            Settings.getInstance().setResumeMusicPlaying(isResumeMusicPlayingTemp);
        }
        if (isAlbumArtistDisplayTemp != Settings.getInstance().isAlbumArtistDisplay()) {
            Settings.getInstance().setAlbumArtistDisplay(isAlbumArtistDisplayTemp);
        }
        if (allSongsOrderTemp != Settings.getInstance().getAllSongsOrder()) {
            Settings.getInstance().setAllSongsOrder(allSongsOrderTemp);
        }
        if (allSongsSortTemp != Settings.getInstance().getAllSongsSort()) {
            Settings.getInstance().setAllSongsSort(allSongsSortTemp);
        }
        if (allAlbumsOrderTemp != Settings.getInstance().getAllAlbumsOrder()) {
            Settings.getInstance().setAllAlbumsOrder(allAlbumsOrderTemp);
        }
        if (allAlbumsSortTemp != Settings.getInstance().getAllAlbumsSort()) {
            Settings.getInstance().setAllAlbumsSort(allAlbumsSortTemp);
        }
        if (artistSongsOrderTemp != Settings.getInstance().getArtistSongsOrder()) {
            Settings.getInstance().setArtistSongsOrder(artistSongsOrderTemp);
        }
        if (artistSongsSortTemp != Settings.getInstance().getArtistSongsSort()) {
            Settings.getInstance().setArtistSongsSort(artistSongsSortTemp);
        }
        if (artistAlbumsOrderTemp != Settings.getInstance().getArtistAlbumsOrder()) {
            Settings.getInstance().setArtistAlbumsOrder(artistAlbumsOrderTemp);
        }
        if (artistAlbumsSortTemp != Settings.getInstance().getArtistAlbumsSort()) {
            Settings.getInstance().setArtistAlbumsSort(artistAlbumsSortTemp);
        }
        if (composersAlbumsOrderTemp != Settings.getInstance().getComposersAlbumsOrder()) {
            Settings.getInstance().setComposersAlbumsOrder(composersAlbumsOrderTemp);
        }
        if (composersSongsOrderTemp != Settings.getInstance().getComposersSongsOrder()) {
            Settings.getInstance().setComposersSongsOrder(composersSongsOrderTemp);
        }
        if (composersAlbumsSortTemp != Settings.getInstance().getComposersAlbumsSort()) {
            Settings.getInstance().setComposersAlbumsSort(composersAlbumsSortTemp);
        }
        if (composersSongsSortTemp != Settings.getInstance().getComposersSongsSort()) {
            Settings.getInstance().setComposersSongsSort(composersSongsSortTemp);
        }
        if (genresAlbumsOrderTemp != Settings.getInstance().getGenresAlbumsOrder()) {
            Settings.getInstance().setGenresAlbumsOrder(genresAlbumsOrderTemp);
        }
        if (genresSongsOrderTemp != Settings.getInstance().getGenresSongsOrder()) {
            Settings.getInstance().setGenresSongsOrder(genresSongsOrderTemp);
        }
        if (genresAlbumsSortTemp != Settings.getInstance().getGenresAlbumsSort()) {
            Settings.getInstance().setGenresAlbumsSort(genresAlbumsSortTemp);
        }
        if (genresSongsSortTemp != Settings.getInstance().getGenresSongsSort()) {
            Settings.getInstance().setGenresSongsSort(genresSongsSortTemp);
        }
    }

    /** Check if any change occurred by comparing with the settings saved on the memory
     * @return true if and only if, isChangesOccurred is true and at least one of the settings isn't
     * equals to it's value in the memory.
     */
    public boolean isAnyChangeHappen() {
        if (!isChangesOccurred) {
            return false;
        }
        else if (isResumeMusicPlayingTemp != Settings.getInstance().isResumeMusicPlaying()) {
            return true;
        }
        else if (isAlbumArtistDisplayTemp != Settings.getInstance().isAlbumArtistDisplay()) {
            return true;
        }
        else if (allSongsOrderTemp != Settings.getInstance().getAllSongsOrder()) {
            return true;
        }
        else if (allSongsSortTemp != Settings.getInstance().getAllSongsSort()) {
            return true;
        }
        else if (allAlbumsOrderTemp != Settings.getInstance().getAllAlbumsOrder()) {
            return true;
        }
        else if (allAlbumsSortTemp != Settings.getInstance().getAllAlbumsSort()) {
            return true;
        }
        else if (artistSongsOrderTemp != Settings.getInstance().getArtistSongsOrder()) {
            return true;
        }
        else if (artistSongsSortTemp != Settings.getInstance().getArtistSongsSort()) {
            return true;
        }
        else if (artistAlbumsSortTemp != Settings.getInstance().getArtistAlbumsSort()) {
            return true;
        }
        else if (artistAlbumsOrderTemp != Settings.getInstance().getArtistAlbumsOrder()) {
            return true;
        }
        else if (composersAlbumsOrderTemp != Settings.getInstance().getComposersAlbumsOrder()) {
            return true;
        }
        else if (composersSongsOrderTemp != Settings.getInstance().getComposersSongsOrder()) {
            return true;
        }
        else if (composersAlbumsSortTemp != Settings.getInstance().getComposersAlbumsSort()) {
            return true;
        }
        else if (composersSongsSortTemp != Settings.getInstance().getComposersSongsSort()) {
            return true;
        }
        else if (genresAlbumsOrderTemp != Settings.getInstance().getGenresAlbumsOrder()) {
            return true;
        }
        else if (genresSongsOrderTemp != Settings.getInstance().getGenresSongsOrder()) {
            return true;
        }
        else if (genresAlbumsSortTemp != Settings.getInstance().getGenresAlbumsSort()) {
            return true;
        }
        else {
            return genresSongsSortTemp != Settings.getInstance().getGenresSongsSort();
        }
    }

    /** Activate interaction with the user and activate the reset to the default settings values.
     * @param view => represent the button element. */
    public void onResetAllSettingsClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Reset")
                .setMessage(Html.fromHtml("Resetting to default settings removes all of your custom settings.\n<b>Are your sure you want to Reset to Factory Defaults?</b>"))
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> resetAllSettings())
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();
    }

    /** reset the default settings values. */
    private void resetAllSettings() {
        Settings.getInstance().revertAllSettings();     // revert all the settings to the default values
        initSettings();                                 // init logical settings after changes
        initViews();                                    // init visuals settings after changes
        Toast.makeText(this, "The settings have been successfully restored.", Toast.LENGTH_SHORT).show();
    }
}
