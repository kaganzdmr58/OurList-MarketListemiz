package com.callbellapp.ourlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AnonimGirisActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private VeriTabaniYardimcisi vt;


    @Override
    public void onStart() {
        super.onStart();
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anonim_giris);




        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //Toast.makeText(AnonimGirisActivity.this, "Anonim Giriş Başarılı.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();

                            // wweb serviste mauth.id ile kullanıcı oluşturulacak.
                            yeniKullaniciOlustur(user.getUid());

                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(AnonimGirisActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                    }
                });

        acilisListeVeNotOlustur();

        // wweb serviste mauth.id ile kullanıcı oluşturulacak.
    }

    //https://callbellapp.xyz/project/ourlist_tablo1_personel/insert_table1_2.php

    public void yeniKullaniciOlustur(final String UID){
        String url = "https://callbellapp.xyz/project/ourlist_tablo1_personel/insert_table1_2.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response", response);
//                Toast.makeText(ProfilActivity.this, "Başarıyla güncellendi.", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Resources res= getResources();
                Toast.makeText(getApplicationContext(),res.getString( R.string.kayit_hatali_lutfen_kontrol_ediniz),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid",UID);
                return params;
            }
        };
        Volley.newRequestQueue(AnonimGirisActivity.this).add(stringRequest);
    }



    public void acilisListeVeNotOlustur(){
        vt = new VeriTabaniYardimcisi(AnonimGirisActivity.this);
        new ListelerDao().listeEkle(vt,"","Liste 1 ",1);

        ArrayList<Liste> listes = new ListelerDao().tumListeler(vt);

        new NotlarDao().notEkle(vt,"false",""," "
                ,"",listes.get(0).getListe_id(),"null");

    }
}