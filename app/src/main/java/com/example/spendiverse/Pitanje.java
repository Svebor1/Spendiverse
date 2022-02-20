package com.example.spendiverse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    String TAG = "Pitanje";
    Integer prosliBodovi = 0;
    RadioGroup odgovori;
    List<Rezultat> rezultati;
    Integer ukupniBodovi = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pitanje);


        Bundle bundle = getIntent().getExtras();

        naslovTeme = bundle.getString("nazivTeme");
        naslovGrupe = bundle.getString("nazivGrupe");
        redniBrojKviza = bundle.getInt("redniBrojKviza");
        odgovori = findViewById(R.id.odgovori);

        String imeBrojaPitanja = naslovGrupe + "_tema" + redniBrojKviza + "_brojpitanja";
        int kolicinaPitanjaId = getResources().getIdentifier("com.example.coinsmart:integer/"+imeBrojaPitanja,null,null);
        kolicinaPitanja = getResources().getInteger(kolicinaPitanjaId);

        data = new Integer[kolicinaPitanja];
        Arrays.fill(data,new Integer(0));

        ucitavanjePitanja();

        RadioGroup odgovori = findViewById(R.id.odgovori);
        procitajRezultate();

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
                spremiRezultat();
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
        odgovori.clearCheck();
    }
    private Integer izracunajRezultat() {
        Integer bodovi = 0;
        for (int i = 0;i<kolicinaPitanja; i++) {
            if (data[i]==1) {
                bodovi++;
            }
        }
        return bodovi;
    }

    private void spremiRezultat() {
        Integer bodovi = izracunajRezultat();
        Map<String, Object> data = new HashMap<>();
        data.put("rezultat", bodovi);
        data.put("naslov teme", naslovTeme);
        data.put("naslov grupe", naslovGrupe);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("korisnici").document(firebaseUser.getUid()).collection("rezultati_kvizova").document(naslovGrupe + "_" + naslovTeme);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        prosliBodovi = Integer.parseInt(document.getData().get("rezultat").toString());
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
                if (bodovi > prosliBodovi) {
                    if (naslovGrupe.equals("lagano")) {

                        ukupniBodovi += (bodovi-prosliBodovi)*10;
                    }
                    else if (naslovGrupe.equals("srednje")) {
                        ukupniBodovi += (bodovi-prosliBodovi)*20;
                    }
                    else {
                        ukupniBodovi += (bodovi-prosliBodovi)*30;
                    }
                    db.collection("korisnici").document(firebaseUser.getUid()).collection("rezultati_kvizova").document(naslovGrupe + "_" + naslovTeme).set(data);
                    Map<String, Object> data2 = new HashMap<>();
                    String email = firebaseUser.getEmail();
                    data2.put("bodovi", ukupniBodovi);
                    data2.put("email", email);
                    db.collection("ljestvica").document(firebaseUser.getUid()).set(data2);
                }

            }
        });



    }

    private void izracunajRezultate() {
        for (Rezultat rezultat : rezultati) {
            if (rezultat.getNazivGrupe().equals("lagano")) {
                ukupniBodovi += rezultat.getRezultat()*10;
            }
            else if (rezultat.getNazivGrupe().equals("srednje")) {
                ukupniBodovi += rezultat.getRezultat()*20;
            }
            else {
                ukupniBodovi += rezultat.getRezultat()*30;
            }

        }
    }
    /**
     * Ova metoda čita sve rezultate korisnika iz baze.
     */

    private void procitajRezultate() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("korisnici").document(firebaseUser.getUid()).collection("rezultati_kvizova")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            rezultati = new ArrayList<Rezultat>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Integer rezultat = Integer.parseInt(document.getData().get("rezultat").toString());
                                String nazivGrupe = document.getData().get("naslov grupe").toString();
                                String nazivTeme = document.getData().get("naslov teme").toString();

                                rezultati.add(new Rezultat(nazivGrupe, nazivTeme,rezultat));
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                        izracunajRezultate();
                    }

                });
    }
    private void prikaziRezultate() {
        Integer bodovi = izracunajRezultat();
        if ((bodovi.floatValue()/kolicinaPitanja.floatValue()*100)>=80) {
            final KonfettiView konfettiView = findViewById(R.id.viewKonfetti);
            konfettiView.build()
                    .addColors(Color.rgb(247, 184, 1), Color.rgb(118, 120, 238))
                    .setDirection(0.0, 359.0)
                    .setSpeed(1f, 5f)
                    .setFadeOutEnabled(true)
                    .setTimeToLive(2000L)
                    .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                    //.addSizes(new Size(12, 5f))
                    .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                    .streamFor(300, 5000L);
        }
        AlertDialog alertDialog =
                new AlertDialog.Builder(this)
                        .setTitle("Rezultat")
                        .setMessage(bodovi + "/" + kolicinaPitanja)
                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton("zatvori", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton("pokušaj ponovno",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int which) {
                                redniBrojPitanja = 0;
                                ucitavanjePitanja();
                                Arrays.fill(data,new Integer(0));

                            }
                        })

                        .setIcon(R.drawable.slika_rezultat)
                        .create();
        alertDialog.show();
    }

}