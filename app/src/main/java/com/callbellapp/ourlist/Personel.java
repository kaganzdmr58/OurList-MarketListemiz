package com.callbellapp.ourlist;

import java.io.Serializable;

public class Personel implements Serializable {

    private int personel_id;
    private String personel_adi;
    private String personel_uid;
    private String personel_tel;

    public Personel() {
    }

    public Personel(int personel_id, String personel_adi, String personel_uid, String personel_tel) {
        this.personel_id = personel_id;
        this.personel_adi = personel_adi;
        this.personel_uid = personel_uid;
        this.personel_tel = personel_tel;
    }

    public int getPersonel_id() {
        return personel_id;
    }

    public void setPersonel_id(int personel_id) {
        this.personel_id = personel_id;
    }

    public String getPersonel_adi() {
        return personel_adi;
    }

    public void setPersonel_adi(String personel_adi) {
        this.personel_adi = personel_adi;
    }

    public String getPersonel_uid() {
        return personel_uid;
    }

    public void setPersonel_uid(String personel_uid) {
        this.personel_uid = personel_uid;
    }

    public String getPersonel_tel() {
        return personel_tel;
    }

    public void setPersonel_tel(String personel_tel) {
        this.personel_tel = personel_tel;
    }
}
