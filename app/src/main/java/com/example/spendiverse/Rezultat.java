package com.example.spendiverse;

public class Rezultat {
    private String nazivGrupe;
    private String nazivTeme;
    private Integer rezultat;

    public void setNazivGrupe(String nazivGrupe) {
        this.nazivGrupe = nazivGrupe;
    }

    public void setNazivTeme(String nazivTeme) {
        this.nazivTeme = nazivTeme;
    }

    public void setRezultat(Integer rezultat) {
        this.rezultat = rezultat;
    }

    public String getNazivGrupe() {
        return nazivGrupe;
    }

    public String getNazivTeme() {
        return nazivTeme;
    }

    public Integer getRezultat() {
        return rezultat;
    }

    public Rezultat(String nazivGrupe, String nazivTeme, Integer rezultat) {
        this.nazivGrupe = nazivGrupe;
        this.nazivTeme = nazivTeme;
        this.rezultat = rezultat;
    }
}
