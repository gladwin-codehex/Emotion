package in.codehex.emotion;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.codehex.emotion.db.DatabaseHandler;
import in.codehex.emotion.model.SongItem;
import in.codehex.emotion.util.Const;
import in.codehex.emotion.util.DividerItemDecoration;

public class SurprisePlaylistFragment extends Fragment {

    private static final String mood = Const.EMOTIONS[4];
    protected View mView;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private List<SongItem> songsItem;
    private RecyclerViewAdapter adapter;
    private DatabaseHandler handler;
    private ItemTouchHelper itemTouchHelper;
    private ItemTouchHelper.SimpleCallback simpleItemTouchCallback;

    public SurprisePlaylistFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_surprise_playlist, container, false);
        this.mView = view;

        linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());

        handler = new DatabaseHandler(getActivity().getApplicationContext());

        songsItem = handler.getAllSongs(mood);

        if (handler.getSongsCount(mood) <= 0)
            Snackbar.make(view, "Go back to add songs to each playlist",
                    Snackbar.LENGTH_INDEFINITE).show();

        recyclerView = (RecyclerView) view.findViewById(R.id.playlist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerViewAdapter(getActivity().getApplicationContext(), songsItem);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(adapter);

        simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                long id = songsItem.get(position).getAlbumId();
                swipeToDelete(position, id);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    Paint paint = new Paint();
                    Bitmap bitmap;

                    if (dX > 0) {
                        paint.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.primary_light));
                        bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_delete);
                        float height = (itemView.getHeight() / 2) - (bitmap.getHeight() / 2);
                        c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom(), paint);
                        c.drawBitmap(bitmap, 24f, (float) itemView.getTop() + height, null);
                    } else {
                        paint.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.primary_light));
                        bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_delete);
                        float height = (itemView.getHeight() / 2) - (bitmap.getHeight() / 2);
                        float bitmapWidth = bitmap.getWidth();
                        c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom(), paint);
                        c.drawBitmap(bitmap, ((float) itemView.getRight() - bitmapWidth) - 24f, (float) itemView.getTop() + height, null);
                    }
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }
        };
        itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return view;
    }

    private void swipeToDelete(final int position, final long id) {
        songsItem.remove(position);
        adapter.notifyItemRemoved(position);
        Snackbar.make(mView, "Song is removed from playlist",
                Snackbar.LENGTH_LONG).setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                switch (event) {
                    case Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE:
                        handler.deleteSong(mood, id);
                        break;
                    case Snackbar.Callback.DISMISS_EVENT_SWIPE:
                        handler.deleteSong(mood, id);
                        break;
                    case Snackbar.Callback.DISMISS_EVENT_TIMEOUT:
                        handler.deleteSong(mood, id);
                        break;
                }
            }
        }).setAction("undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songsItem.add(position, handler.getAllSongs(mood).get(position));
                adapter.notifyItemInserted(position);
            }
        }).show();
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder {
        public TextView playlistTitle, playlistArtist;

        public RecyclerViewHolder(View view) {
            super(view);
            playlistTitle = (TextView) view.findViewById(R.id.playlist_title);
            playlistArtist = (TextView) view.findViewById(R.id.playlist_artist);
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
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist, parent, false);
            RecyclerViewHolder holder = new RecyclerViewHolder(layoutView);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerViewHolder holder, int position) {
            SongItem songItem = songsList.get(position);
            holder.playlistTitle.setText(songItem.getTitle());
            holder.playlistArtist.setText(songItem.getArtist());
        }

        @Override
        public int getItemCount() {
            return this.songsList.size();
        }
    }
}
