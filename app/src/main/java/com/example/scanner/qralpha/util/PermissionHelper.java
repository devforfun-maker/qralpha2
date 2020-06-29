package com.example.scanner.qralpha.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.Manifest.permission_group.CAMERA;

public class PermissionHelper {

    static public boolean checkPermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    static public void requestPermissions(AppCompatActivity appCompatActivity, final int REQUEST_CAMERA) {
        ActivityCompat.requestPermissions(appCompatActivity, new String[]{CAMERA}, REQUEST_CAMERA);
    }

}
