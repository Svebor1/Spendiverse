package com.example.coinsmart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Pitanje extends AppCompatActivity {
    Integer redniBrojPitanja = 1;
    Integer kolicinaPitanja;
    String naslovTeme;
    String naslovGrupe;
    Integer redniBrojKviza;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pitanje);


        Bundle bundle = getIntent().getExtras();

        naslovTeme = bundle.getString("nazivTeme");
        naslovGrupe = bundle.getString("nazivGrupe");
        redniBrojKviza = bundle.getInt("redniBrojKviza");

        String imeBrojaPitanja = naslovGrupe + "_tema" + redniBrojKviza + "_brojpitanja";
        int kolicinaPitanjaId = getResources().getIdentifier("com.example.coinsmart:integer/"+imeBrojaPitanja,null,null);
        kolicinaPitanja = getResources().getInteger(kolicinaPitanjaId);


        ucitavanjePitanja();
        Button prethodnoPitanje = findViewById(R.id.prethodno_pitanje);
        Button sljedecePitanje = findViewById(R.id.sljedece_pitanje);
        View.OnClickListener listener1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (redniBrojPitanja > 1) {
                    redniBrojPitanja--;
                    ucitavanjePitanja();
                }
            }
        };
        prethodnoPitanje.setOnClickListener(listener1);
        View.OnClickListener listener2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (redniBrojPitanja < kolicinaPitanja) {
                    redniBrojPitanja++;
                    ucitavanjePitanja();
                }
            }
        };
        sljedecePitanje.setOnClickListener(listener2);

    }

    private void ucitavanjePitanja() {
        TextView redniBrojPitanjaText = findViewById(R.id.redni_broj_pitanja);

        String idPitanja = naslovGrupe + "_kviz" + redniBrojKviza.toString() + "_pitanje" + redniBrojPitanja.toString();
        int id = getResources().getIdentifier("com.example.coinsmart:array/"+idPitanja, null, null);
        String[] sadrzajPitanja;
        sadrzajPitanja=getResources().getStringArray(id);
        String tekstPitanja = sadrzajPitanja[0];
        String odgovor1 = sadrzajPitanja[1];
        String odgovor2 = sadrzajPitanja[2];
        String odgovor3 = sadrzajPitanja[3];
        String odgovorT = sadrzajPitanja[4];
        TextView pitanjeText = findViewById(R.id.pitanje);
        RadioButton odgovor1Text = findViewById(R.id.odgovor1);
        RadioButton odgovor2Text = findViewById(R.id.odgovor2);
        RadioButton odgovor3Text = findViewById(R.id.odgovor3);
        pitanjeText.setText(tekstPitanja);
        odgovor1Text.setText(odgovor1);
        odgovor2Text.setText(odgovor2);
        odgovor3Text.setText(odgovor3);
        redniBrojPitanjaText.setText(redniBrojPitanja + "/" + kolicinaPitanja);
    }
}