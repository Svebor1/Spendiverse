package com.example.spendiverse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class VidjetiDetaljeActivity extends AppCompatActivity {
    private final String TAG = "VidjetiDetaljeActivity";
    ArrayList<Trosak> troskovi;
    private Spinner poredajPo;
    String[] poredajPoArray = {"datumu uzlazno", "datumu silazno", "cijeni silazno", "cijeni uzlazno"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vidjeti_detalje);
        poredajPo = findViewById(R.id.poredaj_po_spinner);
        ArrayAdapter poredajPoAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, poredajPoArray);
        poredajPo.setAdapter(poredajPoAdapter);
        poredajPo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                prikaziTroskove(poredajPo.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                prikaziTroskove("datumu uzlazno");

            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        troskovi = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("korisnici").document(firebaseUser.getUid()).collection("troskovi")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String naziv = document.getData().get("naziv").toString();
                                String kategorija = document.getData().get("kategorija").toString();
                                Integer datumDan = Integer.parseInt(document.getData().get("datumDan").toString());
                                Integer datumMjesec = Integer.parseInt(document.getData().get("datumMjesec").toString());
                                Integer datumGodina = Integer.parseInt(document.getData().get("datumGodina").toString());
                                Integer cijena = Integer.parseInt(document.getData().get("cijena").toString());
                                String firebaseId = document.getId();
                                troskovi.add(new Trosak(naziv, datumDan, datumMjesec, datumGodina, kategorija, cijena, firebaseId));
                            }
                            prikaziTroskove(poredajPo.getSelectedItem().toString());
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }

                    }
                });

    }

    private void prikaziTroskove(String uvjetSortiranja) {
        Comparator<Trosak> usporediPoDatumu = new Comparator<Trosak>() {
            @Override
            public int compare(Trosak o1, Trosak o2) {
                if (!o1.getDatumGodina().equals(o2.getDatumGodina())) {
                    return o1.getDatumGodina().compareTo(o2.getDatumGodina());
                }
                else {
                    if (!o1.getDatumMjesec().equals(o2.getDatumMjesec())) {
                        return o1.getDatumMjesec().compareTo(o2.getDatumMjesec());
                    }
                    else {
                        return o1.getDatumDan().compareTo(o2.getDatumDan());
                    }
                }
            }
        };
        if (uvjetSortiranja.equals("datumu silazno")) {
            Collections.sort(troskovi, usporediPoDatumu);
        }
        else if (uvjetSortiranja.equals("datumu uzlazno")) {
            Collections.sort(troskovi, usporediPoDatumu);
            Collections.reverse(troskovi);
        }
        ListView prikazTroskova = findViewById(R.id.prikazTroskova);
        TrosakAdapter arrayAdapter = new TrosakAdapter(this, troskovi);
        prikazTroskova.setAdapter(arrayAdapter);
    }
}