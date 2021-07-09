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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
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
import java.util.List;

public class ListeInfoActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    private TextView textViewListeAdi;
    private SeekBar seekBarTamamlanan;
    private TextView textViewTamamlanan;
    private RecyclerView rv;
    private ArrayList<ListePaylasilanPersonel> listeArrayList;
    private ListeInfoRehberAdapter adapter;
    private VeriTabaniYardimcisi vt;
    private Liste liste;
    private FloatingActionButton fabMain,fabBirinci,fabIkinci,fabUcuncu;
    private Boolean fabDurum = false;
    private Animation fabAcik,fabKapali,fabGeriDon,fabIleridon;
    private String personelIDleri;
    public static List<Personel>listedenAtılanPersonel;
    public static List<String> yoneticiYapilanPersonel;
    private Resources res;

    @Override
    protected void onStop() {
        super.onStop();
        if (listedenAtılanPersonel.size()>0){
            StringBuilder stringBuilder = new StringBuilder();
            for (Personel p:listedenAtılanPersonel) {
                stringBuilder.append(p.getPersonel_uid()+":");
            }
            FirebaseVeriGonder.FBGlisteAforozEtme(vt,liste,stringBuilder.toString());

            // FirebaseVeriGonder.FBGlistePaylasma(vt, liste);
            // listeyi paylaşma işlemini sayfadan ayrılırken yapıyoruz her silmeye
        }
        if(yoneticiYapilanPersonel.size()>0){
            StringBuilder stringBuilder = new StringBuilder();

            for (String s: yoneticiYapilanPersonel) {
                stringBuilder.append(s + ":");
            }
            FirebaseVeriGonder.FBGYoneticiEkleme(vt,liste,stringBuilder.toString());
        }
        // MainActivity.bayrakCode = 0 ;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),ListelerActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_info);

        vt = new VeriTabaniYardimcisi(ListeInfoActivity.this);
        listedenAtılanPersonel = new ArrayList<>();
        yoneticiYapilanPersonel = new ArrayList<>();

        res = getResources();

        liste = (Liste) getIntent().getSerializableExtra("nesneListe");
        MainActivity.nesneListe = liste;
        MainActivity.bayrakCode = 2 ;

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setBackgroundColor(getResources().getColor(R.color.color4));
        toolbar.setTitleTextColor(getResources().getColor(R.color.color3));
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.colorBeyaz));
        toolbar.setSubtitle(liste.getListe_adi()+res.getString(R.string.bilgileri));
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

        fabMain = findViewById(R.id.fabMain);
        fabBirinci = findViewById(R.id.fabBirinci);
        fabIkinci = findViewById(R.id.fabIkinci);
        fabUcuncu = findViewById(R.id.fabUcuncu);
        textViewListeAdi = findViewById(R.id.textViewListeAdi);
        seekBarTamamlanan = findViewById(R.id.seekBarTamamlanan);
        textViewTamamlanan = findViewById(R.id.textViewTamamlanan);



        final VeriTabaniYardimcisi vt = new VeriTabaniYardimcisi(ListeInfoActivity.this);
        ArrayList<Notlar> listeNotlari = new NotlarDao().tumNotlar(vt,liste.getListe_id());
        final int count_seakPeak_total = listeNotlari.size();
        int count_seakPeak_tamamlanan = 0;
        //int count_paylasilan_kisi_sayisi =0;
        for (Notlar l : listeNotlari){
            if (Boolean.valueOf(l.getCheck_box())){
                count_seakPeak_tamamlanan ++;
            }
        }

        seekBarTamamlanan.setMax(count_seakPeak_total);
        seekBarTamamlanan.setProgress(count_seakPeak_tamamlanan);

        textViewTamamlanan.setText(count_seakPeak_tamamlanan+" / "+count_seakPeak_total);
        if(liste.getListe_id() == 0) {
            textViewListeAdi.setText(res.getString(R.string.market_listesi));
        }else {
            textViewListeAdi.setText(liste.getListe_adi());
        }
        rv = findViewById(R.id.rvPaylasilanListesi);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));

        listeYenile();
        List<ListePaylasilanPersonel> listePaylasilanPersonel =
                new ListePaylasilanPersonelDao().tumListeler(vt);
        //Toast.makeText(this, res.getString(R.string.liste_paylasilan_personel)+listePaylasilanPersonel.size(), Toast.LENGTH_SHORT).show();


        // fab işlemleri
        fabAcik = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_acik);
        fabKapali = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_kapali);
        fabIleridon = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_main_ileri_don);
        fabGeriDon = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_main_geri_don);

        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabDurum){
                    fabMain.startAnimation(fabGeriDon);
                    fabBirinci.startAnimation(fabKapali);
                    fabBirinci.setClickable(false);
                    fabUcuncu.setAnimation(fabKapali);
                    fabUcuncu.setClickable(false);

                    if(liste.getYoneticimi() == 1 ) {
                        fabIkinci.startAnimation(fabKapali);
                        fabIkinci.setClickable(false);
                    }

                    fabDurum = false;
                }else {
                    fabMain.startAnimation(fabIleridon);
                    fabBirinci.startAnimation(fabAcik);
                    fabBirinci.setClickable(true);
                    fabUcuncu.setAnimation(fabAcik);
                    fabUcuncu.setClickable(true);

                    if(liste.getYoneticimi() == 1 ) {
                        fabIkinci.startAnimation(fabAcik);
                        fabIkinci.setClickable(true);
                    }

                    fabDurum = true;
                }

            }
        });

        fabBirinci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NotlarActivity.class);
                intent.putExtra("nesneListe",liste);
                startActivity(intent);
                finish();
            }
        });
        fabIkinci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // liste için key alıyoruz. sorgulamsı orada yapılıyor veri tabanı güncelleniyor.
                String key = FirebaseVeriGonder.ListeGlobalKeyAlma(vt,liste);
                liste = new Liste(liste.getListe_id(),key,liste.getListe_adi(),liste.getYoneticimi());

                Intent intent = new Intent(getApplicationContext(),RehberActivity.class);
                intent.putExtra("LISTEID",liste.getListe_id());
                intent.putExtra("nesneListe",liste);
                intent.putExtra("personelIDleri",personelIDleri);
                startActivity(intent);
                finish();
            }
        });
        fabUcuncu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(ListeInfoActivity.this, DigerUygulamalarlaPaylasActivity.class);
                intent2.putExtra("nesneListe",liste);
                intent2.putExtra("gelinenYerDurumu",1);
                startActivity(intent2);
                finish();
            }
        });

        textViewListeAdi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Liste adı değiştirme
                LayoutInflater inflater = LayoutInflater.from(ListeInfoActivity.this);
                View alert_tasarim = inflater.inflate(R.layout.alertview_tasarim, null);

                final EditText alert_edittext = (EditText) alert_tasarim.findViewById(R.id.editTextGirisBaslik);

                AlertDialog.Builder alertDialogOlusturucu = new AlertDialog.Builder(ListeInfoActivity.this);
                alertDialogOlusturucu.setMessage(res.getString(R.string.liste_adi));
                alertDialogOlusturucu.setView(alert_tasarim);
                alert_edittext.setHint(res.getString(R.string.liste_adi_giriniz));
                alert_edittext.setText(textViewListeAdi.getText());

                alertDialogOlusturucu.setPositiveButton(res.getString(R.string.kaydet), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        textViewListeAdi.setText(alert_edittext.getText().toString().trim());
                        new ListelerDao().listeGuncelle(vt,liste.getListe_id()
                                ,liste.getListe_global_key(),alert_edittext.getText().toString().trim());
                        Liste liste2 = new Liste(liste.getListe_id(),liste.getListe_global_key()
                                ,alert_edittext.getText().toString().trim(),liste.getYoneticimi());
                        FirebaseVeriGonder.FBGlistePaylasma(vt,liste2);
                    }
                });
                alertDialogOlusturucu.setNegativeButton(res.getString(R.string.iptal), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(ListeInfoActivity.this,res.getString(R.string.liste_adi_guncelleme_iptal_edildi),Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialogOlusturucu.create().show();

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
        StringBuilder stringBuilder = new StringBuilder();
        listeArrayList = new ArrayList<>();
        Personel p00 = new Personel(0,"Siz","","");
        ListePaylasilanPersonel p0 = new ListePaylasilanPersonel(0,liste.getListe_id(),p00,liste.getYoneticimi());
        listeArrayList.add(p0);
        vt = new VeriTabaniYardimcisi(ListeInfoActivity.this);
        // array list bu listede adı geçen personel uid lerine göre tek tek seçilecek ve ayrı listeye eklenecek.
        List<ListePaylasilanPersonel> listePaylasilanPersonel =
                new ListePaylasilanPersonelDao().PerosnelGetirListesi(vt,liste.getListe_id());
        for (ListePaylasilanPersonel lp:listePaylasilanPersonel) {

            listeArrayList.add(lp);
            stringBuilder.append(lp.getPerseonel_uid().getPersonel_uid()+":");
        }
        personelIDleri = stringBuilder.toString();
       /* Personel personel2 = new Personel(12,"aaaa","54asd5a5d4a","2515");
        listeArrayList.add(personel2);*/

        adapter = new ListeInfoRehberAdapter(ListeInfoActivity.this,listeArrayList,liste);
        rv.setAdapter(adapter);
        //Toast.makeText(this, listePaylasilanPersonel.size()+" ", Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.listeler_info_toolbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_market_listesi_yap:
                if (liste.getListe_id()==0){
                    Toast.makeText(this, res.getString(R.string.market_listesinde_calismaktasiniz), Toast.LENGTH_LONG).show();
                }else {
                    AlertDialog.Builder alertDialogOlusturucu = new AlertDialog.Builder(ListeInfoActivity.this);
                    alertDialogOlusturucu.setTitle(res.getString(R.string.market_listesi_yap));
                    alertDialogOlusturucu.setMessage(res.getString(R.string.mevcut_market_listesi_silinecek_emin_misiniz));

                    alertDialogOlusturucu.setPositiveButton(res.getString(R.string.evet), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            yapMarketListesi();
                        }
                    });
                    alertDialogOlusturucu.setNegativeButton(res.getString(R.string.iptal), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(ListeInfoActivity.this,res.getString(R.string.iptal_edildi),Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertDialogOlusturucu.create().show();
                }
                return true;


            case R.id.action_paylas:
                Intent intent2 = new Intent(ListeInfoActivity.this, DigerUygulamalarlaPaylasActivity.class);
                intent2.putExtra("nesneListe",liste);
                intent2.putExtra("gelinenYerDurumu",1);
                startActivity(intent2);
                finish();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void yapMarketListesi(){
        // market listesindeki notlar silinecek
        List<Notlar> marketListesindekiNotlar =new NotlarDao().tumNotlar(vt,0);
        if (marketListesindekiNotlar.size()>0){
            for (Notlar n:marketListesindekiNotlar) {
                new NotlarDao().notSil(vt,n.getNot_id());
            }
        }
        // Notlar liste_id leri 0 yapılcak.
        List<Notlar> listedekiNotlar = new NotlarDao().tumNotlar(vt,liste.getListe_id());
        if (listedekiNotlar.size()>0){
            for (Notlar nn:listedekiNotlar) {
                new NotlarDao().guncelleNotListeID(vt,nn.getNot_id(),0);
            }
        }
        // liste paylaşılanlar market listesi paylaşılanlarda yoksa listeye eklenecek.
        List<ListePaylasilanPersonel> listePaylasilanPersonels = new ListePaylasilanPersonelDao()
                .PerosnelGetirListesi(vt,liste.getListe_id());
        if (listePaylasilanPersonels.size()>0){
            for (ListePaylasilanPersonel lp:listePaylasilanPersonels) {
                List<ListePaylasilanPersonel> mP = new ListePaylasilanPersonelDao().PerosnelGetirListesiPersonelUIDile(vt
                        ,liste.getListe_id(),lp.getPerseonel_uid().getPersonel_uid());
                if ((lp.getPerseonel_uid().getPersonel_uid()) != (mP.get(0).getPerseonel_uid().getPersonel_uid())){
                    new ListePaylasilanPersonelDao().listeEkle(vt,0,lp.getPerseonel_uid().getPersonel_uid());
                }
            }
        }

        // Son olarak liste silinecek.
        new ListelerDao().listeSil(vt,liste.getListe_id());

        Toast.makeText(this, res.getString(R.string.bu_liste_artık_market_listesi), Toast.LENGTH_LONG).show();
    }




}
