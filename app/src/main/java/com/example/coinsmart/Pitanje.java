package com.example.coinsmart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Pitanje extends AppCompatActivity {
    Integer redniBrojPitanja = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pitanje);

        Bundle bundle = getIntent().getExtras();
        String naslovTeme = bundle.getString("nazivTeme");
        String naslovGrupe = bundle.getString("nazivGrupe");
        Integer redniBrojKviza = bundle.getInt("redniBrojKviza");
        String idPitanja = naslovGrupe + "_kviz" + redniBrojKviza.toString() + "_pitanje" + redniBrojPitanja.toString();
        int a = 5;
        int id = getResources().getIdentifier("com.example.coinsmart:array/"+idPitanja, null, null);
        String[] sadrzajPitanja;
        sadrzajPitanja=getResources().getStringArray(id);
        String tekstPitanja = sadrzajPitanja[0];
        String odgovor1 = sadrzajPitanja[1];
        String odgovor2 = sadrzajPitanja[2];
        String odgovor3 = sadrzajPitanja[3]; //zove se po temi a ne po grupi
        String odgovorT = sadrzajPitanja[4];
        TextView pitanjeText = findViewById(R.id.pitanje);
        RadioButton odgovor1Text = findViewById(R.id.odgovor1);
        RadioButton odgovor2Text = findViewById(R.id.odgovor2);
        RadioButton odgovor3Text = findViewById(R.id.odgovor3);
        pitanjeText.setText(tekstPitanja);
        odgovor1Text.setText(odgovor1);
        odgovor2Text.setText(odgovor2);
        odgovor3Text.setText(odgovor3);
    }

}