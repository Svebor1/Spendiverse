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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FinancijskiPlanActivity extends AppCompatActivity {
    //varijable za sve Spinnere u layoutu
    private Spinner spinnerMjeseci;
    private Spinner spinnerGodine;
    //za debugiranje
    private final String TAG = "FinancijskiPlanActivity";
    //za dobivanje trenutnog datuma
    private final Calendar myCalendar = Calendar.getInstance();
    //varijable za sve TextViewove u layoutu
    private TextView dzeparac;
    private TextView poslovi;
    private TextView pokloni;
    private TextView ostalo;
    private TextView ustedzevina;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financijski_plan);
        //pronalazi TextViewove prema id-u
        dzeparac = findViewById(R.id.dzeparac_upis);
        poslovi = findViewById(R.id.poslovi_upis);
        pokloni = findViewById(R.id.pokloni_upis);
        ostalo = findViewById(R.id.ostalo_upis);
        ustedzevina = findViewById(R.id.ustedzevina_upis);
        //pronalazi Button po id-u
        Button azurirajPlan = findViewById(R.id.azuriraj_plan);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dodajNoviPlan();
            }
        };
        //postavlja novi plan na pritisak Buttona
        azurirajPlan.setOnClickListener(listener);

        //Pronalazi treutni datum
        Integer trenutnaGodina = myCalendar.get(Calendar.YEAR);
        Integer trenutniMjesec = myCalendar.get(Calendar.MONTH)+1;

        //postavljanje lista s izborima za mjesece i godine
        String mjeseci[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
        spinnerMjeseci = findViewById(R.id.spinner_mjeseci);
        postaviSpinner(mjeseci, trenutniMjesec.toString(), spinnerMjeseci);

        String godine[] = {"2022","2021","2020","2019","2018"};
        spinnerGodine = findViewById(R.id.spinner_godine);
        postaviSpinner(godine, trenutnaGodina.toString(), spinnerGodine);
    }

    /**
     *Postavlja izbore za Spinner i stavlja trenutni zbor kao početni.
     *
     * @param vrijednosti lista koja sadržava elemente koji se mogu izabrati
     * @param trenutniIzbor element iz liste vrijednosti koji treba izabrati u Spinneru
     * @param spinner Spinner koji učitava izbore
     */
    private void postaviSpinner(String[] vrijednosti,String trenutniIzbor, Spinner spinner){
        //Stvara Adapter za listu vrijednosti
        ArrayAdapter vrijednostiAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, vrijednosti);
        //Određuje izgled ponuđenih izbora u Spinneru
        vrijednostiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Spinner se povezuje s Adapterom
        spinner.setAdapter(vrijednostiAdapter);
        //traži izbor koji želimo postaviti unutar ponuđenih izbora u Spinneru
        int spinnerPosition = vrijednostiAdapter.getPosition(trenutniIzbor.toString());
        spinner.setSelection(spinnerPosition);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //Prikazuje plan za izabranu godinu i mjesec
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

    /**
     * Funkcija koja u bazi traži ukupan iznos troškova u određenoj godini i mjesecu za trenutno
     * prijavljenog korisnika.
     *
     * @param mjesec izabrani mjesec
     * @param godine izabrana godina
     */
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


    /**
     * Uzima sadržaj upisan u TextViewove i dodaje ga u novi financijski plan za
     * izabranu godinu i mjesec.
     */
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

    /**
     * Uzima upisane podatke iz baze i prikazuje napravljen plan za izabrani mjesec i godinu trenutno prijavljenog korisnika.
     *
     * @param mjesec izabrani mjesec
     * @param godine izabrana godina
     */
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

    /**
     * Čisti TextVieweve prije prikazivanja plana.
     */
    private void ocistiTekst(){
        dzeparac.setText("");
        poslovi.setText("");
        pokloni.setText("");
        ostalo.setText("");
        ustedzevina.setText("");

    }


    }

