package com.callbellapp.ourlist;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.HashMap;
import java.util.Map;

public class ProfilActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    private ImageView imageViewResim;
    private TextView textViewCikisYap,textViewTelNu,textViewAd;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        MainActivity.bayrakCode = 0 ;

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        //toolbar.setBackgroundColor(getResources().getColor(R.color.toolbarArkaPlanRengi));
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorSariAmblem));
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.colorAccent));
        toolbar.setLogo(R.drawable.icon36x40);
        setSupportActionBar(toolbar);
        // toolbara tıklamada main activity açılışı
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ListelerActivity.class));
                finish();
            }
        });

        //Navigasyonlar
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.action_listeler:
                        startActivity(new Intent(getApplicationContext(), ListelerActivity.class));
                        finish();
                        break;
                    case R.id.action_ayarlar:
                        startActivity(new Intent(getApplicationContext(), AyarlarActivity.class));
                        finish();
                        break;
                    case R.id.action_market:
                        MainActivity.bayrakCode =5;
                        Liste listeMarket = new Liste(0,MainActivity.marketListesiKey,"Market Listesi",1);
                        Intent intent=new Intent(getApplicationContext(), NotlarActivity.class);
                        intent.putExtra("nesneListe",listeMarket);
                        startActivity(intent);
                        finish();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });


        mAuth = FirebaseAuth.getInstance();

        textViewCikisYap = findViewById(R.id.textViewCikisYap);
        textViewTelNu = findViewById(R.id.textViewTelNu);
        textViewAd = findViewById(R.id.textViewAd);
        imageViewResim = findViewById(R.id.imageViewResim);

        if (mAuth.getUid() != null) {
            textViewTelNu.setText(mAuth.getCurrentUser().getPhoneNumber());

            //imageViewResim.setImageResource(mAuth.getCurrentUser().getPhotoUrl());
            //picasso ile doldur. seçimin  ardından da resim yükle
        }
        if (mAuth.getCurrentUser().getDisplayName() != ""){
            textViewAd.setText(mAuth.getCurrentUser().getDisplayName());
        }

        textViewCikisYap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();

                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        textViewAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }
    private void profilAdiGuncelle(final String ad){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(ad)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfilActivity.this, "Ad başarıyla güncellendi.", Toast.LENGTH_SHORT).show();
                            textViewAd.setText(ad);
                            profilAdiGuncelleWeb(user.getUid(),ad);
                        }
                    }
                });
    }
    private void profilResimiGuncelle(String url){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(url))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfilActivity.this, "Resim başarıyla güncellendi.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void profilAdiGuncelleWeb(final String UID,final String name){
        String url = "https://callbellapp.xyz/project/ourlist_tablo1_personel/update_table1_personel_name.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response", response);
//                Toast.makeText(ProfilActivity.this, "Başarıyla güncellendi.", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Kayit Hatali",Toast.LENGTH_SHORT).show();
                Log.e("response", String.valueOf(error));
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid",UID);
                params.put("name",name);
                return params;
            }
        };
        Volley.newRequestQueue(ProfilActivity.this).add(stringRequest);
    }



}



/*
          <?php
          $_METIN  =   "Savas/Dersim:Celik-Webinyo.COM BoşlukVAR";
          $_PARCALA  =  explode('-', $_METIN);

          echo $_PARCALA[0]."<br />";
          echo $_PARCALA[1]."<br />";
          echo $_PARCALA[2]."<br />";
          echo $_PARCALA[3]."<br />";
          echo $_PARCALA[4]."<br />";
          echo $_PARCALA[5]."<br />";
          ?>*/
