package com.callbellapp.ourlist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.List;

public class DigerUygulamalarlaPaylasActivity extends AppCompatActivity {

    //https://developer.android.com/training/sharing/send


    private CardView cardViewSMS,cardViewEMAİL,cardViewWHATSAAP;
    private TextView textViewUygulamaIciPaylas,textViewIptal,textViewMetinGoster;
    private Switch switchNotDetayi,switchTamamlananNotlar,switchArtiEksi;

    private VeriTabaniYardimcisi vt;
    private Liste liste;
    private String personelIDleri;
    private int gelinenYerDurumu ;
    private Resources res;

    private SharedPreferences spBilgiler;
    private SharedPreferences.Editor editorBilgiler;

    private Boolean NOT_DETAYI_MODU,TAMAMLANAN_NOTLAR_MODU,ARTI_EKSI_MODU;

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        switch (gelinenYerDurumu){

            case 1:
                Intent intent = new Intent(getApplicationContext(), ListeInfoActivity.class);
                intent.putExtra("nesneListe", liste);
                startActivity(intent);
                finish();
                break;

            case 2:
                Intent intent2 = new Intent(getApplicationContext(), ListelerActivity.class);
                //ek yok
                startActivity(intent2);
                finish();
                break;

            case 3:
                Intent intent3 = new Intent(getApplicationContext(), NotlarActivity.class);
                intent3.putExtra("nesneListe", liste);
                startActivity(intent3);
                finish();
                break;
            default:
                break;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paylas_secim);

        vt = new VeriTabaniYardimcisi(DigerUygulamalarlaPaylasActivity.this);
        liste = (Liste) getIntent().getSerializableExtra("nesneListe");
        personelIDleri = getIntent().getStringExtra("personelIDleri");
        gelinenYerDurumu = getIntent().getIntExtra("gelinenYerDurumu",0);

        cardViewSMS = findViewById(R.id.cardViewSMS);
        cardViewEMAİL = findViewById(R.id.cardViewEMAİL);
        cardViewWHATSAAP = findViewById(R.id.cardViewWHATSAAP);
        textViewUygulamaIciPaylas = findViewById(R.id.textViewUygulamaIciPaylas);
        textViewIptal = findViewById(R.id.textViewIptal);
        switchNotDetayi = findViewById(R.id.switchNotDetayi);
        switchTamamlananNotlar = findViewById(R.id.switchTamamlananNotlar);
        textViewMetinGoster = findViewById(R.id.textViewMetinGoster);
        switchArtiEksi = findViewById(R.id.switchArtiEksi);


        res = getResources();




        textViewUygulamaIciPaylas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // diğer uygulamalarla paylaş
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, notlariBirlestir());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        textViewIptal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (gelinenYerDurumu){

                    case 1:
                        Intent intent = new Intent(getApplicationContext(), ListeInfoActivity.class);
                        intent.putExtra("nesneListe", liste);
                        startActivity(intent);
                        finish();
                        break;

                    case 2:
                        Intent intent2 = new Intent(getApplicationContext(), ListelerActivity.class);
                        //ek yok
                        startActivity(intent2);
                        finish();
                        break;

                    case 3:
                        Intent intent3 = new Intent(getApplicationContext(), NotlarActivity.class);
                        intent3.putExtra("nesneListe", liste);
                        startActivity(intent3);
                        finish();
                        break;
                    default:
                        break;
                }
            }
        });


        // müşterinin bilgileri tuttuğu alan
        spBilgiler = getSharedPreferences("BILGILER",MODE_PRIVATE);
        editorBilgiler = spBilgiler.edit();


        TAMAMLANAN_NOTLAR_MODU = spBilgiler.getBoolean("TAMAMLANAN_NOTLAR_MODU",true);
        switchTamamlananNotlar.setChecked(TAMAMLANAN_NOTLAR_MODU);
        switchTamamlananNotlar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editorBilgiler.putBoolean("TAMAMLANAN_NOTLAR_MODU",b);
                editorBilgiler.commit();
                TAMAMLANAN_NOTLAR_MODU = b;
                textViewMetinGoster.setText(notlariBirlestir());

            }
        });

        NOT_DETAYI_MODU = spBilgiler.getBoolean("NOT_DETAYI_MODU",true);
        switchNotDetayi.setChecked(NOT_DETAYI_MODU);
        switchNotDetayi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editorBilgiler.putBoolean("NOT_DETAYI_MODU",b);
                editorBilgiler.commit();
                NOT_DETAYI_MODU = b;
                textViewMetinGoster.setText(notlariBirlestir());

            }
        });

        ARTI_EKSI_MODU = spBilgiler.getBoolean("ARTI_EKSI_MODU",true);
        switchArtiEksi.setChecked(ARTI_EKSI_MODU);
        switchArtiEksi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editorBilgiler.putBoolean("ARTI_EKSI_MODU",b);
                editorBilgiler.commit();
                ARTI_EKSI_MODU = b;
                textViewMetinGoster.setText(notlariBirlestir());
            }
        });


        // textview scroll ederek kaydırma ekliyoruz
        textViewMetinGoster.setMovementMethod(new ScrollingMovementMethod());

        textViewMetinGoster.setText(notlariBirlestir());

    }

    public String notlariBirlestir(){
        // not detayı
        // tamamlanan notlar
        // tammalayan tarih saat (şuanda yok enson)
        List<Notlar> notlarList = new NotlarDao().tumNotlar(vt,liste.getListe_id());
        StringBuilder sb = new StringBuilder();
        for (Notlar n:notlarList) {
            if (TAMAMLANAN_NOTLAR_MODU) {
                if (ARTI_EKSI_MODU){
                    if ((n.getCheck_box()).equals("true")){
                        sb.append(" + ");
                    }else {
                        sb.append(" - ");
                    }
                }
                // tamam notlar eklenecek ise bütün liste yazılacak demektir.
                sb.append(n.getNot_adi()+"\n");
                if (NOT_DETAYI_MODU){
                    if (!(n.getNot_detay().isEmpty())) {
                        sb.append("\t\t\t");
                        sb.append(n.getNot_detay() + "\n");
                    }
                }
                // tamamlayan tarih saat eklenecek.

            }else {
                // tamamlanan notlar listeye eklenmeyecekse
                if ((n.getCheck_box()).equals("false")){
                    if (ARTI_EKSI_MODU){
                        sb.append(" - ");
                    }
                    sb.append(n.getNot_adi()+"\n");
                    if (NOT_DETAYI_MODU){
                        if (!(n.getNot_detay().isEmpty())) {
                            sb.append("\t\t\t");
                            sb.append(n.getNot_detay() + "\n");
                        }
                    }
                    // tamamlayan tarih saat eklenecek.

                }
            }

        }
        return sb.toString();
    }





}