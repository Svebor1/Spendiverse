package com.example.coinsmart;

public class Trosak {
    private String naziv;
    private String datum;
    private String kategorija;
    private Integer cijena;

    public Trosak(String naziv, String datum, String kategorija, Integer cijena) {
        this.naziv = naziv;
        this.datum = datum;
        this.kategorija = kategorija;
        this.cijena = cijena;
    }

    public Integer getCijena() {
        return cijena;
    }

    public void setCijena(Integer cijena) {
        this.cijena = cijena;
    }

    public String getKategorija() {
        return kategorija;
    }

    public void setKategorija(String kategorija) {
        this.kategorija = kategorija;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }
}
