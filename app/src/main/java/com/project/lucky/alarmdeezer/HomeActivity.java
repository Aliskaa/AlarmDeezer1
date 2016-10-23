package com.project.lucky.alarmdeezer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.deezer.sdk.model.AImageOwner;
import com.deezer.sdk.model.Album;
import com.deezer.sdk.model.Artist;
import com.deezer.sdk.model.Playlist;
import com.deezer.sdk.model.Track;
import com.deezer.sdk.network.connect.SessionStore;
import com.deezer.sdk.network.request.DeezerRequest;
import com.deezer.sdk.network.request.DeezerRequestFactory;
import com.deezer.sdk.network.request.event.DeezerError;
import com.deezer.sdk.network.request.event.JsonRequestListener;
import com.deezer.sdk.network.request.event.RequestListener;
import com.deezer.sdk.player.AlbumPlayer;
import com.deezer.sdk.player.ArtistRadioPlayer;
import com.deezer.sdk.player.PlaylistPlayer;
import com.deezer.sdk.player.PlayerWrapper.RepeatMode;
import com.deezer.sdk.player.TrackPlayer;
import com.deezer.sdk.player.exception.TooManyPlayersExceptions;
import com.deezer.sdk.player.networkcheck.WifiAndMobileNetworkStateChecker;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.Calendar;

/**
 * Created by lucky on 07/10/16.
 */

