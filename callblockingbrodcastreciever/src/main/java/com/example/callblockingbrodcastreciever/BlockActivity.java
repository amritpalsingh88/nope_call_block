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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Arrays;
import java.util.List;

public class BlockActivity extends AppCompatActivity {

    private SharedPreferences blockedNumberPrefs;

    AlertDialog permissionDialog, autoStartDialog, alertDialog;

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
        testingClicks();
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            showPermissionRequiredPopup();
        }
        else
            showAlert("Already enabled!", "Call blocking already enabled on your phone.");
    }

    private void showPermissionRequiredPopup() {
        LayoutInflater li = LayoutInflater.from(this);
        View view = li.inflate(R.layout.permissions_popup, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(view);

        TextView tvCancel = view.findViewById(R.id.tvCancel);
        TextView tvOk = view.findViewById(R.id.tvOk);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permissionDialog.dismiss();
                finish();
            }
        });
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permissionDialog.dismiss();
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(BlockActivity.this, Manifest.permission.ANSWER_PHONE_CALLS)
                        || ActivityCompat.shouldShowRequestPermissionRationale(BlockActivity.this, Manifest.permission.READ_PHONE_STATE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(BlockActivity.this, Manifest.permission.CALL_PHONE)) {

                    showAlert("Permissions denied!", "You have already denied the permissions, You have to allow them manually by going" +
                            " to the app settings.");


                } else {
                    ActivityCompat.requestPermissions(BlockActivity.this,
                            new String[]{Manifest.permission.ANSWER_PHONE_CALLS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALL_LOG, Manifest.permission.CALL_PHONE},
                            1001);
                }
            }
        });

        permissionDialog = alertDialogBuilder.create();
        permissionDialog.setCancelable(false);
        permissionDialog.show();

    }

    private void showAlert(String title, String message) {

        LayoutInflater li = LayoutInflater.from(this);
        View view = li.inflate(R.layout.simple_popup, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(view);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvMessage = view.findViewById(R.id.tvMessage);
        TextView tvOk = view.findViewById(R.id.tvOk);

        tvTitle.setText(title);
        tvMessage.setText(message);

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                finish();
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        startPowerSaverIntent(this);

    }

    //    public void callBlockScreen(View view) {
//
//        PhoneCallReceiver receiver = new PhoneCallReceiver();
//
//        receiver.registerReciever(BlockActivity.this);
//
//        //startActivity(new Intent(this, BlockActivity.class));
//
//    }


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

                    showAutoStartPopup(context, Build.MANUFACTURER + " Protected Apps",
                            String.format("%s requires to be enabled in 'Protected Apps' to function properly in background.%n", context.getString(com.example.callblockingbrodcastreciever.R.string.app_name)),
                            finalFoundCorrectIntent, editor, intent);
                    break;
                }
            }
            if(!foundCorrectIntent)
                finish();

        }
        else
            finish();
    }

    private void showAutoStartPopup(final Context context, String title, String message, final boolean finalFoundCorrectIntent, final SharedPreferences.Editor editor, final Intent intent) {
        LayoutInflater li = LayoutInflater.from(this);
        View view = li.inflate(R.layout.auto_start_popup, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(view);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvMessage = view.findViewById(R.id.tvMessage);
        TextView tvCancel = view.findViewById(R.id.tvCancel);
        TextView tvOk = view.findViewById(R.id.tvOk);


        tvTitle.setText(title);
        tvMessage.setText(message);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoStartDialog.dismiss();
                finish();
            }
        });
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoStartDialog.dismiss();
                // Should we show an explanation?
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

                finish();
            }
        });

        autoStartDialog = alertDialogBuilder.create();
        autoStartDialog.setCancelable(false);
        autoStartDialog.show();

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




    private void testingClicks() {
        //        findViewById(R.id.btnCallReciever).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                blockedNumberPrefs = getSharedPreferences("NOPPE_CALL_BLOCKING_PREFFS", Context.MODE_PRIVATE);
//
//                Toast.makeText(BlockActivity.this, "list: " + blockedNumberPrefs.getString("blocked_list", ""), Toast.LENGTH_LONG).show();
//
//            }
//        });
//
//        findViewById(R.id.btnShowToast).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                blockedNumberPrefs = getSharedPreferences("NOPPE_CALL_BLOCKING_PREFFS", Context.MODE_PRIVATE);
//
//                Toast.makeText(BlockActivity.this, "list: " + blockedNumberPrefs.getString("blocked_list", ""), Toast.LENGTH_LONG).show();
//
//            }
//        });
//
//
//        findViewById(R.id.btnShowToast1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(BlockActivity.this, "list: ", Toast.LENGTH_LONG).show();
//
//            }
//        });
    }

}
