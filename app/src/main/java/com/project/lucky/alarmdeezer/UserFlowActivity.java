package com.project.lucky.alarmdeezer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.deezer.sdk.model.PlayableEntity;
import com.deezer.sdk.model.Track;
import com.deezer.sdk.model.User;
import com.deezer.sdk.network.connect.SessionStore;
import com.deezer.sdk.network.request.AsyncDeezerTask;
import com.deezer.sdk.network.request.DeezerRequest;
import com.deezer.sdk.network.request.DeezerRequestFactory;
import com.deezer.sdk.network.request.event.DeezerError;
import com.deezer.sdk.network.request.event.JsonRequestListener;
import com.deezer.sdk.player.RadioPlayer;
import com.deezer.sdk.player.event.RadioPlayerListener;
import com.deezer.sdk.player.exception.TooManyPlayersExceptions;
import com.deezer.sdk.player.networkcheck.WifiAndMobileNetworkStateChecker;

/**
 * Created by lucky on 10/10/16.
 */
public class UserFlowActivity extends PlayerActivity implements RadioPlayerListener {

    private RadioPlayer mRadioPlayer;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // restore existing deezer Connection
        new SessionStore().restore(mDeezerConnect, this);


        // setup UI
        setContentView(R.layout.flow_activity);
        setupPlayerUI();

        findViewById(R.id.button_previous).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                /*Intent intent;
                intent = new Intent(UserFlowActivity.this, MusicActivity.class);
                startActivity(intent);*/
                UserFlowActivity.this.finish();
            }
        });

        // build the player
        createPlayer();

        // get the current user id
        DeezerRequest request = DeezerRequestFactory.requestCurrentUser();
        AsyncDeezerTask task = new AsyncDeezerTask(mDeezerConnect, new JsonRequestListener() {

            @Override
            public void onResult(final Object result, final Object requestId) {
                if (result instanceof User) {
                    mRadioPlayer.playRadio(RadioPlayer.RadioType.USER, ((User) result).getId());
                    setPlayerVisible(true);
                } else {
                    handleError(new IllegalArgumentException());
                }
            }

            @Override
            public void onUnparsedResult(final String response, final Object requestId) {
                handleError(new DeezerError("Unparsed reponse"));
            }


            @Override
            public void onException(final Exception exception, final Object requestId) {
                handleError(exception);
            }
        });

        task.execute(request);

    }


    /**
     * Creates the Radio Player
     */
    private void createPlayer() {
        try {
            mRadioPlayer = new RadioPlayer(getApplication(), mDeezerConnect,
                    new WifiAndMobileNetworkStateChecker());
            mRadioPlayer.addPlayerListener(this);
            setAttachedPlayer(mRadioPlayer);
        }
        catch (DeezerError e) {
            handleError(e);
        }
        catch (TooManyPlayersExceptions e) {
            handleError(e);
        }
    }

    @Override
    protected void onSkipToNextTrack() {
        super.onSkipToNextTrack();

        mRadioPlayer.skipToNextTrack();
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

    //////////////////////////////////////////////////////////////////////////////////////
    // Radio Player Callbacks
    //////////////////////////////////////////////////////////////////////////////////////

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

    @Override
    public void onTooManySkipsException() {
        Toast.makeText(this, R.string.deezer_too_many_skips,
                Toast.LENGTH_LONG).show();
    }

}

