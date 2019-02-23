package com.example.eric.musicorganizer.mediaPlayer;

import com.example.eric.musicorganizer.mediaDataTypes.Song;

public interface MusicServices {

    /** Activated when song completed playing */
    interface SongEndedListener {
        void onSongEnded();
    }

    interface SongPausedListener {
        void onSongPaused();
    }

    /** Invoked when all there is not more songs to be played. */
    interface SongQueueEmptyListener {
        void onSongQueueEmpty();
    }

    /** Activated when song start playing. */
    interface SongStartedListener {
        void onSongStarted(Song song);
    }
    interface SongResumedListener {
        void onSongResumed();
    }
}
