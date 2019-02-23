package com.example.eric.musicorganizer.mediaPlayer;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Action;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.eric.musicorganizer.R;
import com.example.eric.musicorganizer.mediaDataTypes.Song;
import com.example.eric.musicorganizer.musicManager.MusicManager;

/*************************************** Player Notification: ***************************************
 *                                                                                                  *
 * This activity displays and controls the UI of music playing on the MusicService.                 *
 * This activity contains:                                                                          *
 * - Connection to MusicService.                                                                    *
 * - Provide content to the app notification.                                                       *
 * - Provide the player UI.                                                                         *
 * - Control the music playing on the MusicService.                                                 *
 *                                                                                                  *
 * @author eric.epshtein                                                                            *
 ***************************************************************************************************/
public class PlayerPage extends Activity implements ServiceConnection, AudioManager.OnAudioFocusChangeListener {

    private Intent playIntent;
    private MusicService musicService;          // the connect between the music service and the PlayerPage activity
    private boolean musicBound = false;         // connection to music service status

    // Visual elements in PlayerPage:
    private Button playButton;                  // play/pause the song
    private SeekBar positionBar;                // controls the position of the song
    private SeekBar volumeBar;                  // controls and displays the volume
    private TextView elapsedTimeLabel;          // time passed from the beginning of the song
    private TextView remainingTimeLabel;        // time left to the end of the song
    private boolean remainingDisplay;
    private String songTrackLayoutDisplay;
    private Song song;

    private boolean isVolumeBarDragged;
    private boolean isPlaying = false;          // playing status
    private long totalSongDuration;             // the total duration of the current song
    private long lastTimePlayPrevBtnClick = 0;

    // Threads:
    private Thread timeLabelsThread;            // set the display of the time labels in the activity
    private Thread positionBarThread;           // set the action of the position and the volume bars
    private Thread volumeBarThread;             // set the action of the position and the volume bars
    private Thread positionBarOnTouchThreadInstance;    // set the action of the time labels while dragging the position bar

    // Notification:
    private Notification notification;
    private NotificationManager notificationManager;
    public final String CHANNEL_NAME = "channel1";
    public final String CHANNEL_ID = "1";

    private final String NEXT = "⏭";
    private final String PREV = "⏮";
    private final String PLAY = "▶";
    private final String PAUSE = "❙❙";

