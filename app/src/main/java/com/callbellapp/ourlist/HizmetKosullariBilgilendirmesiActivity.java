package com.callbellapp.ourlist;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HizmetKosullariBilgilendirmesiActivity extends AppCompatActivity {

    private TextView textViewHizmetMetni,textViewTamam;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hizmet_kosullari_bilgilendirmesi);

        textViewTamam = findViewById(R.id.textViewTamam);
        textViewHizmetMetni = findViewById(R.id.textViewHizmetMetni);


        // textview scroll ederek kaydırma ekliyoruz
        textViewHizmetMetni.setMovementMethod(new ScrollingMovementMethod());

        textViewTamam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        });





    }
}