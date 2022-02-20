package com.example.spendiverse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
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

import org.w3c.dom.Document;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MojProfil extends AppCompatActivity {
    TextView rjeseniKvizovi;
    TextView ljestvica;
    TextView bodovi;
    Integer brojBodova = 0;
    Integer brojRjesenihKvizova;
    Button prikazLjestvice;
    TextView emailKorisnika;
    List<Rezultat> rezultati;
    Switch prikazNaLjestvici;
    String TAG = "MojProfil";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moj_profil);
        rjeseniKvizovi = findViewById(R.id.rjeseni_kvizovi);
        ljestvica = findViewById(R.id.ljestvica);
        bodovi = findViewById(R.id.bodovi);
        prikazLjestvice = findViewById(R.id.prikaz_ljestvice);
        emailKorisnika = findViewById(R.id.email_korisnika);
        prikazNaLjestvici = findViewById(R.id.switch1);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String email = user.getEmail();
            emailKorisnika.setText(email.toString());
        }

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("korisnici").document(firebaseUser.getUid()).collection("postavke").document("postavke_za_ljestvicu");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        prikazNaLjestvici.setChecked(document.getData().get("prikaz").equals(true));
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        prikazNaLjestvici.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    HashMap data = new HashMap();
                    data.put("prikaz", true);
                    db.collection("korisnici").document(firebaseUser.getUid()).collection("postavke").document("postavke_za_ljestvicu").set(data);
                }
                else {

                    HashMap data = new HashMap();
                    data.put("prikaz", false);
                    db.collection("korisnici").document(firebaseUser.getUid()).collection("postavke").document("postavke_za_ljestvicu").set(data);

                }
            }
        });

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prikaziLjestvicu();
            }
        };
        prikazLjestvice.setOnClickListener(listener);
        procitajRezultate();

    }
    private void prikaziLjestvicu() {
        Intent intent = new Intent(this, PrikazLjestvice.class);
        startActivity(intent);
    }
    private void izracunajRezultate() {
        for (Rezultat rezultat : rezultati) {
            if (rezultat.getNazivGrupe().equals("lagano")) {
                brojBodova += rezultat.getRezultat()*10;
            }
            else if (rezultat.getNazivGrupe().equals("srednje")) {
                brojBodova += rezultat.getRezultat()*20;
            }
            else {
                brojBodova += rezultat.getRezultat()*30;
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
                        brojRjesenihKvizova = rezultati.size();
                        rjeseniKvizovi.setText(brojRjesenihKvizova.toString());
                        bodovi.setText(brojBodova.toString());
                    }

                });
    }
}