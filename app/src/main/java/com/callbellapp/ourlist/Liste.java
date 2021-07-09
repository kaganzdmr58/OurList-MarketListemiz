package com.callbellapp.ourlist;

import java.io.Serializable;

public class Liste implements Serializable {

    private int liste_id;
    private String liste_global_key;
    private String liste_adi;
    private int yoneticimi;

    public Liste() {
    }

    public Liste(int liste_id, String liste_global_key, String liste_adi, int yoneticimi) {
        this.liste_id = liste_id;
        this.liste_global_key = liste_global_key;
        this.liste_adi = liste_adi;
        this.yoneticimi = yoneticimi;
    }

    public int getListe_id() {
        return liste_id;
    }

    public void setListe_id(int liste_id) {
        this.liste_id = liste_id;
    }

    public String getListe_global_key() {
        return liste_global_key;
    }

    public void setListe_global_key(String liste_global_key) {
        this.liste_global_key = liste_global_key;
    }

    public String getListe_adi() {
        return liste_adi;
    }

    public void setListe_adi(String liste_adi) {
        this.liste_adi = liste_adi;
    }

    public int getYoneticimi() {
        return yoneticimi;
    }

    public void setYoneticimi(int yoneticimi) {
        this.yoneticimi = yoneticimi;
    }
}
