package com.callbellapp.ourlist;

import java.io.Serializable;

public class FirebaseIslem implements Serializable {
    private String islemID;
    // Her işlem için ayrı olarak firebase tarafından oluşturulucak db ekleme sonrası silmede kullanılacak.
    private String islemTipi;
    private String listeKey;
    private String islem;




    public FirebaseIslem() {
    }

    public FirebaseIslem(String islemID, String islemTipi, String listeKey, String islem) {
        this.islemID = islemID;
        this.islemTipi = islemTipi;
        this.listeKey = listeKey;
        this.islem = islem;
    }

    public String getIslemID() {
        return islemID;
    }

    public void setIslemID(String islemID) {
        this.islemID = islemID;
    }

    public String getIslemTipi() {
        return islemTipi;
    }

    public void setIslemTipi(String islemTipi) {
        this.islemTipi = islemTipi;
    }

    public String getListeKey() {
        return listeKey;
    }

    public void setListeKey(String listeKey) {
        this.listeKey = listeKey;
    }

    public String getIslem() {
        return islem;
    }

    public void setIslem(String islem) {
        this.islem = islem;
    }
}
