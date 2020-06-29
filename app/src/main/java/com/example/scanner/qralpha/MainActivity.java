package com.example.scanner.qralpha;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.scanner.qralpha.util.ConnectivityHelper;
import com.example.scanner.qralpha.util.PermissionHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 132;
    private ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!ConnectivityHelper.isConnectedToNetwork(this))
            new AlertDialog.Builder(this)
                    .setMessage("No internet connection. Please turn it ON and try again.")
                    .setPositiveButton("OK",
                            (dialog, which) -> {
                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.addCategory(Intent.CATEGORY_HOME);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            })
                    .create()
                    .show();

        if (PermissionHelper.checkPermission(this)) {
            if (scannerView == null) {
                scannerView = new ZXingScannerView(this);
                setContentView(scannerView);
            }
            scannerView.setResultHandler(this);
            scannerView.startCamera();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !PermissionHelper.checkPermission(this))
            PermissionHelper.requestPermissions(this, REQUEST_CAMERA);

    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] Permission, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length > 0) {
                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (cameraAccepted)
                    new AlertDialog.Builder(this)
                            .setTitle("Permission Granted!")
                            .setMessage("User is authenticated")
                            .setPositiveButton(android.R.string.ok,
                                    (dialog, which) -> dialog.dismiss())
                            .show();

                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!PermissionHelper.checkPermission(this)) {
                        new AlertDialog.Builder(this)
                                .setTitle("Camera Permissions")
                                .setMessage("You must allow camera permissions to use the scanner")
                                .setPositiveButton("Allow",
                                        (dialog, which) -> {
                                            dialog.dismiss();
                                            PermissionHelper.requestPermissions(MainActivity.this, REQUEST_CAMERA);
                                        })
                                .setNegativeButton("Cancel",
                                        (dialog, which) -> {
                                            dialog.dismiss();
                                            Intent intent = new Intent(Intent.ACTION_MAIN);
                                            intent.addCategory(Intent.CATEGORY_HOME);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        })
                                .show();
                    }
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionHelper.checkPermission(this)) {
                if (scannerView == null) {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            } else
                PermissionHelper.requestPermissions(this, REQUEST_CAMERA);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (scannerView != null)
            scannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        final String scanResult = result.getText();

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setPositiveButton("OK", (dialogInterface, which) -> scannerView.resumeCameraPreview(MainActivity.this));

        FirebaseDatabase.getInstance().getReference("AllUsers").orderByKey().equalTo(scanResult)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren())
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (snapshot.getKey() != null && snapshot.getKey().equals(scanResult)) {
                                    builder.setMessage("User Verified");
                                    FirebaseDatabase.getInstance().getReference("Attendees").child(snapshot.getKey()).setValue(true);
                                    builder.create().show();
                                } else
                                    builder.setMessage("User Not Found").create().show();

                            }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

}
