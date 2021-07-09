package com.callbellapp.ourlist;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class NotInfoActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    private CheckBox checkBoxNotInfo;
    private TextView textViewNot,textViewTamamlanmaAciklamasi,textViewNotDetayi,textViewTamam;
    private VeriTabaniYardimcisi vt;
    private Liste liste;
    private Notlar notGelen,not;
    private Resources res ;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(NotInfoActivity.this,NotlarActivity.class);
        intent.putExtra("nesneListe",liste);
        startActivity(intent);
        finish();
    }

    /* @Override
     protected void onStop() {
         super.onStop();
         MainActivity.bayrakCode = 0;
     }
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_info_2);

        vt = new VeriTabaniYardimcisi(this);
        res = getResources();

        liste = (Liste) getIntent().getSerializableExtra("nesneListe");
        notGelen = (Notlar) getIntent().getSerializableExtra("nesneNot");
        MainActivity.nesneListe = liste;
        MainActivity.nesneNot = notGelen;
        MainActivity.bayrakCode = 4;

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setBackgroundColor(getResources().getColor(R.color.color4));
        toolbar.setTitleTextColor(getResources().getColor(R.color.color3));
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.colorBeyaz));
        toolbar.setSubtitle(notGelen.getNot_adi()+res.getString(R.string.detayi));
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

        checkBoxNotInfo = findViewById(R.id.checkBoxNotInfo);
        textViewNot = findViewById(R.id.textViewNot);
        textViewTamamlanmaAciklamasi = findViewById(R.id.textViewTamamlanmaAciklamasi);
        textViewNotDetayi = findViewById(R.id.textViewNotDetayi);
        textViewTamam = findViewById(R.id.textViewTamam);








        // burası işlem yapılıp infoya basıldığında ilgili notun en güncel haline ulaşmayı sağlıyor.
        ArrayList<Notlar> notlarArrayList = new NotlarDao().tekNotID(vt,notGelen.getNot_id());
        not = notlarArrayList.get(0);
        // tek not nesnesi getiren metod yazdım ancak hata verdi en kestirme çözüm bu oldu.



        textViewNot.setText(not.getNot_adi());
        textViewTamamlanmaAciklamasi.setText(not.getNot_tamamlayan());
        checkBoxNotInfo.setChecked(Boolean.valueOf(not.getCheck_box()));

        if (Boolean.valueOf(not.getCheck_box())){
            textViewNot.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            textViewNot.setTypeface(null, Typeface.NORMAL);
        }

        if(!(not.getNot_detay().isEmpty())){
            textViewNotDetayi.setText(not.getNot_detay());
        }



        checkBoxNotInfo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String tamamlayan =null;
                if (b){
                    textViewNot.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    textViewNot.setTypeface(null, Typeface.NORMAL);
                    tamamlayan = MainActivity.mAuth.getCurrentUser().getDisplayName()+res.getString(R.string.tarafindan)
                            + MainActivity.gonderTarihSaat()+res.getString(R.string.de_tamamlandi);
                }else {
                    textViewNot.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
                    textViewNot.setTypeface(null, Typeface.BOLD);
                    tamamlayan ="";
                }
                new NotlarDao().notCheckBoxGuncelle(vt,not.getNot_id(),String.valueOf(b),tamamlayan);
                textViewTamamlanmaAciklamasi.setText(tamamlayan);

                // not firebase gönderme
                Notlar nott = new Notlar(not.getNot_id(),String.valueOf(b),tamamlayan,not.getNot_adi()
                        ,liste.getListe_id(),not.getNot_global_key(),not.getNot_detay());
                FirebaseVeriGonder.FBGnotIslem(vt,liste,nott);
            }
        });

        /*
        // edittext'e çevirildiği için iptal edildi.
        textViewNotDetayi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogOlusturucu(not);
            }
        });

        textViewNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogOlusturucuNotBaslik(not);
            }
        });
        */

        textViewTamam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tamamMetodu();
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

    private void alertDialogOlusturucu(final Notlar not){

        LayoutInflater inflater = LayoutInflater.from(NotInfoActivity.this);
        View alert_tasarim = inflater.inflate(R.layout.alertview_tasarim_genis_ve_uzun, null);

        final EditText alert_edittext = (EditText) alert_tasarim.findViewById(R.id.editTextGirisBaslik);

        AlertDialog.Builder alertDialogOlusturucu = new AlertDialog.Builder(NotInfoActivity.this);
        alertDialogOlusturucu.setMessage(R.string.not_detayi);
        alertDialogOlusturucu.setView(alert_tasarim);
        alert_edittext.setHint(R.string.notunuzun_detayini_giriniz);
        alert_edittext.setText(not.getNot_detay());

        alertDialogOlusturucu.setPositiveButton(R.string.kaydet, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new NotlarDao().notGuncelle(vt,not.getNot_id(),not.getCheck_box(),not.getNot_tamamlayan(),not.getNot_adi()
                        ,alert_edittext.getText().toString().trim(),not.getNot_global_key());
                Toast.makeText(NotInfoActivity.this, R.string.not_detay_bilgileri_kayit_edildi, Toast.LENGTH_SHORT).show();

/*                Intent intent = new Intent(NotInfoActivity.this,NotlarActivity.class);
                intent.putExtra("nesne",liste);
                startActivity(intent);*/
                textViewNotDetayi.setText(alert_edittext.getText().toString().trim());

                // not firebase gönderme
                Notlar nott = new Notlar(not.getNot_id(),not.getCheck_box(),not.getNot_tamamlayan(),not.getNot_adi()
                        ,liste.getListe_id(),not.getNot_global_key(),alert_edittext.getText().toString().trim());
                FirebaseVeriGonder.FBGnotIslem(vt,liste,nott);
            }
        });
        alertDialogOlusturucu.setNegativeButton(R.string.iptal, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(NotInfoActivity.this,res.getString(R.string.not_iptal_edildi),Toast.LENGTH_SHORT).show();
            }
        });
        alertDialogOlusturucu.create().show();
    }


    private void alertDialogOlusturucuNotBaslik(final Notlar not){

        LayoutInflater inflater = LayoutInflater.from(NotInfoActivity.this);
        View alert_tasarim = inflater.inflate(R.layout.alertview_tasarim_genis_ve_uzun, null);

        final EditText alert_edittext = (EditText) alert_tasarim.findViewById(R.id.editTextGirisBaslik);

        AlertDialog.Builder alertDialogOlusturucu = new AlertDialog.Builder(NotInfoActivity.this);
        alertDialogOlusturucu.setMessage(R.string.not);
        alertDialogOlusturucu.setView(alert_tasarim);
        alert_edittext.setHint(R.string.notunuzu_giriniz);
        alert_edittext.setText(not.getNot_adi());

        alertDialogOlusturucu.setPositiveButton(R.string.kaydet, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new NotlarDao().notGuncelle(vt,not.getNot_id(),not.getCheck_box(),not.getNot_tamamlayan(),alert_edittext.getText().toString().trim()
                        ,not.getNot_detay(),not.getNot_global_key());
                Toast.makeText(NotInfoActivity.this, R.string.not_bilgileri_kayit_edildi, Toast.LENGTH_SHORT).show();

                textViewNot.setText(alert_edittext.getText().toString().trim());

                // not firebase gönderme
                Notlar nott = new Notlar(not.getNot_id(),not.getCheck_box(),not.getNot_tamamlayan(),alert_edittext.getText().toString().trim()
                        ,liste.getListe_id(),not.getNot_global_key(),not.getNot_detay());
                FirebaseVeriGonder.FBGnotIslem(vt,liste,nott);
            }
        });
        alertDialogOlusturucu.setNegativeButton(R.string.iptal, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(NotInfoActivity.this,res.getString(R.string.not_iptal_edildi),Toast.LENGTH_SHORT).show();
            }
        });
        alertDialogOlusturucu.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.not_info_toolbar_tamam_iconlu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_tamam_kayit:
                //Toast.makeText(this, "Sil'e  basıldı", Toast.LENGTH_SHORT).show();
                tamamMetodu();
                return true;
            case R.id.action_sil:
                new NotlarDao().notSil(vt,not.getNot_id());
                Toast.makeText(this, "Not silindi.", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(NotInfoActivity.this,NotlarActivity.class);
                intent.putExtra("nesneListe",liste);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void tamamMetodu(){
        // textView Edittext E çevirildiğinde burası eklendi
        new NotlarDao().notGuncelle(vt,not.getNot_id(),not.getCheck_box(),not.getNot_tamamlayan(),textViewNot.getText().toString().trim()
                ,textViewNotDetayi.getText().toString().trim(),not.getNot_global_key());
        Toast.makeText(NotInfoActivity.this, R.string.not_detay_bilgileri_kayit_edildi, Toast.LENGTH_SHORT).show();

        textViewNot.setText(textViewNot.getText().toString().trim());
        textViewNotDetayi.setText(textViewNotDetayi.getText().toString().trim());

        // not firebase gönderme
        Notlar nott = new Notlar(not.getNot_id(),not.getCheck_box(),not.getNot_tamamlayan(),textViewNot.getText().toString().trim()
                ,liste.getListe_id(),not.getNot_global_key(),textViewNotDetayi.getText().toString().trim());
        FirebaseVeriGonder.FBGnotIslem(vt,liste,nott);
        //edittext bitiş




        Intent intent = new Intent(NotInfoActivity.this,NotlarActivity.class);
        intent.putExtra("nesneListe",liste);
        startActivity(intent);
        finish();
    }

}
