package com.example.eric.musicorganizer.mediaDataTypes;

public class SongBuilder {

    private Song tempSong;

    public SongBuilder() {
        this.tempSong = new Song();
    }

    public SongBuilder withId(final long id) {
        this.tempSong.setId(id);
        return this;
    }

    public SongBuilder withTitle(final String title) {
        this.tempSong.setTitle(title);
        return this;
    }

    public SongBuilder withAlbum(final String album) {
        this.tempSong.setAlbum(album);
        return this;
    }

    public SongBuilder withAlbumArt(final String albumArtPath) {
        this.tempSong.setAlbumArtPath(albumArtPath);
        return this;
    }

    public SongBuilder withArtists(final String artists) {
        this.tempSong.setArtists(artists);
        return this;
    }

    public SongBuilder withArtists(final String[] artists) {
        this.tempSong.setArtists(artists);
        return this;
    }

    public SongBuilder withAlbumArtist(final String albumArtist) {
        this.tempSong.setAlbumArtist(albumArtist);
        return this;
    }

    public SongBuilder withAlbumArtist(final String[] albumArtist) {
        this.tempSong.setAlbumArtist(albumArtist);
        return this;
    }

    public SongBuilder withComposer(final String composer) {
        this.tempSong.setComposer(composer);
        return this;
    }

    public SongBuilder withGenre(final String genre) {
        this.tempSong.setGenre(genre);
        return this;
    }

    public SongBuilder withDuration(final long duration) {
        this.tempSong.setDuration(duration);
        return this;
    }

    public SongBuilder withYear(final int year) {
        this.tempSong.setYear(year);
        return this;
    }

    public SongBuilder withTrackNumber(final int trackNumber) {
        this.tempSong.setTrackNumber(trackNumber);
        return this;
    }

    public SongBuilder withDiscNumber(final int discNumber) {
        this.tempSong.setDiscNumber(discNumber);
        return this;
    }

    public SongBuilder withTrackNumberAndDiscNumber(final int discAndTrackNumber) {
        this.tempSong.setTrackAndDiscNumber(discAndTrackNumber);
        return this;
    }

    public SongBuilder withTotalTrackNumber(final int totalTrackNumber) {
        this.tempSong.setTotalTrackNumber(totalTrackNumber);
        return this;
    }

    public SongBuilder withData(final String data) {
        this.tempSong.setData(data);
        return this;
    }

    public Song build() {
        Song song = new Song();
        song.setId(tempSong.getId());
        song.setTitle(tempSong.getTitle());
        song.setAlbum(tempSong.getTitle());
        song.setAlbumArtPath(tempSong.getAlbumArtPath());
        song.setArtists(tempSong.getArtist());
        song.setAlbumArtist(tempSong.getAlbumArtists());
        song.setComposer(tempSong.getComposer());
        song.setGenre(tempSong.getGenre());
        song.setDuration(tempSong.getDuration());
        song.setYear(tempSong.getYear());
        song.setTrackNumber(tempSong.getTrackNumber());
        song.setDiscNumber(tempSong.getDiscNumber());
        song.setTotalTrackNumber(tempSong.getTotalTrackNumber());
        song.setData(tempSong.getData());
        return song;
    }
}
