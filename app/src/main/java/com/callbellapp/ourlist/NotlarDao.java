package com.callbellapp.ourlist;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class NotlarDao {
    public void notEkle (VeriTabaniYardimcisi vt, String check_box, String not_tamamlayan, String not_adi
            , String not_detay, int not_liste_id, String not_global_key){

        SQLiteDatabase dbx = vt.getWritableDatabase();
        ContentValues degerler = new ContentValues();

        degerler.put("check_box",check_box);
        degerler.put("not_tamamlayan",not_tamamlayan);
        degerler.put("not_adi", not_adi);
        degerler.put("not_detay",not_detay);
        degerler.put("not_liste_id", String.valueOf(not_liste_id));
        degerler.put("not_global_key",not_global_key);

        dbx.insertOrThrow("notlar",null,degerler);
        dbx.close();
    }
    public ArrayList<Notlar> tumNotlar (VeriTabaniYardimcisi vt,int not_liste_id){
        ArrayList<Notlar> listeArrayList = new ArrayList<>();
        SQLiteDatabase dbx = vt.getWritableDatabase();
        Cursor c = dbx.rawQuery("SELECT * FROM notlar WHERE  notlar.not_liste_id ="+not_liste_id ,null);

        while (c.moveToNext()){
            Notlar not = new Notlar(
                    c.getInt(c.getColumnIndex("not_id"))
                    ,c.getString(c.getColumnIndex("check_box"))
                    ,c.getString(c.getColumnIndex("not_tamamlayan"))
                    ,c.getString(c.getColumnIndex("not_adi"))
                    ,c.getInt(c.getColumnIndex("not_liste_id"))
                    ,c.getString(c.getColumnIndex("not_global_key"))
                    ,c.getString(c.getColumnIndex("not_detay")));
            listeArrayList.add(not);
        }
        return listeArrayList;
    }
    public ArrayList<Notlar> tumNotlarAzalan (VeriTabaniYardimcisi vt,int not_liste_id){
        ArrayList<Notlar> listeArrayList = new ArrayList<>();
        SQLiteDatabase dbx = vt.getWritableDatabase();
        Cursor c = dbx.rawQuery("SELECT * FROM notlar WHERE  notlar.not_liste_id ="+not_liste_id
                +" ORDER BY not_id DESC ",null);

        while (c.moveToNext()){
            Notlar not = new Notlar(
                    c.getInt(c.getColumnIndex("not_id"))
                    ,c.getString(c.getColumnIndex("check_box"))
                    ,c.getString(c.getColumnIndex("not_tamamlayan"))
                    ,c.getString(c.getColumnIndex("not_adi"))
                    ,c.getInt(c.getColumnIndex("not_liste_id"))
                    ,c.getString(c.getColumnIndex("not_global_key"))
                    ,c.getString(c.getColumnIndex("not_detay")));
            listeArrayList.add(not);
        }
        return listeArrayList;
    }

    public ArrayList<Notlar> tekNotID (VeriTabaniYardimcisi vt,int not_id){
        ArrayList<Notlar> listeArrayList = new ArrayList<>();
        SQLiteDatabase dbx = vt.getWritableDatabase();
        Cursor c = dbx.rawQuery("SELECT * FROM notlar WHERE  notlar.not_id ="+not_id,null);

        while (c.moveToNext()){
            Notlar not = new Notlar(
                    c.getInt(c.getColumnIndex("not_id"))
                    ,c.getString(c.getColumnIndex("check_box"))
                    ,c.getString(c.getColumnIndex("not_tamamlayan"))
                    ,c.getString(c.getColumnIndex("not_adi"))
                    ,c.getInt(c.getColumnIndex("not_liste_id"))
                    ,c.getString(c.getColumnIndex("not_global_key"))
                    ,c.getString(c.getColumnIndex("not_detay")));
            listeArrayList.add(not);
        }
        return listeArrayList;
    }

    public ArrayList<Notlar> tekNotGlobalKey (VeriTabaniYardimcisi vt, String not_global_key){
        ArrayList<Notlar> listeArrayList = new ArrayList<>();
        SQLiteDatabase dbx = vt.getWritableDatabase();
        Cursor c = dbx.rawQuery("SELECT * FROM notlar WHERE  " +
                "notlar.not_global_key LIKE  '%"+not_global_key+"'",null);

        while (c.moveToNext()){
            Notlar not = new Notlar(
                    c.getInt(c.getColumnIndex("not_id"))
                    ,c.getString(c.getColumnIndex("check_box"))
                    ,c.getString(c.getColumnIndex("not_tamamlayan"))
                    ,c.getString(c.getColumnIndex("not_adi"))
                    ,c.getInt(c.getColumnIndex("not_liste_id"))
                    ,c.getString(c.getColumnIndex("not_global_key"))
                    ,c.getString(c.getColumnIndex("not_detay")));
            listeArrayList.add(not);
        }
        return listeArrayList;
    }

    public void notSil(VeriTabaniYardimcisi vt, int not_id){
        SQLiteDatabase dbx = vt.getWritableDatabase();
        dbx.delete("notlar","not_id=?",new String[]{String.valueOf(not_id)});
        dbx.close();
    }

    public void notSilGlobalKeyile(VeriTabaniYardimcisi vt, String not_global_key){
        SQLiteDatabase dbx = vt.getWritableDatabase();
        dbx.delete("notlar","not_global_key=?",new String[]{not_global_key});
        dbx.close();
    }


    public void notGuncelle(VeriTabaniYardimcisi vt, int not_id, String check_box, String not_tamamlayan, String not_adi, String not_detay, String not_global_key ){
        SQLiteDatabase dbx = vt.getWritableDatabase();
        ContentValues degerler = new ContentValues();

        degerler.put("check_box",check_box);
        degerler.put("not_tamamlayan",not_tamamlayan);
        degerler.put("not_adi", not_adi);
        degerler.put("not_detay",not_detay);
        degerler.put("not_global_key",not_global_key);

        dbx.update("notlar",degerler,"not_id=?",new String[]{String.valueOf(not_id)});
        dbx.close();
    }

    public void notGlobalKeyGuncelle(VeriTabaniYardimcisi vt, int not_id,  String not_global_key ){
        SQLiteDatabase dbx = vt.getWritableDatabase();
        ContentValues degerler = new ContentValues();

        degerler.put("not_global_key",not_global_key);

        dbx.update("notlar",degerler,"not_id=?",new String[]{String.valueOf(not_id)});
        dbx.close();
    }

    public void notCheckBoxGuncelle(VeriTabaniYardimcisi vt, int not_id, String check_box, String not_tamamlayan){
        SQLiteDatabase dbx = vt.getWritableDatabase();
        ContentValues degerler = new ContentValues();

        degerler.put("check_box",check_box);
        degerler.put("not_tamamlayan", not_tamamlayan);

        dbx.update("notlar",degerler,"not_id=?",new String[]{String.valueOf(not_id)});
        dbx.close();
    }

    public void guncelleNotListeID(VeriTabaniYardimcisi vt, int not_id, int yeni_not_liste_id ){
        SQLiteDatabase dbx = vt.getWritableDatabase();
        ContentValues degerler = new ContentValues();

        degerler.put("not_liste_id",yeni_not_liste_id);

        dbx.update("notlar",degerler,"not_id=?",new String[]{String.valueOf(not_id)});
        dbx.close();
    }

    public void notGuncelleGlobalKeyile(VeriTabaniYardimcisi vt
            , String check_box, String not_tamamlayan, String not_adi, String not_detay, String not_global_key ){

        SQLiteDatabase dbx = vt.getWritableDatabase();
        ContentValues degerler = new ContentValues();

        degerler.put("check_box",check_box);
        degerler.put("not_tamamlayan", not_tamamlayan);
        degerler.put("not_adi", not_adi);
        degerler.put("not_detay",not_detay);
        degerler.put("not_global_key",not_global_key);

        dbx.update("notlar",degerler,"not_global_key=?",new String[]{not_global_key});
        dbx.close();
    }

}
