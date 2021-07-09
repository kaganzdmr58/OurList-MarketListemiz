package com.callbellapp.ourlist;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class TamamlananIslemDao {

    public void islemEkle (VeriTabaniYardimcisi vt, String personel_uid, String not_global_key, String tarih_saat){

        SQLiteDatabase dbx = vt.getWritableDatabase();
        ContentValues degerler = new ContentValues();

        degerler.put("personel_uid",personel_uid);
        degerler.put("not_global_key", not_global_key);
        degerler.put("tarih_saat",tarih_saat);

        dbx.insertOrThrow("tamalananIslem",null,degerler);
        dbx.close();
    }

    public void islemSil(VeriTabaniYardimcisi vt, int islem_id){
        SQLiteDatabase dbx = vt.getWritableDatabase();
        dbx.delete("tamalananIslem","islem_id=?",new String[]{String.valueOf(islem_id)});
        dbx.close();
    }



    public ArrayList<TamamlananIslem> tumTamamlananNotlar (VeriTabaniYardimcisi vt){
        ArrayList<TamamlananIslem> listeArrayList = new ArrayList<>();
        SQLiteDatabase dbx = vt.getWritableDatabase();
        Cursor c = dbx.rawQuery("SELECT * FROM tamalananIslem  , notlar WHERE" +
                " tamalananIslem.not_global_key = notlar.not_global_key ",null);

        while (c.moveToNext()){

            String uid = c.getString(c.getColumnIndex("personel_uid"));
            Personel p;
            if (uid == "siz"){
                p = new Personel(0,"Siz",MainActivity.mAuth.getUid(),MainActivity.mAuth.getCurrentUser().getPhoneNumber());
            }else {
                p = new Personel(
                        c.getInt(c.getColumnIndex("personel_id"))
                        ,c.getString(c.getColumnIndex("personel_adi"))
                        ,c.getString(c.getColumnIndex("personel_uid"))
                        ,c.getString(c.getColumnIndex("personel_tel")));
            }

          // Personel p = new Personel(0,"siz","","51515");

            Notlar n = new Notlar(
                     c.getInt(c.getColumnIndex("not_id"))
                    ,c.getString(c.getColumnIndex("check_box"))
                    ,c.getString(c.getColumnIndex("not_tamamlayan"))
                    ,c.getString(c.getColumnIndex("not_adi"))
                    ,c.getInt(c.getColumnIndex("not_liste_id"))
                    ,c.getString(c.getColumnIndex("not_global_key"))
                    ,c.getString(c.getColumnIndex("not_detay")));

            TamamlananIslem islem = new TamamlananIslem(
                     c.getInt(c.getColumnIndex("islem_id"))
                    ,p
                    ,n
                    ,c.getString(c.getColumnIndex("tarih_saat")));
            listeArrayList.add(islem);
        }
        return listeArrayList;
    }

    public ArrayList<TamamlananIslem> tumTamamlananNotlarNotIDile (VeriTabaniYardimcisi vt,String not_global_key){
        ArrayList<TamamlananIslem> listeArrayList = new ArrayList<>();
        SQLiteDatabase dbx = vt.getWritableDatabase();
        Cursor c = dbx.rawQuery("SELECT * FROM tamalananIslem,personel,notlar WHERE" +
                " tamalananIslem.personel_uid = personel.personel_uid " +
                "AND tamalananIslem.not_global_key = notlar.not_global_key " +
                "AND tamalananIslem.not_global_key LIKE '%"+not_global_key+"'" ,null);

        while (c.moveToNext()){
            String uid = c.getString(c.getColumnIndex("personel_uid"));
            Personel p;
            if (uid.equals(MainActivity.mAuth.getUid())){
                p = new Personel(0,"Siz",uid,MainActivity.mAuth.getCurrentUser().getPhoneNumber());
            }else {
                p = new Personel(
                        c.getInt(c.getColumnIndex("personel_id"))
                        ,c.getString(c.getColumnIndex("personel_adi"))
                        ,c.getString(c.getColumnIndex("personel_uid"))
                        ,c.getString(c.getColumnIndex("personel_tel")));
            }
            Notlar n = new Notlar(
                     c.getInt(c.getColumnIndex("not_id"))
                    ,c.getString(c.getColumnIndex("check_box"))
                    ,c.getString(c.getColumnIndex("not_tamamlayan"))
                    ,c.getString(c.getColumnIndex("not_adi"))
                    ,c.getInt(c.getColumnIndex("not_liste_id"))
                    ,c.getString(c.getColumnIndex("not_global_key"))
                    ,c.getString(c.getColumnIndex("not_detay")));

            TamamlananIslem islem = new TamamlananIslem(
                     c.getInt(c.getColumnIndex("islem_id"))
                    ,p
                    ,n
                    ,c.getString(c.getColumnIndex("tarih_saat")));
            listeArrayList.add(islem);
        }
        return listeArrayList;
    }




}
