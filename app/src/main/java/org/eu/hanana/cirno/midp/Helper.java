package org.eu.hanana.cirno.midp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.Map;

public class Helper {
    public static final int FILE_PICKER_REQUEST_CODE = 989;
    public static final Map<String, byte[]> records = new HashMap<>();

    public static boolean getpermision(String p, Activity activity) {
        // 检查是否已经获得所需权限
        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), p) == PackageManager.PERMISSION_GRANTED) {
            // 已经获得权限，可以执行相应操作
            return true;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.shouldShowRequestPermissionRationale(p)) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
            intent.setData(uri);
            activity.startActivity(intent);
            MainActivity.mainActivity.show_toast(MainActivity.mainActivity.getApplicationContext().getString(R.string.psettig));
            return false;
        } else {
            // 未获得权限，向用户请求权限
            ActivityCompat.requestPermissions(activity, new String[]{p}, 0);
            return false;
        }
    }

    public static CAudioRecorder cAudioRecorder;

    public static void stopRec() {
        if (cAudioRecorder != null && !cAudioRecorder.isRecording)
            throw new IllegalStateException("recorder is stopped");
        cAudioRecorder.stopRecording();
    }

    public static void startRec() {
        if (cAudioRecorder==null) cAudioRecorder=new CAudioRecorder();
        if (cAudioRecorder.isRecording)
            throw new IllegalStateException("recorder is running");
        cAudioRecorder.startRecording();
    }


}
