package com.project.lucky.alarmdeezer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.deezer.sdk.network.connect.DeezerConnect;
import com.deezer.sdk.network.connect.SessionStore;

/**
 * Created by lucky on 07/10/16.
 */

public class BaseActivity extends Activity {

    /** DeezerConnect object used for auhtentification or request. */
    protected DeezerConnect mDeezerConnect = null;

    /** Sample app Deezer appId. */
    public static final String SAMPLE_APP_ID = "210602";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDeezerConnect = new DeezerConnect(this, SAMPLE_APP_ID);
    }

    /*@Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        if (mDeezerConnect.isSessionValid()) {
            new MenuInflater(this).inflate(R.menu.logout, menu);
        }
        return true;
    }*/


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            // disconnect
            disconnectFromDeezer();

            // launch login screen
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
            startActivity(intent);

            // clear the current activity
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private final void disconnectFromDeezer() {
        // if deezerConnect is still valid, clear all auth info
        if (mDeezerConnect != null) {
            mDeezerConnect.logout(this);
        }

        // also clear the session store
        new SessionStore().clear(this);
    }

    /**
     * Handle errors by displaying a toast and logging.
     *
     * @param exception
     *            the exception that occured while contacting Deezer services.
     */
    protected void handleError(final Exception exception) {
        String message = exception.getMessage();
        if (TextUtils.isEmpty(message)) {
            message = exception.getClass().getName();
        }

        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        ((TextView) toast.getView().findViewById(android.R.id.message)).setTextColor(Color.RED);
        toast.show();

        Log.e("BaseActivity", "Exception occured " + exception.getClass().getName(), exception);
    }
}
