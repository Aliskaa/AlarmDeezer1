package com.project.lucky.alarmdeezer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.deezer.sdk.model.PlayableEntity;
import com.deezer.sdk.model.Track;
import com.deezer.sdk.network.connect.SessionStore;
import com.deezer.sdk.network.request.AsyncDeezerTask;
import com.deezer.sdk.network.request.DeezerRequest;
import com.deezer.sdk.network.request.DeezerRequestFactory;
import com.deezer.sdk.network.request.event.DeezerError;
import com.deezer.sdk.network.request.event.JsonRequestListener;
import com.deezer.sdk.player.TrackPlayer;
import com.deezer.sdk.player.event.PlayerWrapperListener;
import com.deezer.sdk.player.exception.TooManyPlayersExceptions;
import com.deezer.sdk.player.networkcheck.WifiAndMobileNetworkStateChecker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lucky on 10/10/16.
 */
public class UserTopTracksActivity extends PlayerActivity {

    /** The list of tracks of displayed by this activity. */
    private List<Track> mTracksList = new ArrayList<Track>();

    /** the tracks list adapter */
    private ArrayAdapter<Track> mTracksAdapter;

    //private TrackPlayer mTrackPlayer;

    private Track mTrack;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // restore existing deezer Connection
        new SessionStore().restore(mDeezerConnect, this);

        // Setup the UI
        setContentView(R.layout.tracklists_activity);
        setupTracksList();
        setPlayerVisible(false);
        //setupPlayerUI();

        findViewById(R.id.button_previous).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                Intent intent;
                //Bundle b = new Bundle();
                //b.putParcelable("song", mTrack);
                intent = new Intent(UserTopTracksActivity.this, MusicActivity.class);
                //intent.putExtras(b);
                startActivity(intent);
            }
        });

        //build the player
        //createPlayer();

        // fetch tracks list
        getUserTracks();
    }

    /**
     * Sets up the player UI (mostly remove unnecessary buttons)
     */
    private void setupPlayerUI() {
        // for now hide the player
        setPlayerVisible(false);

        // disable unnecesary buttons
        setButtonEnabled(mButtonPlayerSkipBackward, false);
        setButtonEnabled(mButtonPlayerSkipForward, false);
    }

    /**
     * Setup the List UI
     */
    private void setupTracksList() {
        mTracksAdapter = new ArrayAdapter<Track>(this,
                android.R.layout.simple_list_item_1, mTracksList) {

            @Override
            public View getView(final int position, final View convertView, final ViewGroup parent) {
                Track track = getItem(position);

                View view = convertView;
                if (view == null) {
                    LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = vi.inflate(android.R.layout.simple_list_item_1, null);
                }

                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setText(track.getTitle());

                return view;
            }
        };

        ListView listview = (ListView) findViewById(android.R.id.list);
        listview.setAdapter(mTracksAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                                    final int position, final long id) {
                //Track track = mTracksList.get(position);
                mTrack = mTracksList.get(position);
                Log.i("Artist TrackActivity", mTrack.getArtist().getName());
                Log.i("Artist TrackActivity", mTrack.getTitle());
                // Capture button clicks
                //mTrackPlayer.playTrack(track.getId());
                //setPlayerVisible(true);

                new AlertDialog.Builder(UserTopTracksActivity.this)
                    .setTitle("Choice")
                    .setMessage(mTrack.getArtist().getName() + " - " + mTrack.getTitle())
                    .setCancelable(false)
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent;
                            Bundle b = new Bundle();
                            b.putParcelable("song", mTrack);
                            intent = new Intent(UserTopTracksActivity.this, HomeActivity.class);
                            intent.putExtras(b);
                            startActivity(intent);
                        }
                    }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Whatever...
                        }
                    }).create().show();



            }
        });
    }
    /**
     * Creates the PlaylistPlayer
     */
    /*private void createPlayer() {
        try {
            mTrackPlayer = new TrackPlayer(getApplication(), mDeezerConnect,
                    new WifiAndMobileNetworkStateChecker());
            mTrackPlayer.addPlayerListener(this);
            setAttachedPlayer(mTrackPlayer);
        }
        catch (TooManyPlayersExceptions e) {
            handleError(e);
        }
        catch (DeezerError e) {
            handleError(e);
        }
    }*/

    /**
     * Search for all tracks splitted by genre
     */
    private void getUserTracks() {
        DeezerRequest request = DeezerRequestFactory.requestCurrentUserCharts();
        AsyncDeezerTask task = new AsyncDeezerTask(mDeezerConnect,
            new JsonRequestListener() {

                @SuppressWarnings("unchecked")
                @Override
                public void onResult(final Object result, final Object requestId) {

                    mTracksList.clear();

                    try {
                        mTracksList.addAll((List<Track>) result);
                    }
                    catch (ClassCastException e) {
                        handleError(e);
                    }

                    if (mTracksList.isEmpty()) {
                        Toast.makeText(UserTopTracksActivity.this, getResources()
                                .getString(R.string.no_results), Toast.LENGTH_LONG).show();
                    }

                    mTracksAdapter.notifyDataSetChanged();
                }

                @Override
                public void onUnparsedResult(final String response, final Object requestId) {
                    handleError(new DeezerError("Unparsed reponse"));
                }

                @Override
                public void onException(final Exception exception,
                                        final Object requestId) {
                    handleError(exception);
                }

            });
        task.execute(request);
    }



    //////////////////////////////////////////////////////////////////////////////////////
    // Player listener
    //////////////////////////////////////////////////////////////////////////////////////

    /*public void onPlayTrack(final Track track) {
        displayTrack(track);
    }

    public void onTrackEnded(final Track track) {
    }

    @Override
    public void onAllTracksEnded() {
    }

    @Override
    public void onPlayTrack(PlayableEntity playableEntity) {

    }

    @Override
    public void onTrackEnded(PlayableEntity playableEntity) {

    }

    @Override
    public void onRequestException(final Exception e, final Object requestId) {
        handleError(e);
    }
*/
}

