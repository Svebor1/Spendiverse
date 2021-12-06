package com.example.coinsmart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class spremnikKategorija {
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> kategorijeFinancijskePismenosti = new HashMap<String, List<String>>();
        List<String> stednja = new ArrayList<String>();
        stednja.add("Bankovni računi");
        stednja.add("Kamate");

        List<String> ulaganja = new ArrayList<String>();
        ulaganja.add("Inflacija");
        ulaganja.add("Deflacija");
        ulaganja.add("Fondovi");
        ulaganja.add("Opcije");
        ulaganja.add("Dionice");

        kategorijeFinancijskePismenosti.put("Štednja", stednja);
        kategorijeFinancijskePismenosti.put("Ulaganja", ulaganja);
        return kategorijeFinancijskePismenosti;
    }
}