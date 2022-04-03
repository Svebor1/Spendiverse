package com.example.spendiverse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

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
import java.util.Map;

public class DodavanjeKategorijeTroska extends AppCompatActivity {
    String TAG = "DodavanjeKategorijeTroska";
    EditText nazivKategorijeUnos;
    Button dodajKategorijuTroskaButton;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String zadaneKategorije[] = {"prehrana","kućanstvo","promet"};
    ArrayList<String> kategorije = new ArrayList<>(Arrays.asList(zadaneKategorije));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodavanje_kategorije_troska);
        nazivKategorijeUnos = findViewById(R.id.naziv_kategorije);
        dodajKategorijuTroskaButton = findViewById(R.id.dodaj_kategoriju_troska);

        prikaziKategorije();
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (provjeriUnos()) {
                    dodajKategoriju();
                    prikaziKategorije();
                }
            }
        };
        dodajKategorijuTroskaButton.setOnClickListener(listener);
    }
    private void dodajKategoriju() {
        String nazivKategorijeTroska = nazivKategorijeUnos.getText().toString();
        Map<String, Object> data = new HashMap<>();
        data.put("naziv", nazivKategorijeTroska);
        db.collection("korisnici").document(firebaseUser.getUid())
                .collection("kategorije").add(data);


    }
    private void prikaziKategorije() {
        kategorije = new ArrayList<>(Arrays.asList(zadaneKategorije));
        Context context = this;
        db.collection("korisnici").document(firebaseUser.getUid()).collection("kategorije")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String nazivKategorije = document.getData().get("naziv").toString();

                                kategorije.add(nazivKategorije);
                            }
                            // prikaziTroskove(poredajPo.getSelectedItem().toString(), filterKategorija.getSelectedItem().toString(), filterzaRazdoblja.getSelectedItem().toString(), filterzaValute.getSelectedItem().toString());
                            ArrayAdapter<String> itemsAdapter =
                            //        new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, kategorije);
                                    new KategorijaAdapter(context, kategorije);
                            ListView listView = (ListView) findViewById(R.id.kategorije_troska);
                            listView.setAdapter(itemsAdapter);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }

                    }
                });
    }
    private boolean provjeriUnos(){
        boolean rezultatBooleana = true;

        if (nazivKategorijeUnos.getText().toString().replace(" ","").equals("")) {
            nazivKategorijeUnos.setError("Unesite naziv kategorije");
            rezultatBooleana = false;
        }
        if (kategorije.stream().anyMatch(str -> str.toLowerCase().equals(nazivKategorijeUnos.getText().toString().toLowerCase()))) {
            nazivKategorijeUnos.setError("Upisana kategorija već postoji");
            rezultatBooleana = false;
        }

        return rezultatBooleana;
    }
}