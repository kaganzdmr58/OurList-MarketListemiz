package com.callbellapp.ourlist;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RehberActivity extends AppCompatActivity {
    //Rehberdeki telefon numaraları alınarak buradan web servise gönderilecek.
    //web servisten dönen cevap (rehber ile eşleşen numaralar) burada sergilenecek.
    //buradaki seçim listedeki paylaşılacak kişiler listesine eklenecek.
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private RecyclerView rv;
    private ArrayList<Personel> listeArrayList;
    private RehberAdapter adapter;
    private VeriTabaniYardimcisi vt;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private StringBuilder stringTopla;
    private int listeID;
    private Liste liste;
    private HashMap<String,Object> integersID= new HashMap<>();
    public static Boolean rehbereEklemeDurumu = false;
    private TextView textViewTamam;
    private Resources res;
    private HashMap<Integer,Personel> personelHashMap = new HashMap<>();

    @Override
    protected void onStop() {
        super.onStop();
        if (rehbereEklemeDurumu){
            FirebaseVeriGonder.FBGlistePaylasma(vt,liste);
            // listeyi paylaşma işlemini sayfadan ayrılırken yapıyoruz her eklemeye ayrı ayrı gönderip cihazı yormayalım.
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (rehbereEklemeDurumu){
            FirebaseVeriGonder.FBGlistePaylasma(vt,liste);
            // listeyi paylaşma işlemini sayfadan ayrılırken yapıyoruz her eklemeye ayrı ayrı gönderip cihazı yormayalım.
        }

        Intent intent = new Intent(getApplicationContext(),ListeInfoActivity.class);
        intent.putExtra("nesneListe",liste);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rehber);

        MainActivity.bayrakCode =0;
        res = getResources();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle(res.getString(R.string.app_name));
        toolbar.setBackgroundColor(getResources().getColor(R.color.color4));
        toolbar.setTitleTextColor(getResources().getColor(R.color.color3));
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.colorBeyaz));
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



        rv = findViewById(R.id.rvRehber);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));


        listeID = getIntent().getIntExtra("LISTEID",1);
        liste = (Liste) getIntent().getSerializableExtra("nesneListe");


        String personelIDleri = getIntent().getStringExtra("personelIDleri");
        String stringList[] = personelIDleri.split(":");

        for (int i = 0; i < stringList.length; i++){
            integersID.put(stringList[i],stringList[i]);
            // buradaki tek maksat personel eğer listede ekli ise ekleme butonunu INVISIBLE yapmak.
        }

        textViewTamam = findViewById(R.id.textViewTamam);
        textViewTamam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rehbereEklemeDurumu){
                    FirebaseVeriGonder.FBGlistePaylasma(vt,liste);
                    // listeyi paylaşma işlemini sayfadan ayrılırken yapıyoruz her eklemeye ayrı ayrı gönderip cihazı yormayalım.
                }

                Intent intent = new Intent(getApplicationContext(),ListeInfoActivity.class);
                intent.putExtra("nesneListe",liste);
                startActivity(intent);
                finish();
            }
        });



        listeYenile();

        showContacts();



    }

    private void listeYenile(){
        listeArrayList = new ArrayList<>();
        vt = new VeriTabaniYardimcisi(RehberActivity.this);
        listeArrayList = new RehberDao().tumRehber(vt);
        adapter = new RehberAdapter(
                RehberActivity.this,listeArrayList,liste.getListe_id(),integersID);
        rv.setAdapter(adapter);
    }

    /*public void personelSorgulamaWeb(final String tel){
        new RehberDao().listeSil(vt);
        String url = "https://callbellapp.xyz/project/ourlist_tablo1_personel/find_table1_tel.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.e("response",response);
                //Toast.makeText(getApplicationContext(),isletmeID,Toast.LENGTH_LONG).show();

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray liste = jsonObject.getJSONArray("tablo1");

                    for (int i = 0; i<liste.length(); i++ ) {

                        JSONObject q = liste.getJSONObject(i);

                        int id = q.getInt("id");
                        String uid = q.getString("uid");
                        String tel = q.getString("tel");
                        String name = q.getString("name");

                        Personel personel = new Personel(id,name,uid,tel);
                        //burada rehberi komple silmek mi? yoksa düzenlemek mi ?
                        new RehberDao().RehbereEkle(vt,name,uid,tel);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                listeYenile();
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
                params.put("tel",tel);
                return params;
            }
        };
        Volley.newRequestQueue(RehberActivity.this).add(stringRequest);
    }

    uidlist

    */

    public void personelToptanSorgulamaWebIDile(){
        //new RehberDao().listeSil(vt);

        final String LOKAL_REHBER_ID = MainActivity.spBilgiler.getString("LOKAL_REHBER_ID","null");
        Toast.makeText(this, LOKAL_REHBER_ID, Toast.LENGTH_SHORT).show();
        // 3 satır eklendi

        String url = "https://callbellapp.xyz/project/ourlist_tablo1_personel/find_table1_uid_list.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.e("response",response);
                //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                HashMap<Integer,Personel> personelHashMap = new HashMap<>();

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray liste = jsonObject.getJSONArray("tablo1");

                    for (int i = 0; i<liste.length(); i++ ) {

                        JSONObject q = liste.getJSONObject(i);

                        int id = q.getInt("id");
                        String uid = q.getString("uid");
                        String tel = q.getString("tel");
                        String name = q.getString("name");

                        Personel personel = new Personel(id,name.trim(),uid.trim(),tel.trim());
                        //burada rehberi komple silmek mi? yoksa düzenlemek mi ?
                        //new RehberDao().RehbereEkle(vt,name,uid,tel);
                        Log.e("HashSet",id+name+tel+uid);

                        personelHashMap.put(id,personel);
                        // her personelin 1 defa kayıt edilmesi için bu yöntem denendi.HasSet yapısı çalışmadı.
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (Personel p: personelHashMap.values()) {
                    new RehberDao().RehbereEkle(vt,p.getPersonel_adi(),p.getPersonel_uid(),p.getPersonel_tel());
                }
                listeYenile();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), res.getString(R.string.kayit_hatali_lutfen_kontrol_ediniz),Toast.LENGTH_SHORT).show();
                Log.e("response", String.valueOf(error));
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uidlist",LOKAL_REHBER_ID);
                return params;
            }
        };
        Volley.newRequestQueue(RehberActivity.this).add(stringRequest);
    }


    public void personelToptanSorgulamaWeb(final String tel){
        new RehberDao().listeSil(vt);

        String LOKAL_REHBER = MainActivity.spBilgiler.getString("LOKAL_REHBER","+9000005151500051515");
        final String telefon = LOKAL_REHBER + tel;
        Toast.makeText(this, telefon, Toast.LENGTH_SHORT).show();
        // 3 satır eklendi

        String url = "https://callbellapp.xyz/project/ourlist_tablo1_personel/find_table1_tel_list.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.e("response",response);
                //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                HashMap<Integer,Personel> personelHashMap = new HashMap<>();

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray liste = jsonObject.getJSONArray("tablo1");

                    for (int i = 0; i<liste.length(); i++ ) {

                        JSONObject q = liste.getJSONObject(i);

                        int id = q.getInt("id");
                        String uid = q.getString("uid");
                        String tel = q.getString("tel");
                        String name = q.getString("name");

                        Personel personel = new Personel(id,name.trim(),uid.trim(),tel.trim());
                        //burada rehberi komple silmek mi? yoksa düzenlemek mi ?
                        //new RehberDao().RehbereEkle(vt,name,uid,tel);
                        Log.e("HashSet",id+name+tel+uid);

                        personelHashMap.put(id,personel);
                        // her personelin 1 defa kayıt edilmesi için bu yöntem denendi.HasSet yapısı çalışmadı.
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (Personel p: personelHashMap.values()) {
                    new RehberDao().RehbereEkle(vt,p.getPersonel_adi(),p.getPersonel_uid(),p.getPersonel_tel());
                }
                personelToptanSorgulamaWebIDile();
                //listeYenile();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), res.getString(R.string.kayit_hatali_lutfen_kontrol_ediniz),Toast.LENGTH_SHORT).show();
                Log.e("response", String.valueOf(error));
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("telList",telefon);
                return params;
            }
        };
        Volley.newRequestQueue(RehberActivity.this).add(stringRequest);
    }

    public void rehberdenNumaraCekme (){
        stringTopla   =   new    StringBuilder ( ) ;

        ContentResolver cr = getContentResolver();
        Cursor cursor = null;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null);

            while (cursor.moveToNext()){
                String phone = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER));
                String phoneArray[] = phone.split(" ");
                phone = "";
                for (String a:phoneArray) {
                    phone += a;
                }
                stringTopla . append (phone +";") ;
            }
            cursor.close();
            //Toast.makeText(this, stringTopla.toString(), Toast.LENGTH_SHORT).show();
            personelToptanSorgulamaWeb(stringTopla.toString());
            //personelToptanSorgulamaWeb("+905535212682;+905380128558;+90123456789");
            //https://www.javahelps.com/2015/10/android-60-runtime-permission-model.html
        }else {// else sona alındı
            Toast.makeText(this, res.getString(R.string.veri_tabani_guncellemede_hata), Toast.LENGTH_LONG).show();
            //return;
            personelToptanSorgulamaWeb("+5553555252125");  //burası eklendi
        }
    }

    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            rehberdenNumaraCekme();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

//burasının altı, rehberden numara çekme ve personel web sorgulama eklentisi var...

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_rehber_kisi_ekle,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.action_add_personal:

                startActivity(new Intent(getApplicationContext(),RehberePersonelEklemeActivity.class));

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}