package com.example.eric.musicorganizer.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {

    public enum OrderOptions { ASCENDING, DESCENDING }
    public enum SortOptions { TITLE, YEAR }

    // Display Settings
    private boolean resumeMusicPlaying;
    private boolean albumArtistDisplay;

    // Songs Display Settings
    private OrderOptions allSongsOrder;
    private SortOptions allSongsSort;

    // Albums Display Settings
    private OrderOptions allAlbumsOrder;
    private SortOptions allAlbumsSort;

    // Artists Display Settings
    private OrderOptions artistAlbumsOrder;
    private OrderOptions artistSongsOrder;
    private SortOptions artistAlbumsSort;
    private SortOptions artistSongsSort;

    // Composers Display Settings
    private OrderOptions composersAlbumsOrder;
    private OrderOptions composersSongsOrder;
    private SortOptions composersAlbumsSort;
    private SortOptions composersSongsSort;

    // Genres Display Settings
    private OrderOptions genresAlbumsOrder;
    private OrderOptions genresSongsOrder;
    private SortOptions genresAlbumsSort;
    private SortOptions genresSongsSort;


    /* Default Settings */

    private final boolean defaultResumeMusicPlaying = false;
    private final boolean defaultAlbumArtistDisplay = true;

    // Songs Display Settings
    private final OrderOptions defaultAllSongsOrder = OrderOptions.ASCENDING;
    private final SortOptions defaultAllSongsSort = SortOptions.TITLE;

    // Albums Display Settings
    private final OrderOptions defaultAllAlbumsOrder = OrderOptions.ASCENDING;
    private final SortOptions defaultAllAlbumsSort = SortOptions.TITLE;

    // Artists Display Settings
    private final OrderOptions defaultArtistAlbumsOrder = OrderOptions.ASCENDING;
    private final OrderOptions defaultArtistSongsOrder = OrderOptions.ASCENDING;
    private final SortOptions defaultArtistAlbumsSort = SortOptions.TITLE;
    private final SortOptions defaultArtistSongsSort = SortOptions.TITLE;

    // Composers Display Settings
    private final OrderOptions defaultComposersAlbumsOrder = OrderOptions.ASCENDING;
    private final OrderOptions defaultComposersArtistsOrder = OrderOptions.ASCENDING;
    private final OrderOptions defaultComposersSongsOrder = OrderOptions.ASCENDING;
    private final SortOptions defaultComposersAlbumsSort = SortOptions.TITLE;
    private final SortOptions defaultComposersSongsSort = SortOptions.TITLE;

    // Genres Display Settings
    private final OrderOptions defaultGenresAlbumsOrder = OrderOptions.ASCENDING;
    private final OrderOptions defaultGenresArtistsOrder = OrderOptions.ASCENDING;
    private final OrderOptions defaultGenresSongsOrder = OrderOptions.ASCENDING;
    private final SortOptions defaultGenresAlbumsSort = SortOptions.TITLE;
    private final SortOptions defaultGenresSongsSort = SortOptions.TITLE;

    private static SharedPreferences sharedPreferences;     // used to connect to the memory
    private static Settings settingsInstance;               // instance of the settings

    private Settings() {
        //revertAllSettings();
    }

    public static Settings getInstance() {
        if (settingsInstance == null) {
            settingsInstance = new Settings();
        }
        return settingsInstance;
    }

    public void initSettingsFromMemory(Context context) {
        sharedPreferences = context.getSharedPreferences("com.example.eric.musicorganizer", Context.MODE_PRIVATE);
        getSettingsFromMemory();
    }

    private void getSettingsFromMemory() {
        resumeMusicPlaying = sharedPreferences.getBoolean("resumeMusicPlaying", defaultResumeMusicPlaying);
        albumArtistDisplay = sharedPreferences.getBoolean("albumArtistDisplay", defaultAlbumArtistDisplay);

        // Songs Display Settings
        this.allSongsOrder = OrderOptions.valueOf(sharedPreferences.getString("allSongsOrder", defaultAllSongsOrder.name()));
        this.allSongsSort = SortOptions.valueOf(sharedPreferences.getString("allSongsSort", defaultAllSongsSort.name()));


        // Albums Display Settings
        allAlbumsOrder = OrderOptions.valueOf(sharedPreferences.getString("allAlbumsOrder" , defaultAllAlbumsOrder.name()));
        allAlbumsSort = SortOptions.valueOf(sharedPreferences.getString("allAlbumsSort", defaultAllAlbumsSort.name()));

        // Artists Display Settings
        artistAlbumsOrder = OrderOptions.valueOf(sharedPreferences.getString("artistAlbumsOrder" , defaultArtistAlbumsOrder.name()));
        artistSongsOrder = OrderOptions.valueOf(sharedPreferences.getString("artistSongsOrder" , defaultArtistSongsOrder.name()));
        artistAlbumsSort = SortOptions.valueOf(sharedPreferences.getString("artistAlbumsSort", defaultArtistAlbumsSort.name()));
        artistSongsSort = SortOptions.valueOf(sharedPreferences.getString("artistSongsSort", defaultArtistSongsSort.name()));

        // Composers Display Settings
        composersAlbumsOrder = OrderOptions.valueOf(sharedPreferences.getString("composersAlbumsOrder" , defaultComposersAlbumsOrder.name()));
        composersSongsOrder = OrderOptions.valueOf(sharedPreferences.getString("composersSongsOrder" , defaultComposersSongsOrder.name()));
        composersAlbumsSort = SortOptions.valueOf(sharedPreferences.getString("composersAlbumsSort", defaultComposersAlbumsSort.name()));
        composersSongsSort = SortOptions.valueOf(sharedPreferences.getString("composersSongsSort", defaultComposersSongsSort.name()));

        // Genres Display Settings
        genresAlbumsOrder = OrderOptions.valueOf(sharedPreferences.getString("genresAlbumsOrder" , defaultGenresAlbumsOrder.name()));
        genresSongsOrder = OrderOptions.valueOf(sharedPreferences.getString("genresSongsOrder" , defaultGenresSongsOrder.name()));
        genresAlbumsSort = SortOptions.valueOf(sharedPreferences.getString("genresAlbumsSort", defaultGenresAlbumsSort.name()));
        genresSongsSort = SortOptions.valueOf(sharedPreferences.getString("genresSongsSort", defaultGenresSongsSort.name()));
    }

    void setSettingsInMemory() {
        if (sharedPreferences == null) {
            return;
        }
        sharedPreferences.edit().clear().apply();
        sharedPreferences.edit()
                .putBoolean("resumeMusicPlaying", resumeMusicPlaying)
                .putBoolean("albumArtistDisplay", albumArtistDisplay)
                .putString("allSongsOrder", allSongsOrder.name())
                .putString("allSongsSort", allSongsSort.name())
                .putString("allAlbumsOrder" , allAlbumsOrder.name())
                .putString("allAlbumsSort", allAlbumsSort.name())
                .putString("artistAlbumsOrder" , artistAlbumsOrder.name())
                .putString("artistSongsOrder" , artistSongsOrder.name())
                .putString("artistAlbumsSort", artistAlbumsSort.name())
                .putString("artistSongsSort", artistSongsSort.name())
                .putString("composersAlbumsOrder" , composersAlbumsOrder.name())
                .putString("composersSongsOrder" , composersSongsOrder.name())
                .putString("composersAlbumsSort", composersAlbumsSort.name())
                .putString("composersSongsSort", composersSongsSort.name())
                .putString("genresAlbumsOrder" , genresAlbumsOrder.name())
                .putString("genresSongsOrder" , genresSongsOrder.name())
                .putString("genresAlbumsSort", genresAlbumsSort.name())
                .putString("genresSongsSort", genresSongsSort.name())
                .apply();
    }

    void revertAllSettings() {
        resumeMusicPlaying = defaultResumeMusicPlaying;
        albumArtistDisplay = defaultAlbumArtistDisplay;

        // Songs Display Settings
        allSongsOrder = defaultAllSongsOrder;
        allSongsSort = defaultAllSongsSort;

        // Albums Display Settings
        allAlbumsOrder = defaultAllAlbumsOrder;
        allAlbumsSort = defaultAllAlbumsSort;

        // Artists Display Settings
        artistAlbumsOrder = defaultArtistAlbumsOrder;
        artistSongsOrder = defaultArtistSongsOrder;
        artistAlbumsSort = defaultArtistAlbumsSort;
        artistSongsSort = defaultArtistSongsSort;

        // Composers Display Settings
        composersAlbumsOrder = defaultComposersAlbumsOrder;
        composersSongsOrder = defaultComposersSongsOrder;
        composersAlbumsSort = defaultComposersAlbumsSort;
        composersSongsSort = defaultComposersSongsSort;

        // Genres Display Settings
        genresAlbumsOrder = defaultGenresAlbumsOrder;
        genresSongsOrder = defaultGenresSongsOrder;
        genresAlbumsSort = defaultGenresAlbumsSort;
        genresSongsSort = defaultGenresSongsSort;
    }

    /* Get Settings values: */

    public boolean isResumeMusicPlaying() {
        return resumeMusicPlaying;
    }

    public boolean isAlbumArtistDisplay() {
        return albumArtistDisplay;
    }

    public OrderOptions getAllSongsOrder() {
        return allSongsOrder;
    }

    public OrderOptions getAllAlbumsOrder() {
        return allAlbumsOrder;
    }

    public OrderOptions getArtistAlbumsOrder() {
        return artistAlbumsOrder;
    }

    public OrderOptions getArtistSongsOrder() {
        return artistSongsOrder;
    }

    public OrderOptions getComposersAlbumsOrder() {
        return composersAlbumsOrder;
    }

    public OrderOptions getComposersSongsOrder() {
        return composersSongsOrder;
    }

    public OrderOptions getGenresAlbumsOrder() {
        return genresAlbumsOrder;
    }

    public OrderOptions getGenresSongsOrder() {
        return genresSongsOrder;
    }

    public SortOptions getAllAlbumsSort() {
        return allAlbumsSort;
    }

    public SortOptions getAllSongsSort() {
        return allSongsSort;
    }

    public SortOptions getArtistAlbumsSort() {
        return artistAlbumsSort;
    }

    public SortOptions getArtistSongsSort() {
        return artistSongsSort;
    }

    public SortOptions getComposersAlbumsSort() {
        return composersAlbumsSort;
    }

    public SortOptions getComposersSongsSort() {
        return composersSongsSort;
    }

    public SortOptions getGenresAlbumsSort() {
        return genresAlbumsSort;
    }

    public SortOptions getGenresSongsSort() {
        return genresSongsSort;
    }

    /* Set Settings values: */

    void setResumeMusicPlaying(boolean resumeMusicPlaying) {
        this.resumeMusicPlaying = resumeMusicPlaying;
    }

    void setAlbumArtistDisplay(boolean albumArtistDisplay) {
        this.albumArtistDisplay = albumArtistDisplay;
    }

    void setAllAlbumsOrder(OrderOptions allAlbumsOrder) {
        this.allAlbumsOrder = allAlbumsOrder;
    }

    void setAllAlbumsSort(SortOptions allAlbumsSort) {
        this.allAlbumsSort = allAlbumsSort;
    }

    void setAllSongsOrder(OrderOptions allSongsOrder) {
        this.allSongsOrder = allSongsOrder;
    }

    void setAllSongsSort(SortOptions allSongsSort) {
        this.allSongsSort = allSongsSort;
    }

    void setArtistAlbumsOrder(OrderOptions artistAlbumsOrder) {
        this.artistAlbumsOrder = artistAlbumsOrder;
    }

    void setArtistAlbumsSort(SortOptions artistAlbumsSort) {
        this.artistAlbumsSort = artistAlbumsSort;
    }

    void setArtistSongsOrder(OrderOptions artistSongsOrder) {
        this.artistSongsOrder = artistSongsOrder;
    }

    void setComposersAlbumsSort(SortOptions composersAlbumsSort) {
        this.composersAlbumsSort = composersAlbumsSort;
    }

    void setComposersAlbumsOrder(OrderOptions composersAlbumsOrder) {
        this.composersAlbumsOrder = composersAlbumsOrder;
    }

    void setArtistSongsSort(SortOptions artistSongsSort) {
        this.artistSongsSort = artistSongsSort;
    }

    void setComposersSongsSort(SortOptions composersSongsSort) {
        this.composersSongsSort = composersSongsSort;
    }

    void setComposersSongsOrder(OrderOptions composersSongsOrder) {
        this.composersSongsOrder = composersSongsOrder;
    }

    void setGenresAlbumsOrder(OrderOptions genresAlbumsOrder) {
        this.genresAlbumsOrder = genresAlbumsOrder;
    }

    void setGenresAlbumsSort(SortOptions genresAlbumsSort) {
        this.genresAlbumsSort = genresAlbumsSort;
    }

    void setGenresSongsOrder(OrderOptions genresSongsOrder) {
        this.genresSongsOrder = genresSongsOrder;
    }

    void setGenresSongsSort(SortOptions genresSongsSort) {
        this.genresSongsSort = genresSongsSort;
    }
}
