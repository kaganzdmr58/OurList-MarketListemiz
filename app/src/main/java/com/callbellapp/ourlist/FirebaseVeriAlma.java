package com.callbellapp.ourlist;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class FirebaseVeriAlma {

    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();


    public static void FVAListeAlmaIslem(VeriTabaniYardimcisi vt, FirebaseIslem f){
        //LISTE_PAYLAS

        // Liste için master olan cihaz listeye eklediğindeburası çalışacak
        // Liste global key ile liste id bölümüne ekleme yapılacak.

        // liste id ile sorgulama yapılacak global liste key varsa döndürülecek yoksa 0 döndürülecek.
        List<Liste> liste = new ListelerDao().ListeAraGlobalKey(vt,f.getListeKey());
        String islem[] = f.getIslem().split(":");

        if (liste.size() == 0){     //liste yoksa
            //islem ilk index liste adı.
            new ListelerDao().listeEkle(vt,f.getListeKey(),islem[0],1);
            // liste ID ile global KEY yardımı ile liste global 'e ekleme işlemi yapılıyor.
            ArrayList<Liste> listeArrayList = new ListelerDao().ListeAraGlobalKey(vt,f.getListeKey());
            for (int i =2; i < islem.length ; i++) {
                if (islem[i].equals ("-"+mAuth.getUid()) ){
                    new ListePaylasilanPersonelDao().listeEkle(vt, listeArrayList.get(0).getListe_id(), islem[1] );
                }else {
                    new ListePaylasilanPersonelDao().listeEkle(vt, listeArrayList.get(0).getListe_id(), islem[i] );
                }
                // gelen uid ler liste id ile eşlenerek listeye ekleniyor.
                // NOT : Rehberde olmayanlar var ise kısmına ayrıca bak.
            }
        }else {
            // liste var ise
            //liste adı değiştirme
            new ListelerDao().listeGuncelleGlobalKeyileListeAdiDegistirme(vt,islem[0],f.getListeKey());
            //Liste paylasilan personel güncelleme (önce hepsini sil sonra yeni listeyi ekle)
            //silme
            ArrayList<Liste> listeArrayList = new ListelerDao().ListeAraGlobalKey(vt,f.getListeKey());
            ArrayList<ListePaylasilanPersonel> PersonelListeArrayList = new ListePaylasilanPersonelDao()
                    .PerosnelGetirListesi(vt,listeArrayList.get(0).getListe_id());
            for (ListePaylasilanPersonel l: PersonelListeArrayList) {
                new ListePaylasilanPersonelDao().listedenSil(vt,l.getPaylasilan_personel_id());
            }

            //ekleme
            for (int i =2; i < islem.length; i++) {
                if (islem[i].equals ("-"+mAuth.getUid()) ){
                    new ListePaylasilanPersonelDao().listeEkle(vt, listeArrayList.get(0).getListe_id(), islem[1] );
                }else {
                    new ListePaylasilanPersonelDao().listeEkle(vt, listeArrayList.get(0).getListe_id(), islem[i] );
                }
                // gelen uid ler liste id ile eşlenerek listeye ekleniyor.
                // NOT : Rehberde olmayanlar var ise kısmına ayrıca bak.
            }

        }
        

        List<Liste> listeMainDegistir = new ListelerDao().ListeAraGlobalKey(vt,f.getListeKey());
        MainActivity.nesneListe = listeMainDegistir.get(0);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("islemListesi").child("-"+mAuth.getUid()).child(f.getIslemID());
        mDatabase.removeValue();

        new ListelerDao().listeYoneticimiGuncelle(vt,listeMainDegistir.get(0).getListe_id(),0);
        new ListePaylasilanPersonelDao().listeYoneticimiGuncelleUIDile(vt, islem[1], 1);
    }


    public static void FVAListeSilGlobalKeyile(VeriTabaniYardimcisi vt, FirebaseIslem f){

        //Liste varmı kontrol edilecek.
        List<Liste> liste = new ListelerDao().ListeAraGlobalKey(vt,f.getListeKey());
        if (liste.size() > 0){
            //
            ArrayList<ListePaylasilanPersonel> listePaylasilanPersonels=new ListePaylasilanPersonelDao()
                    .PerosnelGetirListesiPersonelUIDile(vt,liste.get(0).getListe_id(),f.getIslem());

            if (listePaylasilanPersonels.size() > 0){
                // eğer liste dolu ise silme işlemi yap.
                new ListePaylasilanPersonelDao().listedenSil(vt,listePaylasilanPersonels.get(0).getPaylasilan_personel_id());
            }

        }

        // firebase verileri temizleniyor.
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("islemListesi").child("-"+mAuth.getUid()).child(f.getIslemID());
        mDatabase.removeValue();

    }

    public static void FVAListePaylasilanPersonelleriTemizle(VeriTabaniYardimcisi vt, FirebaseIslem f){
        //Listeden aforoz edilme.
        String islem[] = f.getIslem().split(":");
        for (String s:islem) {
            // aforoz listesinde mauth.uid varsa listeyi silmeyecek ancak paylaşılan kişiler silincek.
            List<Liste> liste = new ListelerDao().ListeAraGlobalKey(vt,f.getListeKey());
            if (liste.size() > 0) {
             // liste var ise
                if (s.equals("-" + mAuth.getUid())) {
                    //Liste varmı kontrol edilecek.
                    // listede varsa yapılacak.
                    ArrayList<ListePaylasilanPersonel> listePaylasilanPersonels = new ListePaylasilanPersonelDao()
                            .PerosnelGetirListesi(vt, liste.get(0).getListe_id());

                    if (listePaylasilanPersonels.size() > 0) {
                        // eğer liste dolu ise silme işlemi yap.
                        for (ListePaylasilanPersonel l : listePaylasilanPersonels) {
                            new ListePaylasilanPersonelDao().listedenSil(vt, l.getPaylasilan_personel_id());
                        }
                    }
                    // liste paylaşılanlar silindikten sonra liste yoneticisi yapılacak.
                    new ListelerDao().listeYoneticimiGuncelle(vt, liste.get(0).getListe_id(), 1);
                } else {
                    ArrayList<ListePaylasilanPersonel> listePaylasilanPersonels = new ListePaylasilanPersonelDao()
                            .PerosnelGetirListesiPersonelUIDile(vt,liste.get(0).getListe_id(),s);
                    if(listePaylasilanPersonels.size()>0) {
                        new ListePaylasilanPersonelDao().listedenSil(vt, listePaylasilanPersonels.get(0).getPaylasilan_personel_id());
                    }
                }
            }
        }


        // firebase verileri temizleniyor.
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("islemListesi").child("-"+mAuth.getUid()).child(f.getIslemID());
        mDatabase.removeValue();

    }

    public static void FVAListeYoneticiEklemeIslemleri(VeriTabaniYardimcisi vt, FirebaseIslem f){

        String islem[] = f.getIslem().split(":");

        List<Liste> liste = new ListelerDao().ListeAraGlobalKey(vt,f.getListeKey());
        if (liste.size() > 0) {
            ArrayList<ListePaylasilanPersonel> listePaylasilanPersonels = new ListePaylasilanPersonelDao()
                    .PerosnelGetirListesi(vt, liste.get(0).getListe_id());

            for (String s:islem) {
                if (s.equals ("-"+mAuth.getUid())){
                    // eğer kullanıcı cihazı yonetici olarak seçilmiş ise listenin verisini güncelleme
                    new ListelerDao().listeYoneticimiGuncelle(vt,liste.get(0).getListe_id(),1);
                }else {
                    for (ListePaylasilanPersonel a : listePaylasilanPersonels) {
                        if (s == a.getPerseonel_uid().getPersonel_uid()) {
                            new ListePaylasilanPersonelDao().listeYoneticimiGuncelle(vt, a.getPaylasilan_personel_id(), 1);
                        }
                    }
                }
            }
        }

        // firebase verileri temizleniyor.
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("islemListesi").child("-"+mAuth.getUid()).child(f.getIslemID());
        mDatabase.removeValue();

        List<Liste> listeMainDegistir = new ListelerDao().ListeAraGlobalKey(vt,f.getListeKey());
        MainActivity.nesneListe = listeMainDegistir.get(0);

    }

    public static void FVAListeYoneticiSilmeIslemleri(VeriTabaniYardimcisi vt, FirebaseIslem f){

        String islem[] = f.getIslem().split(":");

        List<Liste> liste = new ListelerDao().ListeAraGlobalKey(vt,f.getListeKey());
        if (liste.size() > 0) {
            ArrayList<ListePaylasilanPersonel> listePaylasilanPersonels = new ListePaylasilanPersonelDao()
                    .PerosnelGetirListesi(vt, liste.get(0).getListe_id());

            if (listePaylasilanPersonels.size() > 0) {

                for (String s:islem) {
                    if (s == ("-"+mAuth.getUid())){
                        new ListelerDao().listeYoneticimiGuncelle(vt,liste.get(0).getListe_id(),0);
                    }else {
                        for (ListePaylasilanPersonel a : listePaylasilanPersonels) {
                            if (s == a.getPerseonel_uid().getPersonel_uid()) {
                                new ListePaylasilanPersonelDao().listeYoneticimiGuncelleUIDile(vt, s, 0);
                            }
                        }
                    }
                }
            }
        }

        // firebase verileri temizleniyor.
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("islemListesi").child("-"+mAuth.getUid()).child(f.getIslemID());
        mDatabase.removeValue();

    }










    public static void FVAnotAlmaIslem(VeriTabaniYardimcisi vt, FirebaseIslem f){
        // liste id ile sorgulama yapılacak global liste key varsa döndürülecek yoksa 0 döndürülecek.
        List<Liste> liste = new ListelerDao().ListeAraGlobalKey(vt,f.getListeKey());

        if (liste.size() > 0){
            String islem[] = f.getIslem().split(":");

            StringBuilder s = new StringBuilder();
            for (int i =5; i<islem.length; i++){
                s.append(islem[i]);
            }

            Notlar not = new Notlar(
                    Integer.parseInt(islem[0])
                    ,islem[1]
                    ,islem[2]
                    ,islem[3]
                    ,liste.get(0).getListe_id()
                    ,islem[4]
                    ,s.toString());

            ArrayList<Notlar> notlarListe = new NotlarDao().tekNotGlobalKey(vt,not.getNot_global_key());

            if (notlarListe.size()> 0){
                new NotlarDao().notGuncelleGlobalKeyile(vt,not.getCheck_box(),not.getNot_tamamlayan()
                        ,not.getNot_adi(),not.getNot_detay(),not.getNot_global_key());
            }else {
                new NotlarDao().notEkle(vt,not.getCheck_box(),not.getNot_tamamlayan(),not.getNot_adi(),not.getNot_detay()
                        ,liste.get(0).getListe_id(),not.getNot_global_key());
            }

        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("islemListesi").child("-"+mAuth.getUid()).child(f.getIslemID());
        mDatabase.removeValue();

    }

    public static void FVAnotSilGlobalKeyile(VeriTabaniYardimcisi vt, FirebaseIslem f){
        String islem[] = f.getIslem().split(":");

        new NotlarDao().notSilGlobalKeyile(vt,islem[4]);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("islemListesi").child("-"+mAuth.getUid()).child(f.getIslemID());
        mDatabase.removeValue();

    }





}
