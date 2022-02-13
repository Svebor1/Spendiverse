package com.example.spendiverse;

public class RezultatNatjecatelja {
    String imeKorisnika;
    Integer rezultatKorisnika;

    public RezultatNatjecatelja(String imeKorisnika, Integer rezultatKorisnika) {
        this.imeKorisnika = imeKorisnika;
        this.rezultatKorisnika = rezultatKorisnika;
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
}
