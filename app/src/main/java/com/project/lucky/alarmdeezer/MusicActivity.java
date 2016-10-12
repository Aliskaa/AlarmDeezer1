package com.project.lucky.alarmdeezer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.deezer.sdk.model.AImageOwner;
import com.deezer.sdk.model.Track;
import com.deezer.sdk.model.User;
import com.deezer.sdk.network.connect.SessionStore;
import com.deezer.sdk.network.request.AsyncDeezerTask;
import com.deezer.sdk.network.request.DeezerRequest;
import com.deezer.sdk.network.request.DeezerRequestFactory;
import com.deezer.sdk.network.request.event.DeezerError;
import com.deezer.sdk.network.request.event.JsonRequestListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lucky on 07/10/16.
 */

public class MusicActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_activity);

        // Restore authentication
        new SessionStore().restore(mDeezerConnect, this);

        findViewById(R.id.button_previous).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                Intent intent;
                Bundle b = new Bundle();
                b.putParcelable("song", mTrack);
                intent = new Intent(MusicActivity.this, HomeActivity.class);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(
                R.array.user_data));
        ListView list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                                    final int position, final long id) {
                userNavigate(position);
            }
        });

        fetchUserInfo();

    }

    private void displayUserInfo(final User user) {
        ((TextView) findViewById(R.id.name)).setText(user.getLastName());
        ((TextView) findViewById(R.id.first_name)).setText(user.getFirstName());


        setTitle(getString(R.string.activity_home, user.getName()));

        Picasso.with(this).load(user.getImageUrl(AImageOwner.ImageSize.medium))
                .into((ImageView) findViewById(R.id.user_picture));
    }

    private void fetchUserInfo() {
        DeezerRequest request = DeezerRequestFactory.requestCurrentUser();
        AsyncDeezerTask task = new AsyncDeezerTask(mDeezerConnect, new JsonRequestListener() {

            @Override
            public void onResult(final Object result, final Object requestId) {
                if (result instanceof User) {
                    displayUserInfo((User) result);
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

    private static final int PLAYLISTS = 0;
    private static final int ALBUMS = 1;
    private static final int ARTISTS = 2;
    private static final int TRACKS = 3;
    private static final int RADIOS = 4;
    private static final int FLOW = 5;
    private static final int CUSTOM = 6;

    private Track mTrack;

    /**
     * Navigates to another activity depending on what the user clicked on
     *
     * @param selection
     */
    private void userNavigate(final int selection) {
        Intent intent = null;

        switch (selection) {
            case PLAYLISTS:
                intent = new Intent(this, UserPlaylistsActivity.class);
                break;
            case ALBUMS:
                intent = new Intent(this, UserAlbumsActivity.class);
                break;
            case ARTISTS:
                intent = new Intent(this, UserArtistsActivity.class);
                break;
            case RADIOS:
                //intent = new Intent(this, UserRadiosActivity.class);
                break;
            case TRACKS:
                intent = new Intent(this, UserTopTracksActivity.class);
                break;
            case FLOW:
                //intent = new Intent(this, UserFlowActivity.class);
                break;
            case CUSTOM:
                //intent = new Intent(this, UserCustomTrackListActivity.class);
                break;
        }

        if (intent != null) {
            startActivity(intent);
        }
    }
}
