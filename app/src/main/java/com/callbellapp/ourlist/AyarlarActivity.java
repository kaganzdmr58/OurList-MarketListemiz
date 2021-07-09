package com.callbellapp.ourlist;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AyarlarActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    private ImageView imageViewProfil,imageViewQRcode;
    private TextView textViewAd2,textViewTel,textViewAyarlarEkraniCikisYap,textViewKullanıcıID
            ,textViewKullanıcıEkle,textViewPaylas;
    private FirebaseAuth mAuth;
    private CardView cardViewProfil,cardViewKullanıcıID2;
    private Switch switchDegisikliklerIcinBildirimGelsinMi;
    private Resources res;

    private String globalName,globalTel;

// çevrimdışı iken kullanıcı adı ve telefon numarası nasıl eklenecek?

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),ListelerActivity.class));
        finish();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayarlar);

        MainActivity.bayrakCode = 0 ;
        mAuth = FirebaseAuth.getInstance();
        res = getResources();

        profilBilgileriAlWeb(mAuth.getCurrentUser().getUid());

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(res.getString(R.string.app_name));
        toolbar.setBackgroundColor(getResources().getColor(R.color.color4));
        toolbar.setTitleTextColor(getResources().getColor(R.color.color3));
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.colorBeyaz));
        toolbar.setSubtitle(res.getString(R.string.ayarlar));
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

        imageViewProfil = findViewById(R.id.imageViewResim);
        imageViewQRcode = findViewById(R.id.imageViewQRcode);
        textViewAd2 = findViewById(R.id.textViewAd2);
        textViewTel = findViewById(R.id.textViewTel);
        textViewAyarlarEkraniCikisYap = findViewById(R.id.textViewAyarlarEkraniCikisYap);
        cardViewProfil = findViewById(R.id.cardViewProfil);
        cardViewKullanıcıID2 = findViewById(R.id.cardViewKullanıcıID2);
        switchDegisikliklerIcinBildirimGelsinMi = findViewById(R.id.switchDegisikliklerIcinBildirimGelsinMi);
        textViewKullanıcıID =findViewById(R.id.textViewKullanıcıID);
        textViewKullanıcıEkle = findViewById(R.id.textViewRehbereKullanıcıEkle);
        textViewPaylas = findViewById(R.id.textViewPaylas);



        textViewKullanıcıID.setText(mAuth.getCurrentUser().getUid());
        qrCodeUret("OurList:"+mAuth.getCurrentUser().getUid());


        final FirebaseAuth mAuth = FirebaseAuth.getInstance();



        if (MainActivity.kullaniciAdi == "null"){
            textViewAd2.setText("Name");
        }else {
            textViewAd2.setText(MainActivity.kullaniciAdi);
        }

        if (MainActivity.telNu=="null"){
            textViewTel.setText("Tel : " );
        }else {
            textViewTel.setText("Tel : " + MainActivity.telNu);
        }

        textViewKullanıcıEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),RehberePersonelEklemeActivity.class));

               /* // kendi rehberimizi oluşturacağız yalnızca ad ve telefon numarası olacak.
                LayoutInflater inflater = LayoutInflater.from(AyarlarActivity.this);
                View alert_tasarim = inflater.inflate(R.layout.alertview_tasarim, null);

                final EditText alert_edittext = (EditText) alert_tasarim.findViewById(R.id.editTextGiris);

                AlertDialog.Builder alertDialogOlusturucu = new AlertDialog.Builder(AyarlarActivity.this);
                alertDialogOlusturucu.setMessage("Eklenecek numara : ");
                alertDialogOlusturucu.setView(alert_tasarim);
                alert_edittext.setHint("OurList rehberine eklenecek numara giriniz.");
                alert_edittext.setInputType(TYPE_CLASS_PHONE);

                alertDialogOlusturucu.setPositiveButton(R.string.kaydet, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String LOKAL_REHBER = MainActivity.spBilgiler.getString("LOKAL_REHBER","");
                        LOKAL_REHBER = LOKAL_REHBER + (alert_edittext.getText().toString()+";");
                        MainActivity.editorBilgiler.putString("LOKAL_REHBER",LOKAL_REHBER);
                        MainActivity.editorBilgiler.commit();


                        Toast.makeText(AyarlarActivity.this, alert_edittext.getText().toString()+" başarıyla eklendi.", Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialogOlusturucu.setNegativeButton(R.string.iptal, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(AyarlarActivity.this,res.getString(R.string.iptal_edildi),Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialogOlusturucu.create().show();
*/
            }
        });




        switchDegisikliklerIcinBildirimGelsinMi.setChecked(MainActivity.DEGISIKLIK_BILDIRIM_MODU);
        switchDegisikliklerIcinBildirimGelsinMi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                MainActivity.editorBilgiler.putBoolean("DEGISIKLIK_BILDIRIM_MODU",b);
                MainActivity.editorBilgiler.commit();
                MainActivity.DEGISIKLIK_BILDIRIM_MODU = b;
            }
        });


        cardViewProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(AyarlarActivity.this);
                View alert_tasarim = inflater.inflate(R.layout.alertview_tasarim_ikili_ayarlar_personel_bilgiler, null);

                final EditText alert_edittext = (EditText) alert_tasarim.findViewById(R.id.editTextGirisBaslik);
                final EditText alert_edittext2 = (EditText) alert_tasarim.findViewById(R.id.editTextGirisDetay);


                AlertDialog.Builder alertDialogOlusturucu = new AlertDialog.Builder(AyarlarActivity.this);
                //alertDialogOlusturucu.setMessage(R.string.bilgilerinizi_giriniz);
                alertDialogOlusturucu.setView(alert_tasarim);
                alert_edittext.setHint(R.string.ad_giriniz);
                alert_edittext.setText(globalName);

                alert_edittext2.setHint(R.string.telefon_numaranizi_giriniz);
                alert_edittext2.setText(globalTel);

                alertDialogOlusturucu.setPositiveButton(R.string.kaydet, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String kullaniciAdi = alert_edittext.getText().toString().trim();
                        String telNu = alert_edittext2.getText().toString();

                        MainActivity.kullaniciAdi = kullaniciAdi;
                        MainActivity.telNu = telNu;

                        MainActivity.editorBilgiler.putString("kullaniciAdi",kullaniciAdi);
                        MainActivity.editorBilgiler.putString("telNu",telNu);
                        MainActivity.editorBilgiler.commit();

                        textViewAd2.setText(kullaniciAdi);
                        textViewTel.setText("Tel : "+telNu);

                        profilAdiGuncelle(kullaniciAdi,telNu);

                    }
                });
                alertDialogOlusturucu.setNegativeButton(R.string.iptal, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(AyarlarActivity.this,res.getString(R.string.guncelleme_iptal_edildi),Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialogOlusturucu.create().show();
            }
        });

        /*textViewAyarlarEkraniCikisYap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();

                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        }); */




        cardViewKullanıcıID2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, mAuth.getCurrentUser().getUid());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
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



    private void profilAdiGuncelle(final String ad, final String tel){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(ad + " - " + tel)
                .build();


        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AyarlarActivity.this, res.getString(R.string.ad_basariyla_guncellendi), Toast.LENGTH_SHORT).show();
                            //textViewAd2.setText(ad);
                            profilAdiGuncelleWeb(user.getUid(),ad,tel);
                        }
                    }
                });
    }

    public void profilAdiGuncelleWeb(final String UID,final String name,final String tel){
        String url = "https://callbellapp.xyz/project/ourlist_tablo1_personel/update_table1_personel_name_and_tel.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                profilBilgileriAlWeb(mAuth.getCurrentUser().getUid());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),res.getString( R.string.kayit_hatali_lutfen_kontrol_ediniz),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid",UID);
                params.put("name",name);
                params.put("tel",tel);
                return params;
            }
        };
        Volley.newRequestQueue(AyarlarActivity.this).add(stringRequest);
    }

    public void profilBilgileriAlWeb(final String UID){
        String url = "https://callbellapp.xyz/project/ourlist_tablo1_personel/find_table1_uid.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray bilgi = jsonObject.getJSONArray("tablo1");

                    for (int i = 0; i<bilgi.length(); i++ ) {


                        JSONObject q = bilgi.getJSONObject(i);

                        int id = q.getInt("id");
                        String uid = q.getString("uid");
                        String tel = q.getString("tel");
                        String name = q.getString("name");





                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
        Volley.newRequestQueue(AyarlarActivity.this).add(stringRequest);
    }

    public void qrCodeUret(String qr){
        MultiFormatWriter multiFormatWriter =new MultiFormatWriter();
        try {
            BitMatrix bitMatrix =multiFormatWriter.encode(qr, BarcodeFormat.QR_CODE,300,300);
            BarcodeEncoder barcodeEncoder =new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imageViewQRcode.setImageBitmap(bitmap);
        }catch (WriterException e){
            e.printStackTrace();
        }
    }

}
