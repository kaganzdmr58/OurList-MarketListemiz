package com.callbellapp.ourlist;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // firebase başlangıç dinleme metodları buraya yazılacak açılış ve dineleme de aksama olmaması için
    private FirebaseDatabase database;
    private DatabaseReference myRefIsletme;
    VeriTabaniYardimcisi vt = new VeriTabaniYardimcisi(MainActivity.this);



    public static int bayrakCode = 0 ;
    public static Liste nesneListe;
    public static Notlar nesneNot;
    public static String marketListesiKey,telNu,kullaniciAdi;
    public static ImageView imageView2;

    public static SharedPreferences spBilgiler;
    public static SharedPreferences.Editor editorBilgiler;
    public static Boolean DEGISIKLIK_BILDIRIM_MODU;

    public NotificationCompat.Builder builder;
    public static  FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private TextView textViewHosgeldin;
    Resources res;

    /*
        @Override
        protected void onStart() {
            super.onStart();

                //IsletmeFirebaseDinleme();
                mAuth= FirebaseAuth.getInstance();
                if (mAuth.getCurrentUser()== null){
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }else {
                    IsletmeFirebaseDinleme();
                    startActivity(new Intent(getApplicationContext(), ListelerActivity.class));
                    finish();
                }

        }
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        imageView2 = findViewById(R.id.imageView2);

        mAuth= FirebaseAuth.getInstance();
        textViewHosgeldin = findViewById(R.id.textViewHosgeldin);


        // Ayarlar bilgilerinin tuttuğu alan
        spBilgiler = getSharedPreferences("AYARLAR_BILGILER",MODE_PRIVATE);
        editorBilgiler = spBilgiler.edit();
        DEGISIKLIK_BILDIRIM_MODU = MainActivity.spBilgiler.getBoolean("DEGISIKLIK_BILDIRIM_MODU",true);

        telNu = spBilgiler.getString("telNu","null");
        kullaniciAdi = spBilgiler.getString("kullaniciAdi","null");


        res = getResources();



        // Market Listesi Key
        SharedPreferences mSharedPreferences = getSharedPreferences("marketListesiKey",MODE_PRIVATE);
        SharedPreferences.Editor mEditor= mSharedPreferences.edit();

        marketListesiKey = mSharedPreferences.getString("marketListesiKey","key");
        if (marketListesiKey.equals("key")){
            Liste liste2 = new Liste(0,"","Market",1);
            marketListesiKey = FirebaseVeriGonder.ListeGlobalKeyAlma(vt,liste2);

            mEditor.putString("marketListesiKey",marketListesiKey);
            mEditor.commit();
        }



        //  spash screen işlemleri
        if (mAuth.getCurrentUser() != null){
            IsletmeFirebaseDinleme();

        }

        Thread zamanliThread =new Thread(){
            public void run(){
                try {
                    sleep(900);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if (mAuth.getUid() == null){
                        startActivity(new Intent(getApplicationContext(), AnonimGirisActivity.class));
                        finish();
                    }else {
                        startActivity(new Intent(getApplicationContext(), ListelerActivity.class));
                        finish();
                    }
                }
            }
        };
        zamanliThread.start();





    }


    public void IsletmeFirebaseDinleme(){
        // sürekli olarak dinlenilen Cihaz dinleme noktası
        //Toast.makeText(this, "firebase dinleme açık", Toast.LENGTH_SHORT).show();
        database = FirebaseDatabase.getInstance();
        myRefIsletme = database.getReference("islemListesi").child("-"+mAuth.getUid());

        myRefIsletme.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                FirebaseIslem firebaseIslem = dataSnapshot.getValue(FirebaseIslem.class);

                String key = dataSnapshot.getKey();
                String islemTipi = firebaseIslem.getIslemTipi();
                String listeKey = firebaseIslem.getListeKey();
                String islem = firebaseIslem.getIslem();

                FirebaseIslem firebaseIslem1 = new FirebaseIslem(key,islemTipi,listeKey,islem);
                firebaseKarsılamaIslemleri(firebaseIslem1);
                //listeIDveKeyDegistirme(key,firebaseIslem1);
                //Toast.makeText(MainActivity.this, islemTipi, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FirebaseIslem firebaseIslem = dataSnapshot.getValue(FirebaseIslem.class);

                String key = dataSnapshot.getKey();
                String islemTipi = firebaseIslem.getIslemTipi();
                String listeKey = firebaseIslem.getListeKey();
                String islem = firebaseIslem.getIslem();

                FirebaseIslem firebaseIslem1 = new FirebaseIslem(key,islemTipi,listeKey,islem);
                firebaseKarsılamaIslemleri(firebaseIslem1);
                //listeIDveKeyDegistirme(key,firebaseIslem1);
                //Toast.makeText(MainActivity.this, islemTipi, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void firebaseKarsılamaIslemleri(FirebaseIslem firebaseIslem){
        // islemKey gelen firebase verisi veritabanına eklenince silme işleminde kullanılacak.
        // işlem ile gelen string ifade buradaki tipe göre split ile parçalanacak ve işlem verileri girilecek.

        switch (firebaseIslem.getIslemTipi()){

            case "LISTE_PAYLAS":
                if (DEGISIKLIK_BILDIRIM_MODU){         // eğer bildirim alma açık ise bildirim gönder.
                    List<Liste> listes = new ListelerDao().ListeAraGlobalKey(vt,firebaseIslem.getListeKey());
                    String islem[] = firebaseIslem.getIslem().split(":");
                    List<Personel> personels = new RehberDao().personelGetirUIDile(vt,islem[1]);
                   /* if (listes.size()==0){
                        bildirimOlusturma(personels.get(0).getPersonel_adi()
                                +" tarafından yeni bir listeye eklendiniz, görmek için dokunun.");
                    }else {
                        bildirimOlusturma(personels.get(0).getPersonel_adi()+" tarafından "
                                +listes.get(0).getListe_adi()+" listesinde değişiklik yapıldı, görmek için dokunun.");
                    }*/
                }
                //Toast.makeText(MainActivity.this, "Liste ekle", Toast.LENGTH_SHORT).show();
                FirebaseVeriAlma.FVAListeAlmaIslem(vt,firebaseIslem);
                SayfaYenilemeler ();
                break;

            case "NOT_ISLEM":
                if (DEGISIKLIK_BILDIRIM_MODU){         // eğer bildirim alma açık ise bildirim gönder.
                    List<Liste> listes = new ListelerDao().ListeAraGlobalKey(vt,firebaseIslem.getListeKey());
                    bildirimOlusturma(listes.get(0).getListe_adi()+res.getString(R.string.listesinde_degisiklikler_mevcut_goruntulemek_icin_lutfen_dokunun));
                }
                //Toast.makeText(MainActivity.this, "Not ekle", Toast.LENGTH_SHORT).show();
                FirebaseVeriAlma.FVAnotAlmaIslem(vt,firebaseIslem);
                SayfaYenilemeler ();
                break;

            case "NOT_SIL":
                if (DEGISIKLIK_BILDIRIM_MODU){         // eğer bildirim alma açık ise bildirim gönder.
                    List<Liste> listes = new ListelerDao().ListeAraGlobalKey(vt,firebaseIslem.getListeKey());
                    bildirimOlusturma(listes.get(0).getListe_adi()
                            +res.getString(R.string.listesinde_degisiklikler_mevcut_goruntulemek_icin_lutfen_dokunun));
                }
                //Toast.makeText(MainActivity.this, "Not sil", Toast.LENGTH_SHORT).show();
                FirebaseVeriAlma.FVAnotSilGlobalKeyile(vt,firebaseIslem);
                SayfaYenilemeler ();
                break;

            case "LISTE_SIL":
                if (DEGISIKLIK_BILDIRIM_MODU){         // eğer bildirim alma açık ise bildirim gönder.
                    List<Liste> listes = new ListelerDao().ListeAraGlobalKey(vt,firebaseIslem.getListeKey());
                    List<Personel> personels = new RehberDao().personelGetirUIDile(vt,firebaseIslem.getIslem());
                    bildirimOlusturma(personels.get(0).getPersonel_adi() +" , "+listes.get(0).getListe_adi()
                            + res.getString(R.string.listesinden_ayrildi)  );
                }
                //Toast.makeText(MainActivity.this, "Liste sil", Toast.LENGTH_SHORT).show();
                FirebaseVeriAlma.FVAListeSilGlobalKeyile(vt,firebaseIslem);
                SayfaYenilemeler ();
                break;

            case "LISTEDEN_AFOROZ":
                if (DEGISIKLIK_BILDIRIM_MODU){         // eğer bildirim alma açık ise bildirim gönder.
                    List<Liste> listes = new ListelerDao().ListeAraGlobalKey(vt,firebaseIslem.getListeKey());
                    List<Personel> personels = new RehberDao().personelGetirUIDile(vt,firebaseIslem.getIslem());
                    bildirimOlusturma(listes.get(0).getListe_adi() + res.getString( R.string.listesinden_cikarildiniz ) );
                }
                //Toast.makeText(MainActivity.this, "LISTEDEN_AFOROZ", Toast.LENGTH_SHORT).show();
                FirebaseVeriAlma.FVAListePaylasilanPersonelleriTemizle(vt,firebaseIslem);
                SayfaYenilemeler ();
                break;

            case "YONETICI_EKLEME":
                //Toast.makeText(MainActivity.this, "YONETICI_EKLEME", Toast.LENGTH_SHORT).show();
                FirebaseVeriAlma.FVAListeYoneticiEklemeIslemleri(vt,firebaseIslem);
                SayfaYenilemeler ();
                break;

            case "YONETICI_SILME":
                //Toast.makeText(MainActivity.this, "YONETICI_SILME", Toast.LENGTH_SHORT).show();
                FirebaseVeriAlma.FVAListeYoneticiSilmeIslemleri(vt,firebaseIslem);
                SayfaYenilemeler ();
                break;

            default:
                break;
        }
    }

    public void SayfaYenilemeler (){
        switch (bayrakCode){
            case 0:
                break;
            case 1: // ListeActivity açıksa
                bayrakCode = 1;
                Intent intent1 = new Intent(getApplicationContext(),ListelerActivity.class);
                //intent1.putExtra("durumListe",durumListe);
                startActivity(intent1);
                break;

            case 2: // ListeInfoActivity açıksa
                bayrakCode = 2;
                Intent intent2 = new Intent(getApplicationContext(), ListeInfoActivity.class);
                intent2.putExtra("nesneListe",nesneListe);
                startActivity(intent2);
                break;

            case 3: // NotActivity açıksa
                bayrakCode = 3;
                Intent intent3 = new Intent(getApplicationContext(), NotlarActivity.class);
                intent3.putExtra("nesneListe",nesneListe);
                //intent3.putExtra("durumNot",durumNot);
                startActivity(intent3);
                break;

            case 4: // NotInfoActivity açıksa
                bayrakCode = 4;
                Intent intent4 = new Intent(getApplicationContext(), NotInfoActivity.class);
                intent4.putExtra("nesneNot",nesneNot);
                intent4.putExtra("nesneListe",nesneListe);
                startActivity(intent4);
                break;

            case 5: //Market listesini açaçık ise
                MainActivity.bayrakCode =5;
                Liste listeMarket = new Liste(0,"","Market",0);
                Intent intent=new Intent(getApplicationContext(), NotlarActivity.class);
                intent.putExtra("nesneListe",listeMarket);
                startActivity(intent);
                finish();
                break;

            default:
                //Toast.makeText(this, "Hiçbiri olmadı", Toast.LENGTH_SHORT).show();
                break;
        }

    }


    public void bildirimOlusturma(String mesaj){

        /*String mesaj = null;
        List<Liste> listes = new ListelerDao().ListeAraGlobalKey(vt,firebaseIslem.getListeKey());
        mesaj = listes.get(0).getListe_adi()+" listesindeki değişiklikleri görmek için tıklayınız.";*/

        NotificationManager bildirimYoneticisi =
                (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MainActivity.class);

        PendingIntent gidilecekIntent = PendingIntent.getActivity(this
                ,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        long[] pattern = {500,500,500};

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String kanalId = "kanalId";
            String kanalAd = "kanalAd";
            String kanalTanım = "kanalTanım";
            int kanalOnceligi = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel kanal = bildirimYoneticisi.getNotificationChannel(kanalId);

            if(kanal==null){
                kanal = new NotificationChannel(kanalId,kanalAd,kanalOnceligi);
                kanal.setDescription(kanalTanım);
                bildirimYoneticisi.createNotificationChannel(kanal);
            }

            builder = new NotificationCompat.Builder(this,kanalId);

            builder.setContentTitle(res.getString(R.string.our_listten_mesaj_var))
                    .setContentText(mesaj)
                    .setSmallIcon(R.drawable.ic_person_gray_24dp)
                    .setAutoCancel(true)
                    .setVibrate(pattern)
                    //.setSound(Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.chime))
                    .setContentIntent(gidilecekIntent);

        }else {

            builder = new NotificationCompat.Builder(this);

            builder.setContentTitle(res.getString(R.string.our_listten_mesaj_var))
                    .setContentText(mesaj)
                    .setSmallIcon(R.drawable.ic_person_gray_24dp)
                    .setAutoCancel(true)
                    //.setSound(Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.chime))
                    .setVibrate(pattern)
                    .setContentIntent(gidilecekIntent);

        }

        bildirimYoneticisi.notify(1,builder.build());
    }


    public static String gonderTarihSaat(){
        Calendar calendar = Calendar.getInstance();
        String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String minute = String.valueOf(calendar.get(Calendar.MINUTE));
        if ((hour.length())==1){hour = "0" + hour; }
        if ((minute.length())==1){minute = "0" + minute; }
        String saat = hour +" . "+ minute;
        String gun = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        String ay = String.valueOf((calendar.get(Calendar.MONTH))+1);
        String yil = String.valueOf(calendar.get(Calendar.YEAR));
        if ((gun.length())==1){gun = "0" + gun; }
        if ((ay.length())==1){ay = "0" + ay; }
        if ((yil.length())==1){yil = "0" + yil; }
        String tarih = gun+"."+ay+"."+yil;
        return tarih + " _ " + saat;
    }


}


//https://www.simplifiedcoding.net/android-sync-sqlite-database-with-server/

