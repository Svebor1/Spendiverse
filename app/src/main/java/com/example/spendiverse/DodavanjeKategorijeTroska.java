package com.example.spendiverse;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DodavanjeKategorijeTroska extends AppCompatActivity {

    EditText nazivKategorijeUnos;
    Button dodajKategorijuTroskaButton;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodavanje_kategorije_troska);
        nazivKategorijeUnos = findViewById(R.id.naziv_kategorije);
        dodajKategorijuTroskaButton = findViewById(R.id.dodaj_kategoriju_troska);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dodajKategoriju();
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
}