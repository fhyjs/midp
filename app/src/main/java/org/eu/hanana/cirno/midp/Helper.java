package org.eu.hanana.cirno.midp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Helper {
    public static boolean getpermision(String p, Activity activity){
        // 检查是否已经获得所需权限
        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), p) == PackageManager.PERMISSION_GRANTED) {
            // 已经获得权限，可以执行相应操作
            return true;
        } else if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M &&activity.shouldShowRequestPermissionRationale(p)){
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
            intent.setData(uri);
            activity.startActivity(intent);
            MainActivity.mainActivity.show_toast(Resources.getSystem().getString(R.string.psettig));
            return false;
        }else {
            // 未获得权限，向用户请求权限
            ActivityCompat.requestPermissions(activity, new String[]{p}, 0);
            return false;
        }
    }
}
