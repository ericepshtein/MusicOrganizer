package com.example.eric.musicorganizer.musicManager;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.eric.musicorganizer.mediaDataTypes.Album;
import com.example.eric.musicorganizer.mediaDataTypes.AlbumBean;
import com.example.eric.musicorganizer.mediaDataTypes.Artist;
import com.example.eric.musicorganizer.mediaDataTypes.Composer;
import com.example.eric.musicorganizer.mediaDataTypes.Genre;
import com.example.eric.musicorganizer.mediaDataTypes.Song;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is a singleton class that stores all the data about the music in the storage.
 * This class has lists that represent all the music by songs, artists, app_logo, composers and genres.
 */
public class MusicManager {

    private static MusicManager musicManagerInstance;
    private static SongList songsList;
    private static AlbumList albumsList;
    private static ArtistList artistsList;
    private static ComposerList composersList;
    private static GenreList genresList;

    //private final String[] GENRE_PROJECTION = { MediaStore.Audio.Genres.NAME, MediaStore.Audio.Genres._ID };
    private final String UNKNOWN = "<unknown>";
    private static final String TITLE_TO_SORT_REGEX = "(^(([Tt][Hh][Ee])|([Aa] )))|[^0-9A-Za-zА-Яа-яא-ת\\u00C0-\\u00D6\\u00D8-\\u00f6\\u00f8-\\u00ff]";

    private MusicManager() {
        songsList = new SongList();
        albumsList = new AlbumList();
        artistsList = new ArtistList();
        composersList = new ComposerList();
        genresList = new GenreList();
    }

    // Return the only instance of MusicManager
    public static MusicManager getInstance() {
        if (musicManagerInstance == null) {
            musicManagerInstance = new MusicManager();
        }
        return musicManagerInstance;
    }

