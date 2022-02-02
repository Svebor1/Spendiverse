package com.example.spendiverse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class FinancijskiPlanActivity extends AppCompatActivity {
    private Spinner spinnerMjeseci;
    private Spinner spinnerGodine;
    private final String TAG = "FinancijskiPlanActivity";
    private TextView dzeparac;
    private TextView poslovi;
    private TextView pokloni;
    private TextView ostalo;
    private TextView ustedzevina;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financijski_plan);
        dzeparac = findViewById(R.id.dzeparac_upis);
        poslovi = findViewById(R.id.poslovi_upis);
        pokloni = findViewById(R.id.pokloni_upis);
        ostalo = findViewById(R.id.ostalo_upis);
        ustedzevina = findViewById(R.id.ustedzevina_upis);
        Button azurirajPlan = findViewById(R.id.azuriraj_plan);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dodajNoviPlan();
            }
        };
        azurirajPlan.setOnClickListener(listener);

        String mjeseci[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};

        spinnerMjeseci = findViewById(R.id.spinner_mjeseci);
        ArrayAdapter arrayAdapterMjeseci = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mjeseci);
        arrayAdapterMjeseci.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMjeseci.setAdapter(arrayAdapterMjeseci);
        spinnerMjeseci.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String mjesec = spinnerMjeseci.getSelectedItem().toString();
                String godine = spinnerGodine.getSelectedItem().toString();
                nadiTroskove(mjesec,godine);
                prikazPlana(mjesec,godine);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
        String godine[] = {"2022","2021","2020","2019","2018"};

        spinnerGodine = findViewById(R.id.spinner_godine);
        ArrayAdapter arrayAdapterGodine = new ArrayAdapter(this, android.R.layout.simple_spinner_item, godine);
        arrayAdapterGodine.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGodine.setAdapter(arrayAdapterGodine);
        spinnerGodine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String godine = spinnerGodine.getSelectedItem().toString();
                String mjesec = spinnerMjeseci.getSelectedItem().toString();
                nadiTroskove(mjesec,godine);
                prikazPlana(mjesec,godine);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
    }

    private void  nadiTroskove(String mjesec,String godine) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("korisnici").document(firebaseUser.getUid()).collection("troskovi")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Integer troskovi = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String datumMjesec = document.getData().get("datumMjesec").toString();
                                String datumGodina = document.getData().get("datumGodina").toString();
                                Integer cijena = Integer.parseInt(document.getData().get("cijena").toString());
                                if (godine.equals(datumGodina) && mjesec.equals(datumMjesec)) {
                                    troskovi = troskovi + cijena;
                                }
                            }
                            TextView potrosenoText = findViewById(R.id.potroseno_text);
                            potrosenoText.setText(troskovi.toString() + " " + "kn");
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void dodajNoviPlan() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Map<String, Object> data = new HashMap<>();
        data.put("dzeparac",dzeparac.getText().toString());
        data.put("poslovi", poslovi.getText().toString());
        data.put("pokloni", pokloni.getText().toString());
        data.put("ostalo", ostalo.getText().toString());
        data.put("ustedzevina", ustedzevina.getText().toString());
        data.put("mjesec", spinnerMjeseci.getSelectedItem().toString());
        data.put("godina", spinnerGodine.getSelectedItem().toString());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("korisnici").document(firebaseUser.getUid()).collection("planovi").add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    private void prikazPlana(String mjesec,String godine) {
        ocistiTekst();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("korisnici").document(firebaseUser.getUid()).collection("planovi")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String planMjesec = document.getData().get("mjesec").toString();
                                String planGodina = document.getData().get("godina").toString();
                                String planDzeparac = document.getData().get("dzeparac").toString();
                                String planPokloni = document.getData().get("pokloni").toString();
                                String planPoslovi = document.getData().get("poslovi").toString();
                                String planOstalo = document.getData().get("ostalo").toString();
                                String planUstedzevina = document.getData().get("ustedzevina").toString();
                                if (godine.equals(planGodina) && mjesec.equals(planMjesec)) {
                                    dzeparac.setText(planDzeparac);
                                    poslovi.setText(planPoslovi);
                                    pokloni.setText(planPokloni);
                                    ostalo.setText(planOstalo);
                                    ustedzevina.setText(planUstedzevina);
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }
    private void ocistiTekst(){
        dzeparac.setText("");
        poslovi.setText("");
        pokloni.setText("");
        ostalo.setText("");
        ustedzevina.setText("");

    }


    }

