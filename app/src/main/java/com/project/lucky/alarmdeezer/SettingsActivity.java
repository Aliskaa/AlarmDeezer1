package com.project.lucky.alarmdeezer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import com.deezer.sdk.model.Permissions;
import com.deezer.sdk.network.connect.SessionStore;
import com.deezer.sdk.network.connect.event.DialogListener;

import static com.project.lucky.alarmdeezer.LoginActivity.PERMISSIONS;

/**
 * Created by lucky on 07/10/16.
 */

public class SettingsActivity extends BaseActivity {

    protected static final String[] PERMISSIONS = new String[]{
            Permissions.BASIC_ACCESS, Permissions.OFFLINE_ACCESS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            Toast.makeText(this, "Permission checking", Toast.LENGTH_SHORT).show();
            checkPermission();
        }

        findViewById(R.id.buttonVisualizerFFT).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                Intent intent;
                intent = new Intent(SettingsActivity.this, VisualizerActivity.class);
                intent.putExtra(VisualizerActivity.EXTRA_DISPLAY, VisualizerActivity.DISPLAY_FFT);
                startActivity(intent);
            }
        });

        findViewById(R.id.buttonVisualizerWaveform).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                Intent intent;
                intent = new Intent(SettingsActivity.this, VisualizerActivity.class);
                intent.putExtra(VisualizerActivity.EXTRA_DISPLAY,
                        VisualizerActivity.DISPLAY_WAVEFORM);
                startActivity(intent);
            }
        });

        findViewById(R.id.buttonEqualizer).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                Intent intent;
                intent = new Intent(SettingsActivity.this, EqualizerActivity.class);

                startActivity(intent);
            }
        });

        findViewById(R.id.buttonLogin).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                connectToDeezer();
            }
        });

        SessionStore sessionStore = new SessionStore();

        if (sessionStore.restore(mDeezerConnect, this)) {
            Toast.makeText(this, "Already logged in !", Toast.LENGTH_LONG).show();

        }

    }

    /**
     * Asks the SDK to display a log in dialog for the user
     */
    private void connectToDeezer() {
        mDeezerConnect.authorize(this, PERMISSIONS, mDeezerDialogListener);
    }

    private DialogListener mDeezerDialogListener = new DialogListener() {

        @Override
        public void onComplete(final Bundle values) {
            // store the current authentication info
            SessionStore sessionStore = new SessionStore();
            sessionStore.save(mDeezerConnect, SettingsActivity.this);

            // Launch the Home activity
            Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
            startActivity(intent);
        }

        @Override
        public void onException(final Exception exception) {
            Toast.makeText(SettingsActivity.this, R.string.deezer_error_during_login,
                    Toast.LENGTH_LONG).show();
        }


        @Override
        public void onCancel() {
            Toast.makeText(SettingsActivity.this, R.string.login_cancelled, Toast.LENGTH_LONG).show();
        }


    };

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {//Can add more as per requirement

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);

        } else {

        }
    }

}