    private List<Song> getSongPropertiesList(ContentResolver contentResolver, List<AlbumBean> albumBeans) {
        Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        if (cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        final int ALBUM = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
        final int ALBUM_ID = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
        final int ARTIST = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        final int COMPOSER = cursor.getColumnIndex(MediaStore.Audio.Media.COMPOSER);
        final int DATA = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
        final int DURATION = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
        final int ID = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
        final int TITLE = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        final int TRACK = cursor.getColumnIndex(MediaStore.Audio.Media.TRACK);
        final int YEAR = cursor.getColumnIndex(MediaStore.Audio.Media.YEAR);

        List<Song> songProperties = new ArrayList<>();
        Map<Long, String> allGenreProperties = getAllGenreProperties(contentResolver);
        Song song;
        do {
            if (cursor.getString(ARTIST).equals(UNKNOWN)) {
                continue;
            }
            long songId = cursor.getLong(ID);
            int albumId = cursor.getInt(ALBUM_ID);
            AlbumBean albumBean = getAlbumBeanByAlbumId(albumBeans, albumId);
            if (albumBean == null) {
                song = new Song(songId, cursor.getString(TITLE), cursor.getString(ALBUM), null, cursor.getString(ARTIST), null, allGenreProperties.get(songId), cursor.getString(COMPOSER), cursor.getLong(DURATION), cursor.getInt(YEAR), cursor.getInt(TRACK), 0, cursor.getString(DATA));
            }
            else {
                song = new Song(songId, cursor.getString(TITLE), cursor.getString(ALBUM), albumBean.getAlbumArtPath(), cursor.getString(ARTIST), albumBean.getAlbumArtist(), allGenreProperties.get(songId), cursor.getString(COMPOSER), cursor.getLong(DURATION), cursor.getInt(YEAR), cursor.getInt(TRACK), albumBean.getNumberOfSongs(), cursor.getString(DATA));
            }

            /*song = new SongBuilder()
                    .withId(songId)
                    .withTitle(cursor.getString(TITLE))
                    .withAlbum(cursor.getString(ALBUM))
                    .withAlbumArt((albumBean != null) ? albumBean.getAlbumArtPath() : "")
                    .withArtists(cursor.getString(ARTIST))
                    .withAlbumArtist((albumBean != null) ? albumBean.getAlbumArtist() : "")
                    .withGenre(allGenreProperties.get(songId))
                    .withComposer(cursor.getString(COMPOSER))
                    .withDuration(cursor.getLong(DURATION))
                    .withYear(cursor.getInt(YEAR))
                    .withTrackNumberAndDiscNumber(cursor.getInt(TRACK))
                    .withTotalTrackNumber((albumBean != null) ? albumBean.getNumberOfSongs() : 0)
                    .withData(cursor.getString(DATA))
                    .build();*/

            songsList.addSong(song);
            Album album = addSongToAlbum(song);
            List<Artist> artists = addSongToArtists(song, album);
            addSongToComposer(song, album, artists);
            addSongToGenre(song.getGenres(), song, album, artists);
        } while (cursor.moveToNext());
        return songProperties;
    }

    private Album addSongToAlbum(Song song) {
        if (song.getAlbum() != null && !song.getAlbum().isEmpty()) {
            return albumsList.addSongToAlbum(song);
        }
        return null;
    }

    private List<Artist> addSongToArtists(Song song, Album album) {
        List<Artist> artists = new ArrayList<>();
        for (String artist : song.getArtist()) {
            artists.add(artistsList.addSongToArtists(artist, album, song));
        }
        return artists;
    }

    private void addSongToComposer(Song song, Album album, List<Artist> artists) {
        if (song.getComposer() != null && !song.getComposer().isEmpty()) {
            composersList.addSongToComposer(song);
        }
    }

    private void addSongToGenre(String[] genre, Song song, Album album, List<Artist> artists) {
        if (song != null && song.getGenre() != null) {
            genresList.addSongToGenres(genre, song, album, artists);
        }
    }

    private AlbumBean getAlbumBeanByAlbumId(List<AlbumBean> albumBeans, int albumId) {
        if (albumBeans != null && !albumBeans.isEmpty()) {
            for (AlbumBean current : albumBeans) {
                if (current.getId() == albumId) {
                    return current;
                }
            }
        }
        return null;
    }

    private Map<Long, String> getAllGenreProperties(ContentResolver contentResolver) {
        Map<Long, String> genres = getGenres(contentResolver);
        Map<Long, Long> members = getGenreMembers(contentResolver);
        Map<Long, String> genreProperties = new HashMap<>();        // member id => genre name

        for (Map.Entry<Long, Long> member : members.entrySet()) {
            try {
                genreProperties.put(member.getKey(), genres.get(member.getValue()));
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        return genreProperties;
    }

    private Map<Long, String> getGenres(ContentResolver contentResolver) {
        Cursor cursor = contentResolver.query(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, null, null, null, null);
        if (cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        final int GENRE_ID = cursor.getColumnIndex(MediaStore.Audio.Genres._ID);
        final int NAME = cursor.getColumnIndex(MediaStore.Audio.Genres.NAME);

        Map<Long, String> allGenresById = new HashMap<>();

        do {
            allGenresById.put(cursor.getLong(GENRE_ID), cursor.getString(NAME));
        } while (cursor.moveToNext());
        return allGenresById;
    }

    private Map<Long, Long> getGenreMembers(ContentResolver contentResolver) {
        Cursor cursor = contentResolver.query(Uri.parse("content://media/external/audio/genres/all/members"), null, null, null, null);
        if (cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        final int AUDIO_ID = cursor.getColumnIndex(MediaStore.Audio.Genres.Members.AUDIO_ID);
        final int GENRE_ID = cursor.getColumnIndex(MediaStore.Audio.Genres.Members.GENRE_ID);

        Map<Long, Long> allGenreAndSongs = new HashMap<>();
        do {
            allGenreAndSongs.put(cursor.getLong(AUDIO_ID), cursor.getLong(GENRE_ID));
        } while (cursor.moveToNext());
        return allGenreAndSongs;
    }

    private List<AlbumBean> getAlbumPropertiesList(ContentResolver contentResolver) {
        Cursor cursor = contentResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, null, null, null, null);

        if (cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        final int ID = cursor.getColumnIndex(MediaStore.Audio.Albums._ID);
        final int NUMBER_OF_SONGS = cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS);
        final int ARTIST = cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST);
        final int ALBUM_ART = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);

        List<AlbumBean> albumBeans = new ArrayList<>();
        do {
            if (cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ARTIST)).equals(UNKNOWN)) {
                continue;
            }
            final AlbumBean albumBean = new AlbumBean();
            albumBean.setId(cursor.getInt(ID));
            albumBean.setNumberOfSongs(cursor.getInt(NUMBER_OF_SONGS));
            albumBean.setAlbumArtist(cursor.getString(ARTIST));
            albumBean.setAlbumArtPath(cursor.getString(ALBUM_ART));
            albumBeans.add(albumBean);
        } while (cursor.moveToNext());
        return albumBeans;
    }

