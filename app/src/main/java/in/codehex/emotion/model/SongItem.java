package in.codehex.emotion.model;

/**
 * Created by Bobby on 04-09-2015.
 */
public class SongItem {
    private String title;
    private int albumArt;
    private String artist;
    private String path;
    private String album;
    private long duration;
    private long albumId;

    public SongItem(String title, int albumArt, String artist, String path,
                    String album, long duration, long albumId) {
        this.title = title;
        this.albumArt = albumArt;
        this.artist = artist;
        this.path = path;
        this.album = album;
        this.duration = duration;
        this.albumId = albumId;
    }

    public SongItem(String title, String artist, String path,
                    String album, long duration, long albumId) {
        this.title = title;
        this.artist = artist;
        this.path = path;
        this.album = album;
        this.duration = duration;
        this.albumId = albumId;
    }

    public int getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(int albumArt) {
        this.albumArt = albumArt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }
}
