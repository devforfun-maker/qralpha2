package com.example.scanner.qralpha;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scanner.qralpha.util.PermissionHelper;

public class SplashScreenActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 132;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!PermissionHelper.checkPermission(this))
                    PermissionHelper.requestPermissions(this, REQUEST_CAMERA);

            }

            findViewById(R.id.progress_circular).setVisibility(View.GONE);
            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
            finish();
        }, 2 * 1000);
    }

}
