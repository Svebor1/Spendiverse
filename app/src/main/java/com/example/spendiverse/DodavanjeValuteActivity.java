package com.example.spendiverse;

import androidx.appcompat.app.AppCompatActivity;

import android.icu.util.Currency;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class DodavanjeValuteActivity extends AppCompatActivity {
    private Spinner spinnerSvihValuta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodavanje_valute);
        spinnerSvihValuta = findViewById(R.id.spinner_sve_valute);


        List <String> sveValute = getAllCurrencies();
        ArrayAdapter arrayAdapterSveValute = new ArrayAdapter(this, R.layout.spinner_item, sveValute);
        arrayAdapterSveValute.setDropDownViewResource(R.layout.spinner_item);
        spinnerSvihValuta.setAdapter(arrayAdapterSveValute);
    }
    public static List<String> getAllCurrencies() {
        Set<String> popisValuta = new HashSet<String>();
        Locale[] locs = Locale.getAvailableLocales();

        for(Locale loc : locs) {
            try {
                Currency currency = Currency.getInstance( loc );

                if ( currency != null ) {
                    popisValuta.add(currency.getCurrencyCode());
                }
            } catch(Exception exc)
            {
                // Locale not found
            }
        }

        return popisValuta.stream().collect(Collectors.toList());
    }
}