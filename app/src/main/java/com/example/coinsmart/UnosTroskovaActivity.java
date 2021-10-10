package com.example.coinsmart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
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
    final Calendar myCalendar = Calendar.getInstance();
    private TextView nazivTroska;
    private Button dodatiTrosak;
    private TextView cijenaTroska;
    TextView datumTroska;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unos_troskova);
        spinner = findViewById(R.id.spinner);
        String kategorije[] = {"prehrana","kućanstvo","promet"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, kategorije);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        datumTroska = findViewById(R.id.datum_troska);
        prikaziDatum();

        dodatiTrosak = findViewById(R.id.dodati_trosak);
        nazivTroska = findViewById(R.id.naziv_troska);
        cijenaTroska = findViewById(R.id.cijena_troska);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dodajNoviTrosak();
            }
        };
        dodatiTrosak.setOnClickListener(listener);

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
    private void prikaziDatum() {
        int year = myCalendar.get(Calendar.YEAR);
        int month = myCalendar.get(Calendar.MONTH)+1;
        int day = myCalendar.get(Calendar.DAY_OF_MONTH);

        datumTroska.setText(Integer.toString(day) + "." + Integer.toString(month) + "." + Integer.toString(year)); //
    }

    private void dodajNoviTrosak() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Map<String, Object> data = new HashMap<>(); //
        data.put("cijena", cijenaTroska.getText().toString());
        data.put("naziv", nazivTroska.getText().toString());
        data.put("kategorija", spinner.getSelectedItem().toString());
        data.put("datum", datumTroska.getText());
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
        //nesto kao startactivity
        finish();

    }
}
