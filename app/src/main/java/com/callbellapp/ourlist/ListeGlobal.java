package com.callbellapp.ourlist;

import java.io.Serializable;

public class ListeGlobal implements Serializable {

    private int personel_liste_id;
    private int Local_liste_id;
    private String global_liste_id;

    public ListeGlobal() {
    }

    public ListeGlobal(int personel_liste_id, int local_liste_id, String global_liste_id) {
        this.personel_liste_id = personel_liste_id;
        Local_liste_id = local_liste_id;
        this.global_liste_id = global_liste_id;
    }

    public int getPersonel_liste_id() {
        return personel_liste_id;
    }

    public void setPersonel_liste_id(int personel_liste_id) {
        this.personel_liste_id = personel_liste_id;
    }

    public int getLocal_liste_id() {
        return Local_liste_id;
    }

    public void setLocal_liste_id(int local_liste_id) {
        Local_liste_id = local_liste_id;
    }

    public String getGlobal_liste_id() {
        return global_liste_id;
    }

    public void setGlobal_liste_id(String global_liste_id) {
        this.global_liste_id = global_liste_id;
    }
}