    /**
     * We are going to play the music in the Service class, but control it from the Activity class,
     * where the application's user interface operates. To accomplish this, we will have to bind to
     * the Service class. The above instance variables represent the Service class and Intent,
     * as well as a flag to keep track of whether the Activity class is bound to the Service class
     * or not.
     */
    private ServiceConnection musicConnection = this;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
        musicService = binder.getService();
        musicService.addSongEndedListener(this::stopThreads);
        musicService.addSongQueueEmptyListener(this::onSongQueueEmpty);
        musicService.addSongStartedListener((this::onSongStarted));
        musicService.addSongPausedListener(this::onSongPaused);
        musicService.addSongResumedListener(this::onSongResumed);
        musicBound = true;
        onSongStartedByUser();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        if (musicService != null) {
            musicService = null;
        }
        musicBound = false;
    }

    private void onSongStartedByUser() {
        if (musicService.getCurrentSong() != null) {
            this.song = musicService.getCurrentSong();
            changeSongContext();              // Activated because of onSongStarted not activated when the user pressed on a song
            if (!musicService.isSongPlayingNow()) {
                setPlayDisplay();
            }
            else {
                setPauseDisplay();
            }
        }
        else {
            onBackPressed();
        }
    }

    private void onSongStarted(Song song) {
        this.song = song;
        changeSongContext();
        setPauseDisplay();
    }

    private void onSongQueueEmpty() {
        stopThreads();
        finishPlayerPageActivity();
    }

    private void onSongPaused() {
        setPlayDisplay();
    }

    private void onSongResumed() {
        setPauseDisplay();
    }

    private void finishPlayerPageActivity() {
        stopThreads();
        finish();
    }

    /** Load the display settings from activity_player_page layout */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_page);

        this.remainingDisplay = true;
        this.lastTimePlayPrevBtnClick = System.currentTimeMillis();
        this.isVolumeBarDragged = false;
    }

    /** Bind the music service with the activity and change the song context if music service is already bind */
    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null || musicService == null || !musicBound) {
            playIntent = new Intent(getApplicationContext(), MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
        }
    }

    /**
     * Stop the running threads, activate the visual elements on the screen and update the current song data
     */
    private void changeSongContext() {
        //stopThreads();    // make problem when user leave PlayerPage playing song and try to play another song
        setSongData();
        initPlayerDisplay();
        startThreads();
    }

    private void setSongData() {
        if (song == null) {
            return;
        }
        try {
            this.totalSongDuration = song.getDuration();
            this.songTrackLayoutDisplay = musicService.getSongsIndex() + " of " + musicService.getSongsListSize();
            setSongTitle();
            setAlbumArt();
        } catch (Exception e) {
            finishPlayerPageActivity();
        }
    }

    private void initPlayerDisplay() {
        if (musicService == null) {
            onDestroy();
        }
        try {
            setButtonsAndTimeLabels();      // Load general data of display settings
            setPositionBar();               // Time bar
            setVolumeBar();                 // Volume Bar
            setSongTrackLayout();
        } catch (NullPointerException | IllegalStateException e) {
            e.printStackTrace();
            finishPlayerPageActivity();
        }
    }

    /**
     * When the connection to the bound Service instance is made, we pass the song list.
     * We will also be able to interact with the Service instance in order to control playback later.
     */

    private void setButtonsAndTimeLabels() {
        playButton = findViewById(R.id.playBtn);
        elapsedTimeLabel = findViewById(R.id.elapsedTimeLabel);
        remainingTimeLabel = findViewById(R.id.remainingTimeLabel);
        if (!remainingDisplay && remainingTimeLabel != null) {
            remainingTimeLabel.setText(MusicManager.durationToString(totalSongDuration));
        }
        if (isPlaying) {
            setPauseDisplay();
        }
        else {
            setPlayDisplay();
        }
        isPlaying = true;
    }

    private void setSongTitle() {
        if (song == null) {
            return;
        }
        TextView songTitle = findViewById(R.id.song_title);
        TextView albumTitle = findViewById(R.id.album_title);
        TextView artistTitle = findViewById(R.id.artist_title);

        songTitle.setText(song.getTitle());
        songTitle.setSelected(true);
        albumTitle.setText(song.getAlbum());
        albumTitle.setSelected(true);
        artistTitle.setText(song.getArtistsString(song.getArtist()));
        artistTitle.setSelected(true);
    }

    private void setAlbumArt() {
        if (song == null) {
            return;
        }
        ImageView albumArtView = findViewById(R.id.album_art);
        Drawable albumArt = Drawable.createFromPath(song.getAlbumArtPath());
        if (albumArt == null || albumArt.getMinimumWidth() <= 0 || albumArt.getMinimumHeight() <= 0) {
            albumArt = ContextCompat.getDrawable(this, R.drawable.image);
        }
        albumArtView.setImageDrawable(albumArt);
        albumArtView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
    }

    /** controls the position bar */
    private void setPositionBar() {
        positionBar = findViewById(R.id.positionBar);
        if (musicService == null) {
            finishActivity(-1);
        }
        positionBar.setProgress(musicService.getCurrentPosition());
        positionBar.setMax((int)totalSongDuration);
        positionBar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        positionBar.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        positionBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    musicService.seekTo(progress);
                    positionBar.setProgress(progress);
                }
            }

            /** User start drag the position bar */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopThreads();
                if (musicService != null) {
                    //musicService.pause(); - make problems
                    if (positionBarOnTouchThreadInstance == null || !positionBarOnTouchThreadInstance.isAlive()) {
                        positionBarOnTouchThreadInstance = new Thread(this::positionBarOnTouchThread);
                        positionBarOnTouchThreadInstance.start();
                    }
                }
            }

            void positionBarOnTouchThread() {
                while (musicService != null) {
                    elapsedTimeLabel.setText(MusicManager.durationToString(musicService.getCurrentPosition()));
                    if (remainingDisplay) {
                        updateReamingDisplay();
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException | IllegalThreadStateException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }

            /** User finish drag the position bar */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (musicService != null) {
                    if (positionBarOnTouchThreadInstance != null && positionBarOnTouchThreadInstance.isAlive()) {
                        positionBarOnTouchThreadInstance.interrupt();
                        positionBarOnTouchThreadInstance = null;
                    }
                    if (isPlaying) {
                        musicService.play();
                        setPauseDisplay();
                    }
                }
                startThreads();
            }
        });
        isPlaying = true;
    }

    /** controls the volume of the song */
    private void setVolumeBar() {
        volumeBar = findViewById(R.id.volumeBar);
        volumeBar.setMax(musicService.getMaxVolume());
        volumeBar.setProgress(musicService.getVolumeProgress());
        volumeBar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        volumeBar.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                musicService.setStreamVolume(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isVolumeBarDragged = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isVolumeBarDragged = false;
            }
        });
    }

    private void setSongTrackLayout() {
        TextView songTrackLayoutView = findViewById(R.id.song_track_layout);
        if (songTrackLayoutView != null && musicService != null) {
            songTrackLayoutView.setText(songTrackLayoutDisplay);
        }
    }

    public void playBtnClick(View view) {
        if (!musicBound || musicService == null) {
            onDestroy();
        }
        if (isPlaying && musicService.isSongPlayingNow() && musicService.getCurrentSong() != null) {
            isPlaying = false;
            musicService.pause();
            setPlayDisplay();
        }
        else {
            isPlaying = true;
            musicService.start();
            setPauseDisplay();
        }
    }

    private void setPauseDisplay() {
        playButton.setBackgroundResource(R.drawable.pause);
        sendNotification(generateAction(R.drawable.pause_notification, PAUSE, MusicService.ACTION_PAUSE));
    }

    private void setPlayDisplay() {
        playButton.setBackgroundResource(R.drawable.play);
        sendNotification(generateAction(R.drawable.play_notification, PLAY, MusicService.ACTION_PLAY));
    }

    public void changeRemainingDisplay(View view) {
        this.remainingDisplay = !this.remainingDisplay;
        if (!this.remainingDisplay) {
            remainingTimeLabel.setText(MusicManager.durationToString(this.totalSongDuration));
        }
    }

    public void playNextBtnClick(View view) {
        try {
            onPlayNextSelected();
        } catch (NullPointerException | IllegalStateException e) {
            e.printStackTrace();
            finishPlayerPageActivity();
        }
    }

    private void onPlayNextSelected() {
        stopThreads();
        if (musicService == null) {
            finishActivity(0);
        }
        musicService.playNextSong();
        if (musicService == null || musicService.getCurrentSong() == null) {
            onDestroy();
        }
        changeSongContext();       // Activated because of onSongStarted not activated when the user pressed on a song
        if (!musicService.isSongPlayingNow()) {
            initPlayerDisplay();
        }
        startThreads();
    }

    public void playPrevBtnClick(View view) {
        try {
            onPlayPrevSelected();
        } catch (NullPointerException | IllegalStateException e) {
            e.printStackTrace();
            finishPlayerPageActivity();
        }
    }

    private void onPlayPrevSelected() {
        final int TIMEOUT = 1250;

        if (musicService == null) {
            finishActivity(0);
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTimePlayPrevBtnClick > TIMEOUT) {
            musicService.playSong();
        } else {
            musicService.playPrevSong();
        }
        lastTimePlayPrevBtnClick = currentTime;
        musicService.playSong();
        if (musicService == null || musicService.getCurrentSong() == null) {
            onDestroy();
        }
        changeSongContext();       // Activated because of onSongStarted not activated when the user pressed on a song
        if (musicService == null) {
            finishActivity(0);
        }
        if (!musicService.isSongPlayingNow()) {
            initPlayerDisplay();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            unbindService();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            super.onDestroy();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            super.onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unbindService() {
        if (musicBound && musicService != null && !musicService.isSongPlayingNow() && musicConnection != null) {
            unbindService(musicConnection);
            musicBound = false;
        }
    }

    // Control threads:

    /** Start all the non-activated threads */
    private void startThreads() {
        startTimeLabelsThread();
        startPositionBarThread();
        startVolumeBarThread();
        if ((timeLabelsThread == null || !timeLabelsThread.isAlive()) && (positionBarThread == null || !positionBarThread.isAlive()) && (volumeBarThread == null || !volumeBarThread.isAlive())) {
            stopThreads();
            finishActivity(0);
        }
    }

    private void startTimeLabelsThread() {
        if (musicService != null && (timeLabelsThread == null || !timeLabelsThread.isAlive())) {
            timeLabelsThread = new Thread(this::timeLabelsThread);
            timeLabelsThread.start();
        }
    }

    private void startPositionBarThread() {
        if (musicService != null && (positionBarThread == null || !positionBarThread.isAlive())) {
            positionBarThread = new Thread(this::positionThread);
            positionBarThread.start();
        }
    }

    private void startVolumeBarThread() {
        if (musicService != null && (volumeBarThread == null || !volumeBarThread.isAlive())) {
            volumeBarThread = new Thread(this::volumeThread);
            volumeBarThread.start();
        }
    }

    private void timeLabelsThread() {
        final int TIMEOUT = 1000;
        while (musicService != null) {
            try {
                if (musicService != null) {
                    elapsedTimeLabel.setText(MusicManager.durationToString(musicService.getCurrentPosition()));
                    if (remainingDisplay) {
                        updateReamingDisplay();
                    }
                }
                Thread.sleep(TIMEOUT);               // the time between each time label update
            } catch (Exception e) {
                musicService.pause();
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private synchronized void updateReamingDisplay() {
        try {
            remainingTimeLabel.setText(String.format("%s-", MusicManager.durationToString(musicService.getLeftDuration())));
        } catch (IllegalMonitorStateException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void positionThread() {
        final int TIMEOUT = 100;
        while (musicService != null) {
            try {
                positionBar.setProgress(musicService.getCurrentPosition());
                Thread.sleep(TIMEOUT);              // the time between each position bar update
            } catch (Exception e) {
                musicService.pause();
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private void volumeThread() {
        final int TIMEOUT = 10;
        while (musicService != null) {
            if (!isVolumeBarDragged) {
                try {
                    volumeBar.setProgress(musicService.getVolumeProgress());
                    Thread.sleep(TIMEOUT);

                } catch (Exception e) {
                    musicService.pause();
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    /** Stop all activated threads */
    private void stopThreads() {
        stopTimeLabelsThread();
        stopVolumeBarThread();
        stopPositionBarThread();
    }
    public void stopTimeLabelsThread() {
        if (musicService != null && timeLabelsThread != null && timeLabelsThread.isAlive()) {
            timeLabelsThread.interrupt();
            timeLabelsThread = null;
        }
    }

    private void stopVolumeBarThread() {
        if (musicService != null && volumeBarThread != null && volumeBarThread.isAlive()) {
            volumeBarThread.interrupt();
            volumeBarThread = null; }
    }

    private void stopPositionBarThread() {
        if (musicService != null && positionBarThread != null && positionBarThread.isAlive()) {
            positionBarThread.interrupt();
            positionBarThread = null;
        }
    }

    public void onAlbumArtClicked(View view) {
        if (musicService != null) {
            TextView songTrackLayout = findViewById(R.id.song_track_layout);
            if (songTrackLayout.getVisibility() == View.GONE) {
                songTrackLayout.setText(songTrackLayoutDisplay);
                songTrackLayout.setVisibility(View.VISIBLE);
            }
            else {
                songTrackLayout.setVisibility(View.GONE);
            }
        }
    }

    /** Activates when the audio focus on the device has changed.
     * @param focusChange => New audio focus status
     */
    @Override
    public void onAudioFocusChange(int focusChange) {
        if (musicService != null) {
            try {
                musicService.pause();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setPauseDisplay();
    }

    /************************************ Player Notification: **************************************
     *                                                                                              *
     * Should be Activated by the OS from outside the application IU.                               *
     * Notification management includes:                                                            *
     * - Create new notification when music start playing.                                          *
     * - Update notification when song content is change.                                           *
     * - Control the music service from the notification UI.                                        *
     ***********************************************************************************************/

    public void sendNotification(Action action) {
        try {
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            setNotificationProperties(action);
            createNotificationChannel();
            notificationManager.notify(1, notification);  // Hide the notification after its selected
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    private void setNotificationProperties(Action action) {
        notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_logo)
                .setLargeIcon(BitmapFactory.decodeFile((song.getAlbumArtPath())))
                .setCategory(NotificationCompat.EXTRA_MEDIA_SESSION)
                .setContentTitle(song.getTitle())
                .setContentText(song.getAlbum() + " - " + song.getAlbumArtistString())
                .addAction(generateAction(R.drawable.backward_notification, PREV, MusicService.ACTION_PREVIOUS))
                .addAction(action)  // can be play or pause
                .addAction(generateAction(R.drawable.forward_notification, NEXT, MusicService.ACTION_NEXT))
                .setSound(null)
                .setShowWhen(false)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setChannelId(CHANNEL_ID)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setPublicVersion(notification)
                .build();
    }

    private Action generateAction(int icon, String title, String intentAction) {
        Intent intent = new Intent(getApplicationContext(), MusicService.class);
        intent.setAction(intentAction);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        return new Action.Builder(icon, title, pendingIntent).build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("no sound");
            channel.setSound(null,null);
            channel.enableLights(false);
            channel.enableVibration(false);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

