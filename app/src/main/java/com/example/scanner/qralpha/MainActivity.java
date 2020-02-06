package com.example.scanner.qralpha;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission_group.CAMERA;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA=1;
    private ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("onCreate", "onCreate");
        scannerView=new ZXingScannerView(this);
        setContentView(scannerView);

        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.M) {
            if (checkPermission()) {
                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
            }
            else {
                requestPermissions();
            }
        }
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,new String[]{CAMERA}, REQUEST_CAMERA);
    }

    public void onRequestPermissionsResult(int requestCode, String Permission[],int grantResults[]){
        switch (requestCode) {
            case REQUEST_CAMERA:
                if(grantResults.length>0) {
                boolean Cameraaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if(Cameraaccepted)
                    Toast.makeText(MainActivity.this,"Permission Granted", Toast.LENGTH_LONG).show();
                else {
                    Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                        if(shouldShowRequestPermissionRationale(CAMERA)){
                            displayalert("You Need to allow Persmissions to run the app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestPermissions(new String[]{CAMERA} ,REQUEST_CAMERA);
                                        }
                                    });
                            return;
                        }
                    }
                }

            } break;

        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(checkPermission()){
                if(scannerView==null) {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            }
            else{
                requestPermissions();
            }
        }

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void displayalert(String message, DialogInterface.OnClickListener listener){
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK",listener)
                .setNegativeButton("Cancel",null)
                .create()
                .show();
    }

    @Override
    public void handleResult(Result result) {
        final String scanResult=result.getText();
        Log.d("QRCodeScanner", result.getText());
        Log.d("QRCodeScanner", result.getBarcodeFormat().toString());
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("scanResult");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface,int which) {
                scannerView.resumeCameraPreview(MainActivity.this);

            }
        });
        builder.setNeutralButton("Visit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent Intent= new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(scanResult));
                startActivity(Intent);

            }
        });
        builder.setMessage(scanResult);
        AlertDialog alert= builder.create();
        alert.show();



    }


    }
