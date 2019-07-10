package com.example.callblockingbrodcastreciever;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.List;

public class BlockActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block);

        Intent intent = new Intent();
        String manufacturer = android.os.Build.MANUFACTURER;
        switch (manufacturer.toLowerCase()) {
            case "xiaomi":
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                break;
            case "oppo":
                intent.setComponent(new ComponentName("com.coloros.safecenter","com.coloros.safecenter.permission.startup.StartupAppListActivity"));
                break;
            case "vivo":
                intent.setComponent(new ComponentName("com.vivo.permissionmanager","com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
                break;
            case "oneplus":
                intent.setComponent(new ComponentName("com.oneplus.security", "com.oneplus.security.chainlaunch.view.ChainLaunchAppListAct‌​ivity"));
                break;
        }
        List<ResolveInfo> list = this.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            this.startActivity(intent);
        }

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ANSWER_PHONE_CALLS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALL_LOG},
                        1001);
            }
        }

        findViewById(R.id.btnCallReciever).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneCallReceiver receiver=new PhoneCallReceiver();

                receiver.registerReciever(BlockActivity.this);
            }
        });

    }
}
