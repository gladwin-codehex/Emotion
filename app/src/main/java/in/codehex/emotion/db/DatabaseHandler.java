package in.codehex.emotion.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import in.codehex.emotion.model.SongItem;
import in.codehex.emotion.util.Const;

/**
 * Created by Bobby on 04-09-2015.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Playlist";
    private static final String KEY_ID = "id";
    private static final String KEY_ALBUM_ID = "album_id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_ALBUM = "album";
    private static final String KEY_ARTIST = "artist";
    private static final String KEY_DURATION = "duration";
    private static final String KEY_PATH = "path";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (int i = 0; i < Const.EMOTIONS.length; i++) {
            String CREATE_TABLE = "CREATE TABLE " + Const.EMOTIONS[i] + " ("
                    + KEY_ID + " INTEGER PRIMARY KEY," + KEY_ALBUM_ID + " INTEGER,"
                    + KEY_TITLE + " TEXT," + KEY_ALBUM + " TEXT," + KEY_ARTIST + " TEXT,"
                    + KEY_DURATION + " INTEGER," + KEY_PATH + " TEXT" + ")";
            db.execSQL(CREATE_TABLE);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = 0; i < Const.EMOTIONS.length; i++)
            db.execSQL("DROP TABLE IF EXISTS " + Const.EMOTIONS[i]);
        onCreate(db);
    }

    public void addToPlaylist(SongItem songItem, String TABLE) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ALBUM_ID, songItem.getAlbumId());
        values.put(KEY_ALBUM, songItem.getAlbum());
        values.put(KEY_ARTIST, songItem.getArtist());
        values.put(KEY_TITLE, songItem.getTitle());
        values.put(KEY_DURATION, songItem.getDuration());
        values.put(KEY_PATH, songItem.getPath());

        db.insert(TABLE, null, values);
        db.close();
    }

    public List<SongItem> getAllSongs(String mood) {
        List<SongItem> songs = new ArrayList<SongItem>();
        String selectQuery = "SELECT * FROM " + mood;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                long album_id = cursor.getLong(1);
                String title = cursor.getString(2);
                String album = cursor.getString(3);
                String artist = cursor.getString(4);
                long duration = cursor.getLong(5);
                String path = cursor.getString(6);
                songs.add(new SongItem(title,
                        artist, path, album, duration, album_id));
            } while (cursor.moveToNext());
            cursor.close();
            db.close();
        }
        return songs;
    }

    public void deleteSong(String mood, long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(mood, KEY_ALBUM_ID + "=" + id, null);
        db.close();
    }

    public int getSongsCount(String mood) {
        String countQuery = "SELECT * FROM " + mood;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count;
    }
}
