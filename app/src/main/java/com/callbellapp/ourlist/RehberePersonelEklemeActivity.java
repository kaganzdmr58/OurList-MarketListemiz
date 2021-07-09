package com.callbellapp.ourlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RehberePersonelEklemeActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private CardView cardViewQrCode,cardViewKullaniciID,cardViewTelefonNu;
    private EditText editTextPhone,editTextKullaniciID;
    private ImageView imageViewPhoneCheck,imageViewKullaniciIdCheck;

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(getApplicationContext(),AyarlarActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rehbere_personel_ekleme);

        final Resources res = getResources();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(res.getString(R.string.app_name));
        toolbar.setBackgroundColor(getResources().getColor(R.color.color4));
        toolbar.setTitleTextColor(getResources().getColor(R.color.color3));
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.colorBeyaz));
        toolbar.setLogo(R.drawable.icon36x40);
        toolbar.setSubtitle(R.string.listeye_personel_ekleme);
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


        cardViewQrCode = findViewById(R.id.cardViewQrCode);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextKullaniciID = findViewById(R.id.editTextKullaniciID);
        imageViewPhoneCheck = findViewById(R.id.imageViewPhoneCheck);
        imageViewKullaniciIdCheck = findViewById(R.id.imageViewKullaniciIdCheck);
        cardViewTelefonNu = findViewById(R.id.cardViewTelefonNu);
        cardViewKullaniciID =findViewById(R.id.cardViewKullaniciID);

        imageViewPhoneCheck.setVisibility(View.INVISIBLE);
        imageViewKullaniciIdCheck.setVisibility(View.INVISIBLE);


        cardViewQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // burada camera açılacak.
                startActivity(new Intent(getApplicationContext(),ScannerActivity.class));
                finish();
            }
        });


        editTextKullaniciID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageViewKullaniciIdCheck.setVisibility(View.VISIBLE);
            }
        });

        imageViewKullaniciIdCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String eklenecekKullaniciID = editTextKullaniciID.getText().toString();
                if (eklenecekKullaniciID.isEmpty()){


                    Toast.makeText(RehberePersonelEklemeActivity.this,
                            res.getText(R.string.kullanici_id_girilmedigi_icin_islem_gercekletirilemedi), Toast.LENGTH_SHORT).show();
                    return;
                }

                String LOKAL_REHBER = MainActivity.spBilgiler.getString("LOKAL_REHBER_ID","");
                LOKAL_REHBER = LOKAL_REHBER + (eklenecekKullaniciID+";");
                MainActivity.editorBilgiler.putString("LOKAL_REHBER_ID",LOKAL_REHBER);
                MainActivity.editorBilgiler.commit();

                Toast.makeText(RehberePersonelEklemeActivity.this,
                        editTextKullaniciID.getText().toString()+res.getText(R.string.basariyla_eklendi)
                        , Toast.LENGTH_SHORT).show();

            }
        });

        editTextPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageViewPhoneCheck.setVisibility(View.VISIBLE);
            }
        });

        imageViewPhoneCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String eklenecekTelefon = editTextPhone.getText().toString();
                if (eklenecekTelefon.isEmpty()){
                    Toast.makeText(RehberePersonelEklemeActivity.this,
                            res.getText(R.string.kullanici_id_girilmedigi_icin_islem_gercekletirilemedi), Toast.LENGTH_SHORT).show();
                    return;
                }
                String LOKAL_REHBER = MainActivity.spBilgiler.getString("LOKAL_REHBER","");
                LOKAL_REHBER = LOKAL_REHBER + (eklenecekTelefon+";");
                MainActivity.editorBilgiler.putString("LOKAL_REHBER",LOKAL_REHBER);
                MainActivity.editorBilgiler.commit();

                Toast.makeText(RehberePersonelEklemeActivity.this,
                        editTextPhone.getText().toString()+res.getText(R.string.basariyla_eklendi)
                        , Toast.LENGTH_SHORT).show();

            }
        });

    }
}