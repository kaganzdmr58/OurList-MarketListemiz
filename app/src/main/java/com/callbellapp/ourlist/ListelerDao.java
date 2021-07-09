package com.callbellapp.ourlist;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class ListelerDao {
    public void listeEkle (VeriTabaniYardimcisi vt, String liste_global_key, String liste_adi, int yoneticimi){
        //String listeGlobalKey = liste_global_key.
        SQLiteDatabase dbx = vt.getWritableDatabase();
        ContentValues degerler = new ContentValues();

        degerler.put("liste_adi",liste_adi);
        degerler.put("liste_global_key",liste_global_key);
        degerler.put("yoneticimi",yoneticimi);


        dbx.insertOrThrow("liste",null,degerler);
        dbx.close();
    }
    public ArrayList<Liste> tumListeler (VeriTabaniYardimcisi vt){
        ArrayList<Liste> listeArrayList = new ArrayList<>();
        SQLiteDatabase dbx = vt.getWritableDatabase();
        Cursor c = dbx.rawQuery("SELECT * FROM liste",null);

        while (c.moveToNext()){
            Liste liste = new Liste(
                     c.getInt(c.getColumnIndex("liste_id"))
                    ,c.getString(c.getColumnIndex("liste_global_key"))
                    ,c.getString(c.getColumnIndex("liste_adi"))
                    ,c.getInt(c.getColumnIndex("yoneticimi")));
            listeArrayList.add(liste);
        }
        return listeArrayList;
    }

    public void listeSil(VeriTabaniYardimcisi vt, int liste_id){
        SQLiteDatabase dbx = vt.getWritableDatabase();
        dbx.delete("liste","liste_id=?",new String[]{String.valueOf(liste_id)});
        dbx.close();
    }

    public void listeGuncelle(VeriTabaniYardimcisi vt, int liste_id, String liste_global_key, String liste_adi){
        SQLiteDatabase dbx = vt.getWritableDatabase();
        ContentValues degerler = new ContentValues();

        degerler.put("liste_adi",liste_adi);
        degerler.put("liste_global_key",liste_global_key);

        dbx.update("liste",degerler,"liste_id=?",new String[]{String.valueOf(liste_id)});
        dbx.close();
    }

    public void listeYoneticimiGuncelle(VeriTabaniYardimcisi vt, int liste_id, int yoneticimi){
        SQLiteDatabase dbx = vt.getWritableDatabase();
        ContentValues degerler = new ContentValues();

        degerler.put("yoneticimi",yoneticimi);

        dbx.update("liste",degerler,"liste_id =?",new String[]{String.valueOf(liste_id)});
        dbx.close();
    }

    public void listeGuncelleGlobalKeyi(VeriTabaniYardimcisi vt, int liste_id, String liste_global_key){
        SQLiteDatabase dbx = vt.getWritableDatabase();
        ContentValues degerler = new ContentValues();

        degerler.put("liste_global_key",liste_global_key);

        dbx.update("liste",degerler,"liste_id =?",new String[]{String.valueOf(liste_id)});
        dbx.close();
    }

    public void listeGuncelleGlobalKeyileListeAdiDegistirme(VeriTabaniYardimcisi vt, String liste_adi, String liste_global_key){
        SQLiteDatabase dbx = vt.getWritableDatabase();
        ContentValues degerler = new ContentValues();

        degerler.put("liste_adi",liste_adi);

        dbx.update("liste",degerler,"liste_global_key=?",new String[]{liste_global_key});
        dbx.close();
    }

    public ArrayList<Liste> ListeAraGlobalKey (VeriTabaniYardimcisi vt,String liste_global_key){
        ArrayList<Liste> listeArrayList = new ArrayList<>();
        SQLiteDatabase dbx = vt.getWritableDatabase();
        Cursor c = dbx.rawQuery("SELECT * FROM liste WHERE liste.liste_global_key " +
                "LIKE "+"'%"+liste_global_key+"'",null);

        while (c.moveToNext()){
            Liste liste = new Liste(
                    c.getInt(c.getColumnIndex("liste_id"))
                    ,c.getString(c.getColumnIndex("liste_global_key"))
                    ,c.getString(c.getColumnIndex("liste_adi"))
                    ,c.getInt(c.getColumnIndex("yoneticimi")));
            listeArrayList.add(liste);
        }
        return listeArrayList;
    }



}

