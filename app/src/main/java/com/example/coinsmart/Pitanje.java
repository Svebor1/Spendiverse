package com.example.coinsmart;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Arrays;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;

public class Pitanje extends AppCompatActivity {
    Integer redniBrojPitanja = 0;
    Integer kolicinaPitanja;
    String naslovTeme;
    String naslovGrupe;
    Integer redniBrojKviza;
    Integer[] data;
    String tocanOdgovor;
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

        data = new Integer[kolicinaPitanja];
        Arrays.fill(data,new Integer(0));

        ucitavanjePitanja();

        RadioGroup odgovori = findViewById(R.id.odgovori);
        odgovori.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                RadioButton trenutniOdgovor = findViewById(checkedId);
                if (trenutniOdgovor==null) {
                    return;
                }
                if (trenutniOdgovor.getText().toString().equals(tocanOdgovor)){
                    data[redniBrojPitanja] = 1;
                }
                else {
                    data[redniBrojPitanja] = 0;
                }
            }
        });
        Button zavrsiKviz = findViewById(R.id.zavrsi_kviz);
        View.OnClickListener listener3 = new View.OnClickListener() {
            @Override //
            public void onClick(View v) {
                prikaziRezultate();
            }
        };
        zavrsiKviz.setOnClickListener(listener3);

        Button prethodnoPitanje = findViewById(R.id.prethodno_pitanje);
        Button sljedecePitanje = findViewById(R.id.sljedece_pitanje);
        View.OnClickListener listener1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (redniBrojPitanja > 0) {
                    redniBrojPitanja--;
                    ucitavanjePitanja();
                }
            }
        };
        prethodnoPitanje.setOnClickListener(listener1);
        View.OnClickListener listener2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (redniBrojPitanja+1 < kolicinaPitanja) {
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
        tocanOdgovor = odgovorT;
        TextView pitanjeText = findViewById(R.id.pitanje);
        RadioButton odgovor1Text = findViewById(R.id.odgovor1);
        RadioButton odgovor2Text = findViewById(R.id.odgovor2);
        RadioButton odgovor3Text = findViewById(R.id.odgovor3);
        pitanjeText.setText(tekstPitanja);
        odgovor1Text.setText(odgovor1);
        odgovor2Text.setText(odgovor2);
        odgovor3Text.setText(odgovor3);
        redniBrojPitanjaText.setText((redniBrojPitanja+1) + "/" + kolicinaPitanja);
        RadioGroup odgovori = findViewById(R.id.odgovori);
        odgovori.clearCheck();
    }

    private void prikaziRezultate() {
        final KonfettiView konfettiView = findViewById(R.id.viewKonfetti);
        konfettiView.build()
                .addColors(Color.rgb(247, 184, 1),Color.rgb(118, 120, 238))
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                //.addSizes(new Size(12, 5f))
                .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                .streamFor(300, 5000L);
        Integer bodovi = 0;
        for (int i = 0;i<kolicinaPitanja; i++) {
            if (data[i]==1) {
                bodovi++;
            }
        }
        AlertDialog alertDialog =
                new AlertDialog.Builder(this)
                        .setTitle("Rezultat")
                        .setMessage(bodovi + "/" + kolicinaPitanja)
                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(R.drawable.slika_rezultat)
                        .create();
        alertDialog.show();
    }
}