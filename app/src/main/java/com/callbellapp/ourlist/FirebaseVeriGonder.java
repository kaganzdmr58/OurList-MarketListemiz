package com.callbellapp.ourlist;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseVeriGonder {

    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public static String ListeGlobalKeyAlma(VeriTabaniYardimcisi vt, Liste liste){
        String key = liste.getListe_global_key();
        // liste id ile sorgulama yapılacak global liste key varsa döndürülecek yoksa null döndürülecek.
        if (liste.getListe_global_key().length() < 7){
            // listenin global key'i alınacak bu key artık bütün cihazlardan listeye ulaşmak için global key olucak.
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("islemListesi").child("-"+mAuth.getUid());
            key = mDatabase.push().getKey();
            //mDatabase.child(key).setValue("Hello, World!");

            if (liste.getListe_id()!=0) {
                // liste ID ile global KEY yardımı ile liste global 'e ekleme işlemi yapılıyor.
                new ListelerDao().listeGuncelleGlobalKeyi(vt, liste.getListe_id(), key);
            }
        }
        return key;
    }

    public static void FBGveriGonderme(VeriTabaniYardimcisi vt, int liste_id ,FirebaseIslem firebaseIslem){
        List<ListePaylasilanPersonel> listePaylasilanPersonel =
                new ListePaylasilanPersonelDao().PerosnelGetirListesi(vt,liste_id);

        for (ListePaylasilanPersonel l:listePaylasilanPersonel) {
            if (!(l.getPerseonel_uid().getPersonel_uid().equals("-"+mAuth.getUid()))) {
                // kendi kendimize tekrardan veri göndermeyelim.
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                        .child("islemListesi").child(l.getPerseonel_uid().getPersonel_uid());
                Map<String, Object> bilgi = new HashMap<>();
                bilgi.put("islemID", "");
                bilgi.put("islemTipi", firebaseIslem.getIslemTipi());
                bilgi.put("listeKey", firebaseIslem.getListeKey());
                bilgi.put("islem", firebaseIslem.getIslem());
                mDatabase.push().setValue(bilgi);// push işlemID kısmı.
                //Log.e("firebase",l.getPerseonel_uid().getPersonel_uid());
            }
        }
    }

    public static void FBGlistePaylasma(VeriTabaniYardimcisi vt, Liste liste){

            List<ListePaylasilanPersonel> listePaylasilanPersonel =
                    new ListePaylasilanPersonelDao().PerosnelGetirListesi(vt, liste.getListe_id());

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(liste.getListe_adi() + ":");
            stringBuilder.append("-" + mAuth.getUid() + ":");
            // burada ilk index liste adı olarak gönderiliyor karşıdan o şekilde alınacak.

            for (ListePaylasilanPersonel l : listePaylasilanPersonel) {
                stringBuilder.append(l.getPerseonel_uid().getPersonel_uid() + ":");
            }

            FirebaseIslem f = new FirebaseIslem("",// islem id giden cihazda islemin id numarasıdır silmek için
                    "LISTE_PAYLAS", liste.getListe_global_key(), stringBuilder.toString());

            FBGveriGonderme(vt, liste.getListe_id(), f);

            // Listede not mevcut ise
            List<Notlar> listeNotlar = new NotlarDao().tumNotlar(vt,liste.getListe_id());
            if (listeNotlar.size() > 0){
                for (Notlar n:listeNotlar) {
                    FBGnotIslem(vt,liste,n);
                    // Veri tasarrufu için burada listeye yeni eklenen ID ye gönderim yapılabilir.
                    // Bu konu üzerine çalışılablir.
                }
            }
    }

    public static void FBGlisteSilme(VeriTabaniYardimcisi vt, Liste liste){

            // eğer listede tek yönetici bensem yerime paylaşılan listemdeki ilk kişiyi yönetici yapıp öyle ayrılmam lazım
            // eğer listede yönetici başka birisi var ise hiçbirşey yapma.
            List<ListePaylasilanPersonel> listePaylasilanPersonels = new ListePaylasilanPersonelDao()
                .PerosnelGetirListesi(vt,liste.getListe_id());
            Boolean durum = false;
            if (listePaylasilanPersonels.size()>0){

                for (ListePaylasilanPersonel l:listePaylasilanPersonels) {
                    if (l.getYoneticimi() == 1 ){
                        durum = true;
                    }
                }
                if (!durum){
                    FBGYoneticiEkleme(vt,liste,listePaylasilanPersonels.get(0).getPerseonel_uid().getPersonel_uid());
                }
                FirebaseIslem f = new FirebaseIslem("",// islem id giden cihazda islemin id numarasıdır silmek için
                        "LISTE_SIL", liste.getListe_global_key(), "-" + mAuth.getUid());

                FBGveriGonderme(vt, liste.getListe_id(), f);
            }

    }

    public static void FBGlisteAforozEtme(VeriTabaniYardimcisi vt, Liste liste, String islemAforozEdilecekUIDler){
        // personellerin uid si işlem bölmesinde gönderilecek ve karşı tarafın uid si varsa silme işlemini başlatacak.

            FirebaseIslem f = new FirebaseIslem("",// islem id giden cihazda islemin id numarasıdır silmek için
                    "LISTEDEN_AFOROZ",liste.getListe_global_key(),islemAforozEdilecekUIDler);

            FBGveriGonderme(vt,liste.getListe_id(),f);

            // bu kısım gönderimden çıktığı için burada gönderiliyor.
        String[] aforozListesi = islemAforozEdilecekUIDler.split(":");
        for (String s : aforozListesi) {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                        .child("islemListesi").child(s);
                Map<String, Object> bilgi = new HashMap<>();
                bilgi.put("islemID", "");
                bilgi.put("islemTipi", f.getIslemTipi());
                bilgi.put("listeKey", f.getListeKey());
                bilgi.put("islem", f.getIslem());
                mDatabase.push().setValue(bilgi);// push işlemID kısmı.
        }

    }

    public static void FBGYoneticiEkleme(VeriTabaniYardimcisi vt, Liste liste, String yoneticiYapilanPersonel ){

        FirebaseIslem f = new FirebaseIslem("",// islem id giden cihazda islemin id numarasıdır silmek için
                "YONETICI_EKLEME", liste.getListe_global_key(), yoneticiYapilanPersonel);

        FBGveriGonderme(vt, liste.getListe_id(), f);
    }










    public static void FBGnotIslem(VeriTabaniYardimcisi vt, Liste liste, Notlar not) {

        String key = not.getNot_global_key();
        if (not.getNot_global_key().length() < 6){
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("islemListesi").child(mAuth.getUid());
            key = mDatabase.push().getKey();
            new NotlarDao().notGlobalKeyGuncelle(vt,not.getNot_id(),key);
        }


        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(not.getNot_id()+":");
        stringBuilder.append(not.getCheck_box()+":");
        stringBuilder.append(not.getNot_tamamlayan()+":");
        stringBuilder.append(not.getNot_adi()+":");
        stringBuilder.append(key+":");
        stringBuilder.append(not.getNot_detay()+":");

        FirebaseIslem f = new FirebaseIslem("",// islem id giden cihazda islemin id numarasıdır silmek için
                "NOT_ISLEM",liste.getListe_global_key(),stringBuilder.toString());

        FBGveriGonderme(vt,liste.getListe_id(),f);

    }

    public static void FBGnotSilme(VeriTabaniYardimcisi vt, Liste liste, Notlar not) {

        if (not.getNot_global_key().length() > 5){

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(not.getNot_id()+":");
        stringBuilder.append(not.getCheck_box()+":");
        stringBuilder.append(not.getNot_tamamlayan()+":");
        stringBuilder.append(not.getNot_adi()+":");
        stringBuilder.append(not.getNot_global_key()+":");
        stringBuilder.append(not.getNot_detay()+":");

        FirebaseIslem f = new FirebaseIslem("",// islem id giden cihazda islemin id numarasıdır silmek için
                "NOT_SIL",liste.getListe_global_key(),stringBuilder.toString());

        FBGveriGonderme(vt,liste.getListe_id(),f);
        }
    }

}
