package com.callbellapp.ourlist;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class ListePaylasilanPersonelDao {

    public void listeEkle (VeriTabaniYardimcisi vt, int personel_liste_id, String perseonel_uid){

            SQLiteDatabase dbx = vt.getWritableDatabase();
            ContentValues degerler = new ContentValues();

            degerler.put("personel_liste_id", personel_liste_id);
            degerler.put("perseonel_uid", perseonel_uid);

            dbx.insertOrThrow("ListePaylasilanPersonel", null, degerler);

            dbx.close();
            Log.e("hata","11");

    }

    public ArrayList<ListePaylasilanPersonel> tumListeler (VeriTabaniYardimcisi vt){
        ArrayList<ListePaylasilanPersonel> listeArrayList = new ArrayList<>();
        SQLiteDatabase dbx = vt.getWritableDatabase();
        Cursor c = dbx.rawQuery("SELECT * FROM ListePaylasilanPersonel , personel WHERE " +
                "ListePaylasilanPersonel.perseonel_uid = personel.personel_uid",null);

        while (c.moveToNext()){
            Personel personel = new Personel(
                     c.getInt(c.getColumnIndex("personel_id"))
                    ,c.getString(c.getColumnIndex("personel_adi"))
                    ,c.getString(c.getColumnIndex("personel_uid"))
                    ,c.getString(c.getColumnIndex("personel_tel")));

            ListePaylasilanPersonel liste = new ListePaylasilanPersonel(
                     c.getInt(c.getColumnIndex("paylasilan_personel_id"))
                    ,c.getInt(c.getColumnIndex("personel_liste_id")) //liste id
                    ,personel
                    ,c.getInt(c.getColumnIndex("yoneticimi")));
            listeArrayList.add(liste);
        }
        return listeArrayList;
    }

    public ArrayList<ListePaylasilanPersonel> PerosnelGetirListesi (VeriTabaniYardimcisi vt,int personel_liste_id){
        ArrayList<ListePaylasilanPersonel> listeArrayList = new ArrayList<>();
        SQLiteDatabase dbx = vt.getWritableDatabase();

            Cursor c = dbx.rawQuery("SELECT * FROM ListePaylasilanPersonel , personel WHERE " +
                    "ListePaylasilanPersonel.perseonel_uid = personel.personel_uid AND " +
                    "ListePaylasilanPersonel.personel_liste_id = " + personel_liste_id,null);

            while (c.moveToNext()){
                Personel personel = new Personel(
                         c.getInt(c.getColumnIndex("personel_id"))
                        ,c.getString(c.getColumnIndex("personel_adi"))
                        ,c.getString(c.getColumnIndex("personel_uid"))
                        ,c.getString(c.getColumnIndex("personel_tel")));

                ListePaylasilanPersonel liste = new ListePaylasilanPersonel(
                         c.getInt(c.getColumnIndex("paylasilan_personel_id"))
                        ,c.getInt(c.getColumnIndex("personel_liste_id"))
                        ,personel
                        ,c.getInt(c.getColumnIndex("yoneticimi")) );

                listeArrayList.add(liste);
            }

        return listeArrayList;
    }

    public ArrayList<ListePaylasilanPersonel> PerosnelGetirListesiPersonelUIDile (VeriTabaniYardimcisi vt
            ,int personel_liste_id ,String personel_uid ){
        ArrayList<ListePaylasilanPersonel> listeArrayList = new ArrayList<>();
        SQLiteDatabase dbx = vt.getWritableDatabase();

        Cursor c = dbx.rawQuery("SELECT * FROM ListePaylasilanPersonel , personel WHERE " +
                "ListePaylasilanPersonel.perseonel_uid = personel.personel_uid AND " +
                "ListePaylasilanPersonel.personel_liste_id = " + personel_liste_id +
                " AND ListePaylasilanPersonel.perseonel_uid LIKE '%"+personel_uid+"'",null);

        while (c.moveToNext()){
            Personel personel = new Personel(
                    c.getInt(c.getColumnIndex("personel_id"))
                    ,c.getString(c.getColumnIndex("personel_adi"))
                    ,c.getString(c.getColumnIndex("personel_uid"))
                    ,c.getString(c.getColumnIndex("personel_tel")));

            ListePaylasilanPersonel liste = new ListePaylasilanPersonel(
                    c.getInt(c.getColumnIndex("paylasilan_personel_id"))
                    ,c.getInt(c.getColumnIndex("personel_liste_id"))
                    ,personel
                    ,c.getInt(c.getColumnIndex("yoneticimi")) );

            listeArrayList.add(liste);
        }

        return listeArrayList;
    }

    public void listedenSil(VeriTabaniYardimcisi vt, int paylasilan_personel_id){
        SQLiteDatabase dbx = vt.getWritableDatabase();
        dbx.delete("ListePaylasilanPersonel","paylasilan_personel_id =?"
                , new String[]{String.valueOf(paylasilan_personel_id)});
        dbx.close();
    }


    public void listeYoneticimiGuncelleUIDile(VeriTabaniYardimcisi vt,String perseonel_uid, int yoneticimi){
        SQLiteDatabase dbx = vt.getWritableDatabase();
        ContentValues degerler = new ContentValues();

        degerler.put("yoneticimi",yoneticimi);

        dbx.update("ListePaylasilanPersonel",degerler,"perseonel_uid=?"
                ,new String[]{String.valueOf(perseonel_uid)});
        dbx.close();
    }
    public void listeYoneticimiGuncelle(VeriTabaniYardimcisi vt,int paylasilan_personel_id, int yoneticimi){
        SQLiteDatabase dbx = vt.getWritableDatabase();
        ContentValues degerler = new ContentValues();

        degerler.put("yoneticimi",yoneticimi);

        dbx.update("ListePaylasilanPersonel",degerler,"paylasilan_personel_id=?"
                ,new String[]{String.valueOf(paylasilan_personel_id)});
        dbx.close();
    }

    public void listeGuncelle(VeriTabaniYardimcisi vt, int paylasilan_personel_id, int personel_liste_id
            , String perseonel_uid, String yoneticimi){
        SQLiteDatabase dbx = vt.getWritableDatabase();
        ContentValues degerler = new ContentValues();

        degerler.put("personel_liste_id",personel_liste_id);
        degerler.put("perseonel_uid", perseonel_uid);
        degerler.put("yoneticimi",yoneticimi);

        dbx.update("ListePaylasilanPersonel",degerler,"paylasilan_personel_id=?"
                ,new String[]{String.valueOf(paylasilan_personel_id)});
        dbx.close();
    }

}
