package com.callbellapp.ourlist;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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

public class NotlarActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    private RecyclerView rv;
    private ArrayList<Notlar> notlarArrayList;
    private VeriTabaniYardimcisi vt;
    private NotlarAdapter adapter;

    private FloatingActionButton fab;
    private Liste liste;
    private int notDurum;
    private Resources res ;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),ListelerActivity.class));
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notlar);

        liste = (Liste) getIntent().getSerializableExtra("nesneListe");
        notDurum =  getIntent().getIntExtra("durumNot",1);
        MainActivity.bayrakCode = 3;
        MainActivity.nesneListe = liste;

        res = getResources();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(res.getString(R.string.app_name));
        toolbar.setBackgroundColor(getResources().getColor(R.color.color4));
        toolbar.setTitleTextColor(getResources().getColor(R.color.color3));
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.colorBeyaz));
        toolbar.setLogo(R.drawable.icon36x40);
        if(liste.getListe_id() == 0) {
            toolbar.setSubtitle(res.getString(R.string.market_listesi));
        }else {
            toolbar.setSubtitle(liste.getListe_adi());
        }
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
                }
                return true;
            }
        });




        rv  = findViewById(R.id.RVNotlar);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));

        listeYenile(notDurum);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notEkle();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notlar_toolbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_sil:
                //Toast.makeText(this, "Sil'e  basıldı", Toast.LENGTH_SHORT).show();
                listeYenile(2);
                return true;
            case R.id.action_liste_info:
                Intent intent = new Intent(NotlarActivity.this, ListeInfoActivity.class);
                intent.putExtra("nesneListe",liste);
                startActivity(intent);
                return true;
            case R.id.action_paylas:
                Intent intent2 = new Intent(NotlarActivity.this, DigerUygulamalarlaPaylasActivity.class);
                intent2.putExtra("nesneListe",liste);
                intent2.putExtra("gelinenYerDurumu",3);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void listeYenile(int drum){
        notlarArrayList = new ArrayList<>();
        vt = new VeriTabaniYardimcisi(NotlarActivity.this);
        notlarArrayList = new NotlarDao().tumNotlar(vt,liste.getListe_id());
        adapter = new NotlarAdapter(NotlarActivity.this,notlarArrayList,vt,drum,liste);
        rv.setAdapter(adapter);
    }


    public void notEkle(){
        new NotlarDao().notEkle(vt,"false","",""
                ,"",liste.getListe_id(),"null");

        ArrayList<Notlar> notlarArrayList = new NotlarDao().tumNotlarAzalan(vt,liste.getListe_id());
        Notlar nott = new Notlar(notlarArrayList.get(0).getNot_id(),"false","",""
                ,liste.getListe_id(),"","");
        FirebaseVeriGonder.FBGnotIslem(vt,liste,nott);

        listeYenile(1);
    }
    public void notEkleAlertli(){
        LayoutInflater inflater = LayoutInflater.from(NotlarActivity.this);
        View alert_tasarim = inflater.inflate(R.layout.alertview_tasarim, null);

        final EditText alert_edittext = (EditText) alert_tasarim.findViewById(R.id.editTextGirisBaslik);

        AlertDialog.Builder alertDialogOlusturucu = new AlertDialog.Builder(NotlarActivity.this);
        alertDialogOlusturucu.setMessage(R.string.not);
        alertDialogOlusturucu.setView(alert_tasarim);
        alert_edittext.setHint(R.string.notunuzu_giriniz);

        alertDialogOlusturucu.setPositiveButton(R.string.kaydet, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new NotlarDao().notEkle(vt,"false","",alert_edittext.getText().toString().trim()
                        ,"",liste.getListe_id(),"null");

                ArrayList<Notlar> notlarArrayList = new NotlarDao().tumNotlarAzalan(vt,liste.getListe_id());
                Notlar nott = new Notlar(notlarArrayList.get(0).getNot_id(),"false","",alert_edittext.getText().toString()
                        ,liste.getListe_id(),"","");
                FirebaseVeriGonder.FBGnotIslem(vt,liste,nott);

                listeYenile(1);
            }
        });
        alertDialogOlusturucu.setNegativeButton(R.string.iptal, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(NotlarActivity.this,res.getString(R.string.not_iptal_edildi),Toast.LENGTH_SHORT).show();
            }
        });
        alertDialogOlusturucu.create().show();
    }

}
