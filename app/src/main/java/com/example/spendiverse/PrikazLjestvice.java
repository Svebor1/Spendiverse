package com.example.spendiverse;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class PrikazLjestvice extends AppCompatActivity {

    private final String TAG = "PrikazLjestvice";
    ArrayList<RezultatNatjecatelja> rezultatiNatjecatelja = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prikaz_ljestvice);
        //postavlja strelicu za natrag
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("ljestvica")
                .whereEqualTo("prikaz", true)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        rezultatiNatjecatelja = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("bodovi") != null && doc.get("nadimak") != null ) {
                                RezultatNatjecatelja trenutniRezultat = new RezultatNatjecatelja(doc.getString( "nadimak"), doc.getLong( "bodovi").intValue());
                                rezultatiNatjecatelja.add(trenutniRezultat);

                            }
                        }
                        Comparator<RezultatNatjecatelja> usporediPoBodovima =
                                (RezultatNatjecatelja o1, RezultatNatjecatelja o2) -> o1.getRezultatKorisnika().compareTo( o2.getRezultatKorisnika() );

                        Collections.sort(rezultatiNatjecatelja, usporediPoBodovima.reversed());
                        prikaziLjestvicu();
                    }
                });


    }

    private void prikaziLjestvicu() {
        ListView prikazLjestvice = findViewById(R.id.ljestvica);
        LjestvicaAdapter arrayAdapter = new LjestvicaAdapter(this,R.id.ljestvica, rezultatiNatjecatelja);
        prikazLjestvice.setAdapter(arrayAdapter);
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