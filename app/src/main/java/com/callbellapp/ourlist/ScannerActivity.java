package com.callbellapp.ourlist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    ZXingScannerView ScannerView;


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),RehberePersonelEklemeActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScannerView = new ZXingScannerView(this);
        setContentView(ScannerView);

    }
    @Override
    public void handleResult(Result result) {
        // işletme key shared ile kayıt altına alınacak.
        String [] scanCode = result.getText().split(":");     // işletme ve masa qr codlarını java ile ayırma.

        // bu bölüme taratılan kodu sorgulama algoritması gelecek
        if(scanCode[0].equals("OurList")){
            String LOKAL_REHBER = MainActivity.spBilgiler.getString("LOKAL_REHBER_ID","");
            LOKAL_REHBER = LOKAL_REHBER + (scanCode[1]+";");
            MainActivity.editorBilgiler.putString("LOKAL_REHBER_ID",LOKAL_REHBER);
            MainActivity.editorBilgiler.commit();
            Resources res=getResources();
            Toast.makeText(getApplicationContext(), scanCode[1]+res.getText(R.string.basariyla_eklendi), Toast.LENGTH_SHORT).show();
        }else {
            Resources res=getResources();
            Toast.makeText(getApplicationContext(), res.getText(R.string.taratilan_qr_code_ourliste_ait_degildir), Toast.LENGTH_SHORT).show();
            onBackPressed();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        ScannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ScannerView.setResultHandler(this);
        ScannerView.startCamera();
    }


}

