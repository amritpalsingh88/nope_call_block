package com.example.callblockingbrodcastreciever;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;

public class PhoneCallReceiver extends BroadcastReceiver {

    SharedPreferences prefs;

    @Override
    public void onReceive(Context context, Intent intent) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        PhoneCallStateListener customPhoneListener = new PhoneCallStateListener(context);

        telephony.listen(customPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        Log.e("onReceive: ", "recieved000000000000000000000000000000000000000000000000000000000000");


    }


    public void registerReciever(Context context) {

        IntentFilter filter = new IntentFilter();

        filter.addAction("android.intent.action.PHONE_STATE");
        //filter.addAction("android.intent.action.PHONE_STATE");
        context.registerReceiver(this, filter);


    }
}



