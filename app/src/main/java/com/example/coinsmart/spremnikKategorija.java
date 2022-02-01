package com.example.coinsmart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class spremnikKategorija {
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> kategorijeFinancijskePismenosti = new HashMap<String, List<String>>();
        List<String> lagano = new ArrayList<String>();
        lagano.add("Što je novac?");
        lagano.add("Valute");

        List<String> srednje = new ArrayList<String>();
        srednje.add("Online kupovina");
        srednje.add("Financijski plan");

        List<String> tesko = new ArrayList<String>();
        tesko.add("Kamate");
        tesko.add("nesto");
        /*
        List<String> stednja = new ArrayList<String>();
        stednja.add("Bankovni računi");
        stednja.add("Kamate");
        List<String> ulaganja = new ArrayList<String>();
        ulaganja.add("Inflacija");
        ulaganja.add("Deflacija");
        ulaganja.add("Fondovi");
        ulaganja.add("Opcije");
        ulaganja.add("Dionice");
         */

        int emoji = 0x1F31F;
        String zvjezdica = new String(Character.toChars(emoji));
        kategorijeFinancijskePismenosti.put("Lagano" + zvjezdica, lagano);
        kategorijeFinancijskePismenosti.put("Srednje"+ zvjezdica + zvjezdica, srednje);
        kategorijeFinancijskePismenosti.put("Teško"+ zvjezdica + zvjezdica + zvjezdica, tesko);
        return kategorijeFinancijskePismenosti;
    }
}