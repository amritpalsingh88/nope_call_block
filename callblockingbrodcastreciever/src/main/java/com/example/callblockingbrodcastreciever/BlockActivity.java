package com.example.callblockingbrodcastreciever;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockActivity extends AppCompatActivity {

    private SharedPreferences blockedNumberPrefs;

    public List<Intent> POWERMANAGER_INTENTS = Arrays.asList(
            new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.entry.FunctionActivity")).setData(android.net.Uri.parse("mobilemanager://function/entry/AutoStart"))
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block);

        startPowerSaverIntent(this);


        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ANSWER_PHONE_CALLS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALL_LOG, Manifest.permission.CALL_PHONE},
                        1001);
            }
        }

        blockedNumberPrefs=getSharedPreferences("NOPPE_CALL_BLOCKING_PREFFS", Context.MODE_PRIVATE);

        Toast.makeText(this, blockedNumberPrefs.getString("blocked_list", ""),  Toast.LENGTH_LONG).show();

    }







    public void callBlockScreen(View view) {

        PhoneCallReceiver receiver = new PhoneCallReceiver();

        receiver.registerReciever(BlockActivity.this);

        //startActivity(new Intent(this, BlockActivity.class));

    }




    public void startPowerSaverIntent(final Context context) {
        SharedPreferences settings = context.getSharedPreferences("ProtectedApps", Context.MODE_PRIVATE);
        boolean skipMessage = settings.getBoolean("skipProtectedAppCheck", false);
        if (!skipMessage) {
            final SharedPreferences.Editor editor = settings.edit();
            boolean foundCorrectIntent = false;
            for (final Intent intent : POWERMANAGER_INTENTS) {
                if (isCallable(context, intent)) {
                    foundCorrectIntent = true;
                    final AppCompatCheckBox dontShowAgain = new AppCompatCheckBox(context);
                    dontShowAgain.setText("Do not show again");
                    dontShowAgain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            editor.putBoolean("skipProtectedAppCheck", isChecked);
                            editor.apply();
                        }
                    });

                    final boolean finalFoundCorrectIntent = foundCorrectIntent;
                    new AlertDialog.Builder(context)
                            .setTitle(Build.MANUFACTURER + " Protected Apps")
                            .setMessage(String.format("%s requires to be enabled in 'Protected Apps' to function properly.%n", context.getString(com.example.callblockingbrodcastreciever.R.string.app_name)))
                            .setView(dontShowAgain)
                            .setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    if (finalFoundCorrectIntent) {
                                        editor.putBoolean("skipProtectedAppCheck", true);
                                        editor.apply();
                                    }
                                    try {
                                        if (Build.MANUFACTURER.toLowerCase().equals("oppo"))
                                            requestAutoStartPermission();
                                        else
                                            context.startActivity(intent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            })
                            .setNegativeButton(android.R.string.cancel, null)
                            .show();
                    break;
                }
            }

        }
    }

    private void requestAutoStartPermission() {
        //com.coloros.safecenter.permission.singlepage.PermissionSinglePageActivity     listpermissions
        //com.coloros.privacypermissionsentry.PermissionTopActivity                     privacypermissions
        // getPackageManager().getLaunchIntentForPackage("com.coloros.safecenter");
        if (Build.MANUFACTURER.equals("OPPO")) {
            try {
                startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.FakeActivity")));
            } catch (Exception e) {
                try {
                    startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startupapp.StartupAppListActivity")));
                } catch (Exception e1) {
                    try {
                        startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startupmanager.StartupAppListActivity")));
                    } catch (Exception e2) {
                        try {
                            startActivity(new Intent().setComponent(new ComponentName("com.coloros.safe", "com.coloros.safe.permission.startup.StartupAppListActivity")));
                        } catch (Exception e3) {
                            try {
                                startActivity(new Intent().setComponent(new ComponentName("com.coloros.safe", "com.coloros.safe.permission.startupapp.StartupAppListActivity")));
                            } catch (Exception e4) {
                                try {
                                    startActivity(new Intent().setComponent(new ComponentName("com.coloros.safe", "com.coloros.safe.permission.startupmanager.StartupAppListActivity")));
                                } catch (Exception e5) {
                                    try {
                                        startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startsettings")));
                                    } catch (Exception e6) {
                                        try {
                                            startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startupapp.startupmanager")));
                                        } catch (Exception e7) {
                                            try {
                                                startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startupmanager.startupActivity")));
                                            } catch (Exception e8) {
                                                try {
                                                    startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.startupapp.startupmanager")));
                                                } catch (Exception e9) {
                                                    try {
                                                        startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.privacypermissionsentry.PermissionTopActivity.Startupmanager")));
                                                    } catch (Exception e10) {
                                                        try {
                                                            startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.privacypermissionsentry.PermissionTopActivity")));
                                                        } catch (Exception e11) {
                                                            try {
                                                                startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.FakeActivity")));
                                                            } catch (Exception e12) {
                                                                e12.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    private boolean isCallable(Context context, Intent intent) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
}
