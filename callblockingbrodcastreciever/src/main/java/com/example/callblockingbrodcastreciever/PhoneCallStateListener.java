package com.example.callblockingbrodcastreciever;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;


public class PhoneCallStateListener extends PhoneStateListener {

    private Context context;
    public PhoneCallStateListener(Context context){
        this.context = context;
    }


    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);

        switch (state) {

            case TelephonyManager.CALL_STATE_RINGING:

                //Toast.makeText(context, "Call from :"+incomingNumber, Toast.LENGTH_LONG).show();
                //String block_number = prefs.getString("block_number", null);
                String block_number = "5219";
                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                //Turn ON the mute
                TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
                try {
                    Class clazz = Class.forName(telephonyManager.getClass().getName());
                    Method method = clazz.getDeclaredMethod("getITelephony");
                    method.setAccessible(true);
                    //Checking incoming call number
                    System.out.println("Call "+block_number);
                    ITelephony telephonyService;
                    if (incomingNumber.contains(block_number)) {
                        audioManager.setStreamMute(AudioManager.STREAM_RING, true);
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
                            System.out.println(" in  " + block_number);
                            telephonyService.endCall();
                        }

                        Toast.makeText(context, "Blocked: "+ block_number , Toast.LENGTH_LONG).show();
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
    }}