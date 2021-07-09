package com.callbellapp.ourlist;

import java.io.Serializable;

public class ListePaylasilanPersonel implements Serializable {

    private int paylasilan_personel_id;
    private int personel_liste_id;
    private Personel perseonel_uid;
    private int yoneticimi;

    public ListePaylasilanPersonel() {
    }

    public ListePaylasilanPersonel(int paylasilan_personel_id, int personel_liste_id, Personel perseonel_uid, int yoneticimi) {
        this.paylasilan_personel_id = paylasilan_personel_id;
        this.personel_liste_id = personel_liste_id;
        this.perseonel_uid = perseonel_uid;
        this.yoneticimi = yoneticimi;
    }

    public int getPaylasilan_personel_id() {
        return paylasilan_personel_id;
    }

    public void setPaylasilan_personel_id(int paylasilan_personel_id) {
        this.paylasilan_personel_id = paylasilan_personel_id;
    }

    public int getPersonel_liste_id() {
        return personel_liste_id;
    }

    public void setPersonel_liste_id(int personel_liste_id) {
        this.personel_liste_id = personel_liste_id;
    }

    public Personel getPerseonel_uid() {
        return perseonel_uid;
    }

    public void setPerseonel_uid(Personel perseonel_uid) {
        this.perseonel_uid = perseonel_uid;
    }

    public int getYoneticimi() {
        return yoneticimi;
    }

    public void setYoneticimi(int yoneticimi) {
        this.yoneticimi = yoneticimi;
    }
}
