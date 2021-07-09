package com.callbellapp.ourlist;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private Button buttonKabul;
    private FirebaseAuth mAuth;
    private TextView textViewKabulMetini;
    private Resources res;
    /*@Override
    protected void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getUid() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        res = getResources();

        buttonKabul = findViewById(R.id.buttonKabul);
        textViewKabulMetini = findViewById(R.id.textViewKabulMetini);

        buttonKabul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),LoginTelNoGirisActivity.class));
                finish();
            }
        });

        textViewKabulMetini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(getApplicationContext(),HizmetKosullariBilgilendirmesiActivity.class);
                intent2.putExtra("urlEk",res.getString(R.string.kullanım_kosullar_url));
                intent2.putExtra("baslik",res.getString(R.string.kullanım_kosullari));
                startActivity(intent2);
            }
        });



    }
}
