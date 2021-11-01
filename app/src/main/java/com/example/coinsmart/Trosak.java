package com.example.coinsmart;

public class Trosak {
    private String naziv;
    private Integer datumDan;
    private Integer datumMjesec;
    private Integer datumGodina;
    private String kategorija;
    private Integer cijena;

    public Trosak(String naziv, Integer datumDan, Integer datumMjesec, Integer datumGodina, String kategorija, Integer cijena) {
        this.naziv = naziv;
        this.datumDan = datumDan;
        this.datumMjesec = datumMjesec;
        this.datumGodina = datumGodina;
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
