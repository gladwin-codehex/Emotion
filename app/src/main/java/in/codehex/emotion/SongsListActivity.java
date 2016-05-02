package in.codehex.emotion;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import in.codehex.emotion.db.DatabaseHandler;
import in.codehex.emotion.model.SongItem;
import in.codehex.emotion.util.Const;

public class SongsListActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private GridLayoutManager gridLayoutManager;
    private List<SongItem> songsItem;
    private RecyclerViewAdapter adapter;
    private DatabaseHandler db;
    private String mood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs_list);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SongsListActivity.this, PlaylistActivity.class);
                startActivity(intent);
            }
        });

        songsItem = new ArrayList<>();
        songsItem = getAllSongs();

        db = new DatabaseHandler(getApplicationContext());

        gridLayoutManager = new GridLayoutManager(SongsListActivity.this, 2);
        recyclerView = (RecyclerView) findViewById(R.id.songs_list);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new RecyclerViewAdapter(SongsListActivity.this, songsItem);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private List<SongItem> getAllSongs() {
        List<SongItem> songs = new ArrayList<SongItem>();
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null,
                MediaStore.Audio.Media.IS_MUSIC + " =1", null, null);
        if (cursor.moveToFirst()) {
            do {
                String fileColumn = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String artistColumn = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String titleColumn = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String albumColumn = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                long durationColumn = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                long idColumn = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                songs.add(new SongItem(titleColumn, R.drawable.music, artistColumn, fileColumn,
                        albumColumn, durationColumn, idColumn));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return songs;
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView songTitle;
        public ImageView albumCover;

        public RecyclerViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            songTitle = (TextView) view.findViewById(R.id.song_title);
            albumCover = (ImageView) view.findViewById(R.id.album_cover);
        }

        @Override
        public void onClick(View view) {
            mood = Const.EMOTIONS[0];
            int position = recyclerView.getChildAdapterPosition(view);
            final SongItem songItem = songsItem.get(position);

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(SongsListActivity.this);
            builder.setTitle("Add to playlist");
            builder.setSingleChoiceItems(Const.EMOTIONS, 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mood = Const.EMOTIONS[which];
                }
            });
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    db.addToPlaylist(songItem, mood);
                    Toast.makeText(getApplicationContext(),
                            "Song added to " + mood + " playlist", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancel", null);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
        private List<SongItem> songsList;
        private Context context;

        public RecyclerViewAdapter(Context context, List<SongItem> songsList) {
            this.songsList = songsList;
            this.context = context;
        }

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int view) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.songs_list, null);
            RecyclerViewHolder holder = new RecyclerViewHolder(layoutView);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerViewHolder holders, int position) {
            SongItem songItem = songsList.get(position);
            holders.songTitle.setText(songItem.getTitle());
            holders.albumCover.setImageResource(songItem.getAlbumArt());
        }

        @Override
        public int getItemCount() {
            return this.songsList.size();
        }
    }
}
