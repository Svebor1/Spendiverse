package com.example.spendiverse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.icu.util.Currency;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DodavanjeValuteActivity extends AppCompatActivity {
    private Spinner spinnerSvihValuta;
    private Button dodajValutuButton;
    String TAG = "DodavanjeValuteActivity";
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String zadaneValute[] = {"HRK", "USD", "EUR", "GBP"};
    ArrayList<String> valute = new ArrayList<>(Arrays.asList(zadaneValute));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodavanje_valute);
        spinnerSvihValuta = findViewById(R.id.spinner_sve_valute);
        dodajValutuButton = findViewById(R.id.dodaj_valutu);

        List <String> sveValute = getAllCurrencies();
        ArrayAdapter arrayAdapterSveValute = new ArrayAdapter(this, R.layout.spinner_item, sveValute);
        arrayAdapterSveValute.setDropDownViewResource(R.layout.spinner_item);
        spinnerSvihValuta.setAdapter(arrayAdapterSveValute);
        prikaziValute();

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (provjeriUnos()) {
                    dodajValutu();
                    prikaziValute();
                }
            }
        };
        dodajValutuButton.setOnClickListener(listener);
    }
    public static List<String> getAllCurrencies() {
        Set<String> popisValuta = new HashSet<String>();
        Locale[] locs = Locale.getAvailableLocales();

        for(Locale loc : locs) {
            try {
                Currency currency = Currency.getInstance( loc );

                if ( currency != null ) {
                    popisValuta.add(currency.getCurrencyCode());
                }
            } catch(Exception exc)
            {
                // Locale not found
            }
        }

        return popisValuta.stream().collect(Collectors.toList());
    }
    private void dodajValutu() {
        String nazivValute = spinnerSvihValuta.getSelectedItem().toString();
        Map<String, Object> data = new HashMap<>();
        data.put("naziv", nazivValute);
        db.collection("korisnici").document(firebaseUser.getUid())
                .collection("valute").add(data);
    }
    private void prikaziValute() {

        valute = new ArrayList<>(Arrays.asList(zadaneValute));
        Context context = this;
        db.collection("korisnici").document(firebaseUser.getUid()).collection("valute")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String nazivValute = document.getData().get("naziv").toString();

                                valute.add(nazivValute);
                            }

                            ArrayAdapter<String> itemsAdapter =
                                    new ValutaAdapter(context, valute);
                            ListView listView = (ListView) findViewById(R.id.lista_valuta);
                            listView.setAdapter(itemsAdapter);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }

                    }
                });


    }
    boolean provjeriUnos() {
        return !valute.contains(spinnerSvihValuta.getSelectedItem().toString());
    }
}