package com.example.spendiverse;

import android.graphics.Bitmap;

public class Trosak {
    private String naziv;
    private Integer datumDan;
    private Integer datumMjesec;
    private Integer datumGodina;
    private String kategorija;
    private String valuta;
    private Double cijena;
    private String firebaseId;

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public Trosak(String naziv, Integer datumDan, Integer datumMjesec, Integer datumGodina, String kategorija, Double cijena, String valuta, String firebaseId) {
        this.naziv = naziv;
        this.datumDan = datumDan;
        this.datumMjesec = datumMjesec;
        this.datumGodina = datumGodina;
        this.kategorija = kategorija;
        this.cijena = cijena;
        this.valuta = valuta;
        this.firebaseId = firebaseId;
    }

    public Double getCijena() {
        return cijena;
    }

    public void setCijena(Double cijena) {
        this.cijena = cijena;
    }

    public String getKategorija() {
        return kategorija;
    }

    public void setKategorija(String kategorija) {
        this.kategorija = kategorija;
    }

    public String getValuta() {
        return valuta;
    }

    public void setValuta(String valuta) {
        this.valuta = valuta;
    }

    public Integer getDatumDan() {
        return datumDan;
    }

    public void setDatumDan(Integer datumDan) {
        this.datumDan = datumDan;
    }

    public Integer getDatumMjesec() {
        return datumMjesec;
    }

    public void setDatumMjesec(Integer datumMjesec) {
        this.datumMjesec = datumMjesec;
    }

    public Integer getDatumGodina() {
        return datumGodina;
    }

    public void setDatumGodina(Integer datumGodina) {
        this.datumGodina = datumGodina;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

}
