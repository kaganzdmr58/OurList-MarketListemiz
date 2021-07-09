package com.callbellapp.ourlist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginTelNoGirisActivity extends AppCompatActivity {
    private Button buttonIleri;
    private EditText editTextTelNuGiris;
    private TextView textViewUyari;
    private String TELNU;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_tel_no_giris);

        editTextTelNuGiris = findViewById(R.id.editTextTelNuGiris);
        textViewUyari = findViewById(R.id.textViewUyari);



        buttonIleri = findViewById(R.id.buttonIleri);

        buttonIleri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TELNU = editTextTelNuGiris.getText().toString().trim();
                if(TELNU.length()<10){
                    textViewUyari.setVisibility(View.VISIBLE);
                }else {
                    textViewUyari.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(getApplicationContext(),LoginSmsDogrulamaActivity.class);
                    intent.putExtra("telNu",TELNU);
                    startActivity(intent);
                    finish();
                }


            }
        });
    }
}
