package com.callbellapp.ourlist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class VeriTabaniYardimcisi extends SQLiteOpenHelper {
    private static String veriTabaniAdi = "Notlar";
    private static  final int SURUM = 37;
    // veri tabanı değişikliklerinde sürüm güncellemeyi unutma.

    public VeriTabaniYardimcisi(@Nullable Context context) {
        super(context, veriTabaniAdi, null, SURUM);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE liste (" +
                "liste_id INTEGER PRIMARY KEY AUTOINCREMENT" +
                ",liste_global_key Text, liste_adi TEXT, yoneticimi INTEGER)");
        db.execSQL("CREATE TABLE personel (" +
                "personel_id INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", personel_adi TEXT, personel_uid TEXT, personel_tel TEXT)");
        db.execSQL("CREATE TABLE notlar (" +
                "not_id INTEGER PRIMARY KEY " +
                ", check_box TEXT, not_tamamlayan TEXT, not_adi TEXT, not_detay TEXT" +
                ", not_liste_id INTEGER, not_global_key TEXT " +
                ", FOREIGN KEY(not_liste_id) REFERENCES liste(liste_id))");
        db.execSQL("CREATE TABLE ListePaylasilanPersonel (" +
                "paylasilan_personel_id INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", personel_liste_id INTEGER, perseonel_uid TEXT, yoneticimi INTEGER " +
                ", FOREIGN KEY(perseonel_uid) REFERENCES personel(personel_uid))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS liste ");
        db.execSQL("DROP TABLE IF EXISTS personel ");
        db.execSQL("DROP TABLE IF EXISTS notlar ");
        db.execSQL("DROP TABLE IF EXISTS ListePaylasilanPersonel");
        onCreate(db);
    }
}
