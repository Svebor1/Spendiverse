package com.ctk.spendiverse;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PomocneFunkcijeTest {
    @Test
    public void provjeraDuljineUnosa_radi(){
        String errorZaZnamenke = "Trošak ne može biti veći od 99999999. ";
        String errorZaDecimale = "Trošak ne može imati više od 2 decimale.";
        String greska = PomocneFunkcije.provjeraDuljineUnosa("999999999999.4342234");
        assertEquals(errorZaZnamenke+errorZaDecimale, greska);

        String greska2 = PomocneFunkcije.provjeraDuljineUnosa("999999999999.43");
        assertEquals(errorZaZnamenke, greska2);

        String greska3 = PomocneFunkcije.provjeraDuljineUnosa("999.4342234246565");
        assertEquals(errorZaDecimale, greska3);

        String greska4 = PomocneFunkcije.provjeraDuljineUnosa("999.34");
        assertEquals("", greska4);
    }
}
