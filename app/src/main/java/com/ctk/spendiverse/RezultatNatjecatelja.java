package com.ctk.spendiverse;

import java.util.ArrayList;

public class RezultatNatjecatelja {
    String imeKorisnika;
    Integer rezultatKorisnika;
    Integer pozicija;
    String[] listaBedzeva;

    public Integer getPozicija() {
        return pozicija;
    }

    public void setPozicija(Integer pozicija) {
        this.pozicija = pozicija;
    }
    public RezultatNatjecatelja(String imeKorisnika, Integer rezultatKorisnika, String[] listaBedzeva) {
        this.imeKorisnika = imeKorisnika;
        this.rezultatKorisnika = rezultatKorisnika;
        this.pozicija = null;
        this.listaBedzeva = listaBedzeva;
    }

    public String getImeKorisnika() {
        return imeKorisnika;
    }

    public void setImeKorisnika(String imeKorisnika) {
        this.imeKorisnika = imeKorisnika;
    }

    public Integer getRezultatKorisnika() {
        return rezultatKorisnika;
    }

    public void setRezultatKorisnika(Integer rezultatKorisnika) {
        this.rezultatKorisnika = rezultatKorisnika;
    }

    public String[] getListaBedzeva() {
        return listaBedzeva;
    }

    public void setListaBedzeva(String[] listaBedzeva) {
        this.listaBedzeva = listaBedzeva;
    }
}
