package com.example.spendiverse;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
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
        //kako ću ići po svim korisnicima kako ako ih je više
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("ljestvica")
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
                            if (doc.get("bodovi") != null && doc.get("email") != null ) {
                                RezultatNatjecatelja trenutniRezultat = new RezultatNatjecatelja(doc.getString( "email"), doc.getLong( "bodovi").intValue());
                                rezultatiNatjecatelja.add(trenutniRezultat);

                            }
                        }
                        prikaziLjestvicu();
                    }
                });


    }

    private void prikaziLjestvicu() {
        ListView prikazLjestvice = findViewById(R.id.ljestvica);
        LjestvicaAdapter arrayAdapter = new LjestvicaAdapter(this,R.id.ljestvica, rezultatiNatjecatelja);
        prikazLjestvice.setAdapter(arrayAdapter);
    }
}