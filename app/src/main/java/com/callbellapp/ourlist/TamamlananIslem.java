package com.callbellapp.ourlist;

import java.io.Serializable;

public class TamamlananIslem implements Serializable {

    private int islem_id;
    private Personel personel_uid;
    private Notlar not_global_key;
    private String tarih_saat;


    public TamamlananIslem() {
    }

    public TamamlananIslem(int islem_id, Personel personel_uid, Notlar not_global_key, String tarih_saat) {
        this.islem_id = islem_id;
        this.personel_uid = personel_uid;
        this.not_global_key = not_global_key;
        this.tarih_saat = tarih_saat;
    }

    public int getIslem_id() {
        return islem_id;
    }

    public void setIslem_id(int islem_id) {
        this.islem_id = islem_id;
    }

    public Personel getPersonel_uid() {
        return personel_uid;
    }

    public void setPersonel_uid(Personel personel_uid) {
        this.personel_uid = personel_uid;
    }

    public Notlar getNot_global_key() {
        return not_global_key;
    }

    public void setNot_global_key(Notlar not_global_key) {
        this.not_global_key = not_global_key;
    }

    public String getTarih_saat() {
        return tarih_saat;
    }

    public void setTarih_saat(String tarih_saat) {
        this.tarih_saat = tarih_saat;
    }
}
