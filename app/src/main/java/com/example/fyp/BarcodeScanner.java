package com.example.fyp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class BarcodeScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    private static final int REQUEST_CAMERA=1;
    private ZXingScannerView ScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScannerView =new ZXingScannerView(this);
        setContentView(ScannerView);

        int currentapiVersion= Build.VERSION.SDK_INT;
        if(currentapiVersion>=Build.VERSION_CODES.M){
            if(checkPermission()){
                Toast.makeText(getApplicationContext(),"Permission Granted", Toast.LENGTH_LONG).show();
            }
            else{
                requestPermission();
            }
        }
    }

    private boolean checkPermission(){
        return(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(BarcodeScanner.this,new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA);
    }

    public void onRequestPermission(int requestCode, String permission[],int[] grantResult){
        switch(requestCode){
            case REQUEST_CAMERA:
                if(grantResult.length>0){
                    boolean cameraAccept = grantResult[0] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccept){
                        Toast.makeText(getApplicationContext(),"Permission Granted",Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Permission Not Granted",Toast.LENGTH_LONG).show();
                        if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                            showMessageOKCancel("You need to grant permission",
                                    new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                                                requestPermissions(new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                }
                break;
        }
    }

    public void onResume(){
        super.onResume();
        int currentapiVersion = Build.VERSION.SDK_INT;
        if(currentapiVersion>= Build.VERSION_CODES.N){
            if(checkPermission()){
                if(ScannerView==null){
                    ScannerView = new ZXingScannerView(this);
                    setContentView(ScannerView);
                }
                ScannerView.setResultHandler(this);
                ScannerView.startCamera();
            }
        }
    }

    public void onDestroy(){
        super.onDestroy();
        ScannerView.stopCamera();
        ScannerView=null;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener oklistener){
        new AlertDialog.Builder(BarcodeScanner.this)
                .setMessage(message)
                .setPositiveButton("OK",oklistener)
                .setNegativeButton("Cancel",null)
                .create()
                .show();
    }

    @Override
    public void handleResult(Result result) {

        final String rawresult=result.getText();

        String[] fullresult= rawresult.split(" ");
        String foodname=fullresult[0];
        String water=fullresult[1];
        String protein=fullresult[2];
        String calories=fullresult[3];

        AlertDialog.Builder builder= new AlertDialog.Builder(this);

        builder.setTitle("Nutrition Composition For " + foodname + " :");
        builder.setMessage("Water" +"\t" + ":" + water +"\n"
                +"Protein" +"\t" + ":" + protein +"\n"
                +"Calories" +"\t" + ":" + calories);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ScannerView.resumeCameraPreview(BarcodeScanner.this);
            }
        });
        builder.show();
    }
}