package com.callbellapp.ourlist;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class RehberDao {
    public void RehbereEkle (VeriTabaniYardimcisi vt, String personel_adi
            , String personel_uid, String personel_tel){

            SQLiteDatabase dbx = vt.getWritableDatabase();
            ContentValues degerler = new ContentValues();

            degerler.put("personel_adi", personel_adi);
            degerler.put("personel_uid", "-"+personel_uid);
            degerler.put("personel_tel", personel_tel);

            dbx.insertOrThrow("personel",null,degerler);
            dbx.close();
    }
    public ArrayList<Personel> tumRehber (VeriTabaniYardimcisi vt){
        ArrayList<Personel> listeArrayList = new ArrayList<>();
        SQLiteDatabase dbx = vt.getWritableDatabase();
        Cursor c = dbx.rawQuery("SELECT * FROM personel",null);

        while (c.moveToNext()){
            Personel personel = new Personel(
                     c.getInt(c.getColumnIndex("personel_id"))
                    ,c.getString(c.getColumnIndex("personel_adi"))
                    ,c.getString(c.getColumnIndex("personel_uid"))
                    ,c.getString(c.getColumnIndex("personel_tel")));
            listeArrayList.add(personel);
        }
        return listeArrayList;
    }

    public ArrayList<Personel> personelGetirUIDile (VeriTabaniYardimcisi vt,String UID){
        ArrayList<Personel> listeArrayList = new ArrayList<>();
        SQLiteDatabase dbx = vt.getWritableDatabase();
        Cursor c = dbx.rawQuery("SELECT * FROM personel WHERE personel.personel_uid LIKE '%"+UID+"'",null);

        while (c.moveToNext()){
            Personel personel = new Personel(
                    c.getInt(c.getColumnIndex("personel_id"))
                    ,c.getString(c.getColumnIndex("personel_adi"))
                    ,c.getString(c.getColumnIndex("personel_uid"))
                    ,c.getString(c.getColumnIndex("personel_tel")));
            listeArrayList.add(personel);
        }
        return listeArrayList;
    }

    public void perosnelSil(VeriTabaniYardimcisi vt, int personel_id){
        SQLiteDatabase dbx = vt.getWritableDatabase();
        dbx.delete("personel","personel_id=?",new String[]{String.valueOf(personel_id)});
        dbx.close();
    }
    public void listeSil(VeriTabaniYardimcisi vt){
        SQLiteDatabase dbx = vt.getWritableDatabase();
        dbx.delete("personel",null,null);
        dbx.close();
    }

    public void personelGuncelle(VeriTabaniYardimcisi vt, int personel_id, String personel_adi, String personel_uid, String personel_tel ){
        SQLiteDatabase dbx = vt.getWritableDatabase();
        ContentValues degerler = new ContentValues();

        degerler.put("personel_adi", personel_adi);
        degerler.put("personel_uid",personel_uid);
        degerler.put("personel_tel", personel_tel);

        dbx.update("personel",degerler,"personel_id=?",new String[]{String.valueOf(personel_id)});
        dbx.close();
    }
}
