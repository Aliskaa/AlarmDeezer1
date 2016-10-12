package com.project.lucky.alarmdeezer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.deezer.sdk.network.connect.SessionStore;

/**
 * Created by lucky on 07/10/16.
 */

public class OptionsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clock_activity);

        // Restore authentication
        new SessionStore().restore(mDeezerConnect, this);


        /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
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
        });*/

    }

    private static final int PLAYLISTS = 0;
    private static final int ALBUMS = 1;
    private static final int ARTISTS = 2;
    private static final int RADIOS = 3;
    private static final int TRACKS = 4;
    private static final int FLOW = 5;
    private static final int CUSTOM = 6;

    /**
     * Navigates to another activity depending on what the user clicked on
     *
     * @param selection
     */
    private void userNavigate(final int selection) {
        Intent intent = null;

        switch (selection) {
            case PLAYLISTS:
                //intent = new Intent(this, UserPlaylistsActivity.class);
                break;
            case ALBUMS:
                //intent = new Intent(this, UserAlbumsActivity.class);
                break;
            case ARTISTS:
                //intent = new Intent(this, UserArtistsActivity.class);
                break;
            case RADIOS:
                //intent = new Intent(this, UserRadiosActivity.class);
                break;
            case TRACKS:
                //intent = new Intent(this, UserTopTracksActivity.class);
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
