package com.callbellapp.ourlist;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ListelerActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    private RecyclerView rv;
    private ArrayList<Liste> listeArrayList;
    private ListelerAdapter adapter;
    private FloatingActionButton FabListeEkle;
    private VeriTabaniYardimcisi vt;
    private Resources res ;

    /*@Override
    protected void onStop() {
        super.onStop();
        MainActivity.bayrakCode = 0 ;
    }*/

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        moveTaskToBack(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listeler);

        MainActivity.bayrakCode = 1 ;

        res = getResources();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(res.getString(R.string.app_name));
        toolbar.setBackgroundColor(getResources().getColor(R.color.color4));
        toolbar.setTitleTextColor(getResources().getColor(R.color.color3));
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.colorBeyaz));
        toolbar.setSubtitle(res.getString(R.string.listeler));
        toolbar.setLogo(R.drawable.icon36x40);
        setSupportActionBar(toolbar);
        // toolbara tıklamada main activity açılışı
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

        //Navigasyonlar
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.action_listeler:
                        startActivity(new Intent(getApplicationContext(),ListelerActivity.class));
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


        rv = findViewById(R.id.RVListeler);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));

        listeYenile();




        FabListeEkle = findViewById(R.id.FabListeEkle);

        FabListeEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*LayoutInflater inflater = LayoutInflater.from(ListelerActivity.this);
                View alert_tasarim = inflater.inflate(R.layout.alertview_tasarim, null);

                final EditText alert_edittext = (EditText) alert_tasarim.findViewById(R.id.editTextGiris);

                AlertDialog.Builder alertDialogOlusturucu = new AlertDialog.Builder(ListelerActivity.this);
                alertDialogOlusturucu.setMessage(res.getString(R.string.liste_adi));
                alertDialogOlusturucu.setView(alert_tasarim);
                alert_edittext.setHint(res.getString(R.string.liste_adi_giriniz));

                alertDialogOlusturucu.setPositiveButton(res.getString(R.string.kaydet), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new ListelerDao().listeEkle(vt,"",alert_edittext.getText().toString().trim(),1);
                        listeYenile();
                    }
                });
                alertDialogOlusturucu.setNegativeButton(res.getString(R.string.iptal), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(ListelerActivity.this,res.getString(R.string.liste_olusturma_iptal_edildi),Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialogOlusturucu.create().show();*/

            }
        });



        // ADMOB banner
        MobileAds.initialize(this, "ca-app-pub-2183039164562504~9839448579");
        //cihaz admob ID
        final AdView banner = findViewById(R.id.banner);
        AdRequest adRequest = new AdRequest.Builder().build();
        banner.loadAd(adRequest);
        banner.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                // Reklam yüklendiğinde çalışır
                Log.e("Banner"," onAdLoaded yüklendi");
            }
            @Override
            public void onAdFailedToLoad(int i) {
                // Hata oluştuğunda çalışır.
                Log.e("Banner","onAdFailedToLoad yüklendi : " + i);

                // banner.loadAd( new AdRequest.Builder().build());
            }
            @Override
            public void onAdOpened() {
                // Reklam tıklanarak açılma
                Log.e("Banner","onAdOpened yüklendi");
            }

            @Override
            public void onAdLeftApplication() {
                //Uygulamadan ayrılınca
                Log.e("Banner","onAdLeftApplication çalıştı");
            }

            @Override
            public void onAdClosed() {
                //Reklamdan geri dönünce çalışır.
                Log.e("Banner","onAdClosed çalıştı");
            }
        });




    }


    private void listeYenile(){
        listeArrayList = new ArrayList<>();
        vt = new VeriTabaniYardimcisi(ListelerActivity.this);
        listeArrayList = new ListelerDao().tumListeler(vt);
        adapter = new ListelerAdapter(ListelerActivity.this,listeArrayList);
        rv.setAdapter(adapter);
    }




}
