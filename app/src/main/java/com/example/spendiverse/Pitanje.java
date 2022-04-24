package com.example.spendiverse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
    Integer[] tocnostPitanja;
    Integer[] prethodniOdgovori; //lista sa prethodnim odgovorima korisnika
    String tocanOdgovor;
    String TAG = "Pitanje";
    Integer prosliBodovi = 0;
    RadioGroup odgovori;
    List<Rezultat> rezultati;
    String[] sadrzajPitanja;
    Integer laganiKvizovi;
    Integer srednjiKvizovi;
    Integer teskiKvizovi;
    Integer ukupniBodovi = 0;
    Integer postojanjeBedzaZaLaganeKvizove;
    Integer postojanjeBedzaZaSrednjeKvizove;
    Integer postojanjeBedzaZaTeskeKvizove;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pitanje);
        //postavlja strelicu za natrag
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_back);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        naslovTeme = bundle.getString("nazivTeme");
        naslovGrupe = bundle.getString("nazivGrupe");
        redniBrojKviza = bundle.getInt("redniBrojKviza");
        odgovori = findViewById(R.id.odgovori);

        String imeBrojaPitanja = naslovGrupe + "_tema" + redniBrojKviza + "_brojpitanja";
        int kolicinaPitanjaId = getResources().getIdentifier("com.example.spendiverse:integer/"+imeBrojaPitanja,null,null);
        kolicinaPitanja = getResources().getInteger(kolicinaPitanjaId);
        //tocnostPitanja je lista u kojoj broj 1, za odredeno pitanje, predstavlja da je odgovor tocan, a 0 da nije
        tocnostPitanja = new Integer[kolicinaPitanja];
        Arrays.fill(tocnostPitanja,new Integer(0));

        //u početku nema odgovora na pitanja pa su svi elementi u listi prethodniOdgovori 0
        //lista sluzi za obnavljanje prethodnog odgovora ako se vracamo na prethodno pitanje
        prethodniOdgovori = new Integer[kolicinaPitanja];
        Arrays.fill(prethodniOdgovori,new Integer(0));

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
                    //ako korisnik ništa nije odgovorio
                    prethodniOdgovori[redniBrojPitanja] = 0;
                    return;
                }
                if (trenutniOdgovor.getText().equals(sadrzajPitanja[1])) {
                    //ako je korisnik odabrao prvi odgovor
                    prethodniOdgovori[redniBrojPitanja] = 1;
                }
                else if (trenutniOdgovor.getText().equals(sadrzajPitanja[2])) {
                    //ako je korisnik odabrao drugi odgovor
                    prethodniOdgovori[redniBrojPitanja] = 2;
                }
                else {
                    //ako je korisnik odabrao treći odgovor
                    prethodniOdgovori[redniBrojPitanja] = 3;
                }
                if (trenutniOdgovor.getText().toString().equals(tocanOdgovor)){
                    tocnostPitanja[redniBrojPitanja] = 1;
                }
                else {
                    tocnostPitanja[redniBrojPitanja] = 0;
                }
            }
        });
        Button zavrsiKviz = findViewById(R.id.zavrsi_kviz);
        View.OnClickListener listener3 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spremiRezultat();
                prikaziRezultate();
            }
        };

        zavrsiKviz.setOnClickListener(listener3);

        ImageButton prethodnoPitanje = findViewById(R.id.prethodno_pitanje);
        ImageButton sljedecePitanje = findViewById(R.id.sljedece_pitanje);
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
        int id = getResources().getIdentifier("com.example.spendiverse:array/"+idPitanja, null, null);

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
        if (prethodniOdgovori[redniBrojPitanja].equals(0)) {
            //ako korisnik nije još odgovorio na trenutno pitanje
            odgovori.clearCheck();
        }
        else {
            //ako je korisnik već odgovorio na trenutno pitanje
            if (prethodniOdgovori[redniBrojPitanja].equals(1)) {
                odgovor1Text.setChecked(true);
            }
            else if (prethodniOdgovori[redniBrojPitanja].equals(2)) {
                odgovor2Text.setChecked(true);
            }
            else {
                odgovor3Text.setChecked(true);
            }
        }


    }
    private String odrediKriveOdgovore(){
        String kriviOdgovori = getResources().getString(R.string.krivi_odgovori);
        Integer netocni = 0;
        for(int i=0;i<kolicinaPitanja; i++){
            if (tocnostPitanja[i]==0){
                kriviOdgovori = kriviOdgovori+" "+Integer.toString(i+1)+".,";
                netocni++;
            }
        }
        if(netocni==0){
            return "";
        }
        return kriviOdgovori.substring(0,kriviOdgovori.length()-1);
    }
    private Integer izracunajRezultat() {
        Integer bodovi = 0;
        for (int i = 0;i<kolicinaPitanja; i++) {
            if (tocnostPitanja[i]==1) {
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
                    db.collection("ljestvica").document(firebaseUser.getUid()).update("bodovi", ukupniBodovi);
                }

            }
        });
        laganiKvizovi = 0;
        srednjiKvizovi = 0;
        teskiKvizovi = 0;
        postojanjeBedzaZaLaganeKvizove = 0;
        postojanjeBedzaZaSrednjeKvizove = 0;
        postojanjeBedzaZaTeskeKvizove = 0;
        Context context = this;
        db.collection("korisnici").document(firebaseUser.getUid()).collection("bedzevi")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document.getId().equals("bedz_lagani_kvizovi")){
                                    postojanjeBedzaZaLaganeKvizove = 1;
                                }
                                if (document.getId().equals("bedz_srednji_kvizovi")){
                                    postojanjeBedzaZaSrednjeKvizove = 1;
                                }
                                if (document.getId().equals("bedz_teski_kvizovi")){
                                    postojanjeBedzaZaTeskeKvizove = 1;
                                }
                            }
                            if (postojanjeBedzaZaLaganeKvizove==0 || postojanjeBedzaZaSrednjeKvizove==0 || postojanjeBedzaZaTeskeKvizove==0){
                                db.collection("korisnici").document(firebaseUser.getUid()).collection("rezultati_kvizova")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    rezultati = new ArrayList<Rezultat>();
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                                        String nazivTezine = document.getData().get("naslov grupe").toString();
                                                        Integer rezultatKviza = Integer.parseInt(document.getData().get("rezultat").toString());
                                                        String naslovTeme = document.get("naslov teme").toString();
                                                        Integer redniBrojKviza = spremnikKategorija.vracanjeRednogBrojaKviza(naslovTeme,nazivTezine);
                                                        String imeBrojaPitanja = nazivTezine + "_tema" + redniBrojKviza + "_brojpitanja";
                                                        int kolicinaPitanjaId = getResources().getIdentifier("com.example.spendiverse:integer/"+imeBrojaPitanja,null,null);
                                                        Integer brojPitanja = getResources().getInteger(kolicinaPitanjaId);

                                                        if (nazivTezine.equals("lagano") && brojPitanja==rezultatKviza){
                                                            laganiKvizovi++;
                                                        }
                                                        if (nazivTezine.equals("srednje") && brojPitanja==rezultatKviza){
                                                            srednjiKvizovi++;
                                                        }
                                                        if (nazivTezine.equals("tesko") && brojPitanja==rezultatKviza){
                                                            teskiKvizovi++;
                                                        }
                                                    }
                                                    if (laganiKvizovi==getResources().getInteger(R.integer.broj_laganih_kvizova)){
                                                        db.collection("korisnici").document(firebaseUser.getUid()).collection("bedzevi")
                                                                .document("bedz_lagani_kvizovi").set(new HashMap<>());
                                                        db.collection("ljestvica").document(firebaseUser.getUid())
                                                                .get()
                                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                        if(task.isSuccessful()){
                                                                            String popisBedzeva = task.getResult().getString("bedzevi") + " bedz_lagani_kvizovi";

                                                                            db.collection("ljestvica").document(firebaseUser.getUid()).update("bedzevi", popisBedzeva);
                                                                            if (postojanjeBedzaZaLaganeKvizove==0) {
                                                                                Toast.makeText(context, "Osvojili ste bedž za lagane kvizove!", Toast.LENGTH_LONG).show();
                                                                            }
                                                                        }
                                                                        else{
                                                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                    if (srednjiKvizovi==getResources().getInteger(R.integer.broj_srednjih_kvizova)){
                                                        db.collection("korisnici").document(firebaseUser.getUid()).collection("bedzevi")
                                                                .document("bedz_srednji_kvizovi").set(new HashMap<>());
                                                        db.collection("ljestvica").document(firebaseUser.getUid())
                                                                .get()
                                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                        if(task.isSuccessful()){
                                                                            String popisBedzeva = task.getResult().getString("bedzevi") + " bedz_srednji_kvizovi";

                                                                            db.collection("ljestvica").document(firebaseUser.getUid()).update("bedzevi", popisBedzeva);
                                                                            if (postojanjeBedzaZaSrednjeKvizove==0) {
                                                                                Toast.makeText(context, "Osvojili ste bedž za srednje kvizove!", Toast.LENGTH_LONG).show();
                                                                            }
                                                                        }
                                                                        else{
                                                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                    if (teskiKvizovi==getResources().getInteger(R.integer.broj_teskih_kvizova)){
                                                        db.collection("korisnici").document(firebaseUser.getUid()).collection("bedzevi")
                                                                .document("bedz_teski_kvizovi").set(new HashMap<>());
                                                        db.collection("ljestvica").document(firebaseUser.getUid())
                                                                .get()
                                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                        if(task.isSuccessful()){
                                                                            String popisBedzeva = task.getResult().getString("bedzevi") + " bedz_teski_kvizovi";

                                                                            db.collection("ljestvica").document(firebaseUser.getUid()).update("bedzevi", popisBedzeva);
                                                                            if (postojanjeBedzaZaTeskeKvizove==0) {
                                                                                Toast.makeText(context, "Osvojili ste bedž za teške kvizove!", Toast.LENGTH_LONG).show();
                                                                            }
                                                                        }
                                                                        else{
                                                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                                                        }
                                                                    }
                                                                });
                                                    }

                                                } else {
                                                    Log.d(TAG, "get failed with ", task.getException());
                                                }
                                            }
                                        });

                                /*db.collection("korisnici").document(firebaseUser.getUid()).collection("bedzevi")
                                        .document("prvi_trosak").set(new HashMap<>());*/
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
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
                        .setMessage(bodovi + "/" + kolicinaPitanja + "\n" + odrediKriveOdgovore())
                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(R.string.kviz_odgovor_zatvori, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.kviz_odgovor_pokusaj_ponovno,new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int which) {
                                redniBrojPitanja = 0;
                                ucitavanjePitanja();
                                Arrays.fill(tocnostPitanja,new Integer(0));

                            }
                        })

                        .setIcon(R.drawable.slika_rezultat)
                        .create();
        alertDialog.show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}