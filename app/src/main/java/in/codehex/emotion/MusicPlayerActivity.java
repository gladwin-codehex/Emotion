package in.codehex.emotion;

import android.content.ContentUris;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import in.codehex.emotion.db.DatabaseHandler;
import in.codehex.emotion.model.SongItem;

public class MusicPlayerActivity extends AppCompatActivity {
    private static int position = 0;
    private static int length;
    private Toolbar toolbar;
    private MediaPlayer mediaPlayer;
    private List<SongItem> songItem;
    private ImageView cover;
    private MediaMetadataRetriever metaRetriver;
    private DatabaseHandler handler;
    private ImageButton previous, play, next;
    private SeekBar songProgress;
    private String mood;
    private Handler seekHandler;
    private long album_id;
    private String title;
    private String album;
    private String artist;
    private long duration;
    private String path;
    private Uri mpId;
    private int songCount;
    private byte[] art;
    private Bitmap album_art;
    private String total_duration;
    private String current_duration = "0:00";
    private TextView totalDuration, currentDuration;
    private Runnable run = new Runnable() {

        @Override
        public void run() {
            seekUpdation();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        mood = extras.getString("mood");

        cover = (ImageView) findViewById(R.id.album_cover);
        previous = (ImageButton) findViewById(R.id.skip_previous);
        play = (ImageButton) findViewById(R.id.play);
        next = (ImageButton) findViewById(R.id.skip_next);

        currentDuration = (TextView) findViewById(R.id.current_duration);
        totalDuration = (TextView) findViewById(R.id.total_duration);

        songProgress = (SeekBar) findViewById(R.id.progress);

        handler = new DatabaseHandler(getApplicationContext());

        seekHandler = new Handler();

        songItem = handler.getAllSongs(mood);
        songCount = handler.getSongsCount(mood);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        if (position == 0) {
            previous.setClickable(false);
            previous.setEnabled(false);
        }

        startMusic();

        seekUpdation();

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
                play.setImageResource(R.drawable.ic_pause);
                mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (position < songCount - 1) {
                    position++;
                    startMusic();
                    length = 0;
                    mediaPlayer.seekTo(length);
                    songProgress.setProgress(0);
                } else {
                    length = 0;
                    songProgress.setProgress(0);
                    next.setClickable(false);
                    next.setEnabled(false);
                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position > 0) {
                    position--;
                    mediaPlayer.stop();
                    startMusic();
                }

                if (position < songCount - 1) {
                    next.setClickable(true);
                    next.setEnabled(true);
                }
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    play.setImageResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                    length = mediaPlayer.getCurrentPosition();
                } else {
                    play.setImageResource(R.drawable.ic_pause);
                    mediaPlayer.seekTo(length);
                    mediaPlayer.start();
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position < songCount - 1) {
                    position++;
                    startMusic();
                    previous.setClickable(true);
                    previous.setEnabled(true);
                }
            }
        });

        songProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

  /*  @Override
    public void onResume() {
        super.onResume();
        mediaPlayer.seekTo(length);
        mediaPlayer.start();
        seekUpdation();
    }

    @Override
    public void onPause() {
        super.onPause();
        mediaPlayer.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        seekHandler.removeCallbacks(run);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.reset();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        seekHandler.removeCallbacks(run);
    } */

    private void startMusic() {
        List<SongItem> songsList = songItem;
        SongItem songItem = songsList.get(position);

        album_id = songItem.getAlbumId();
        title = songItem.getTitle();
        album = songItem.getAlbum();
        artist = songItem.getArtist();
        duration = songItem.getDuration();
        path = songItem.getPath();

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        mpId = ContentUris.withAppendedId(uri, album_id);
        prepareMusic();
    }

    private void prepareMusic() {
        album_art = getAlbumArt(path);
        if (album_art != null)
            cover.setImageBitmap(album_art);
        else
            cover.setImageResource(R.drawable.music);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setSubtitle(artist);

        total_duration = milliSecondsToTimer(duration);
        totalDuration.setText(total_duration);

        try {
            mediaPlayer.setDataSource(getApplicationContext(), mpId);
            mediaPlayer.prepare();
            songProgress.setMax(mediaPlayer.getDuration());
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),
                    "Can't play song!", Toast.LENGTH_LONG).show();
        }
    }

    public void seekUpdation() {
        songProgress.setProgress(mediaPlayer.getCurrentPosition());
        current_duration = milliSecondsToTimer(mediaPlayer.getCurrentPosition());
        currentDuration.setText(current_duration);
        seekHandler.postDelayed(run, 200);
    }

    private Bitmap getAlbumArt(String path) {
        metaRetriver = new MediaMetadataRetriever();
        metaRetriver.setDataSource(path);
        Bitmap img = null;
        try {
            art = metaRetriver.getEmbeddedPicture();
            img = BitmapFactory.decodeByteArray(art, 0, art.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return img;
    }

    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        return finalTimerString;
    }
}
