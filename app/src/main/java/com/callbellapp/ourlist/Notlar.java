package com.callbellapp.ourlist;

import java.io.Serializable;

public class Notlar implements Serializable {

    private int not_id;
    private String check_box;
    private String not_tamamlayan;
    private String not_adi;
    private int not_liste_id;
    private String not_global_key;
    private String not_detay;


    public Notlar() {
    }

    public Notlar(int not_id, String check_box, String not_tamamlayan, String not_adi, int not_liste_id, String not_global_key, String not_detay) {
        this.not_id = not_id;
        this.check_box = check_box;
        this.not_tamamlayan = not_tamamlayan;
        this.not_adi = not_adi;
        this.not_liste_id = not_liste_id;
        this.not_global_key = not_global_key;
        this.not_detay = not_detay;
    }

    public int getNot_id() {
        return not_id;
    }

    public void setNot_id(int not_id) {
        this.not_id = not_id;
    }

    public String getCheck_box() {
        return check_box;
    }

    public void setCheck_box(String check_box) {
        this.check_box = check_box;
    }

    public String getNot_tamamlayan() {
        return not_tamamlayan;
    }

    public void setNot_tamamlayan(String not_tamamlayan) {
        this.not_tamamlayan = not_tamamlayan;
    }

    public String getNot_adi() {
        return not_adi;
    }

    public void setNot_adi(String not_adi) {
        this.not_adi = not_adi;
    }

    public int getNot_liste_id() {
        return not_liste_id;
    }

    public void setNot_liste_id(int not_liste_id) {
        this.not_liste_id = not_liste_id;
    }

    public String getNot_global_key() {
        return not_global_key;
    }

    public void setNot_global_key(String not_global_key) {
        this.not_global_key = not_global_key;
    }

    public String getNot_detay() {
        return not_detay;
    }

    public void setNot_detay(String not_detay) {
        this.not_detay = not_detay;
    }
}
