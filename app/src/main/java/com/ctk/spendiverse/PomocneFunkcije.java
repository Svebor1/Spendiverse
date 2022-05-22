package com.ctk.spendiverse;

public class PomocneFunkcije {
    public static String provjeraDuljineUnosa(String uneseniText){
        String greska = "";
        String cijenaBezDecimala;
        String cijenaNakonTocke;
        Integer pozicijaTocke = uneseniText.indexOf('.');
        if (pozicijaTocke == -1){
            cijenaBezDecimala = uneseniText;
            cijenaNakonTocke = "";
        }
        else {
            cijenaBezDecimala = uneseniText.substring(0, pozicijaTocke);
            cijenaNakonTocke = uneseniText.substring(pozicijaTocke + 1);
        }

        if (cijenaBezDecimala.length()>8){
            greska = greska + "Trošak ne može biti veći od 99999999. ";
        }
        if (cijenaNakonTocke.length()>2){
            greska = greska + ("Trošak ne može imati više od 2 decimale.");
        }
        return greska;
    }
}