    public void fillMusicDataFromStorage(ContentResolver contentResolver) {
        songsList.clear();
        albumsList.clear();
        artistsList.clear();
        composersList.clear();
        genresList.clear();
        getSongPropertiesList(contentResolver, getAlbumPropertiesList(contentResolver));
    }

    public List<Song> getSongsList() {
        return songsList.getAllSongs();
    }

    public List<Album> getAlbumsList() {
        return albumsList.getAlbumList();
    }

    public List<Artist> getArtistsList() {
        return artistsList.getArtistList();
    }

    public List<Composer> getComposersList() {
        return composersList.getComposersList();
    }

    public List<Genre> getGenresList() {
        return genresList.getGenresList();
    }

    public static String titleToSort(String title) {
        String normalized = Normalizer.normalize(title, Normalizer.Form.NFD);
        title = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        title = title.replaceAll(TITLE_TO_SORT_REGEX, "").toLowerCase();
        char[] chars = title.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                chars[i] += 'z';
            }
        }
        return String.copyValueOf(chars);
    }

    public static String durationToString(int duration) {
        return durationToString((long) duration);
    }

    public static String durationToString(long duration) {
        long second = (duration / 1000) % 60;
        long minute = (duration / (1000 * 60)) % 60;
        long hour = (duration / (1000 * 60 * 60)) % 24;
        long day = (duration / (1000 * 60 * 60)) / 24;

        int dayLength = Long.toString(day).length();
        int hourLength = Long.toString(hour).length();
        int minuteLength = Long.toString(minute).length();

        if (day > 0) {
            return String.format("%" + dayLength + "d:%02d:%02d:%02d", day, hour, minute, second);
        }
        if (hour > 0) {
            return String.format("%" + hourLength + "d:%02d:%02d", hour, minute, second);
        }
        if (minuteLength > 0) {
            return String.format("%0" + minuteLength + "d:%02d", minute, second);
        }
        return String.format("%1d:%02d", minute, second);
    }

    public void sortSongsList() {
        songsList.sortSongList();
    }

    public void sortAlbumsList() {
        albumsList.sortAlbumList();
    }

    public void sortComposersList() {
        composersList.sortComposerList();
    }

    public void sortGenresList() {
        genresList.sortGenresList();
    }

    public Album getAlbumByNameAndArtist(String albumName, String albumArtist) {
        return albumsList.getAlbumByNameAndArtist(albumName, albumArtist);
    }

    public Artist getArtistByName(String name) {
        return artistsList.getArtistByName(name);
    }

    public Composer getComposerByName(String composer) {
        return composersList.getComposerByName(composer);
    }

    public Genre getGenreByName(String genre) {
        return genresList.getGenreByName(genre);
    }

    public List<Song> getAllMatchedSongs(String query) {
        return songsList.getAllMatchedSongs(query);
    }

    public List<Album> getAllMatchedAlbums(String query) {
        return albumsList.getAllMatchedAlbums(query);
    }

    public List<Artist> getAllMatchedArtists(String query) {
        return artistsList.getAllMatchedArtists(query);
    }

    public List<Genre> getAllMatchedGenres(String query) {
        return genresList.getAllMatchedGenres(query);
    }
}