package com.example.eric.musicorganizer.mediaPlayer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.example.eric.musicorganizer.mediaDataTypes.Album;
import com.example.eric.musicorganizer.mediaDataTypes.Artist;
import com.example.eric.musicorganizer.mediaDataTypes.Genre;
import com.example.eric.musicorganizer.musicManager.MusicManager;
import com.example.eric.musicorganizer.mediaDataTypes.Song;
import com.example.eric.musicorganizer.settings.Settings;

import java.util.ArrayList;
import java.util.List;

/****************************************** Music Service *******************************************
 *                                                                                                  *
 *  This service use to:                                                                            *
 *  - Handel the playing song (from inside and outside the app).                                    *
 *  - Provide the playing songs data to the different activities.                                   *
 *  - Activate the observer on the changing states of the playing song.                             *
 *  - Play the next song when the current song has been completed.                                  *
 *  - Play the next or the previous song by the users action.                                       *
 *                                                                                                  *
 * @author Eric Epshtein                                                                            *
 ***************************************************************************************************/
public class MusicService extends Service implements
        MusicServices, OnPreparedListener, OnErrorListener, OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

    /** Used to describe the action request sanded by the notification to the service */
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PREVIOUS = "action_previous";

    private int currentSongDuration;                            // The duration in milliseconds of the current song
    private Song currentSong;                                   // The current song playing
    private Album currentAlbum;                                 // The current album playing
    private Artist currentArtist;                               // The artist of the current song, not null when the song selected from the specific artist page.
    private Genre currentGenre;
    private List<Song> songsList;                               // This queue of the songs in the service.
    private int songIndex;                                      // This index of the current song in the songs queue.
    private Uri currentSongUri;                                 // The location of the current song in the local memory.
    private Source source;                                      // Specify from which activity the song selected.
    private AudioManager audioManager;                          // Instance of the system audio manager.
    private final IBinder musicBind = new MusicBinder();        // interface for clients that bind.
    private MediaPlayer musicPlayer;                            // Media Player instance to control the music playing.

    private List<SongEndedListener> songEndedListenerList = new ArrayList<>();              // All the SongEndedListener instances.
    private List<SongQueueEmptyListener> songQueueEmptyListenerList = new ArrayList<>();    // All the SongQueueEmptyListener instances.
    private List<SongPausedListener> songPausedListenersList = new ArrayList<>();           // All the SongPausedListener instances.
    private List<SongStartedListener> songStartedListenerList = new ArrayList<>();          // All the SongStartedListener instances.
    private List<SongResumedListener> songResumedListenerList = new ArrayList<>();          // All the SongResumedListener instances.

    public enum Source { SONGS, ARTISTS, ALBUMS, COMPOSERS, GENRES }

    /** Called when the service is being created. */
    @Override
    public void onCreate() {
        super.onCreate();

        musicPlayer = new MediaPlayer();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        initMusicService();
    }

    public void initMusicService() {
        musicPlayer = new MediaPlayer();
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        musicPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        musicPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        musicPlayer.setOnPreparedListener(this);                    // when the MediaPlayer instance is prepared
        musicPlayer.setOnCompletionListener(this);                  // when a song has completed playback
        musicPlayer.setOnErrorListener(this);                       // when an error is thrown
    }

    /** Let it continue running until it is stopped. */
    /*@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }*/

    /**
     * A client is binding to the service with bindService()
     * @param intent instance of the service intent to be unbind.
     * @return binder instance to connect the activity and this service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    /**
     * Called when all clients have unbound with unbindService()
     * This will execute when the user exits the app, at which point we will stop the service.
     * @param intent instance of the service intent to be unbind.
     * @return false
     */
    @Override
    public boolean onUnbind(Intent intent) {
        try {
            musicPlayer.stop();
            musicPlayer.release();
            return true;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Called when a client is binding to the service with bindService()
     * @param intent instance of the service intent to be unbind.
     */
    @Override
    public void onRebind(Intent intent) { }

    public Source getSource() {
        return this.source;
    }

    private void handleIntent(Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }
        switch (intent.getAction()) {
            case ACTION_PLAY: play();
                break;
            case ACTION_PAUSE: pause();
                break;
            case ACTION_PREVIOUS: playPrevSong();
                break;
            case ACTION_NEXT: playNextSong();
                break;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    public void addSongEndedListener(SongEndedListener listener) {
        songEndedListenerList.add(listener);
    }
    public void addSongQueueEmptyListener(SongQueueEmptyListener listener) {
        songQueueEmptyListenerList.add(listener);
    }
    public void addSongPausedListener(SongPausedListener listener) {
        songPausedListenersList.add(listener);
    }
    public void addSongStartedListener(SongStartedListener listener) {
        songStartedListenerList.add(listener);
    }
    public void addSongResumedListener(SongResumedListener listener) {
        songResumedListenerList.add(listener);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.reset();
        if (musicPlayer.getCurrentPosition() >= 0) {
            playNextSong();
            for (SongEndedListener current : songEndedListenerList) {
                current.onSongEnded();
            }
        }
    }

    // Check this changes: Check if there is a song, if so, play it. Else, play next song.
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        final int USER_CHANGE_SONG = -38;

        mp.reset();
        if (currentSong == null || currentSongUri == null) {
            onCompletion(mp);
        }
        if (what == USER_CHANGE_SONG) {
            playSong();
            return true;
        }
        mp.pause();
        Log.e("MusicService::onError","Error no. " + what + " occurred (" + extra + ")");
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        audioManager.requestAudioFocus(this, 1, android.media.AudioManager.AUDIOFOCUS_GAIN);
        currentSongDuration = mp.getDuration();
        for (SongStartedListener current : songStartedListenerList) {
            current.onSongStarted(currentSong);
        }
    }

    public void playSong() {
        if (currentSong == null || currentSongUri == null) {
            onDestroy();
        }
        if (musicPlayer == null) {
            musicPlayer = new MediaPlayer();
        }
        try {
            musicPlayer.reset(); // resetting the MediaPlayer since we will also use this code when the user is playing subsequent songs
            musicPlayer.setDataSource(this, currentSongUri);
            musicPlayer.prepareAsync();
            musicPlayer.setLooping(false);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                onAudioFocusGain();
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                onAudioFocusLoss();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                onAudioFocusLossTransient();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                onAudioFocusLossTransientCanDuck();
                break;
            default:
                Toast.makeText(this, "focusChange unknown (" + focusChange + ")", Toast.LENGTH_LONG).show();
        }
    }

    /** Used to indicate a gain of audio focus, or a request of audio focus, of unknown duration.*/
    private void onAudioFocusGain() {
        if (Settings.getInstance().isResumeMusicPlaying()) {
            musicPlayer.start(); // Phone Call ended!
            for (SongResumedListener current : songResumedListenerList) {
                current.onSongResumed();
            }
        }
    }

    /** Used to indicate a loss of audio focus of unknown duration. */
    private void onAudioFocusLoss() {
        // on song change
        /*musicPlayer.pause();
        for (SongPausedListener current : songPausedListenersList) {
            current.onSongPaused(); // Song changing!!!
        }*/
    }

    /** Used to indicate a transient loss of audio focus. */
    private void onAudioFocusLossTransient() {
        musicPlayer.pause();
        for (SongPausedListener current : songPausedListenersList) {
            current.onSongPaused(); // Phone call comes!
        }
    }

    /** Used to indicate a transient loss of audio focus where the loser of the audio focus can lower its output volume if it wants to continue playing (also referred to as "ducking"), as the new focus owner doesn't require others to be silent. */
    private void onAudioFocusLossTransientCanDuck() {
        Toast.makeText(this, "onAudioFocusLossTransientCanDuck", Toast.LENGTH_LONG).show();
    }


    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public int getLeftDuration() {
        try {
            return currentSongDuration - musicPlayer.getCurrentPosition();
        } catch (NullPointerException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int getSongsListSize() {
        return (songsList != null) ? songsList.size() : 0;
    }

    public int getSongsIndex() {
        return (songsList != null && currentSong != null) ? songsList.indexOf(currentSong) + 1: 0;
    }

    public void playSelectedSong(Song selectedSong, Source source) {
        playSelectedSong(selectedSong, null, null, null, source);
    }

    public void playSelectedSong(Song selectedSong, Album currentAlbum, Source source) {
        playSelectedSong(selectedSong, currentAlbum, null, null, source);
    }

    public void playSelectedSong(Song selectedSong, Artist currentArtist, Source source) {
        playSelectedSong(selectedSong, null, currentArtist, null, source);
    }

    public void playSelectedSong(Song selectedSong, Genre currentGenre, Source source) {
        playSelectedSong(selectedSong, null, null, currentGenre, source);
    }

    public void playSelectedSong(Song selectedSong, Album currentAlbum, Artist currentArtist, Genre currentGenre, Source source) {
        if (selectedSong == null || source == null) {
            return;
        }
        Uri selectedSongUri = Uri.parse(selectedSong.getData());
        if (selectedSongUri == null) {
            return;
        }
        if ((currentSong == null || !currentSong.equals(selectedSong)) || (this.source == null || !this.source.equals(source)))
        {
            this.currentSong = selectedSong;
            this.currentAlbum = currentAlbum;
            this.currentArtist = currentArtist;
            this.currentGenre = currentGenre;
            this.currentSongUri = selectedSongUri;
            this.source = source;
            this.songsList = initSongsList();
            initSongIndex();
            playSong();
        }
    }

    private void initSongIndex() {
        if (currentSong != null && songsList != null) {
            songIndex = songsList.indexOf(currentSong);
        }
    }

    public void pause() {
        try {
            musicPlayer.pause();
            for (SongPausedListener current : songPausedListenersList) {
                current.onSongPaused();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        try {
            musicPlayer.start();
            for (SongResumedListener current : songResumedListenerList) {
                current.onSongResumed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            musicPlayer.start();
            for (SongResumedListener current : songResumedListenerList) {
                current.onSongResumed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getCurrentPosition() {
        return musicPlayer.getCurrentPosition();
    }

    public boolean isSongPlayingNow() {
        try {
            return musicPlayer != null && musicPlayer.isPlaying();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private List<Song> initSongsList() {
        if (source == null) {
            return null;
        }
        switch (source) {
            case SONGS:     return initSongsListBySongs();
            case ARTISTS:   return initSongsListByArtist();
            case ALBUMS:    return initSongsListByAlbum();
            case COMPOSERS: return initSongsListByComposer();
            case GENRES:    return initSongsListByGenre();
            default:        return null;
        }
    }

    private List<Song> initSongsListBySongs() {
        return MusicManager.getInstance().getSongsList();
    }

    private List<Song> initSongsListByAlbum() {
        if (currentAlbum == null) {
            this.currentAlbum = MusicManager.getInstance().getAlbumByNameAndArtist(currentSong.getAlbum(), currentSong.getAlbumArtistString());
        }
        return MusicManager.getInstance().getAlbumByNameAndArtist(currentSong.getAlbum(), currentSong.getAlbumArtistString()).getAllSongs();
    }
    private List<Song> initSongsListByArtist() {
        return (currentArtist == null) ? MusicManager.getInstance().getArtistByName(currentSong.getAlbumArtistString()).getAllSongs() : currentArtist.getAllSongs();
    }

    private List<Song> initSongsListByComposer() {
        return MusicManager.getInstance().getComposerByName(currentSong.getComposer()).getAllSongs();
    }

    private List<Song> initSongsListByGenre() {
        return (currentGenre == null) ? MusicManager.getInstance().getGenreByName(currentSong.getGenre()).getAllSongs() : currentGenre.getAllSongs();
    }

    public boolean playPrevSong() {
        try {
            songIndex--;
            if (songsList == null || songsList.isEmpty() || songIndex < 0) {
               onSongQueueEmpty();
               return false;
            }
            currentSong = songsList.get(songIndex);
            currentSongUri = Uri.parse(currentSong.getData());
        } catch (Exception e) {
            onSongQueueEmpty();
            return false;
        }
        if (currentSong != null && currentSongUri != null) {
            playSong();
            return true;
        }
        onSongQueueEmpty();
        return false;
    }

    public boolean playNextSong() {
        try {
            songIndex++;
            if (songsList == null || songsList.isEmpty() || songIndex < 0 || songIndex >= songsList.size()) {
                onSongQueueEmpty();
                return false;
            }
            currentSong = songsList.get(songIndex);
            currentSongUri = Uri.parse(currentSong.getData());
        } catch (Exception e) {
            onSongQueueEmpty();
            return false;
        }
        if (currentSong != null && currentSongUri != null) {
            playSong();
            return true;
        }
        onSongQueueEmpty();
        return false;
    }

    public void onSongQueueEmpty() {
        if (musicPlayer != null) {
            musicPlayer.stop();
            musicPlayer.release();
        }
        currentSong = null;
        currentArtist = null;
        currentAlbum = null;
        currentSongUri = null;
        for (SongQueueEmptyListener current : songQueueEmptyListenerList) {
            current.onSongQueueEmpty();
        }
    }

    public int getMaxVolume() {
        return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    public int getVolumeProgress() {
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public void setStreamVolume(int progress) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
    }

    public void seekTo(int progress) {
        if (progress <= currentSongDuration) {
            musicPlayer.seekTo(progress);
        }
    }

    /** Called when The service is no longer used and is being destroyed */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (musicPlayer != null) {
            musicPlayer.release();
        }
        stopForeground(true);
    }
}
