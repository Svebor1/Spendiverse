package com.example.spendiverse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class spremnikKategorija {
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> kategorijeFinancijskePismenosti = new HashMap<String, List<String>>();
        //u listu lagano se dodaju 2 stringa (teme)
        List<String> lagano = new ArrayList<String>();
        lagano.add("Što je novac?");
        lagano.add("Valute");

        //u listu srednje se dodaju 2 stringa (teme)
        List<String> srednje = new ArrayList<String>();
        srednje.add("Online kupovina");
        srednje.add("Financijski plan");

        //u listu tesko se dodaju 2 stringa (teme)
        List<String> tesko = new ArrayList<String>();
        tesko.add("Kamate");
        tesko.add("Kriptovalute");

        //postavljanje emoji-a na emoji zvjezdice
        int emoji = 0x1F31F;
        String zvjezdica = new String(Character.toChars(emoji));
        //postavljanje emojia zvjezdica
        kategorijeFinancijskePismenosti.put("Lagano" + zvjezdica, lagano);
        kategorijeFinancijskePismenosti.put("Srednje"+ zvjezdica + zvjezdica, srednje);
        kategorijeFinancijskePismenosti.put("Teško"+ zvjezdica + zvjezdica + zvjezdica, tesko);
        return kategorijeFinancijskePismenosti;
    }
}