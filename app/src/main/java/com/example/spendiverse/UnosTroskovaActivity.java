package com.example.spendiverse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UnosTroskovaActivity extends AppCompatActivity {
    private final String TAG = "Unos troskova activity";
    private Spinner spinner;
    private Spinner spinnerValuta;
    private final Calendar myCalendar = Calendar.getInstance();
    private TextView nazivTroska;
    private Button dodatiTrosak;
    private Integer dan;
    private Integer mjesec;
    private Integer godina;
    private TextView cijenaTroska;
    private TextView datumTroska;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unos_troskova);
        //postavlja strelicu za natrag
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        spinner = findViewById(R.id.spinner);
        spinnerValuta = findViewById(R.id.spinner_valuta);
        datumTroska = findViewById(R.id.datum_troska);
        dodatiTrosak = findViewById(R.id.dodati_trosak);
        nazivTroska = findViewById(R.id.naziv_troska);
        cijenaTroska = findViewById(R.id.cijena_troska);
        String kategorije[] = {"prehrana","kućanstvo","promet"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.spinner_item, kategorije);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(arrayAdapter); //postavljanje niz mogućih kategorija u izbornik za kategorije

        String valute[] = {"HRK","USD","EUR", "GBP"};
        ArrayAdapter arrayAdapterValute = new ArrayAdapter(this, R.layout.spinner_item , valute);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerValuta.setAdapter(arrayAdapterValute);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) { //ako je to upis novog troška

            prikaziDatum();
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (provjeriUnos()) {
                        dodajNoviTrosak();
                    }
                }
            };
            dodatiTrosak.setOnClickListener(listener);
        }
        else {
            //uzimaju se podaci starog troška i prikazuju se na ekranu
            String nazivTroskaText = bundle.getString("naziv");
            nazivTroska.setText(nazivTroskaText);
            Integer cijenaTroskaText = bundle.getInt("cijena");
            cijenaTroska.setText(cijenaTroskaText.toString());
            String kategorijaTroska = bundle.getString("kategorija");
            int spinnerPosition = arrayAdapter.getPosition(kategorijaTroska);
            spinner.setSelection(spinnerPosition);

            String valutaTroska = bundle.getString("valuta");
            int spinnerPositionValuta = arrayAdapterValute.getPosition(valutaTroska);
            spinnerValuta.setSelection(spinnerPositionValuta);

            String firebaseIdTroska = bundle.getString("firebaseId");
            Integer datumDan = bundle.getInt("datumDan");
            Integer datumMjesec = bundle.getInt("datumMjesec")-1;
            Integer datumGodina = bundle.getInt("datumGodina");
            postaviDatum(datumDan, datumMjesec, datumGodina);
            prikaziDatum();
            dodatiTrosak.setText("promijeni trošak");
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (provjeriUnos()) {
                        promijeniTrosak(firebaseIdTroska);
                    }
                }
            };
            dodatiTrosak.setOnClickListener(listener);

        }
    }
    public void showDatePickerDialog(View v) {
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                prikaziDatum();
            }

        };
        new DatePickerDialog(this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * ova metoda postavlja datum troška i prima tri parametra
     * @param dan dan troška
     * @param mjesec mjesec troška
     * @param godina godina troška
     */
    private void postaviDatum(Integer dan, Integer mjesec, Integer godina) {
        myCalendar.set(Calendar.YEAR, godina);
        myCalendar.set(Calendar.MONTH, mjesec);
        myCalendar.set(Calendar.DAY_OF_MONTH, dan);
    }

    /**
     * ova metoda prikazuje datum troška
     */
    private void prikaziDatum() {
        godina = myCalendar.get(Calendar.YEAR);
        mjesec = myCalendar.get(Calendar.MONTH)+1;
        dan = myCalendar.get(Calendar.DAY_OF_MONTH);
        datumTroska.setText(Integer.toString(dan) + "." + Integer.toString(mjesec) + "." + Integer.toString(godina));
    }

    /**
     * ova metoda uzima sve podatke vezane uz trošak i sprema ih u bazu
     */
    private void dodajNoviTrosak() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Map<String, Object> data = new HashMap<>();
        data.put("cijena", cijenaTroska.getText().toString());
        data.put("naziv", nazivTroska.getText().toString());
        data.put("kategorija", spinner.getSelectedItem().toString());
        data.put("valuta", spinnerValuta.getSelectedItem().toString());
        data.put("datumDan", dan);
        data.put("datumMjesec", mjesec);
        data.put("datumGodina", godina);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("korisnici").document(firebaseUser.getUid()).collection("troskovi").add(data)
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
        finish();

    }

    /**
     * ova metoda uzima sve podatke vezane uz promijenjeni trošak i mijenja trošak u bazi
     */
    private void promijeniTrosak(String firebaseId) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Map<String, Object> data = new HashMap<>();
        data.put("cijena", cijenaTroska.getText().toString());
        data.put("naziv", nazivTroska.getText().toString());
        data.put("kategorija", spinner.getSelectedItem().toString());
        data.put("valuta", spinner.getSelectedItem().toString());
        data.put("datumDan", dan);
        data.put("datumMjesec", mjesec);
        data.put("datumGodina", godina);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("korisnici").document(firebaseUser.getUid()).collection("troskovi").document(firebaseId)
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
        finish();
    }

    /**
     * ova metoda provjerava je su li sva potrebna polja ispunjena
     * @return
     */
    private boolean provjeriUnos(){
        boolean rezultatBooleana = true;

        if (cijenaTroska.getText().toString().replace(" ","").equals("")) {
            cijenaTroska.setError("Unesite cijenu troška");
            rezultatBooleana = false;
        }
        if (nazivTroska.getText().toString().replace(" ","").equals("")) {
            nazivTroska.setError("Unesite naziv troška");
            rezultatBooleana = false;
        }

        return rezultatBooleana;
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
