package com.example.callblockingbrodcastreciever;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Method;
import java.util.ArrayList;


public class PhoneCallStateListener extends PhoneStateListener {

    private Context context;
    private SharedPreferences prefs;
    private SharedPreferences blockedNumberPrefs;

    public PhoneCallStateListener(Context context){
        this.context = context;
    }

    private boolean isFirst=true;

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        //AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        blockedNumberPrefs=context.getSharedPreferences("NOPPE_CALL_BLOCKING_PREFFS", Context.MODE_PRIVATE);


        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                Log.e( "onCallStateChanged: ", "CALL_STATE_IDLE");
                //audioManager.setStreamVolume(AudioManager.STREAM_RING, prefs.getInt("current_volume", 0), 0);
                prefs.edit().putBoolean("is_first", true).apply();
                break;

            case TelephonyManager.CALL_STATE_RINGING:
                Log.e( "onCallStateChanged: ", "CALL_STATE_RINGING");
                //Toast.makeText(context, "Call from :"+incomingNumber, Toast.LENGTH_LONG).show();
                //String block_number = prefs.getString("block_number", null);

                if(prefs.getBoolean("is_first", true)){
                    //prefs.edit().putInt("current_volume", audioManager.getStreamVolume(AudioManager.STREAM_RING)).apply();
                    prefs.edit().putBoolean("is_first", false).apply();
                }
                //Turn ON the mute
                TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
                try {
                    Class clazz = Class.forName(telephonyManager.getClass().getName());
                    Method method = clazz.getDeclaredMethod("getITelephony");
                    method.setAccessible(true);
                    //Checking incoming call number
                    ITelephony telephonyService;
                    if (containsBlockedNumber(context, incomingNumber)) {
                        //audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                        Log.e( "contains: ", true+"");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            TelecomManager tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);

                            if (tm != null) {
                                try {
                                    tm.silenceRinger();
                                }
                                catch (Exception e)
                                {}
                                boolean success = tm.endCall();
                                // success == true if call was terminated.
                                Log.e( "blocked: ", success+"");
                            }
                        }
                        else {
                            telephonyService = (ITelephony) method.invoke(telephonyManager);
                            telephonyService.silenceRinger();
                            telephonyService.endCall();
                        }

                        Toast.makeText(context, "Blocked: "+ incomingNumber , Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                    Log.e( "onCallStateChanged: ",  e.toString());
                }
                //Turn OFF the mute
                //audioManager.setStreamMute(AudioManager.STREAM_RING, false);
                break;
            case PhoneStateListener.LISTEN_CALL_STATE:
        }
        super.onCallStateChanged(state, incomingNumber);
    }

    private boolean containsBlockedNumber(Context context, String number) {
        ArrayList<String> list=new Gson().fromJson( blockedNumberPrefs.getString("blocked_list", ""), new TypeToken<ArrayList<String>>(){}.getType());
        if(list.contains(number))
            return true;
        else
            return false;
    }

}