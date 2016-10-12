package com.project.lucky.alarmdeezer;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.deezer.sdk.model.AImageOwner;
import com.deezer.sdk.model.Album;
import com.deezer.sdk.model.Artist;
import com.deezer.sdk.model.Playlist;
import com.deezer.sdk.model.Track;
import com.deezer.sdk.network.connect.SessionStore;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.Calendar;

/**
 * Created by lucky on 07/10/16.
 */

public class HomeActivity extends BaseActivity {

    private enum Option {
        oClock,
        oMusic,
        oSettings,
    }

    private Track mTrack;
    private Playlist mPlaylist;
    private Album mAlbum;
    private Artist mArtist;
    private String mType;

    private Option mOption;

    private static DecimalFormat nf = new DecimalFormat("00");
    TextView textViewTime, textViewRepeat;


    private static int timeHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    private static int timeMinute = Calendar.getInstance().get(Calendar.MINUTE);

    AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        textViewTime = (TextView) findViewById(R.id.time);
        textViewRepeat = (TextView) findViewById(R.id.repeat);

        // Restore authentication
        new SessionStore().restore(mDeezerConnect, this);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent myIntent = new Intent(HomeActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(HomeActivity.this, 0, myIntent, 0);


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
                intent = new Intent(this, OptionsActivity.class);
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

    }

    public void onResume() {
        super.onResume();

        Bundle b = getIntent().getExtras();

        Log.i("Artist HomeActivity", "Track : "+String.valueOf(mTrack));

        if(b != null) {

            Object mObject = b.getParcelable("song");
            Log.i("Artist HomeActivity", "Object : "+String.valueOf(mObject));

            if(mObject != null) {
                setPlayer();
            }
        }

        timeHour = getIntent().getIntExtra("timeHour", timeHour);
        timeMinute = getIntent().getIntExtra("timeMinute", timeMinute);

        textViewTime.setText(nf.format(timeHour) + ":" + nf.format(timeMinute));

    }

    public void setPlayer() {
        Bundle b = getIntent().getExtras();
        Object mObject = b.getParcelable("song");

        if(mObject != null) {
            Log.i("Artist HomeActivity", String.valueOf(mObject.getClass()));
            mType = String.valueOf(mObject.getClass());

            switch (mType) {
                case "class com.deezer.sdk.model.Track":
                    mTrack = b.getParcelable("song");
                    ((TextView) findViewById(R.id.text_artist)).setText(mTrack.getArtist().getName());
                    ((TextView) findViewById(R.id.text_track)).setText(mTrack.getTitle());
                    Picasso.with(this).load(mTrack.getAlbum().getImageUrl(AImageOwner.ImageSize.medium))
                            .into((ImageView) findViewById(R.id.song_picture));
                    break;

                case "class com.deezer.sdk.model.Playlist":
                    mPlaylist = b.getParcelable("song");
                    ((TextView) findViewById(R.id.text_track)).setText(mPlaylist.getTitle());
                    Picasso.with(this).load(mPlaylist.getImageUrl(AImageOwner.ImageSize.medium))
                            .into((ImageView) findViewById(R.id.song_picture));
                    break;

                case "class com.deezer.sdk.model.Album":
                    mAlbum = b.getParcelable("song");
                    ((TextView) findViewById(R.id.text_track)).setText(mAlbum.getTitle());
                    Picasso.with(this).load(mAlbum.getImageUrl(AImageOwner.ImageSize.medium))
                            .into((ImageView) findViewById(R.id.song_picture));
                    break;

                case "class com.deezer.sdk.model.Artist":
                    mArtist = b.getParcelable("song");
                    ((TextView) findViewById(R.id.text_track)).setText(mArtist.getName());
                    Picasso.with(this).load(mArtist.getImageUrl(AImageOwner.ImageSize.medium))
                            .into((ImageView) findViewById(R.id.song_picture));
                    break;
            }
        }

        Log.i("Artist HomeActivity", "Bunble effacé !!!");
        b.clear();
    }
}
