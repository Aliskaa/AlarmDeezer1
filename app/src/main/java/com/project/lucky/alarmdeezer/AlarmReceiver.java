package com.project.lucky.alarmdeezer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by lucky on 17/10/16.
 */

public class AlarmReceiver extends BroadcastReceiver {

    private static Ringtone ringtone;

    @Override
    public void onReceive(final Context context, Intent intent) {

        //Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        //ringtone = RingtoneManager.getRingtone(context, uri);

        //ringtone.play();

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            HomeActivity.play();
        }


    }





}