public class HomeActivity extends BaseActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Home Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    private enum Option {
        oClock,
        oMusic,
        oSettings,
    }

    private static Track mTrack;
    static TrackPlayer mTrackPlayer;

    private static Playlist mPlaylist;
    static PlaylistPlayer mPlaylistPlayer;

    private static Album mAlbum;
    static AlbumPlayer mAlbumPlayer;

    private static Artist mArtist;
    static ArtistRadioPlayer mArtistPlayer;

    private static String mType;

    private Option mOption;

    private static DecimalFormat nf = new DecimalFormat("00");
    TextView textViewTime, textViewRepeat, textViewArtist, textViewTrack;
    ImageView imageTrack;
    ToggleButton buttonStart;

    private static int timeHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    private static int timeMinute = Calendar.getInstance().get(Calendar.MINUTE);
    private static int snooze = 5;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        textViewTime = (TextView) findViewById(R.id.time);
        textViewRepeat = (TextView) findViewById(R.id.repeat);
        textViewArtist = (TextView) findViewById(R.id.text_artist);
        textViewTrack = (TextView) findViewById(R.id.text_track);
        imageTrack = (ImageView) findViewById(R.id.song_picture);
        buttonStart = (ToggleButton) findViewById(R.id.buttonSet);

        // Restore authentication
        new SessionStore().restore(mDeezerConnect, this);

        //createPlayer();

        // Restore song
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent myIntent = new Intent(HomeActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(HomeActivity.this, 0, myIntent, 0);

        OnClickListener listenerStart = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonStart.isChecked()) {
                    setAlarm();
                } else {
                    cancelAlarm();
                }

            }
        };

        buttonStart.setOnClickListener(listenerStart);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void searchAlbum(long albumID) {
        // the request listener

        RequestListener listener = new JsonRequestListener() {
            public void onResult(Object result, Object requestId) {
                mAlbum = (Album) result;

            }

            public void onUnparsedResult(String requestResponse, Object requestId) {
            } // 2

            public void onException(Exception e, Object requestId) {
            } // 3

        };

        //long artistId = 11413776;
        DeezerRequest request = DeezerRequestFactory.requestAlbum(albumID);
        mDeezerConnect.requestAsync(request, listener);

    }

    private void searchArtist(long artistId) {
        // the request listener
        RequestListener listener = new JsonRequestListener() {
            public void onResult(Object result, Object requestId) {
                mArtist = (Artist) result;
            }

            public void onUnparsedResult(String requestResponse, Object requestId) {
            } // 2

            public void onException(Exception e, Object requestId) {
            } // 3

        };

        //long artistId = 11413776;
        DeezerRequest request = DeezerRequestFactory.requestArtist(artistId);
        mDeezerConnect.requestAsync(request, listener);
    }

    private void searchPlaylist(long playlistId) {
        // the request listener
        RequestListener listener = new JsonRequestListener() {
            public void onResult(Object result, Object requestId) {
                mPlaylist = (Playlist) result;
                Log.i("Artiste request", mPlaylist.getTitle());
            }

            public void onUnparsedResult(String requestResponse, Object requestId) {
            } // 2

            public void onException(Exception e, Object requestId) {
            } // 3

        };

        //long artistId = 11413776;
        DeezerRequest request = DeezerRequestFactory.requestPlaylist(playlistId);
        mDeezerConnect.requestAsync(request, listener);
    }

    private void searchTrack(long trackId) {
        // the request listener
        RequestListener listener = new JsonRequestListener() {
            public void onResult(Object result, Object requestId) {
                mTrack = (Track) result;
                Log.i("Artiste request", mTrack.getTitle());
            }

            public void onUnparsedResult(String requestResponse, Object requestId) {
            } // 2

            public void onException(Exception e, Object requestId) {
            } // 3

        };

        //long artistId = 11413776;
        DeezerRequest request = DeezerRequestFactory.requestTrack(trackId);
        mDeezerConnect.requestAsync(request, listener);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        boolean res = true;
        Intent intent;

        switch (item.getItemId()) {
            case R.id.action_clock:
                intent = new Intent(this, ClockActivity.class);
                startActivity(intent);
                break;
            case R.id.action_music:
                intent = new Intent(this, MusicActivity.class);
                startActivity(intent);
                break;
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            default:
                res = super.onOptionsItemSelected(item);
                break;
        }

        return res;
    }

    public void onPause() {
        super.onPause();

    }

    public void onResume() {
        super.onResume();

        Bundle b = getIntent().getExtras();

        //Log.i("Artist HomeActivity", "Bundle : "+String.valueOf(b));

        if (b != null) {

            Object mObject = b.getParcelable("song");
            //Log.i("Artist HomeActivity", "Object : "+String.valueOf(mObject));

            if (mObject != null) {
                //Log.i("Artist mObject", "YES");
                getSongBundle(mObject);

                setPlayer();
            } else {
                getSongShared gSP = new getSongShared();
                gSP.execute();
            }
        } else {

            getSongShared gSP = new getSongShared();
            gSP.execute();
        }

        timeHour = getIntent().getIntExtra("timeHour", timeHour);
        timeMinute = getIntent().getIntExtra("timeMinute", timeMinute);
        snooze = getIntent().getIntExtra("snooze", snooze);

        textViewTime.setText(nf.format(timeHour) + ":" + nf.format(timeMinute));

    }

    private void getSongBundle(Object mObject) {
        mType = String.valueOf(mObject.getClass());

        //Log.i("Artiste Contains", mType);
        //Log.i("Artiste Contains", String.valueOf(mType.contains("Track")));
        //Log.i("Artiste Contains", String.valueOf(mType.contains("Album")));
        //Log.i("Artiste Contains", String.valueOf(mType.contains("Playlist")));
        //Log.i("Artiste Contains", String.valueOf(mType.contains("Artist")));

        if (mType.contains("Track")) {
            mTrack = (Track) mObject;
            mType = "Track";
            editor.putLong("track", mTrack.getId());
            editor.putString("type", "Track");

        } else if (mType.contains("Album")) {
            mAlbum = (Album) mObject;
            mType = "Album";
            editor.putLong("album", mAlbum.getId());
            editor.putString("type", "Album");

        } else if (mType.contains("Artist")) {
            mArtist = (Artist) mObject;
            mType = "Artist";
            editor.putLong("artist", mArtist.getId());
            editor.putString("type", "Artist");

        } else if (mType.contains("Playlist")) {
            mPlaylist = (Playlist) mObject;
            mType = "Playlist";
            editor.putLong("playlist", mPlaylist.getId());
            editor.putString("type", "Playlist");
        }

        editor.commit();
    }

    public void setPlayer() {

        switch (mType) {
            case "Track":
                Log.i("Artiste Title", mTrack.getTitle());
                textViewArtist.setText(mTrack.getArtist().getName());
                textViewTrack.setText(mTrack.getTitle());
                Picasso.with(this).load(mTrack.getAlbum().getImageUrl(AImageOwner.ImageSize.medium))
                        .into((ImageView) findViewById(R.id.song_picture));
                break;

            case "Playlist":
                Log.i("Artiste Playlist", mPlaylist.getTitle());
                textViewTrack.setText(mPlaylist.getTitle());
                Picasso.with(this).load(mPlaylist.getImageUrl(AImageOwner.ImageSize.medium))
                        .into(imageTrack);
                break;

            case "Album":
                Log.i("Artiste Album", mAlbum.getTitle());
                textViewTrack.setText(mAlbum.getTitle());
                Picasso.with(this).load(mAlbum.getImageUrl(AImageOwner.ImageSize.medium))
                        .into(imageTrack);
                break;

            case "Artist":
                Log.i("Artiste Artist", mArtist.getName());
                textViewTrack.setText(mArtist.getName());
                Picasso.with(this).load(mArtist.getImageUrl(AImageOwner.ImageSize.medium))
                        .into(imageTrack);
                break;
        }
    }

    private class getSongShared extends AsyncTask<Void, Integer, Void> {

        protected ProgressDialog Dialog;

        @Override
        protected void onPreExecute() {
            Dialog = new ProgressDialog(HomeActivity.this);
            Dialog.setMessage("Searching..");
            Dialog.setCancelable(false);
            Dialog.show();


        }

        @Override
        protected Void doInBackground(Void... params) {

            mType = preferences.getString("type", null);
            Log.i("Artist mType", mType);

            switch (mType) {
                case "Track":
                    searchTrack(preferences.getLong("track", 0)); //109563980
                    break;

                case "Album":
                    searchAlbum(preferences.getLong("album", 0)); //11413776
                    break;

                case "Playlist":
                    searchPlaylist(preferences.getLong("playlist", 0)); //1291623845
                    break;

                case "Artist":
                    searchArtist(preferences.getLong("artist", 0)); //293585
                    break;
            }

            //Tu mets un sleep de 4s pour ta progressbar
            SystemClock.sleep(2000);

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            //Ton code pour passer à l'activité suivante
            setPlayer();
            Dialog.dismiss();
            //Log.i("Artist Progress", mAlbum.getTitle());
            //setPlayer();
        }
    }

    private void setAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, timeHour);
        calendar.set(Calendar.MINUTE, timeMinute);
        createPlayer();
        //alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60 * snooze, pendingIntent);
        Log.i("Artist", "Alarme activée");
        Log.i("Artist", String.valueOf(timeHour) + String.valueOf(timeMinute));
    }

    private void cancelAlarm() {
        if (alarmManager != null) {
            //AlarmReceiver.setRingtone();
            alarmManager.cancel(pendingIntent);
            Log.i("Artist", "Alarme désactivée");
            if(mType == "Track"){
                mTrackPlayer.stop();
                mTrackPlayer.release();
            } else if (mType == "Album") {
                mAlbumPlayer.stop();
                mAlbumPlayer.release();
            } else if (mType == "Artist") {
                mArtistPlayer.stop();
                mArtistPlayer.release();
            } else if(mType == "Playlist") {
                mPlaylistPlayer.stop();
                mPlaylistPlayer.release();
            }

        }
    }

    /**
     * Creates the PlaylistPlayer
     */
    private void createPlayer() {
        try {
            if(mType == "Track"){
                mTrackPlayer = new TrackPlayer(getApplication(), mDeezerConnect,
                        new WifiAndMobileNetworkStateChecker());
            } else if (mType == "Album") {
                mAlbumPlayer = new AlbumPlayer(getApplication(), mDeezerConnect,
                        new WifiAndMobileNetworkStateChecker());
            } else if (mType == "Artist") {
                mArtistPlayer = new ArtistRadioPlayer(getApplication(), mDeezerConnect,
                        new WifiAndMobileNetworkStateChecker());
            } else if(mType == "Playlist") {
                mPlaylistPlayer = new PlaylistPlayer(getApplication(), mDeezerConnect,
                        new WifiAndMobileNetworkStateChecker());
            }
        }
        catch (TooManyPlayersExceptions e) {
            handleError(e);
        }
        catch (DeezerError e) {
            handleError(e);
        }
    }

    public static void play() {
        SecureRandom random = new SecureRandom();

        switch (mType){
            case "Track" :
                mTrackPlayer.playTrack(mTrack.getId());
                mTrackPlayer.setRepeatMode(RepeatMode.ALL);
                break;
            case "Album" :
                mAlbumPlayer.playAlbum(mAlbum.getId(), random.nextInt(mAlbum.getNbTracks()));
                mAlbumPlayer.setRepeatMode(RepeatMode.ALL);
                break;
            case "Artist" :
                mArtistPlayer.playArtistRadio(mArtist.getId());
                break;
            case "Playlist" :
                mPlaylistPlayer.playPlaylist(mPlaylist.getId());
                break;
        }
    }
}
