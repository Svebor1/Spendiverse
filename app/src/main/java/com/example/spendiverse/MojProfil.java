package com.example.spendiverse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
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
import java.util.List;

public class MojProfil extends AppCompatActivity {
    TextView rjeseniKvizovi;
    TextView ljestvica;
    TextView bodovi;
    Integer brojBodova = 0;
    Integer brojRjesenihKvizova;
    List<Rezultat> rezultati;
    String TAG = "MojProfil";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moj_profil);
        rjeseniKvizovi = findViewById(R.id.rjeseni_kvizovi);
        ljestvica = findViewById(R.id.ljestvica);
        bodovi = findViewById(R.id.bodovi);
        procitajRezultate();

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