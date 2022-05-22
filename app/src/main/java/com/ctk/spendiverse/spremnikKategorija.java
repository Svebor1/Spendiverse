package com.ctk.spendiverse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class spremnikKategorija {
    public static String laganoArray[] = {"Što je novac?", "Valute", "Banke"};
    public static String srednjeArray[] = {"Online kupovina","Financijski plan", "Bankovni računi", "Inflacija"};
    public static String teskoArray[] = {"Kamate","Kriptovalute", "Inflacija napredno", "Vrste inflacije", "Dionice", "Cijene dionica", "Dividende", "Obveznice", "Fondovi", "Futuresi i opcije"};
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> kategorijeFinancijskePismenosti = new HashMap<String, List<String>>();
        //u listu lagano se dodaju 2 stringa (teme)
        List<String> lagano = Arrays.asList(laganoArray.clone());

        //u listu srednje se dodaju 2 stringa (teme)
        List<String> srednje = Arrays.asList(srednjeArray.clone());

        //u listu tesko se dodaju 2 stringa (teme)
        List<String> tesko = Arrays.asList(teskoArray.clone());

        //postavljanje emoji-a na emoji zvjezdice
        int emoji = 0x1F31F;
        String zvjezdica = new String(Character.toChars(emoji));
        //postavljanje emojia zvjezdica
        kategorijeFinancijskePismenosti.put("Lagano" + zvjezdica, lagano);
        kategorijeFinancijskePismenosti.put("Srednje" + zvjezdica + zvjezdica, srednje);
        kategorijeFinancijskePismenosti.put("Teško" + zvjezdica + zvjezdica + zvjezdica, tesko);
        return kategorijeFinancijskePismenosti;
    }
    public static Integer vracanjeRednogBrojaKviza(String imeTeme, String imeGrupe){
        if (imeGrupe.equals("lagano")){
            List<String> lagano = Arrays.asList(laganoArray.clone());
            return (lagano.indexOf(imeTeme));
        }
        if (imeGrupe.equals("srednje")){
            List<String> srednje = Arrays.asList(srednjeArray.clone());
            return (srednje.indexOf(imeTeme));
        }
        if (imeGrupe.equals("tesko")){
            List<String> tesko = Arrays.asList(teskoArray.clone());
            return (tesko.indexOf(imeTeme));
        }
        return -1;
    }
}