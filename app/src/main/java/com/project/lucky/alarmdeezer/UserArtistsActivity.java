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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.deezer.sdk.model.AImageOwner;
import com.deezer.sdk.model.Artist;
import com.deezer.sdk.model.PlayableEntity;
import com.deezer.sdk.model.Track;
import com.deezer.sdk.network.connect.SessionStore;
import com.deezer.sdk.network.request.AsyncDeezerTask;
import com.deezer.sdk.network.request.DeezerRequest;
import com.deezer.sdk.network.request.DeezerRequestFactory;
import com.deezer.sdk.network.request.event.DeezerError;
import com.deezer.sdk.network.request.event.JsonRequestListener;
import com.deezer.sdk.player.ArtistRadioPlayer;
import com.deezer.sdk.player.event.RadioPlayerListener;
import com.deezer.sdk.player.exception.TooManyPlayersExceptions;
import com.deezer.sdk.player.networkcheck.WifiAndMobileNetworkStateChecker;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lucky on 10/10/16.
 */

public class UserArtistsActivity extends PlayerActivity implements RadioPlayerListener {

    /** The list of artists of displayed by this activity. */
    private List<Artist> mArtistsList = new ArrayList<Artist>();

    /** the Artists list adapter */
    private ArrayAdapter<Artist> mArtistsAdapter;

    private ArtistRadioPlayer mArtistPlayer;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // restore existing deezer Connection
        new SessionStore().restore(mDeezerConnect, this);

        // Setup the UI
        setContentView(R.layout.tracklists_activity);
        setupArtistsList();
        setPlayerVisible(false);
        //setupPlayerUI();

        findViewById(R.id.button_previous).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                Intent intent;
                intent = new Intent(UserArtistsActivity.this, MusicActivity.class);
                startActivity(intent);
            }
        });

        //build the player
        //createPlayer();

        // fetch artists list
        getUserArtists();
    }

    /**
     * Sets up the player UI (mostly remove unnecessary buttons)
     */
    private void setupPlayerUI() {
        // for now hide the player
        setPlayerVisible(false);

        // disable unnecesary buttons
        setButtonEnabled(mButtonPlayerSeekBackward, false);
        setButtonEnabled(mButtonPlayerSeekForward, false);
        setButtonEnabled(mButtonPlayerSkipBackward, false);
        setButtonEnabled(mButtonPlayerRepeat, false);
    }


    /**
     * Setup the List UI
     */
    private void setupArtistsList() {
        mArtistsAdapter = new ArrayAdapter<Artist>(this,
                R.layout.item_title_cover, mArtistsList) {

            @Override
            public View getView(final int position, final View convertView, final ViewGroup parent) {
                Artist artist = getItem(position);

                View view = convertView;
                if (view == null) {
                    LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = vi.inflate(R.layout.item_title_cover, null);
                }


                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setText(artist.getName());

                ImageView imageView = (ImageView) view.findViewById(android.R.id.icon);
                Picasso.with(UserArtistsActivity.this).load(artist.getImageUrl(AImageOwner.ImageSize.medium))
                        .into(imageView);


                return view;
            }
        };
        ListView listview = (ListView) findViewById(android.R.id.list);
        listview.setAdapter(mArtistsAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                                    final int position, final long id) {
                final Artist artist = mArtistsList.get(position);
                //mArtistPlayer.playArtistRadio(artist.getId());
                //setPlayerVisible(true);

                new AlertDialog.Builder(UserArtistsActivity.this)
                        .setTitle("Choice")
                        .setMessage("Album : " + artist.getName())
                        .setCancelable(false)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent;
                                Bundle b = new Bundle();
                                b.putParcelable("song", artist);
                                intent = new Intent(UserArtistsActivity.this, HomeActivity.class);
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
    private void createPlayer() {
        try {
            mArtistPlayer = new ArtistRadioPlayer(getApplication(), mDeezerConnect,
                    new WifiAndMobileNetworkStateChecker());
            mArtistPlayer.addPlayerListener(this);
            setAttachedPlayer(mArtistPlayer);
        }
        catch (TooManyPlayersExceptions e) {
            handleError(e);
        }
        catch (DeezerError e) {
            handleError(e);
        }
    }

    /**
     * Search for all artists splitted by genre
     */
    private void getUserArtists() {
        DeezerRequest request = DeezerRequestFactory.requestCurrentUserArtists();
        AsyncDeezerTask task = new AsyncDeezerTask(mDeezerConnect,
                new JsonRequestListener() {

                    @SuppressWarnings("unchecked")
                    @Override
                    public void onResult(final Object result, final Object requestId) {

                        mArtistsList.clear();

                        try {
                            mArtistsList.addAll((List<Artist>) result);
                        }
                        catch (ClassCastException e) {
                            handleError(e);
                        }

                        if (mArtistsList.isEmpty()) {
                            Toast.makeText(UserArtistsActivity.this, getResources()
                                    .getString(R.string.no_results), Toast.LENGTH_LONG).show();
                        }

                        mArtistsAdapter.notifyDataSetChanged();
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

    @Override
    protected void onSkipToNextTrack() {
        mArtistPlayer.skipToNextTrack();
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // Player listener
    //////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onTooManySkipsException() {
        Toast.makeText(this, R.string.deezer_too_many_skips,
                Toast.LENGTH_LONG).show();
    }

    public void onPlayTrack(final Track track) {
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
}

