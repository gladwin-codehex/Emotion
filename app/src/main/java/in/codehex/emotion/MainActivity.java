package in.codehex.emotion;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import in.codehex.emotion.db.DatabaseHandler;
import in.codehex.emotion.util.Const;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Button button;
    private CoordinatorLayout coordinatorLayout;
    private FloatingActionButton fab;
    private Intent intent;
    private DatabaseHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        handler = new DatabaseHandler(getApplicationContext());

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, SongsListActivity.class);
                startActivity(intent);
            }
        });

        button = (Button) findViewById(R.id.take_snap);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPlaylist()) {
                    Intent intent = new Intent(MainActivity.this, EmotionDetectionActivity.class);
                    startActivity(intent);
                } else Snackbar.make(coordinatorLayout,
                        "Add at least a song to each playlist", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private boolean checkPlaylist() {
        for (int i = 0; i < Const.EMOTIONS.length; i++) {
            int count;
            count = handler.getSongsCount(Const.EMOTIONS[i]);
            if (count < 1)
                return false;
        }
        return true;
    }
}